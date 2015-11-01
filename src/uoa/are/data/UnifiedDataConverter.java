// Copyright 2015 Tony (Huansheng) Liu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package uoa.are.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnifiedDataConverter extends RawDataConverter {

    private final String PATTERN = "(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)_([a-zA-Z0-9]+)\\.csv";

    public UnifiedDataConverter(File src, String dst_dir, String project_id, String subject_id, String user_id,
            String algorithm) {
        super(src, dst_dir, project_id, subject_id, user_id, algorithm);

        String file_name = src.getName();
        Pattern datePatt = Pattern.compile(PATTERN);
        Matcher m = datePatt.matcher(file_name);
        logger.debug(file_name + " vs. " + datePatt);

        int day, month, year, hour, minute;
        if (m.matches()) {
            year = Integer.parseInt(m.group(1));
            month = Integer.parseInt(m.group(2));
            day = Integer.parseInt(m.group(3));
            hour = Integer.parseInt(m.group(4));
            minute = Integer.parseInt(m.group(5));
            setStartTime(year, month, day, hour, minute);
        } else {
            throw new IllegalArgumentException("NOT match: " + file_name + ":" + datePatt);
        }
    }

    /**
     * Converting Raw Data file to unified CSV file.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    protected void doConvert() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(_srcFile));

            PrintWriter pw = new PrintWriter(_dstFile);

            long offset = 0;
            String line;
            while ((line = br.readLine()) != null) {
                pw.println(line);
                offset++;
                // Split files and process every individual file in parallel
                if (offset == BLOCK_SIZE_100HZ) {
                    // Add up duration
                    addDuration(offset / 100);

                    offset = 0;
                    // close current file
                    pw.close();

                    // Start activity recognition thread
                    processCurrentBlock();

                    // now _dstFile is renewed
                    pw = new PrintWriter(_dstFile);
                }
            }
            br.close();
            pw.close();

            if (offset > 0) {
                // Add up duration
                addDuration(offset / 100);

                processCurrentBlock();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}

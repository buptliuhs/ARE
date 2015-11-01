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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class USenseDataConverter extends RawDataConverter {

    private final String PATTERN = "([a-zA-Z0-9]+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)_([a-zA-Z0-9]+)\\.txt";

    public USenseDataConverter(File src, String dst_dir, String project_id, String subject_id, String user_id, String algorithm) {
        super(src, dst_dir, project_id, subject_id, user_id, algorithm);

        String file_name = src.getName();
        Pattern datePatt = Pattern.compile(PATTERN);
        Matcher m = datePatt.matcher(file_name);
        logger.debug(file_name + " vs. " + datePatt);

        int day, month, year, hour, minute;
        if (m.matches()) {
            day = Integer.parseInt(m.group(2));
            month = Integer.parseInt(m.group(3));
            year = Integer.parseInt(m.group(4));
            hour = Integer.parseInt(m.group(5));
            minute = Integer.parseInt(m.group(6));
            setStartTime(year, month, day, hour, minute);
        } else {
            throw new IllegalArgumentException("NOT match: " + file_name + ":" + datePatt);
        }
    }

    /**
     * Converting USense Raw Data file to unified CSV file.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    protected void doConvert() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(_srcFile));

            PrintWriter pw = new PrintWriter(_dstFile);

            int length = 2;
            byte[] header_block = new byte[length];
            long offset = 0;
            int number_of_read;
            boolean started = false;
            byte page_number = 0;
            while ((number_of_read = bis.read(header_block, 0, length)) > 0) {
                if (header_block[0] != 0x20 || header_block[1] != 0x0a) {
                    logger.warn("Invalid new page/header, exit parsing");
                    break;
                }

                // page size: 4088 bytes (227 records, each has 9 * 2 bytes = 18
                // bytes)
                // 227 * 18 = 4086 bytes
                // 2 bytes: page number + CRC
                // For more details, refer to Huansheng's masters thesis
                int page_size = 4088;
                int records_per_page = 227;
                byte[] data_block = new byte[page_size];

                number_of_read = bis.read(data_block, 0, page_size);
                if (number_of_read < page_size) {
                    logger.info("END OF FILE");
                    break;
                }

                if (!started) {
                    page_number = data_block[4086];
                    started = true;
                    logger.debug("Set page_number: " + (int) (page_number & 0xff));
                }

                // logger.debug("page #: "
                // + String.format("%02x", page[4086] & 0xff) + ", "
                // + (int) (page[4086] & 0xff) + ", "
                // + (int) (page_number & 0xff) + "; crc: "
                // + String.format("%02x", page[4087] & 0xff));

                if (data_block[4086] != page_number) {
                    logger.error("Page #: " + (int) (page_number & 0xff) + " is missing");
                }

                for (int p = 0; p < records_per_page; ++p) {
                    String ax = String.format("%.4f", raw2RealAcc(twoBytes2Short(data_block[18 * p + 0], data_block[18 * p + 1])));
                    String ay = String.format("%.4f", raw2RealAcc(twoBytes2Short(data_block[18 * p + 2], data_block[18 * p + 3])));
                    String az = String.format("%.4f", raw2RealAcc(twoBytes2Short(data_block[18 * p + 4], data_block[18 * p + 5])));
                    pw.println(ax + "," + ay + "," + az);
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
                page_number++;
            }
            bis.close();
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

    private short twoBytes2Short(byte hi, byte lo) {
        return (short) (((hi & 0xFF) << 8) | (lo & 0xFF));
    }

    private double raw2RealAcc(short val) {
        return (double) val * 2 / 32768;
    }

}

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

package uoa.are.dm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import uoa.are.common.Env;
import uoa.are.util.TimeUtil;

/**
 * Utility class to manipulate intermediate files to produce related reports.
 * 
 * @author hliu482
 * 
 */
class IntermediateFileFilter implements FilenameFilter {

    private String pattern;

    public IntermediateFileFilter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.matches(pattern);
    }
}

public class IntermediateFileManager {

    static Logger logger = Logger.getLogger(IntermediateFileManager.class);

    /**
     * List all Intermediate files under a specific directory
     * 
     * @param dir
     * @return
     */
    private static String[] listAllFiles(String dir, String pattern) {
        logger.debug("List ALL Intermediate files in " + dir);
        File file = new File(dir);
        if (file.exists()) {
            IntermediateFileFilter iff = new IntermediateFileFilter(pattern);
            String[] files = file.list(iff);
            Arrays.sort(files, new Comparator<String>() {
                public int compare(String f1, String f2) {
                    return f1.compareTo(f2);
                }
            });
            return files;
        }
        return new String[0];
    }

    /**
     * Find Intermediate file which contains the period [start_second,
     * end_second] of Intermediate.
     * 
     * @param dir
     * @param prj_id
     * @param sub_id
     * @param date
     * @param start_second
     * @param end_second
     * @return
     */
    private static String getIntermediateFileFullPath(String dir,
            String pattern, String prj_id, String sub_id, String date,
            int start_second, int end_second) {
        // list all Intermediate files
        String[] files = listAllFiles(dir, pattern);
        int hour = start_second / 3600;
        int minute = (start_second - hour * 3600) / 60;

        // make a check point for Intermediate
        String p = date.substring(0, 4) + "_" + date.substring(4, 6) + "_"
                + date.substring(6, 8) + "_" + (hour < 10 ? "0" : "") + hour
                + "_" + (minute < 10 ? "0" : "") + minute;
        logger.info("Looking for Intermediate file containing Intermediate of "
                + p);

        // check which file is just before the Intermediate's check point (p),
        // then
        // return the file name for further parsing
        String fileName = "";
        for (int i = files.length - 1; i >= 0; --i) {
            logger.info("Checking " + files[i]);
            String f = files[i].substring(0, 16);
            if (f.compareTo(p) <= 0) {
                fileName = files[i];
                logger.info("Found");
                break;
            }
        }
        return fileName;
    }

    /**
     * Read accelerometer data from a Intermediate file.
     * 
     * @param id
     * @param file_name
     * @param offset
     *            start position
     * @param how_many
     *            number of records to be read
     */
    private static void readInIntermediate(IntermediateData id,
            String file_name, long offset, int how_many) {

        logger.info("# of expected: " + how_many);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(file_name)));
            int skip = 0;
            double[] v = new double[how_many];
            Arrays.fill(v, 0);
            String line = null;
            int index = 0;
            while (((line = br.readLine()) != null) && (index < how_many)) {
                // Need to skip 'offset' records in file
                if (skip < offset) {
                    skip++;
                    continue;
                }
                // start to read
                String[] values = line.split(",");
                double vv = Double.parseDouble(values[0]);
                v[index] = vv;
                index++;
            }
            id.setV(v);
            id.setOffset(index);
            logger.info("# of read in: " + index);
            if (index != how_many) {
                // read enough Intermediate, otherwise, there is no enough
                // continuous
                // Intermediate to be read
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Get Intermediate data. Steps: 1) Find suitable file; 2) Read in
     * Intermediate.
     * 
     * @param prj_id
     * @param sub_id
     * @param date
     * @param start_second
     * @param end_second
     * @return
     * @throws Exception
     */
    public static IntermediateData getIntermediate(String pattern,
            String prj_id, String sub_id, String date, int start_second,
            int end_second) throws Exception {

        String dir = Env.RAW_PATH + prj_id + File.separator + sub_id
                + File.separator + Env.SIGNAL_PATH + File.separator;

        // Find suitable file
        String fileName = IntermediateFileManager.getIntermediateFileFullPath(
                dir, pattern, prj_id, sub_id, date, start_second, end_second);

        logger.debug("fileName: " + fileName);
        if (StringUtils.isEmpty(fileName)) {
            logger.error("Can not find Intermediate file");
            return null;
        }

        String full_path = dir + fileName;
        logger.debug("full_path: " + full_path);

        // now calculate the difference between 1) Intermediate file and 2) date
        // +
        // start_second
        // fileName: 2015_01_16_18_34.csv

        // 1)
        int y = Integer.parseInt(fileName.substring(0, 4));
        int m = Integer.parseInt(fileName.substring(5, 7));
        int d = Integer.parseInt(fileName.substring(8, 10));
        int H = Integer.parseInt(fileName.substring(11, 13));
        int M = Integer.parseInt(fileName.substring(14, 16));
        int S = 0;

        Calendar time_of_file = new GregorianCalendar(y, m - 1, d, H, M, S);
        logger.debug("time_of_file  = " + time_of_file.getTime());

        // 2)
        y = Integer.parseInt(date.substring(0, 4));
        m = Integer.parseInt(date.substring(4, 6));
        d = Integer.parseInt(date.substring(6, 8));
        H = TimeUtil.getHour(start_second);
        M = TimeUtil.getMinute(start_second);
        S = TimeUtil.getSecond(start_second);
        Calendar time_of_start = new GregorianCalendar(y, m - 1, d, H, M, S);
        logger.debug("time_of_start = " + time_of_start.getTime());

        // offset: unit 1 s or 1Hz
        long offset = (time_of_start.getTimeInMillis() - time_of_file
                .getTimeInMillis()) / 1000;
        logger.info("offset = " + offset + " (1 s)");

        // read in accelerometer data from Intermediate file
        IntermediateData id = new IntermediateData();
        id.setStart_second(start_second);
        id.setEnd_second(end_second);

        int how_many = (end_second - start_second + 1);
        readInIntermediate(id, full_path, offset, how_many);

        // Check if need to load another file?
        if (id.getOffset() < id.getV().length) {
            logger.info("More to be loaded: "
                    + (id.getV().length - id.getOffset()));
            IntermediateData id_next = getIntermediate(pattern, prj_id, sub_id,
                    date, id.getStart_second() + id.getOffset(),
                    id.getEnd_second());
            // Combine id & id_next together
            id.combine(id_next);
            logger.info("After combination, length of Intermediate: "
                    + id.getV().length);
        }

        logger.info("getIntermediate: " + id.getV().length);
        return id;
    }
}
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

package uoa.are.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import uoa.are.dm.DeviceType;

/**
 * Utility class to handle file and file name related stuffs.
 * 
 * @author hliu482
 * 
 */
public class FileUtil {

    private static Logger logger = Logger.getLogger(FileUtil.class);

    /**
     * Get device name from CSV file (USense device).
     * 
     * @param filename
     * @return
     */
    public static String getDeviceNameCSV(DeviceType dt, String filename) {
        Pattern datePatt = Pattern.compile(dt.getPattern());
        Matcher m = datePatt.matcher(filename);
        if (m.matches()) {
            String device_name = m.group(dt.getDeviceNameIndex());
            return device_name;
        }
        return null;
    }

    /**
     * Get file name.
     * 
     * @param filename
     * @return
     */
    public static String getName(String filename) {
        return new File(filename).getName();
    }

    /**
     * Remove suffix.
     * 
     * @param filename
     * @return
     */
    public static String removeSuffix(String filename) {
        String nameWithoutSuffix = filename;
        int pos = filename.lastIndexOf('.');
        if (pos > 0 && pos < filename.length() - 1) {
            nameWithoutSuffix = filename.substring(0, pos);
        }
        return nameWithoutSuffix;
    }

    /**
     * Get suffix of file name.
     * 
     * @param filename
     * @return
     */
    public static String getSuffix(String filename) {
        String suffix = "";
        int pos = filename.lastIndexOf('.');
        if (pos > 0 && pos < filename.length() - 1) {
            suffix = filename.substring(pos + 1);
        }
        return suffix;
    }

    /**
     * Get Mime type of file.
     * 
     * @param file
     * @return
     */
    public static String getMimeType(File file) {
        String mimetype = "";
        if (file.exists()) {
            if (getSuffix(file.getName()).equalsIgnoreCase("csv")) {
                mimetype = "application/vnd.ms-excel";
            } else if (getSuffix(file.getName()).equalsIgnoreCase("txt")) {
                mimetype = "text/plain";
            } else {
                javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
                mimetype = mtMap.getContentType(file);
            }
        }
        return mimetype;
    }

    /**
     * Make all necessary directories.
     * 
     * @param dir
     */
    public static void mkdirs(String dir) {
        logger.info("mkdirs: " + dir);
        File file = new File(dir);
        if (!file.exists())
            file.mkdirs();
    }

    /**
     * Delete file.
     * 
     * @param file
     */
    public static void deleteFile(String file) {
        logger.info("deleteFile: " + file);
        File f = new File(file);
        f.delete();
    }

    /**
     * Delete directory.
     * 
     * @param file
     */
    public static void deleteDir(String dir) {
        logger.info("deleteDir: " + dir);
        File d = new File(dir);
        String[] entries = d.list();
        for (String f : entries) {
            File e = new File(dir + File.separator + f);
            if (e.isDirectory()) {
                deleteDir(e.getAbsolutePath());
            } else {
                deleteFile(e.getAbsolutePath());
            }
        }
        d.delete();
    }

    /**
     * Check folder's size. Folder's size means that all sub folders' size +
     * total size of all files under this folder.
     * 
     * @param directory
     * @return
     */
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }
}

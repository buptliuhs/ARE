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

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.log4j.Logger;

import uoa.are.common.Configure;
import uoa.are.common.Const;
import uoa.are.common.Env;
import uoa.are.common.UserRole;
import uoa.are.data.RawDataConverter;
import uoa.are.database.ConnectionEx;
import uoa.are.database.SC;
import uoa.are.dm.DeviceType;
import uoa.are.dm.DeviceTypeManager;

/**
 * Servlet to upload files by using jQuery Upload.
 * 
 * @author hliu482
 * 
 */
public class UploadServlet extends AuthorizedServlet {
    private static final long serialVersionUID = 1L;
    protected Logger logger = Logger.getLogger(UploadServlet.class);

    private static final boolean ALLOW_DELETION = Boolean.parseBoolean(Configure.getInstance().getProperty(
            "ALLOW_DELETION"));

    /**
     * Build reponse to contains all files under specific folder.
     * 
     * @param request
     * @param response
     * @param dir
     * @param prjId
     * @param subId
     * @throws IOException
     */
    private void buildResponse(HttpServletRequest request, HttpServletResponse response, String dir, String prjId,
            String subId, DeviceType dt) throws IOException {
        logger.info("get file list for: (" + prjId + ", " + subId + ")");
        PrintWriter writer = response.getWriter();
        String prefix = request.getContextPath() + "/UploadServlet?";
        String ret = "";
        try {
            String user_id = "" + getSessionUserID(request);
            String role_id = "" + this.getSessionRoleID(request);
            ret = listAllFilesInJsonString(user_id, role_id, dir, prefix, prjId, subId, dt);
            logger.debug(ret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            writer.write(ret);
            writer.close();
        }
    }

    /**
     * Get json string for a given file.
     * 
     * @param user_id
     * @param file
     * @param prefix
     * @param prjId
     * @param subId
     * @param update_status
     * @return
     * @throws JSONException
     */
    private JSONObject getJson(String user_id, String role_id, File file, String prefix, String prjId, String subId,
            boolean update_status) throws JSONException {
        String suffix = "&project_id=" + prjId + "&subject_id=" + subId;
        JSONObject f = new JSONObject();
        f.put("name", file.getName());
        f.put("size", file.length());
        f.put("url", prefix + "file=" + file.getName() + suffix);
        if (role_id.equals("" + UserRole.ADMIN) || ALLOW_DELETION) {
            f.put("deleteUrl", prefix + "file=" + file.getName() + suffix);
            f.put("deleteType", "DELETE");
        }
        f.put("rerunUrl", prefix + "rerun=1&file=" + file.getName() + suffix);
        // f.put("pending", 1);
        if (update_status) {
            if (TaskManager.getInstance().isRunning(user_id, file.getName()))
                f.put("pending", 1);
        } else
            f.put("pending", 1);
        // algorithm
        f.put("algorithm", TaskManager.getInstance().getAlgorithmName(user_id, file.getName()));
        // processing time
        f.put("time", TaskManager.getInstance().getProcessingTime(user_id, file.getName()));
        f.put("stime", TaskManager.getInstance().getProcessedAt(user_id, file.getName()));
        f.put("jobs", TaskManager.getInstance().getNumberOfJobs(user_id, file.getName()));
        // duration
        f.put("duration", TaskManager.getInstance().getDuration(user_id, file.getName()));
        return f;
    }

    class FielNameComparator implements Comparator<File> {
        private Pattern pattern;
        private int[] timeIndex;

        public FielNameComparator(DeviceType dt) {
            pattern = Pattern.compile(dt.getPattern());
            String[] s = dt.getTimeIndex().split(",");
            timeIndex = new int[s.length];
            for (int i = 0; i < s.length; ++i)
                timeIndex[i] = Integer.parseInt(s[i]);
        }

        private long getTime(String name) {
            Matcher m = pattern.matcher(name);
            if (m.matches()) {
                long time = 0;
                for (int i = 0; i < timeIndex.length; ++i) {
                    int p = Integer.parseInt(m.group(timeIndex[i]));
                    // logger.info("p: " + p);
                    time *= 100;
                    // logger.info("time: " + time);
                    time += p;
                    // logger.info("sum: " + time);
                }
                // logger.info("Time: " + time);
                return time;
            }
            return 0;
        }

        @Override
        public int compare(File f1, File f2) {
            long time1 = getTime(f1.getName());
            long time2 = getTime(f2.getName());
            return (int) (time2 - time1);
        }

    }

    /**
     * List all files and return json string.
     * 
     * @param user_id
     * @param localPath
     * @param prefix
     * @param prjId
     * @param subId
     * @return
     * @throws JSONException
     */
    private String listAllFilesInJsonString(String user_id, String role_id, String localPath, String prefix,
            String prjId, String subId, DeviceType dt) throws JSONException {
        logger.debug("List ALL files");
        File file = new File(localPath);
        JSONArray json = new JSONArray();
        JSONObject resp = new JSONObject();
        if (file.exists()) {
            File[] files = file.listFiles();
            Arrays.sort(files, new FielNameComparator(dt));

            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory())
                    continue;
                json.put(getJson(user_id, role_id, files[i], prefix, prjId, subId, true));
            }
        }
        resp.put("files", json);
        String jsonString = resp.toString();
        return jsonString;
    }

    /**
     * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if (!isAuthorized(request))
            return;

        String project_id = request.getParameter("project_id");
        String subject_id = request.getParameter("subject_id");
        String file_name = request.getParameter("file");
        logger.info("project_id = " + project_id + ", subject_id = " + subject_id + ", file_name = " + file_name);

        String dir = Env.RAW_PATH + project_id + File.separator + subject_id + File.separator;
        DeviceType dt = DeviceTypeManager.getDeviceTypeBySubjectId(subject_id);
        if (!StringUtils.isEmpty(file_name)) {
            logger.debug("DEL: " + file_name);
            File file = new File(dir + file_name);
            if (file.exists()) {
                logger.info("Deleting file: " + file.getAbsolutePath());
                file.delete();
            } else {
                logger.error("File not existed: " + file.getAbsolutePath());
            }
        }

        buildResponse(request, response, dir, project_id, subject_id, dt);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     * 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAuthorized(request))
            return;

        String project_id = request.getParameter("project_id");
        String subject_id = request.getParameter("subject_id");
        String user_id = "" + getSessionUserID(request);
        logger.info("project_id = " + project_id + ", subject_id = " + subject_id);

        if (StringUtils.isEmpty(project_id) || StringUtils.isEmpty(subject_id)) {
            logger.error("project_id or subject_id is not present");
            return;
        }

        String dir = Env.RAW_PATH + project_id + File.separator + subject_id + File.separator;
        FileUtil.mkdirs(dir);

        DeviceType dt = DeviceTypeManager.getDeviceTypeBySubjectId(subject_id);
        String file_name = request.getParameter("file");
        boolean rerun = "1".equals(request.getParameter("rerun"));
        if (!StringUtils.isEmpty(file_name) && !rerun) {
            // Download file
            File file = new File(dir + file_name);
            logger.info("get file: " + file.getAbsolutePath());

            if (file.exists()) {
                int bytes = 0;
                ServletOutputStream op = response.getOutputStream();

                response.setContentType(FileUtil.getMimeType(file));
                response.setContentLength((int) file.length());
                response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

                byte[] bbuf = new byte[1024];
                DataInputStream in = new DataInputStream(new FileInputStream(file));

                while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
                    op.write(bbuf, 0, bytes);
                }

                in.close();
                op.flush();
                op.close();
            } else {
                logger.error("File not existed: " + file);
            }
        } else if (!StringUtils.isEmpty(file_name) && rerun) {
            String algorithm = request.getParameter("algorithm");
            File file = new File(dir + file_name);
            if (!TaskManager.getInstance().isRunning(user_id, file.getName())) {
                logger.info("Rerun file: " + file.getName());
                try {
                    startConvertingRawFile(dt, file.getAbsolutePath(), project_id, subject_id, user_id, algorithm);
                } catch (Exception e) {
                    logger.error("startConvertingRawFile", e);
                }
            } else {
                logger.warn("In processing...");
            }
        } else {
            // Get file list
            buildResponse(request, response, dir, project_id, subject_id, dt);
        }
    }

    private void startConvertingRawFile(DeviceType dt, String fileName, String prj_id, String sub_id, String user_id,
            String algorithm) throws Exception {
        File srcFile = new File(fileName);
        String outDir = srcFile.getParent() + File.separator + Env.SIGNAL_PATH + File.separator;
        FileUtil.mkdirs(outDir);

        String className = dt.getConverter();
        logger.info("Using Converter: " + className);
        @SuppressWarnings("unchecked")
        Class<? extends RawDataConverter> c = (Class<? extends RawDataConverter>) Class.forName(className);
        RawDataConverter rawDataConverter = c.getConstructor(File.class, String.class, String.class, String.class,
                String.class, String.class).newInstance(srcFile, outDir, prj_id, sub_id, user_id, algorithm);

        logger.info("Add new ActivityRecognition task to thread executor");
        DataConverterExecutor.getInstance().execute(rawDataConverter);
        // new Thread(rawDataConverter).start();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     * 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException(
                    "Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }

        if (!isAuthorized(request))
            return;

        // Upload file
        String project_id = request.getParameter("project_id");
        String subject_id = request.getParameter("subject_id");
        logger.info("project_id = " + project_id + ", subject_id = " + subject_id);

        String dir = Env.RAW_PATH + project_id + File.separator + subject_id + File.separator;

        DeviceType dt = DeviceTypeManager.getDeviceTypeBySubjectId(subject_id);

        ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
        response.setContentType("application/json");
        JSONArray json = new JSONArray();
        JSONObject resp = new JSONObject();
        String prefix = request.getContextPath() + "/UploadServlet?";
        try {
            List<FileItem> items = uploadHandler.parseRequest(request);
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    logger.debug("Uploading " + item.getName() + " ...");

                    String user_id = "" + getSessionUserID(request);
                    String role_id = "" + getSessionRoleID(request);

                    // check if the file name follows the naming convention
                    if (!item.getName().matches(dt.getPattern())) {
                        logger.info(item.getName().matches(dt.getPattern()));
                        logger.error("FILE NAME IS NOT VALID: " + item.getName() + ", pattern = " + dt.getPattern()
                                + ", deviceNameIndex = " + dt.getDeviceNameIndex());
                        JSONObject f = new JSONObject();
                        f.put("name", item.getName());
                        f.put("size", item.getSize());
                        f.put("error", "Filename is NOT valid");
                        json.put(f);
                        continue;
                    }
                    // check if the uSense file belongs to the device
                    String device_name = getDeviceNameForSubject(subject_id);
                    logger.info("Device Name in DB: " + device_name);
                    String device_name1 = FileUtil.getDeviceNameCSV(dt, item.getName());
                    logger.info("Device Name in FileName: " + device_name1);
                    if (!device_name.equals(device_name1)) {
                        logger.error("FILE DOES NOT BELONG TO THE SUBJECT: " + item.getName());
                        JSONObject f = new JSONObject();
                        f.put("name", item.getName());
                        f.put("size", item.getSize());
                        f.put("error", "File does not belong to the device: " + device_name);
                        json.put(f);
                        continue;
                    }
                    // check if the file is already existed
                    String fileName = dir + item.getName();
                    File file = new File(fileName);
                    if (file.exists()) {
                        logger.error("FILE EXISTS: " + item.getName());
                        JSONObject f = new JSONObject();
                        f.put("name", item.getName());
                        f.put("size", item.getSize());
                        f.put("error", "File exists");
                        json.put(f);
                        continue;
                    }
                    // save file
                    logger.info("Writing file to " + file.getAbsolutePath());
                    item.write(file);
                    json.put(getJson(user_id, role_id, file, prefix, project_id, subject_id, false));

                    // Start converting thread
                    startConvertingRawFile(dt, fileName, project_id, subject_id, user_id, null);
                }
            }
            resp.put("files", json);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            PrintWriter writer = response.getWriter();
            String ret = resp.toString();
            logger.info(ret);
            writer.write(ret);
            writer.close();
        }
    }

    /**
     * Get subject's device name.
     * 
     * @param sub_id
     * @return
     */
    private String getDeviceNameForSubject(String sub_id) {
        SC sc = null;
        try {
            sc = new SC(new ConnectionEx(Const.ARE));
            String sql = "SELECT count(*) AS count" + ", d.name as device_name"
                    + " FROM subject s, device d WHERE s.device_id = d.id" + " AND s.id = " + sub_id;
            logger.debug(sql);
            sc.execute(sql);
            sc.next();

            int count = sc.getInt("count");
            if (count != 0) {
                return sc.getString("device_name");
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }
}

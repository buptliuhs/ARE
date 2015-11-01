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

package uoa.are.validation;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uoa.are.common.ActivityType;
import uoa.are.common.Code;
import uoa.are.common.Env;
import uoa.are.dm.RecElement;
import uoa.are.dm.ReportManager;
import uoa.are.util.AuthorizedServlet;
import uoa.are.util.FileUtil;
import uoa.are.util.JSONUtil;
import uoa.are.util.NumberUtil;
import uoa.are.util.RandomStringGenerator;
import uoa.are.util.TimeUtil;

/**
 * Servlet to upload files by using jQuery Upload.
 * 
 * @author hliu482
 * 
 */
public class ValidationServlet extends AuthorizedServlet {
    private static final long serialVersionUID = 1L;
    protected Logger logger = Logger.getLogger(ValidationServlet.class);

    private Map<Object, Object> saveItem(FileItem item, String dir, String name)
            throws Exception {
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        result.put("result", Code.FAILED);
        result.put("message", "Unknown Error");
        logger.debug("saveItem " + item.getName() + " ...");

        String suffix = FileUtil.getSuffix(item.getName());
        logger.info("item.size: " + item.getSize());
        logger.info("item.suffix: " + suffix);
        // check if the file type is allowed and supported
        if ("xlsx".equals(suffix.toLowerCase())) {
            // check if the file is already existed
            String fileName = dir + name;
            File file = new File(fileName);
            // save file
            logger.info("Writing file to " + file.getAbsolutePath());
            item.write(file);
            result.put("result", Code.SUCCESSFUL);
            result.put("message", "Successful");
            return result;
        } else {
            logger.error("NOT SUPPORTED FILE TYPE: " + item.getName());
            result.put("result", Code.FAILED);
            result.put("message", "Only .xlsx file supported");
            return result;
        }
    }

    @SuppressWarnings("deprecation")
    private int[] loadAct(String fileName) throws IOException {
        int[] act = new int[60 * 60 * 24];
        Arrays.fill(act, ActivityType.UNDEFINED);

        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        boolean header_flag = true;
        for (; rowIterator.hasNext();) {
            Row row = rowIterator.next();
            if (header_flag) {
                logger.info("Skip header");
                header_flag = false;
                continue;
            }
            Cell t = row.getCell(0);
            Date d = DateUtil.getJavaDate(t.getNumericCellValue());
            int h = d.getHours();
            int m = d.getMinutes();
            int s = d.getSeconds();
            int i = h * 60 * 60 + m * 60 + s;
            int a = ActivityType.intValue(row.getCell(1).getStringCellValue());
            act[i] = a;
        }
        workbook.close();
        return act;
    }

    private Result validate(String prj_id, String sub_id, String date,
            String fileName) {
        Result result = new Result();
        try {
            RecElement[] actInDB = ReportManager.getActForDay(prj_id, sub_id,
                    date);
            int[] actMarkedByUser = loadAct(fileName);

            int total = 0;
            int unmatched = 0;
            for (int i = 0; i < actInDB.length; ++i) {
                // Skip invalid record, user does not want to validate it
                if (actMarkedByUser[i] == ActivityType.UNDEFINED
                        || actInDB[i].num_filt == ActivityType.UNDEFINED)
                    continue;

                if (actInDB[i].num_filt != actMarkedByUser[i]) {
                    Item item = new Item();
                    item.setSecond(i);
                    item.setActA(actInDB[i].num_filt);
                    item.setActB(actMarkedByUser[i]);
                    String time = TimeUtil.formatSecond2(i);
                    item.setTime(time);
                    result.getResults().add(item);
                    unmatched++;
                }
                total++;
            }
            logger.info("Total: " + total);
            logger.info("Unmatched: " + unmatched);
            double p = ((double) (total - unmatched) * 100) / ((double) total);
            result.setPercentage(NumberUtil.getDouble(p));
        } catch (Exception e) {
            logger.error("validate failed", e);
        }
        return result;
    }

    /*
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException(
                    "Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }

        if (!isAuthorized(request))
            return;

        // Upload file
        String prj_id = request.getParameter("prj_id");
        String sub_id = request.getParameter("sub_id");
        String date = request.getParameter("date");
        String user_id = "" + getSessionUserID(request);

        logger.info("user_id = " + user_id + ", prj_id = " + prj_id
                + ", sub_id = " + sub_id + ", date = " + date);

        String dir = Env.DATA_PATH + "tmp" + File.separator;

        ServletFileUpload uploadHandler = new ServletFileUpload(
                new DiskFileItemFactory());
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        result.put("result", Code.FAILED);
        result.put("message", "Unknown Error");
        try {
            // upload file
            List<FileItem> items = uploadHandler.parseRequest(request);
            String fileName = "";
            for (FileItem item : items) {
                logger.info("item.getName = " + item.getName());
                if (!item.isFormField()) {
                    fileName = item.getName() + "."
                            + RandomStringGenerator.generate();
                    result = saveItem(item, dir, fileName);
                }
            }
            // validate result
            String fullName = dir + fileName;
            Result r = validate(prj_id, sub_id, date, fullName);
            result.put("percentage", r.getPercentage());
            result.put("validation_result", r.getResults());
            // delete file
            FileUtil.deleteFile(fullName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            String ret = JSONUtil.newInstance(result).toString();
            // logger.info(ret);
            PrintWriter writer = response.getWriter();
            writer.write(ret);
            writer.close();
        }
    }

}

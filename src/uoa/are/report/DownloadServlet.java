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

package uoa.are.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.ibm.icu.math.BigDecimal;

import uoa.are.common.ActivityType;
import uoa.are.dm.RecElement;
import uoa.are.dm.ReportManager;
import uoa.are.util.AuthorizedServlet;
import uoa.are.util.NumberUtil;
import uoa.are.util.TimeUtil;

/**
 * Servlet to return download report data to client.
 * 
 * @author hliu482
 * 
 */
public class DownloadServlet extends AuthorizedServlet {

    private static final long serialVersionUID = 1L;

    private void addHeader(Row header, String[] names) {
        for (int i = 0; i < names.length; ++i) {
            Cell c = header.createCell(i);
            c.setCellValue(names[i]);
        }
    }

    private void createActSheet(SXSSFWorkbook wb, String prj_id, String sub_id, String date) {
        // get activity for the day
        try {
            RecElement[] status = ReportManager.getActForDay(prj_id, sub_id, date);

            // create sheet
            Sheet sh = wb.createSheet("Second-by-second result");
            // create header
            Row header = sh.createRow(0);
            String[] names = { "Time", "Activity" };
            addHeader(header, names);

            // create contents
            CreationHelper createHelper = wb.getCreationHelper();
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("hh:mm:ss"));

            int y = Integer.parseInt(date.substring(0, 4));
            int m = Integer.parseInt(date.substring(4, 6));
            int d = Integer.parseInt(date.substring(6));
            Calendar base = new GregorianCalendar(y, m - 1, d, 0, 0, 0);
            for (int i = 0; i < status.length; ++i) {
                // create a new row
                Row row = sh.createRow(i + 1);

                // column 'time'
                Cell cell = row.createCell(0);
                cell.setCellValue(base);
                cell.setCellStyle(cellStyle);
                base.add(Calendar.SECOND, 1);

                // column 'activity'
                cell = row.createCell(1);
                cell.setCellValue(ActivityType.stringValue(status[i].num_filt));
            }
        } catch (Exception e) {
            logger.error("ReportManager.getActForDay failed", e);
            return;
        }
    }

    private void addNewRow(Sheet sh, String prj_id, String sub_id, String date, int total, int act_type, int row_index) {
        try {
            int s = ReportManager.getDurationOfActForDay(prj_id, sub_id, date, act_type);
            double m = TimeUtil.secondsToMinutes(s);
            logger.info("d: " + m);
            String p = NumberUtil.getDouble((double) s / total * 100, BigDecimal.ROUND_UP, 1) + "%";

            // create a new row
            Row row = sh.createRow(row_index);

            Cell cell = row.createCell(0);
            cell.setCellValue(ActivityType.stringValue(act_type));

            cell = row.createCell(1);
            cell.setCellValue(m);

            cell = row.createCell(2);
            cell.setCellValue(p);
        } catch (Exception e) {
            logger.error("ReportManager.getDurationOfActForDay failed", e);
            return;
        }
    }

    private void addChartPicture(SXSSFWorkbook workbook, Sheet sheet, JFreeChart chart, int width, int height,
            int col1, int row1) throws IOException {
        ByteArrayOutputStream chart_out = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsPNG(chart_out, chart, width, height);
        /* Add picture to workbook */
        int picture_id = workbook.addPicture(chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);
        chart_out.close();

        /* Create the drawing container */
        Drawing drawing = sheet.createDrawingPatriarch();
        /* Create an anchor point */
        ClientAnchor anchor = new XSSFClientAnchor();
        /* Define top left corner, and we can resize picture suitable from there */
        anchor.setCol1(col1);
        anchor.setRow1(row1);
        /* Invoke createPicture and pass the anchor point and ID */
        Picture picture = drawing.createPicture(anchor, picture_id);
        /* Call resize method, which resizes the image */
        picture.resize();
    }

    private void createOverallSheet(SXSSFWorkbook wb, String prj_id, String sub_id, String date) {
        // create sheet
        Sheet sh = wb.createSheet("Overall");
        // create header
        Row header = sh.createRow(0);
        String[] names = { "Activity", "Total Time (minutes)", "Percentage" };
        addHeader(header, names);

        try {
            int total = ReportManager.getDuration(prj_id, sub_id, date);
            logger.info("Total: " + total);

            int row_index = 1;
            addNewRow(sh, prj_id, sub_id, date, total, ActivityType.WALKING, row_index++);
            addNewRow(sh, prj_id, sub_id, date, total, ActivityType.SHUFFLING, row_index++);
            addNewRow(sh, prj_id, sub_id, date, total, ActivityType.STANDING, row_index++);
            addNewRow(sh, prj_id, sub_id, date, total, ActivityType.SITTING, row_index++);
            addNewRow(sh, prj_id, sub_id, date, total, ActivityType.LYING, row_index++);
            addNewRow(sh, prj_id, sub_id, date, total, ActivityType.NONWEAR, row_index++);
            addNewRow(sh, prj_id, sub_id, date, total, ActivityType.INVERTED, row_index++);

            // add pie chart
            DefaultPieDataset chart_data = new DefaultPieDataset();

            for (int i = 1; i < row_index; ++i) {
                Row r = sh.getRow(i);
                String name = r.getCell(0).getStringCellValue();
                Number value = r.getCell(1).getNumericCellValue();
                chart_data.setValue(name, value);
            }

            JFreeChart pie_chart = ChartFactory.createPieChart("Daily Overall", chart_data, true, true, false);
            addChartPicture(wb, sh, pie_chart, 500, 500, 2, 10);
        } catch (Exception e) {
            logger.error("ReportManager.getDuration failed", e);
            return;
        }
    }

    private void createDetailedSheet(SXSSFWorkbook wb, String prj_id, String sub_id, String date) {
        // create sheet
        Sheet sh = wb.createSheet("Detailed");
        // create header
        Row header = sh.createRow(0);
        String[] names = { "Hour", ActivityType.S_WALKING, ActivityType.S_SHUFFLING, ActivityType.S_STANDING,
                ActivityType.S_SITTING, ActivityType.S_LYING, ActivityType.S_NONWEAR, ActivityType.S_INVERTED };
        int[] types = { ActivityType.WALKING, ActivityType.SHUFFLING, ActivityType.STANDING, ActivityType.SITTING,
                ActivityType.LYING, ActivityType.NONWEAR, ActivityType.INVERTED };

        addHeader(header, names);
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (int i = 0; i < 24; ++i) {
                String strHour = ((i < 10) ? "0" : "") + i;

                Row row = sh.createRow(i + 1);
                Cell cell = row.createCell(0);
                cell.setCellValue(strHour);

                for (int j = 0; j < types.length; ++j) {
                    int s = ReportManager.getDurationOfActForHour(prj_id, sub_id, date, types[j], i);
                    double m = TimeUtil.secondsToMinutes(s);
                    cell = row.createCell(j + 1);
                    cell.setCellValue(m);
                    // construct value in dataset
                    dataset.addValue(m, names[j + 1], strHour);
                }
            }

            // add bar chart
            JFreeChart bar_chart = ChartFactory.createBarChart("Activity per Hour", "Hour", "Minutes", dataset,
                    PlotOrientation.VERTICAL, true, true, false);
            addChartPicture(wb, sh, bar_chart, 1000, 500, 2, 28);
        } catch (Exception e) {
            logger.error("ReportManager.getDurationOfActForHour", e);
        }
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

        String prj_id = request.getParameter("prj_id");
        String sub_id = request.getParameter("sub_id");
        String date = request.getParameter("date");

        logger.info("prj_id = " + prj_id + ", sub_id = " + sub_id + ", date = " + date);

        if (StringUtils.isEmpty(prj_id) || StringUtils.isEmpty(sub_id) || StringUtils.isEmpty(date)) {
            logger.error("project_id or subject_id is not present");
            return;
        }

        // create workbook
        SXSSFWorkbook wb = new SXSSFWorkbook();

        // create second-by-second sheet
        createOverallSheet(wb, prj_id, sub_id, date);
        createDetailedSheet(wb, prj_id, sub_id, date);
        createActSheet(wb, prj_id, sub_id, date);

        // output spreadsheet
        String mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(mimeType);
        // response.setContentLength((int) downloadFile.length());

        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", "dailyReport_" + prj_id + "_" + sub_id + "_"
                + date + ".xlsx");
        response.setHeader(headerKey, headerValue);

        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();
        wb.write(outStream);
        outStream.close();

        // dispose of temporary files backing this workbook on disk
        wb.dispose();
        wb.close();
    }
}

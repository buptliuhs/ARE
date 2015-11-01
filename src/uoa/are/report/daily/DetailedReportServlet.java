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

package uoa.are.report.daily;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import uoa.are.common.ActivityType;
import uoa.are.common.Env;
import uoa.are.dm.ReportManager;
import uoa.are.report.BaseReportServlet;
import uoa.are.util.NumberUtil;
import uoa.are.util.TimeUtil;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.ibm.icu.math.BigDecimal;

/**
 * Servlet to return detailed report data to client.
 * 
 * @author hliu482
 * 
 */
public class DetailedReportServlet extends BaseReportServlet {

    private static final long serialVersionUID = 1L;

    private TableRow newTableRow(String prj_id, String sub_id, String date, int intAct, int total) throws Exception {
        TableRow tr = new TableRow();
        tr.addCell(ActivityType.stringValue(intAct));
        int s = ReportManager.getDurationOfActForDay(prj_id, sub_id, date, intAct);
        tr.addCell(TimeUtil.formatSecond(s));
        tr.addCell(TimeUtil.formatSecond(s, total));
        tr.addCell(NumberUtil.getDouble((double) s / total * 100, BigDecimal.ROUND_UP, 1) + " %");
        return tr;
    }

    /**
     * Generates the data table. This servlet assumes a special parameter that
     * contains the CSV URL from which to load the data.
     */
    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest request) throws DataSourceException {
        if (!this.isAuthorized(request))
            return null;

        String prj_id = request.getParameter(Env.PROJECT_PARAM_NAME);
        String sub_id = request.getParameter(Env.SUBJECT_PARAM_NAME);
        String date = request.getParameter(Env.DATE_PARAM_NAME);

        logger.info("DetailedReportServlet.generateDataTable: " + prj_id + "/" + sub_id + "/" + date);

        DataTable dataTable = new DataTable();
        List<ColumnDescription> cds = new LinkedList<ColumnDescription>();
        cds.add(new ColumnDescription("ACTIVITY", ValueType.TEXT, "ACTIVITY"));
        cds.add(new ColumnDescription("TOTAL TIME", ValueType.TEXT, "TOTAL TIME"));
        cds.add(new ColumnDescription("RELATIVE / 24H", ValueType.TEXT, "RELATIVE / 24H"));
        cds.add(new ColumnDescription("PERCENTAGE", ValueType.TEXT, "PERCENTAGE"));

        dataTable.addColumns(cds);

        List<TableRow> rows = new LinkedList<TableRow>();

        try {
            int total = ReportManager.getDuration(prj_id, sub_id, date);
            // logger.info("total: " + total);

            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.WALKING, total));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.SHUFFLING, total));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.STANDING, total));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.SITTING, total));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.LYING, total));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.NONWEAR, total));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.INVERTED, total));

            // logger.info("s: " + s);
            TableRow tr = new TableRow();
            tr.addCell("Total");
            tr.addCell(TimeUtil.formatSecond(total));
            tr.addCell(TimeUtil.formatSecond(total, total));
            tr.addCell("100.0 %");
            rows.add(tr);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DataSourceException(ReasonType.OTHER, e.getMessage());
        }

        dataTable.addRows(rows);
        return dataTable;
    }
}

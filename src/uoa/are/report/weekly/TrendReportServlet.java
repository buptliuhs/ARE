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

package uoa.are.report.weekly;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import uoa.are.common.ActivityType;
import uoa.are.common.Env;
import uoa.are.dm.ReportManager;
import uoa.are.report.BaseReportServlet;
import uoa.are.util.TimeUtil;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

/**
 * Servlet to return weekly report data to client.
 * 
 * @author hliu482
 * 
 */
public class TrendReportServlet extends BaseReportServlet {

    private static final long serialVersionUID = 1L;

    private String convertDate(String s) {
        return s.substring(6) + "/" + s.substring(4, 6);
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

        logger.info("TrendReportServlet.generateDataTable: " + prj_id + "/" + sub_id + "/" + date);

        List<String> list_of_day = TimeUtil.getListOfPreviousDays(date, 7);

        DataTable dataTable = new DataTable();
        List<ColumnDescription> cds = new LinkedList<ColumnDescription>();
        cds.add(new ColumnDescription("X", ValueType.TEXT, ""));
        cds.add(new ColumnDescription(ActivityType.S_WALKING, ValueType.NUMBER, ActivityType.S_WALKING));
        cds.add(new ColumnDescription(ActivityType.S_SHUFFLING, ValueType.NUMBER, ActivityType.S_SHUFFLING));
        cds.add(new ColumnDescription(ActivityType.S_STANDING, ValueType.NUMBER, ActivityType.S_STANDING));
        cds.add(new ColumnDescription(ActivityType.S_SITTING, ValueType.NUMBER, ActivityType.S_SITTING));
        cds.add(new ColumnDescription(ActivityType.S_LYING, ValueType.NUMBER, ActivityType.S_LYING));

        dataTable.addColumns(cds);

        List<TableRow> rows = new LinkedList<TableRow>();

        try {
            for (String d : list_of_day) {
                TableRow tr = new TableRow();
                tr.addCell(convertDate(d));
                tr.addCell(TimeUtil.secondsToMinutes(ReportManager.getDurationOfActForDay(prj_id, sub_id, d,
                        ActivityType.WALKING)));
                tr.addCell(TimeUtil.secondsToMinutes(ReportManager.getDurationOfActForDay(prj_id, sub_id, d,
                        ActivityType.SHUFFLING)));
                tr.addCell(TimeUtil.secondsToMinutes(ReportManager.getDurationOfActForDay(prj_id, sub_id, d,
                        ActivityType.STANDING)));
                tr.addCell(TimeUtil.secondsToMinutes(ReportManager.getDurationOfActForDay(prj_id, sub_id, d,
                        ActivityType.SITTING)));
                tr.addCell(TimeUtil.secondsToMinutes(ReportManager.getDurationOfActForDay(prj_id, sub_id, d,
                        ActivityType.LYING)));
                rows.add(tr);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DataSourceException(ReasonType.OTHER, e.getMessage());
        }

        dataTable.addRows(rows);
        return dataTable;
    }
}

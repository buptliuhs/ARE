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
 * Servlet to return overall report data to client.
 * 
 * @author hliu482
 * 
 */
public class OverallReportServlet extends BaseReportServlet {

    private static final long serialVersionUID = 1L;

    private TableRow newTableRow(String prj_id, String sub_id, List<String> date_list, int intAct) throws Exception {
        int s = 0;
        for (String d : date_list) {
            s += ReportManager.getDurationOfActForDay(prj_id, sub_id, d, intAct);
        }
        TableRow tr = new TableRow();
        tr.addCell(ActivityType.stringValue(intAct));
        tr.addCell(TimeUtil.secondsToMinutes(s));
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

        logger.info("TrendReportServlet.generateDataTable: " + prj_id + "/" + sub_id + "/" + date);

        List<String> list_of_day = TimeUtil.getListOfPreviousDays(date, 7);

        DataTable dataTable = new DataTable();
        List<ColumnDescription> cds = new LinkedList<ColumnDescription>();
        cds.add(new ColumnDescription("ACTIVITY", ValueType.TEXT, "ACTIVITY"));
        cds.add(new ColumnDescription("MINUTES", ValueType.NUMBER, "MINUTES"));

        dataTable.addColumns(cds);

        List<TableRow> rows = new LinkedList<TableRow>();

        try {
            rows.add(newTableRow(prj_id, sub_id, list_of_day, ActivityType.WALKING));
            rows.add(newTableRow(prj_id, sub_id, list_of_day, ActivityType.SHUFFLING));
            rows.add(newTableRow(prj_id, sub_id, list_of_day, ActivityType.STANDING));
            rows.add(newTableRow(prj_id, sub_id, list_of_day, ActivityType.SITTING));
            rows.add(newTableRow(prj_id, sub_id, list_of_day, ActivityType.LYING));
            rows.add(newTableRow(prj_id, sub_id, list_of_day, ActivityType.NONWEAR));
            rows.add(newTableRow(prj_id, sub_id, list_of_day, ActivityType.INVERTED));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DataSourceException(ReasonType.OTHER, e.getMessage());
        }

        dataTable.addRows(rows);
        return dataTable;
    }
}

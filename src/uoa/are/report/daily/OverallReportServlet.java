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

    private TableRow newTableRow(String prj_id, String sub_id, String date, int intAct) throws Exception {
        TableRow tr = new TableRow();
        tr.addCell(ActivityType.stringValue(intAct));
        tr.addCell(TimeUtil.secondsToMinutes(ReportManager.getDurationOfActForDay(prj_id, sub_id, date, intAct)));
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

        logger.info("OverallReportServlet.generateDataTable: " + prj_id + "/" + sub_id + "/" + date);

        DataTable dataTable = new DataTable();
        List<ColumnDescription> cds = new LinkedList<ColumnDescription>();
        cds.add(new ColumnDescription("ACTIVITY", ValueType.TEXT, "ACTIVITY"));
        cds.add(new ColumnDescription("MINUTES", ValueType.NUMBER, "MINUTES"));

        dataTable.addColumns(cds);

        List<TableRow> rows = new LinkedList<TableRow>();

        try {
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.WALKING));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.SHUFFLING));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.STANDING));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.SITTING));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.LYING));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.NONWEAR));
            rows.add(newTableRow(prj_id, sub_id, date, ActivityType.INVERTED));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DataSourceException(ReasonType.OTHER, e.getMessage());
        }

        dataTable.addRows(rows);
        return dataTable;
    }
}

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
import uoa.are.common.PhaseType;
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
 * Servlet to return per-activity report data to client.
 * 
 * @author hliu482
 * 
 */
public class PerActReportServlet extends BaseReportServlet {

    private static final long serialVersionUID = 1L;

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
        String act_type = request.getParameter(Env.ACT_TYPE_PARAM_NAME);
        String type = request.getParameter(Env.TYPE_PARAM_NAME);

        logger.info("PerActReportServlet.generateDataTable: " + prj_id + "/" + sub_id + "/" + date + "/" + act_type);

        int iType;
        if ("lie".equalsIgnoreCase(act_type))
            iType = ActivityType.LYING;
        else if ("sit".equalsIgnoreCase(act_type))
            iType = ActivityType.SITTING;
        else if ("std".equalsIgnoreCase(act_type))
            iType = ActivityType.STANDING;
        else if ("wlk".equalsIgnoreCase(act_type))
            iType = ActivityType.WALKING;
        else if ("shf".equalsIgnoreCase(act_type))
            iType = ActivityType.SHUFFLING;
        else if ("inv".equalsIgnoreCase(act_type))
            iType = ActivityType.INVERTED;
        else if ("nwr".equalsIgnoreCase(act_type))
            iType = ActivityType.NONWEAR;
        else
            return null;

        DataTable dataTable = new DataTable();
        List<ColumnDescription> cds = new LinkedList<ColumnDescription>();
        cds.add(new ColumnDescription("PHASE", ValueType.TEXT, "PHASE"));
        cds.add(new ColumnDescription("MINUTES", ValueType.NUMBER, "MINUTES"));
        dataTable.addColumns(cds);

        // List<TableRow> rows = getPhaseData(prj_id, sub_id, date, iType);
        List<TableRow> rows;
        if (type.equals("hour"))
            rows = getHourData(prj_id, sub_id, date, iType);
        else
            rows = getPhaseData(prj_id, sub_id, date, iType);

        dataTable.addRows(rows);

        return dataTable;
    }

    private TableRow newTableRow(String prj_id, String sub_id, String date, int intAct, String phase, int intPhase)
            throws Exception {
        TableRow tr = new TableRow();
        tr.addCell(phase);
        int s = ReportManager.getDurationOfActForPhase(prj_id, sub_id, date, intAct, intPhase);
        tr.addCell(TimeUtil.secondsToMinutes(s));
        return tr;
    }

    private List<TableRow> getPhaseData(String prj_id, String sub_id, String date, int iType)
            throws DataSourceException {
        List<TableRow> rows = new LinkedList<TableRow>();

        try {
            rows.add(newTableRow(prj_id, sub_id, date, iType, "Morning", PhaseType.MORNING));
            rows.add(newTableRow(prj_id, sub_id, date, iType, "Afternoon", PhaseType.AFTERNOON));
            rows.add(newTableRow(prj_id, sub_id, date, iType, "Evening", PhaseType.EVENING));
            rows.add(newTableRow(prj_id, sub_id, date, iType, "Night", PhaseType.NIGHT));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DataSourceException(ReasonType.OTHER, e.getMessage());
        }
        return rows;
    }

    private List<TableRow> getHourData(String prj_id, String sub_id, String date, int iType) throws DataSourceException {
        List<TableRow> rows = new LinkedList<TableRow>();

        try {
            for (int i = 0; i < 24; ++i) {
                TableRow tr = new TableRow();
                String strHour = ((i < 10) ? "0" : "") + i;

                tr.addCell(strHour);
                int s = ReportManager.getDurationOfActForHour(prj_id, sub_id, date, iType, i);
                tr.addCell(TimeUtil.secondsToMinutes(s));
                rows.add(tr);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DataSourceException(ReasonType.OTHER, e.getMessage());
        }
        return rows;
    }
}

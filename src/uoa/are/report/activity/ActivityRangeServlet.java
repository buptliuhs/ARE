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

package uoa.are.report.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import uoa.are.common.Env;
import uoa.are.dm.RecElement;
import uoa.are.dm.ReportManager;
import uoa.are.report.BaseReportServlet;

import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

/**
 * Servlet to return activity range data to client.
 * 
 * @author hliu482
 * 
 */
public class ActivityRangeServlet extends BaseReportServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Generates the data table. This servlet assumes a special parameter that
     * contains the CSV URL from which to load the data.
     */
    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest request) throws DataSourceException {
        if (!this.isAuthorized(request))
            return null;

        // int user_id = (Integer)
        // request.getSession().getAttribute(AuthorizedAction.USER_ID);
        String prj_id = request.getParameter(Env.PROJECT_PARAM_NAME);
        String sub_id = request.getParameter(Env.SUBJECT_PARAM_NAME);
        String date = request.getParameter(Env.DATE_PARAM_NAME);
        boolean includeExtraFields = "1".equals(request.getParameter(Env.TYPE_PARAM_NAME));

        logger.info("ActivityRangeServlet.generateDataTable: " + prj_id + "/" + sub_id + "/" + date + "/"
                + includeExtraFields);

        DataTable dataTable = new DataTable();
        List<ColumnDescription> cds = new LinkedList<ColumnDescription>();
        cds.add(new ColumnDescription("TIME", ValueType.NUMBER, "TIME"));
        cds.add(new ColumnDescription("SIGNAL", ValueType.NUMBER, "SIGNAL"));
        // Below 3 columns are algorithm specific
        if (includeExtraFields) {
            cds.add(new ColumnDescription("SMA", ValueType.NUMBER, "SMA"));
            // cds.add(new ColumnDescription("SMA-H", ValueType.NUMBER,
            // "SMA-H"));
            // cds.add(new ColumnDescription("SMA-L", ValueType.NUMBER,
            // "SMA-L"));
        }

        dataTable.addColumns(cds);

        List<TableRow> rows = new ArrayList<TableRow>();
        try {
            RecElement[] status = ReportManager.getActForDay(prj_id, sub_id, date);
            logger.info("Set act data ...");
            // double sma_h =
            // Double.parseDouble(SettingManager.getValue(user_id, "sma_h"));
            // double sma_l =
            // Double.parseDouble(SettingManager.getValue(user_id, "sma_l"));
            for (int i = 0; i < status.length; ++i) {
                TableRow tr = new TableRow();
                tr.addCell(i);
                tr.addCell(status[i].num_filt);
                // Below 3 columns are algorithm specific
                if (includeExtraFields) {
                    tr.addCell(status[i].sma);
                    // tr.addCell(sma_h);
                    // tr.addCell(sma_l);
                }
                rows.add(tr);
            }
            logger.info("Set act data DONE");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DataSourceException(ReasonType.OTHER, e.getMessage());
        }

        logger.info("Return data (rows): " + rows.size() + " x " + rows.get(0).getCells().size());
        dataTable.addRows(rows);
        return dataTable;
    }

}

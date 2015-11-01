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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import uoa.are.common.Env;
import uoa.are.dm.ActData;
import uoa.are.dm.ReportManager;
import uoa.are.dm.SignalData;
import uoa.are.dm.SignalFileManager;
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
 * Servlet to return activity raw signal data to client.
 * 
 * @author hliu482
 * 
 */
public class ActivitySignalServlet extends BaseReportServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Generates the data table. This servlet assumes a special parameter that
     * contains the CSV URL from which to load the data.
     */
    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest request)
            throws DataSourceException {
        if (!this.isAuthorized(request))
            return null;

        String prj_id = request.getParameter(Env.PROJECT_PARAM_NAME);
        String sub_id = request.getParameter(Env.SUBJECT_PARAM_NAME);
        String date = request.getParameter(Env.DATE_PARAM_NAME);
        String time = request.getParameter(Env.TIME_PARAM_NAME);
        boolean includeExtraFields = "1".equals(request
                .getParameter(Env.TYPE_PARAM_NAME));

        logger.info("ActivitySignalServlet.generateDataTable: " + prj_id + "/"
                + sub_id + "/" + date + "/" + time + "/" + includeExtraFields);

        DataTable dataTable = new DataTable();
        List<ColumnDescription> cds = new LinkedList<ColumnDescription>();
        cds.add(new ColumnDescription("TIME", ValueType.NUMBER, "TIME"));
        cds.add(new ColumnDescription("Ax", ValueType.NUMBER, "Ax"));
        cds.add(new ColumnDescription("Ay", ValueType.NUMBER, "Ay"));
        cds.add(new ColumnDescription("Az", ValueType.NUMBER, "Az"));
        // Below 1 column1 is algorithm specific
        if (includeExtraFields) {
            cds.add(new ColumnDescription("Angle", ValueType.NUMBER, "Angle"));
        }

        dataTable.addColumns(cds);

        List<TableRow> rows = new LinkedList<TableRow>();
        try {
            ActData ad = ReportManager.getAct(prj_id, sub_id, date,
                    Integer.parseInt(time));

            SignalData sd = SignalFileManager.getSignal(prj_id, sub_id, date,
                    ad.getStart_second(), ad.getEnd_second());
            if (sd != null) {
                logger.debug("Get # of signal: " + sd.getX().length);
                for (int i = 0; i < sd.getX().length; ++i) {
                    TableRow tr = new TableRow();
                    tr.addCell(ad.getStart_second() * 100 + i);
                    tr.addCell(sd.getX()[i]);
                    tr.addCell(sd.getY()[i]);
                    tr.addCell(sd.getZ()[i]);
                    if (includeExtraFields) {
                        tr.addCell(ad.getAct()[i / 100].angle);
                    }
                    rows.add(tr);
                }
            } else {
                logger.error("No signal found, generate blank signal for range: ["
                        + TimeUtil.formatSecond2(ad.getStart_second())
                        + ", "
                        + TimeUtil.formatSecond2(ad.getEnd_second()) + "]");
                for (int i = 0; i < ad.getAct().length; ++i) {
                    // expend to 100Hz data to sync with raw signal
                    for (int j = 0; j < 100; ++j) {
                        TableRow tr = new TableRow();
                        tr.addCell((ad.getStart_second() + i) * 100 + j);
                        tr.addCell(0);
                        tr.addCell(1);
                        tr.addCell(0);
                        if (includeExtraFields) {
                            tr.addCell(0);
                        }
                        rows.add(tr);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DataSourceException(ReasonType.OTHER, e.getMessage());
        }

        logger.info("Return data (rows): " + rows.size() + " x "
                + rows.get(0).getCells().size());
        dataTable.addRows(rows);
        return dataTable;
    }
}

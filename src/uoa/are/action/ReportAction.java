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

package uoa.are.action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import uoa.are.dm.DailyReport;
import uoa.are.dm.ReportManager;
import uoa.are.util.JSONUtil;

/**
 * This is action for report generation.
 * 
 * @author hliu482
 *
 */
@SuppressWarnings("serial")
public class ReportAction extends AuthorizedAction {

    private String project_id;
    private String subject_id;

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id.trim();
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id.trim();
    }

    /**
     * Initialize report list.
     * 
     * @throws Exception
     */
    public void initReportList() throws Exception {
        if (!isAuthorized())
            return;
        logger.info("initReportList (" + this.getSessionUserID() + ", "
                + project_id + ", " + subject_id + ")");

        Map<String, Map<String, Object>> data = new LinkedHashMap<String, Map<String, Object>>();

        List<DailyReport> report_list = ReportManager.getDailyReportList(
                project_id, subject_id);

        for (DailyReport dr : report_list) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            String id = project_id + "-" + subject_id + "-" + dr.getDate();
            map.put("id", id);
            map.put("text", dr.getText());
            map.put("report_date", dr.getDate());
            map.put("duration", dr.getDuration());
            data.put(id, map);
        }
        String response = JSONUtil.newInstance(data).toString();
        logger.debug(response);
        ServletActionContext.getResponse().getWriter().write(response);
    }
}

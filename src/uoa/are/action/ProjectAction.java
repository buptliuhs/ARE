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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uoa.are.common.Code;
import uoa.are.common.Message;
import uoa.are.common.ResponseUtil;
import uoa.are.dm.Project;
import uoa.are.dm.ProjectManager;

/**
 * This is action for project management.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class ProjectAction extends AuthorizedAction {

    /**
     * Initialize project list.
     * 
     * @throws IOException
     */
    public void initProjectList() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("initProjectList (" + getSessionUserID() + ")");

        List<Project> list = ProjectManager.getAllProjects(getSessionUserID());
        Map<Object, Object> data = new LinkedHashMap<Object, Object>();
        for (Project p : list) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            data.put(p.getId(), map);
        }
        ResponseUtil.setResponseMap(data);
    }

    private String id;
    private String name;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    /**
     * Add a new project.
     * 
     * @throws IOException
     */
    public void addProject() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("addProject (" + getSessionUserID() + ", " + name + ", " + description + ")");

        int ret = ProjectManager.addProject(getSessionUserID(), name, description);
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        if (ret == Code.SUCCESSFUL) {
            result.put("result", Code.SUCCESSFUL);
            result.put("message", Message.SUCCESSFUL);
        } else {
            result.put("result", Code.FAILED);
            result.put("message", Message.FAILED);
        }
        ResponseUtil.setResponseMap(result);
    }

    /**
     * Edit existing project.
     * 
     * @throws IOException
     */
    public void editProject() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("editProject (" + getSessionUserID() + ", " + id + ", " + name + ", " + description + ")");
        int ret = ProjectManager.editProject(getSessionUserID(), id, name, description);
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        if (ret == Code.SUCCESSFUL) {
            result.put("result", Code.SUCCESSFUL);
            result.put("message", Message.SUCCESSFUL);
        } else {
            result.put("result", Code.FAILED);
            result.put("message", Message.FAILED);
        }
        ResponseUtil.setResponseMap(result);
    }

    /**
     * Delete a project.
     * 
     * @throws IOException
     */
    public void deleteProject() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("deleteProject (" + getSessionUserID() + ", " + id + ")");
        int ret = ProjectManager.deleteProject(getSessionUserID(), id);
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        if (ret == Code.SUCCESSFUL) {
            result.put("result", Code.SUCCESSFUL);
            result.put("message", Message.SUCCESSFUL);
        } else {
            result.put("result", Code.FAILED);
            result.put("message", Message.FAILED);
        }
        ResponseUtil.setResponseMap(result);
    }

}

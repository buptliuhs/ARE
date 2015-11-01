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
import uoa.are.dm.ReportManager;
import uoa.are.dm.Subject;
import uoa.are.dm.SubjectManager;
import uoa.are.util.TimeUtil;

/**
 * This is action for subject management.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class SubjectAction extends AuthorizedAction {

    /**
     * Initialize subject list.
     * 
     * @throws IOException
     */
    public void initSubjectList() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("initSubjectList (" + getSessionUserID() + ", " + project_id + ")");
        List<Subject> list = SubjectManager.getAllSubjects(getSessionUserID(), project_id);
        Map<Integer, Map<Object, Object>> data = new LinkedHashMap<Integer, Map<Object, Object>>();
        for (Subject sub : list) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("id", sub.getId());
            map.put("name", sub.getName());
            map.put("project_name", sub.getProject_name());
            map.put("dob", sub.getDob());
            map.put("height", sub.getHeight());
            map.put("weight", sub.getWeight());
            String g;
            switch (sub.getGender()) {
            case 0:
                g = "Male";
                break;
            case 1:
                g = "Female";
                break;
            case 2:
                g = "Other";
                break;
            default:
                g = "Other";
            }
            map.put("gender", g);
            map.put("device_id", sub.getDevice_id());
            map.put("device_name", sub.getDevice_name());
            map.put("duration", TimeUtil.formatSecond(ReportManager.getDuration(project_id, "" + sub.getId())));
            data.put(sub.getId(), map);
        }
        ResponseUtil.setResponse2DMap(data);
    }

    private String id;
    private String project_id;
    private String name;
    private String dob;
    private String height;
    private String weight;
    private String gender;
    private String device_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob.trim();
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height.trim();
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender.trim();
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id.trim();
    }

    /**
     * Add a new subject.
     * 
     * @throws IOException
     */
    public void addSubject() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("addSubject (" + getSessionUserID() + ", " + project_id + ", " + name + ", " + dob + ", " + height
                + ", " + weight + ", " + gender + ", " + device_id + ")");
        int ret = SubjectManager.addSubject(project_id, name, dob, height, weight, gender, device_id);
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
     * Edit existing subject.
     * 
     * @throws IOException
     */
    public void editSubject() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("editSubject (" + getSessionUserID() + ", " + id + ", " + name + ", " + dob + ", " + height + ", "
                + weight + ", " + gender + ", " + device_id + ")");
        int ret = SubjectManager.editSubject(id, name, dob, height, weight, gender, device_id);
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
     * Delete a subject.
     * 
     * @throws IOException
     */
    public void deleteSubject() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("deleteSubject (" + getSessionUserID() + ", " + id + ")");
        int ret = SubjectManager.deleteSubject(id);
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

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
import uoa.are.dm.Setting;
import uoa.are.dm.SettingManager;

/**
 * This is action for system setting.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class SettingAction extends AuthorizedAction {

    private String id;
    private String name;
    private String description;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.trim();
    }

    /**
     * Initialize setting list.
     * 
     * @throws IOException
     */
    public void initSettingList() throws IOException {
        if (!isAuthorized())
            return;
        int user_id = getSessionUserID();
        logger.info("initSettingList (" + user_id + ")");
        List<Setting> list = SettingManager.getSettings(user_id);

        Map<Object, Object> data = new LinkedHashMap<Object, Object>();
        for (int i = 0; i < list.size(); ++i) {
            Setting s = list.get(i);
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("id", s.getId());
            map.put("name", s.getName().toUpperCase());
            map.put("description", s.getDescription());
            map.put("value", s.getValue());
            data.put(s.getId(), map);
        }
        ResponseUtil.setResponseMap(data);
    }

    /**
     * Edit existing setting.
     * 
     * @throws IOException
     */
    public void editSetting() throws IOException {
        if (!isAuthorized())
            return;

        int user_id = getSessionUserID();
        logger.info("editSetting (" + user_id + ", " + id + ", " + name + ", " + description + ", " + value + ")");
        Setting s = new Setting();
        s.setId(Integer.parseInt(id));
        s.setName(name);
        s.setValue(value);
        s.setDescription(description);

        int ret = SettingManager.editSetting(user_id, s);
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

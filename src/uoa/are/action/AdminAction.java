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
import java.util.regex.Pattern;

import uoa.are.common.Code;
import uoa.are.common.Message;
import uoa.are.common.ResponseUtil;
import uoa.are.dm.User;
import uoa.are.dm.UserManager;

/**
 * This is action for administration
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class AdminAction extends AuthorizedAction {

    /**
     * Initialize user list.
     * 
     * @throws IOException
     */
    public void initUserList() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("initUserList (" + getSessionUserID() + ")");
        List<User> users = UserManager.getAllUsers();
        Map<Object, Object> data = new LinkedHashMap<Object, Object>();
        for (User u : users) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            if (!isAdmin() && u.getId() != getSessionUserID())
                continue;
            map.put("id", u.getId());
            map.put("username", u.getName());
            map.put("role", u.getRoleName());
            map.put("enabled", u.getEnabled());
            data.put(u.getId(), map);
        }
        ResponseUtil.setResponseMap(data);
    }

    private String username;
    private String password;
    private int enabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    private boolean isMatch(String data, String pattern) {
        return Pattern.matches(pattern, data);
    }

    private boolean isValidData() throws IOException {
        if (!isMatch(username, "[a-zA-Z][a-zA-Z0-9_]*")) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("result", Code.FAILED);
            map.put("message", Message.RULE_NOT_MATCHED);
            ResponseUtil.setResponseMap(map);
            return false;
        }
        password = password.replace("\\", "\\\\");
        return true;
    }

    /**
     * Add new user.
     * 
     * @throws IOException
     */
    public void addUser() throws IOException {
        if (!isAuthorized() || !isValidData())
            return;

        if (!isAdmin()) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("result", Code.FAILED);
            map.put("message", Message.ADMIN_ONLY);
            ResponseUtil.setResponseMap(map);
            return;
        }

        logger.info("addUser (" + getSessionUserID() + ", " + username + ")");
        User user = UserManager.addUser(username, password);
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        if (user != null) {
            result.put("result", Code.SUCCESSFUL);
            result.put("message", Message.SUCCESSFUL);
        } else {
            result.put("result", Code.FAILED);
            result.put("message", Message.FAILED);
        }
        ResponseUtil.setResponseMap(result);
    }

    /**
     * Edit existing user.
     * 
     * @throws IOException
     */
    public void editUser() throws IOException {
        if (!isAuthorized() || !isValidData())
            return;

        logger.info("editUser (" + getSessionUserID() + ", " + username + ")");

        if (!isAdmin() && !getSessionUserName().equals(username)) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("result", Code.FAILED);
            map.put("message", Message.ADMIN_ONLY);
            ResponseUtil.setResponseMap(map);
            return;
        }

        int ret = UserManager.editUser(username, password);
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
     * Disable existing user.
     * 
     * @throws IOException
     */
    public void disableUser() throws IOException {
        if (!isAuthorized())
            return;

        logger.info("disableUser (" + getSessionUserID() + ", " + username + ")");

        if (!isAdmin()) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("result", Code.FAILED);
            map.put("message", Message.ADMIN_ONLY);
            ResponseUtil.setResponseMap(map);
            return;
        }

        int ret = UserManager.disableUser(username);
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
     * Enable existing user.
     * 
     * @throws IOException
     */
    public void enableUser() throws IOException {
        if (!isAuthorized())
            return;

        logger.info("enableUser (" + getSessionUserID() + ", " + username + ")");

        if (!isAdmin()) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("result", Code.FAILED);
            map.put("message", Message.ADMIN_ONLY);
            ResponseUtil.setResponseMap(map);
            return;
        }

        int ret = UserManager.enableUser(username);
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

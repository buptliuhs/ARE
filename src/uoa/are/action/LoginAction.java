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
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import uoa.are.common.Code;
import uoa.are.common.Message;
import uoa.are.common.ResponseUtil;
import uoa.are.database.SC;
import uoa.are.dm.User;
import uoa.are.dm.UserManager;

/**
 * This is action for login.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class LoginAction extends AuthorizedAction {
    private String username;
    private String password;
    private String id;

    private static int SESSION_TIMEOUT = -1; // NEVER timeout!

    /**
     * Login user.
     * 
     * @throws IOException
     */
    public void login() throws IOException {
        logger.info("login ... (" + username + ") from " + ServletActionContext.getRequest().getRemoteAddr());
        username = username.toLowerCase();
        User user = UserManager.getUserByName(username);

        if (user == null) {
            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            result.put("result", Code.FAILED);
            result.put("message", Message.WRONG_USERNAME_PASSWORD);
            ResponseUtil.setResponseMap(result);
            return;
        }

        int id = user.getId();
        int role = user.getRole();
        String pw = user.getPassword();
        int enabled = user.getEnabled();
        if (!pw.equals(password)) {
            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            result.put("result", Code.FAILED);
            result.put("message", Message.WRONG_USERNAME_PASSWORD);
            ResponseUtil.setResponseMap(result);
            return;
        }
        if (enabled != 1) {
            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            result.put("result", Code.FAILED);
            result.put("message", Message.USER_DISABLED);
            ResponseUtil.setResponseMap(result);
            return;
        }
        loginSession(username, id, role);

        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        result.put("result", Code.SUCCESSFUL);
        result.put("message", Message.USER_LOGIN);
        result.put("id", id);

        ResponseUtil.setResponseMap(result);
        ServletActionContext.getRequest().getSession().setMaxInactiveInterval(SESSION_TIMEOUT);
    }

    /**
     * Logout user.
     * 
     * @throws IOException
     */
    public void logout() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("logout ... (" + this.getSessionUserName() + ")");
        SC sc = null;
        try {
            logoutSession();

            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            result.put("result", Code.SUCCESSFUL);
            result.put("message", Message.USER_LOGOUT);

            ResponseUtil.setResponseMap(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            result.put("result", Code.FAILED);
            result.put("message", e.getMessage());

            ResponseUtil.setResponseMap(result);
        } finally {
            if (sc != null)
                sc.closeAll();
        }
    }

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

    public String getUserid() {
        return id;
    }

    public void setUserid(String userid) {
        this.id = userid.trim();
    }

}

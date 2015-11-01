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

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import uoa.are.common.Code;
import uoa.are.common.Message;
import uoa.are.common.ResponseUtil;
import uoa.are.common.UserRole;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This is a base action which can be extended from by any others that want do
 * do authorization when processing requests from client.
 * 
 * @author hliu482
 *
 */
@SuppressWarnings("serial")
public class AuthorizedAction extends ActionSupport {

    protected Logger logger = Logger.getLogger(this.getClass());

    public static String USER_NAME = "username";
    public static String USER_ID = "userid";
    public static String ROLE_ID = "roleid";

    protected String getSessionUserName() {
        return (String) ServletActionContext.getRequest().getSession()
                .getAttribute(USER_NAME);
    }

    protected int getSessionUserID() {
        return (Integer) ServletActionContext.getRequest().getSession()
                .getAttribute(USER_ID);
    }

    protected int getSessionRoleID() {
        return (Integer) ServletActionContext.getRequest().getSession()
                .getAttribute(ROLE_ID);
    }

    /**
     * Set login information to http session.
     * 
     * @param userName
     * @param userID
     * @param roleID
     */
    protected void loginSession(String userName, int userID, int roleID) {
        HttpSession session = ServletActionContext.getRequest().getSession();
        session.setAttribute(USER_NAME, userName);
        session.setAttribute(USER_ID, userID);
        session.setAttribute(ROLE_ID, roleID);
    }

    /**
     * Remove login information from http session.
     */
    protected void logoutSession() {
        HttpSession session = ServletActionContext.getRequest().getSession();
        session.removeAttribute(USER_NAME);
        session.removeAttribute(USER_ID);
        session.removeAttribute(ROLE_ID);
    }

    /**
     * Login check.
     * 
     * @return
     * @throws IOException
     */
    protected boolean isAuthorized() throws IOException {
        if (getSessionUserName() == null) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("result", Code.FAILED);
            map.put("message", Message.USER_NOT_LOGIN);
            ResponseUtil.setResponseMap(map);
            return false;
        }
        return true;
    }

    /**
     * Administrator check.
     * 
     * @return
     */
    protected boolean isAdmin() {
        return (getSessionRoleID() == UserRole.ADMIN);
    }

}

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

package uoa.are.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import uoa.are.common.Code;
import uoa.are.common.Message;
import uoa.are.common.ResponseUtil;
import uoa.are.common.UserRole;

/**
 * This is a base servlet which can be extended from by any others that want do
 * do authorization when processing servlet requests from client.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class AuthorizedServlet extends HttpServlet {

    protected Logger logger = Logger.getLogger(this.getClass());

    public static String USER_NAME = "username";
    public static String USER_ID = "userid";
    public static String ROLE_ID = "roleid";

    protected String getSessionUserName(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(USER_NAME);
    }

    protected int getSessionUserID(HttpServletRequest request) {
        return (Integer) request.getSession().getAttribute(USER_ID);
    }

    protected int getSessionRoleID(HttpServletRequest request) {
        return (Integer) request.getSession().getAttribute(ROLE_ID);
    }

    protected boolean isAuthorized(HttpServletRequest request)
            throws IOException {
        if (getSessionUserName(request) == null) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("result", Code.FAILED);
            map.put("message", Message.USER_NOT_LOGIN);
            ResponseUtil.setResponseMap(map);
            return false;
        }
        return true;
    }

    protected boolean isAdmin(HttpServletRequest request) {
        return (getSessionRoleID(request) == UserRole.ADMIN);
    }

}

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

package uoa.are.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.visualization.datasource.DataSourceServlet;

/**
 * Base / Abstract class for DataSourceServlet with authorization check feature
 * enabled.
 * 
 * @author hliu482
 *
 */
public abstract class BaseReportServlet extends DataSourceServlet {

    private static final long serialVersionUID = 1L;

    protected Logger logger = Logger.getLogger(this.getClass());

    /**
     * Check whether user has logged in or not.
     * 
     * @param request
     * @return
     */
    protected boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            return false;
        }
        return true;
    }

    /**
     * NOTE: By default, this function returns true, which means that cross
     * domain requests are rejected. This check is disabled here so examples can
     * be used directly from the address bar of the browser. Bear in mind that
     * this exposes your data source to xsrf attacks. If the only use of the
     * data source url is from your application, that runs on the same domain,
     * it is better to remain in restricted mode.
     */
    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }

}

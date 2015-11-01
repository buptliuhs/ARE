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

package uoa.are.common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * Tomcat Listener
 * 
 * @author hliu482
 *
 */
public class ConfigurationListener implements ServletContextListener {
    protected Logger logger = Logger.getLogger(this.getClass());

    public ConfigurationListener() {
        super();
        logger.info("ConfigurationListener::ConfigurationListener");
    }

    public void contextInitialized(ServletContextEvent context) {
        String confFilePath = context.getServletContext().getRealPath(
                "/WEB-INF/conf/app.properties");
        Configure.getInstance().setConfFileName(confFilePath);
        logger.info("ConfigurationListener::contextInitialized");
    }

    public void contextDestroyed(ServletContextEvent context) {
        logger.info("ConfigurationListener::contextDestroyed");
    }

}

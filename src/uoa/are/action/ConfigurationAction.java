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

import uoa.are.common.Configure;
import uoa.are.common.ResponseUtil;

/**
 * This is action for system configuration.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class ConfigurationAction extends AuthorizedAction {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * Initialize setting list.
     * 
     * @throws IOException
     */
    public void get() throws IOException {
        if (!isAuthorized())
            return;
        int user_id = getSessionUserID();
        logger.info("ConfigurationAction.get (" + user_id + ")");
        String value = Configure.getInstance().getProperty(name);

        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put("name", name);
        map.put("value", value);
        ResponseUtil.setResponseMap(map);
    }
}

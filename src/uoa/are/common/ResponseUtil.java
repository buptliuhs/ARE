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

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import uoa.are.util.JSONUtil;

/**
 * Utility used to construct response message.
 * 
 * @author hliu482
 * 
 */
public class ResponseUtil {

    protected static Logger logger = Logger.getLogger(ResponseUtil.class);

    public static void setResponseMap(Map<Object, Object> result) throws IOException {
        logger.debug("Response: " + result.toString());

        ServletActionContext.getResponse().getWriter().write(JSONUtil.newInstance(result).toString());
    }

    public static void setResponse2DMap(Map<Integer, Map<Object, Object>> data) throws IOException {
        String response = JSONUtil.newInstance(data).toString();
        logger.debug(response);
        ServletActionContext.getResponse().getWriter().write(response);
    }
}

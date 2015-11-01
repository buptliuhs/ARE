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

import java.util.Map;

import net.sf.json.JSONObject;

/**
 * Utility class to build json string.
 * 
 * @author hliu482
 *
 */
public class JSONUtil {

    public static JSONObject newInstance(Object key, Object value) {
        JSONObject result = new JSONObject();
        result.put(key, value);
        return result;
    }

    public static JSONObject newInstance(
            Map<? extends Object, ? extends Object> map) {
        JSONObject result = new JSONObject();
        for (Object key : map.keySet()) {
            result.put(key, map.get(key));
        }
        return result;
    }
}
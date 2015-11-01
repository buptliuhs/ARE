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

import uoa.are.common.ResponseUtil;
import uoa.are.util.TaskManager;

/**
 * This is action for progress checking.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class ProgressAction extends AuthorizedAction {

    /**
     * Check current running tasks. Tasks include 1) converting tasks; 2)
     * analysing tasks.
     * 
     * @throws IOException
     */
    public void checkProgress() throws IOException {
        if (!isAuthorized())
            return;

        String user_id = "" + getSessionUserID();

        Map<Object, Object> data = new LinkedHashMap<Object, Object>();
        data.put("id", user_id);
        Map<String, Integer> tasks = TaskManager.getInstance()
                .getRunningTaskSummary(user_id);
        int count = 0;
        for (String c : tasks.keySet()) {
            int sub_count = tasks.get(c);
            data.put(c, sub_count);
            count += sub_count;
        }
        data.put("count", count);
        ResponseUtil.setResponseMap(data);
    }
}

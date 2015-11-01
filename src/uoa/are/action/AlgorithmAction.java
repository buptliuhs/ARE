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

import uoa.are.common.ResponseUtil;
import uoa.are.dm.Algorithm;
import uoa.are.dm.AlgorithmManager;

/**
 * This is action for algorithm.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class AlgorithmAction extends AuthorizedAction {

    /**
     * Initialize algorithm list.
     * 
     * @throws IOException
     */
    public void initAlgorithmList() throws IOException {
        if (!isAuthorized())
            return;
        int user_id = getSessionUserID();
        logger.info("initAlgorithmList (" + user_id + ")");
        List<Algorithm> list = AlgorithmManager.getAlgorithms();

        Map<Object, Object> data = new LinkedHashMap<Object, Object>();
        for (int i = 0; i < list.size(); ++i) {
            Algorithm s = list.get(i);
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("name", s.getName());
            map.put("class_name", s.getClass_name());
            data.put(s.getName(), map);
        }
        ResponseUtil.setResponseMap(data);
    }
}

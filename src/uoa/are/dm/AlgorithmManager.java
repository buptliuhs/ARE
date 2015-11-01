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

package uoa.are.dm;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import uoa.are.common.Configure;

/**
 * Utility class to manage algorithm
 * 
 * @author hliu482
 * 
 */
public class AlgorithmManager {
    static Logger logger = Logger.getLogger(AlgorithmManager.class);

    // ALGORITHM_NUMBER=2
    // ALGORITHM_NAME_1=SMA (More walking) - DEFAULT
    // ALGORITHM_CLASS_1=uoa.are.algorithm.AR_HS1_Process
    // ALGORITHM_NAME_2=SMA & Posture (Ignore lying movement)
    // ALGORITHM_CLASS_2=uoa.are.algorithm.AR_HS2_Process

    static public List<Algorithm> getAlgorithms() {
        logger.info("getAlgorithms...");

        int ALGORITHM_NUMBER = Integer.parseInt(Configure.getInstance().getProperty("ALGORITHM_NUMBER"));

        List<Algorithm> ret = new LinkedList<Algorithm>();
        for (int i = 1; i <= ALGORITHM_NUMBER; ++i) {
            String name = Configure.getInstance().getProperty("ALGORITHM_NAME_" + i);
            String className = Configure.getInstance().getProperty("ALGORITHM_CLASS_" + i);
            Algorithm a = new Algorithm();
            a.setClass_name(className);
            a.setName(name);
            ret.add(a);
        }
        return ret;
    }

    static public Algorithm getAlgorithmByClassName(String c) {
        logger.info("getAlgorithmByClassName (" + c + ")");
        List<Algorithm> list = getAlgorithms();
        for (Algorithm a : list) {
            if (a.getClass_name().equals(c))
                return a;
        }
        return null;
    }
}
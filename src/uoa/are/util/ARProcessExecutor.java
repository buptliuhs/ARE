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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import uoa.are.common.Configure;

/**
 * This process executor is used for activity recognition process. It starts
 * PROCESS_NUMBER of threads do activity analysis.
 * 
 * @author hliu482
 *
 */
public class ARProcessExecutor {

    protected static Logger logger = Logger.getLogger(ARProcessExecutor.class);

    private static final int AR_PROCESS_NUMBER = Integer.parseInt(Configure.getInstance().getProperty(
            "AR_PROCESS_NUMBER"));

    private static ExecutorService _instance = Executors.newFixedThreadPool(AR_PROCESS_NUMBER);

    private ARProcessExecutor() {
    }

    synchronized public static ExecutorService getInstance() {
        return _instance;
    }
}

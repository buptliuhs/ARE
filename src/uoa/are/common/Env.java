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

import java.io.File;

/**
 * Some environment values.
 * 
 * @author hliu482
 * 
 */
public class Env {
    public static final String BASE_PATH = File.separator + "opt" + File.separator + "uoa" + File.separator;
    public static final String SCRIPTS_PATH = BASE_PATH + "scripts" + File.separator;
    public static final String LOG_PATH = BASE_PATH + "log" + File.separator;
    public static final String DATA_PATH = BASE_PATH + "data" + File.separator;
    public static final String TMP_PATH = DATA_PATH + "tmp" + File.separator;

    public static final String SIGNAL_PATH = "signal";

    // public static final String REPORT_PATH = DATA_PATH + "report" +
    // File.separator;
    public static final String RAW_PATH = DATA_PATH + "raw" + File.separator;

    public static final String PROJECT_PARAM_NAME = "prj_id";
    public static final String SUBJECT_PARAM_NAME = "sub_id";
    public static final String DATE_PARAM_NAME = "date";
    public static final String ACT_TYPE_PARAM_NAME = "act_type";
    public static final String TYPE_PARAM_NAME = "type";

    public static final String SIGNAL_PARAM_NAME = "sig_file";
    public static final String TIME_PARAM_NAME = "time";

    public static final String UNIFIED_SIGNAL_FILE_NAME_PATTERN = "(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)\\.csv";
    public static final String UNIFIED_DATETIME_PATTERN = "(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})";

}

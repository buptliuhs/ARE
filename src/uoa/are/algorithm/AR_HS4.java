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

package uoa.are.algorithm;

//import static uoa.are.common.ActivityType.LYING;
import static uoa.are.common.ActivityType.NONWEAR;

import java.util.Map;

import uoa.are.dm.RecElement;
import uoa.are.dm.SettingManager;

public class AR_HS4 extends AR_HS3 {
    protected int nonwear_time_th;
    protected double nonwear_sma_th;

    /**
     * Constructor to specify scilab script.
     * 
     * @param conf
     */
    public AR_HS4(Map<String, Object> conf) {
        super(conf);
        nonwear_time_th = Integer.parseInt(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "nonwear_time_th"));
        nonwear_sma_th = Double.parseDouble(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "nonwear_sma_th"));
    }

    private void correctNonwear(RecElement[] result, int start, int end) {
        if (end - start + 1 >= nonwear_time_th)
            markResult(result, start, end, NONWEAR);
    }

    @Override
    protected void doCorrection() {
        super.doCorrection();
        logger.info("doCorrection: " + activities.length);

        boolean lying_flag = false;
        int lying_start_second = 0;
        int lying_end_second = 0;
        for (int i = 0; i < activities.length; ++i) {
            // NOTE: ignore posture, do correction for all posture
            if (!lying_flag && activities[i].sma < nonwear_sma_th) {
                lying_flag = true;
                lying_start_second = i;
            } else if (lying_flag && activities[i].sma >= nonwear_sma_th) {
                lying_flag = false;
                lying_end_second = i - 1;
                // check lying_end_second & lying_start_second and
                // determine it is (1) lying; (2) nonwear;
                correctNonwear(activities, lying_start_second, lying_end_second);
            }
        }
        // still lying at the end of the signal
        if (lying_flag) {
            lying_end_second = activities.length - 1;
            correctNonwear(activities, lying_start_second, lying_end_second);
        }
    }
}

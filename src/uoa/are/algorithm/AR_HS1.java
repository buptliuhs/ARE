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

import java.util.Map;

import uoa.are.dm.RecElement;
import uoa.are.dm.SettingManager;
import static uoa.are.common.ActivityType.*;

public class AR_HS1 extends AR_Base {
    protected int walking_th;
    protected double sit_stand_th;
    protected double sit_lie_th;
    protected double lie_invert_th;
    protected double sma_h;
    protected double sma_l;

    /**
     * Constructor to specify scilab script.
     * 
     * @param conf
     */
    public AR_HS1(Map<String, Object> conf) {
        super(conf);
        walking_th = Integer.parseInt(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "walking_th"));
        sit_stand_th = Double.parseDouble(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "sit_stand_th"));
        sit_lie_th = Double.parseDouble(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "sit_lie_th"));
        lie_invert_th = Double.parseDouble(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "lie_invert_th"));
        sma_h = Double.parseDouble(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "sma_h"));
        sma_l = Double.parseDouble(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "sma_l"));
    }

    /**
     * Determine static activity's posture.
     * 
     * @param angle
     * @return
     */
    protected int angleToPosture(Double angle) {
        int result = UNDEFINED;
        if (angle <= sit_stand_th)
            result = STANDING;
        else if (angle > sit_stand_th && angle <= sit_lie_th)
            result = SITTING;
        else if (angle > sit_lie_th && angle <= lie_invert_th)
            result = LYING;
        else if (angle > lie_invert_th)
            result = INVERTED;
        return result;
    }

    protected RecElement[] analyseActivity(Double[] SMA_value, Double[] seg_val_ang, Double[] seg_val_ang_vel) {
        logger.info("analyseActivity...");
        RecElement[] result = new RecElement[SMA_value.length];

        int pre_act = STATIC;
        for (int i = 0; i < SMA_value.length; ++i) {
            result[i] = new RecElement();
            result[i].angle = seg_val_ang[i];
            result[i].sma = SMA_value[i];

            // Use two threshold values to determine static and dynamic
            int act = STATIC;
            if (SMA_value[i] < sma_l) // Static activity
                act = STATIC;
            else if ((SMA_value[i] >= sma_l) && (SMA_value[i] < sma_h))
                act = pre_act;
            else if (SMA_value[i] >= sma_h) // Dynamic activity
                act = ACTIVE;
            pre_act = act;

            // Do activity recognition
            result[i].num_filt = result[i].num = (act == STATIC) ? angleToPosture(seg_val_ang[i]) : WALKING;
        }
        logger.info("Done: " + _conf.get(SRC_FILE));
        return result;
    }

    private void correctShuffling(RecElement[] result, int start, int end) {
        if (end - start + 1 < walking_th)
            markResult(result, start, end, SHUFFLING);
    }

    @Override
    protected void doCorrection() {
        logger.info("doCorrection: " + activities.length);
        boolean walking_flag = false;
        int walking_start_second = 0;
        int walking_end_second = 0;
        for (int i = 0; i < activities.length; ++i) {
            if (!walking_flag && activities[i].num == WALKING) {
                // start walking
                walking_flag = true;
                walking_start_second = i;
            } else if (walking_flag && activities[i].num != WALKING) {
                // stop walking
                walking_flag = false;
                walking_end_second = i - 1;
                // check walking_end_second & walking_start_second and
                // determine it is (1) walking; (2) shuffling;
                correctShuffling(activities, walking_start_second, walking_end_second);
            }
        }
        // still walking at the end of the signal
        if (walking_flag) {
            walking_end_second = activities.length - 1;
            correctShuffling(activities, walking_start_second, walking_end_second);
        }
    }
}

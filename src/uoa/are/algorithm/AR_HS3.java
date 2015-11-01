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

public class AR_HS3 extends AR_HS2 {
    protected double offset;

    /**
     * Constructor to specify scilab script.
     * 
     * @param conf
     */
    public AR_HS3(Map<String, Object> conf) {
        super(conf);
        offset = Double.parseDouble(SettingManager.getValue(Integer.parseInt((String) conf.get(USER_ID)), "offset"));
    }

    /**
     * Determine static activity's posture.
     * 
     * @param angle
     * @return
     */
    protected int angleToPosture(int pre_posture, Double angle) {
        int result = UNDEFINED;
        if (angle <= sit_stand_th - offset)
            result = STANDING;
        else if (angle > sit_stand_th - offset && angle <= sit_stand_th + offset) {
            // standing or sitting
            if (pre_posture == UNDEFINED)
                result = (angle <= sit_stand_th) ? STANDING : SITTING;
            else {
                result = (pre_posture <= STANDING) ? STANDING : SITTING;
            }
        } else if (angle > sit_stand_th + offset && angle <= sit_lie_th - offset)
            result = SITTING;
        else if (angle > sit_lie_th - offset && angle <= sit_lie_th + offset) {
            // sitting or lying
            if (pre_posture == UNDEFINED)
                result = (angle <= sit_lie_th) ? SITTING : LYING;
            else {
                result = (pre_posture <= SITTING) ? SITTING : LYING;
            }
        } else if (angle > sit_lie_th + offset && angle <= lie_invert_th - offset)
            result = LYING;
        else if (angle > lie_invert_th - offset && angle <= lie_invert_th + offset) {
            // lying or inverted
            if (pre_posture == UNDEFINED)
                result = (angle <= lie_invert_th) ? LYING : INVERTED;
            else {
                result = (pre_posture <= LYING) ? LYING : INVERTED;
            }
        } else if (angle > lie_invert_th + offset)
            result = INVERTED;
        return result;
    }

    protected RecElement[] analyseActivity(Double[] SMA_value, Double[] seg_val_ang, Double[] seg_val_ang_vel) {
        logger.info("analyseActivity...");
        RecElement[] result = new RecElement[SMA_value.length];

        int pre_act = STATIC;
        int pre_posture = UNDEFINED;
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
            int posture = angleToPosture(pre_posture, result[i].angle);
            if (act == STATIC) {
                result[i].num = posture;
            } else {
                // Moving at upright posture is classified as walking
                result[i].num = (posture == STANDING || posture == SITTING) ? WALKING : posture;
            }
            pre_posture = posture;
            result[i].num_filt = result[i].num;
        }
        logger.info("Done: " + _conf.get(SRC_FILE));
        return result;
    }
}

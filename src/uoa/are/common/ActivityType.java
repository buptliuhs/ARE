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

/**
 * Activity Type
 * 
 * @author hliu482
 * 
 */
public class ActivityType {
    public static final int STATIC = 99;
    public static final int ACTIVE = 98;

    public static final int WALKING = 3;
    public static final int SHUFFLING = 2;
    public static final int STANDING = 1;
    public static final int SITTING = 0;
    public static final int LYING = -1;
    public static final int INVERTED = -2;
    public static final int NONWEAR = -3;
    public static final int UNDEFINED = -4;

    public static final String S_WALKING = "Walking";
    public static final String S_SHUFFLING = "Shuffling";
    public static final String S_STANDING = "Standing";
    public static final String S_SITTING = "Sitting";
    public static final String S_LYING = "Lying";
    public static final String S_INVERTED = "Inverted";
    public static final String S_NONWEAR = "Nonwear";
    public static final String S_UNDEFINED = "Undefined";

    static public int intValue(String strValue) {
        if (S_WALKING.equalsIgnoreCase(strValue))
            return WALKING;
        if (S_SHUFFLING.equalsIgnoreCase(strValue))
            return SHUFFLING;
        if (S_STANDING.equalsIgnoreCase(strValue))
            return STANDING;
        if (S_SITTING.equalsIgnoreCase(strValue))
            return SITTING;
        if (S_LYING.equalsIgnoreCase(strValue))
            return LYING;
        if (S_INVERTED.equalsIgnoreCase(strValue))
            return INVERTED;
        if (S_NONWEAR.equalsIgnoreCase(strValue))
            return NONWEAR;
        if (S_UNDEFINED.equalsIgnoreCase(strValue))
            return UNDEFINED;
        return UNDEFINED;
    }

    static public String stringValue(int intValue) {
        switch (intValue) {
        case WALKING:
            return S_WALKING;
        case SHUFFLING:
            return S_SHUFFLING;
        case STANDING:
            return S_STANDING;
        case SITTING:
            return S_SITTING;
        case LYING:
            return S_LYING;
        case INVERTED:
            return S_INVERTED;
        case NONWEAR:
            return S_NONWEAR;
        case UNDEFINED:
            return S_UNDEFINED;
        }
        return S_UNDEFINED;
    }
}

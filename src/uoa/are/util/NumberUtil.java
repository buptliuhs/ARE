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

import java.math.BigDecimal;

/**
 * Utility class to format numbers.
 * 
 * @author hliu482
 *
 */
public class NumberUtil {
    public static int getInt(double d) {
        BigDecimal bg = new BigDecimal(d);
        return (int) bg.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double getDouble(double d) {
        return getDouble(d, BigDecimal.ROUND_HALF_UP);
    }

    public static double getDouble(double d, int type) {
        return getDouble(d, type, 2);
    }

    public static double getDouble(double d, int type, int digit) {
        BigDecimal bg = new BigDecimal(d);
        return bg.setScale(digit, type).doubleValue();
    }

    public static void main(String args[]) {
        System.out.println(NumberUtil.getDouble(0.004));
        System.out.println(NumberUtil.getDouble(0.005));
        System.out.println(NumberUtil.getDouble(0.004, BigDecimal.ROUND_UP));
        System.out.println(NumberUtil.getDouble(0.005, BigDecimal.ROUND_UP));
        System.out.println(NumberUtil.getDouble(0.01, BigDecimal.ROUND_UP));
    }
}

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

import java.util.Random;

/**
 * Utility class to generate random strings.
 * 
 * @author hliu482
 *
 */
public class RandomStringGenerator {
    static private final int len = 5;
    static private final int number = 10;

    static public String generate() {
        Random random = new Random();
        int max = (int) Math.pow(number, len);
        String currentTime = TimeUtil.getTime("yyMMddHHmmssSSS");
        String s = Long.toString(random.nextInt(max), number);
        String prefix = "";
        for (int i = s.length(); i < len; ++i)
            prefix += "0";
        return currentTime + prefix + s;
    }

    static public void main(String[] args) {
        System.out.println(RandomStringGenerator.generate());
        System.out.println(RandomStringGenerator.generate());
        System.out.println(RandomStringGenerator.generate());
        System.out.println(RandomStringGenerator.generate());
        System.out.println(RandomStringGenerator.generate());
    }

}

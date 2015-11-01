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

import uoa.are.common.ActivityType;

/**
 * Class to keep recognized activity result.
 * 
 * @author hliu482
 * 
 */
public class RecElement {
    public int num = ActivityType.UNDEFINED;
    public int num_filt = ActivityType.UNDEFINED;
    public double angle = 0.0;
    public double sma = 0.0;
}

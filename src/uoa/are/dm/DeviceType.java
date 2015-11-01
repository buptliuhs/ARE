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

/**
 * Class to hold period of device type
 * 
 * @author hliu482
 * 
 */
public class DeviceType {

    private int id;
    private String name;
    private String pattern;
    private String sample;
    private int deviceNameIndex;
    private String timeIndex;
    private String converter;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public int getDeviceNameIndex() {
        return deviceNameIndex;
    }

    public void setDeviceNameIndex(int deviceNameIndex) {
        this.deviceNameIndex = deviceNameIndex;
    }

    public String getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(String timeIndex) {
        this.timeIndex = timeIndex;
    }

    public String getConverter() {
        return converter;
    }

    public void setConverter(String converter) {
        this.converter = converter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

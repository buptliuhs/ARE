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
 * Class to hold period of signal data.
 * 
 * @author hliu482
 *
 */
public class IntermediateData {

    private int start_second;
    private int end_second;
    private int offset;
    private double[] v;

    public int getStart_second() {
        return start_second;
    }

    public void setStart_second(int start_second) {
        this.start_second = start_second;
    }

    public int getEnd_second() {
        return end_second;
    }

    public void setEnd_second(int end_second) {
        this.end_second = end_second;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public double[] getV() {
        return v;
    }

    public void setV(double[] v) {
        this.v = v;
    }

    public void combine(IntermediateData id) {
        for (int i = 0; i < id.offset; ++i) {
            v[offset + i] = id.v[i];
        }
        this.offset += id.offset;
    }
}

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
public class SignalData {

    private int start_second;
    private int end_second;
    private int offset;
    private double[] x;
    private double[] y;
    private double[] z;

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

    public double[] getX() {
        return x;
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public double[] getY() {
        return y;
    }

    public void setY(double[] y) {
        this.y = y;
    }

    public double[] getZ() {
        return z;
    }

    public void setZ(double[] z) {
        this.z = z;
    }

    public void combine(SignalData sd) {
        for (int i = 0; i < sd.offset; ++i) {
            x[offset + i] = sd.x[i];
            y[offset + i] = sd.y[i];
            z[offset + i] = sd.z[i];
        }
        this.offset += sd.offset;
    }
}

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

package uoa.are.action;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import uoa.are.common.Code;
import uoa.are.common.Message;
import uoa.are.common.ResponseUtil;
import uoa.are.dm.Device;
import uoa.are.dm.DeviceManager;
import uoa.are.dm.DeviceType;
import uoa.are.dm.DeviceTypeManager;

/**
 * This is action for device.
 * 
 * @author hliu482
 * 
 */
@SuppressWarnings("serial")
public class DeviceAction extends AuthorizedAction {

    private String project_id;
    private String subject_id;

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id.trim();
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id.trim();
    }

    /**
     * Initialize device type list.
     * 
     * @throws IOException
     */
    public void initDeviceTypeList() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("initDeviceTypeList (" + getSessionUserID() + ", " + subject_id + ")");
        DeviceType d = null;
        if (!StringUtils.isEmpty(subject_id))
            d = DeviceTypeManager.getDeviceTypeBySubjectId(subject_id);
        try {
            List<DeviceType> list = DeviceTypeManager.getAllDeviceType();

            Map<Object, Object> data = new LinkedHashMap<Object, Object>();
            for (DeviceType dt : list) {
                if ((d != null) && (d.getId() != dt.getId()))
                    continue;
                Map<Object, Object> map = new LinkedHashMap<Object, Object>();
                map.put("id", dt.getId());
                map.put("name", dt.getName());
                map.put("description", dt.getDescription());
                map.put("sample", dt.getSample());
                map.put("suffix", dt.getSample().substring(dt.getSample().lastIndexOf('.')));
                data.put(dt.getId(), map);
            }

            ResponseUtil.setResponseMap(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String device_type;
    private String device_name;

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type.trim();
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name.trim();
    }

    /**
     * Initialized device list.
     * 
     * @throws IOException
     */
    public void initDeviceList() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("initDeviceList (" + getSessionUserID() + ")");

        List<Device> list = DeviceManager.getAllDevices(getSessionUserID(), device_type);
        Map<Object, Object> data = new LinkedHashMap<Object, Object>();
        int index = 0;
        for (Device d : list) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("device_id", d.getId());
            map.put("device_name", d.getName());
            map.put("device_type", d.getType());
            map.put("allocated", d.getAllocatedTo());
            data.put((index++), map);

        }
        ResponseUtil.setResponseMap(data);
    }

    /**
     * Initialized idle device list.
     * 
     * @throws IOException
     */
    public void initIdleDeviceList() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("initIdleDeviceList (" + getSessionUserID() + ")");

        List<Device> list = DeviceManager.getIdelDevices(getSessionUserID());
        Map<Object, Object> data = new LinkedHashMap<Object, Object>();
        int index = 0;
        for (Device d : list) {
            Map<Object, Object> map = new LinkedHashMap<Object, Object>();
            map.put("device_id", d.getId());
            map.put("device_name", d.getName());
            map.put("device_type", d.getType());
            data.put((index++), map);

        }
        ResponseUtil.setResponseMap(data);
    }

    /**
     * Add a new device.
     * 
     * @throws IOException
     */
    public void addDevice() throws IOException {
        if (!isAuthorized())
            return;
        logger.info("addDevice (" + getSessionUserID() + ", " + device_name + ", " + device_type + ")");

        if (DeviceManager.hasDevice(getSessionUserID(), device_type, device_name)) {
            logger.error("Duplicated device name");
            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            result.put("result", Code.FAILED);
            result.put("message", "Duplicated device name NOT allowed!");
            ResponseUtil.setResponseMap(result);
            return;
        }
        int ret = DeviceManager.addDevice(getSessionUserID(), device_type, device_name);
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        if (ret == Code.SUCCESSFUL) {
            result.put("result", Code.SUCCESSFUL);
            result.put("message", Message.SUCCESSFUL);
        } else {
            result.put("result", Code.FAILED);
            result.put("message", Message.FAILED);
        }
        ResponseUtil.setResponseMap(result);
    }

}

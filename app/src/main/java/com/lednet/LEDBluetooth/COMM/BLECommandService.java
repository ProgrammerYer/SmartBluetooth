package com.lednet.LEDBluetooth.COMM;

import com.blebulb.core.BLECommandRequest;
import com.blebulb.core.BLEPeripheralClient;


public class BLECommandService {

    public static LEDResponse<DeviceStateInfoBase> getDeviceStateInfoBaseByUniID(BLEPeripheralClient client, int timeOut) {
        LEDResponse<DeviceStateInfoBase> r_resp = new LEDResponse<DeviceStateInfoBase>();
        BLECommandRequest request = new BLECommandRequest(client);
        try {
            byte[] data = LEDDeviceCMDMgr.getCommandDataForQuery();
            byte[] resp = request.startRequest(data, timeOut);

            DeviceStateInfoBase info = LEDDeviceCMDMgr.getDeviceStateInfoBaseByData(resp);
            if (info == null) {
                r_resp.setErrorMessage("Response empty");
                return r_resp;
            } else {
                r_resp.setResponseResult(info);
                return r_resp;
            }
        } catch (Exception e) {
            r_resp.setErrorMessage(e.getMessage());
            return r_resp;
        } finally {
            request.Close();
        }
    }


}

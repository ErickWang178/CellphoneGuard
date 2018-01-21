package com.erick.cellphoneguard.interfaces;

import com.erick.cellphoneguard.services.SensorService;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public interface IServiceCallback {
    void onConnect(SensorService service);
    void onDisconnect();
}

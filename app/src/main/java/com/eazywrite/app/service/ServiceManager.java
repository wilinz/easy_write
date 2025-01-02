package com.eazywrite.app.service;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class ServiceManager {
    private static ServiceManager serviceManager;
    private boolean isBind;

    private ServiceManager() {
    }

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            synchronized (ServiceManager.class) {
                if (serviceManager == null) {
                    serviceManager = new ServiceManager();
                }
            }
        }
        return serviceManager;
    }

    /**
     * 绑定启动服务
     *
     * @param packageContext
     * @param bind 是否进行绑定启动服务
     * @param serviceCls
     * @return
     */
    public boolean bindService(Context packageContext, boolean bind, ServiceConnection connection, Class<?> serviceCls) {
        Intent startIntent = new Intent(packageContext, serviceCls);
        packageContext.startService(startIntent);
        if (bind) {
            Intent intent = new Intent(packageContext, serviceCls);
            isBind = packageContext.bindService(intent, connection, BIND_AUTO_CREATE);
        } else {
            isBind = false;
        }
        return isBind;
    }

    /**
     * 解绑停止服务
     *
     * @param packageContext
     * @param connection
     * @param serviceCls
     */
    public void unbindService(Context packageContext, ServiceConnection connection, Class<?> serviceCls) {
        Intent stopIntent = new Intent(packageContext, serviceCls);
        packageContext.stopService(stopIntent);
        if (isBind) {
            packageContext.unbindService(connection);
        }
    }
}

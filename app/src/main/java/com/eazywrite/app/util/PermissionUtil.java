package com.eazywrite.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

    public static final int WRITE_EXTERNAL_STORAGE=0;
    public static final int READ_EXTERNAL_STORAGE=1;
    public static final int CAMERA = 2;

    /**
     * 使用单例模式
     */
    private PermissionUtil() {}
    private static PermissionUtil permissionUtil;

    public static PermissionUtil getInstance(){
        if(permissionUtil ==null){
            synchronized (PermissionUtil.class){
                if(null== permissionUtil){
                    permissionUtil =new PermissionUtil();
                }
            }
        }
        return permissionUtil;
    }

    /**
     * 申请权限，
     * @param stringPermission 要检查的权限
     * @param requestCode 请求码
     * @return 是否已同意
     */
    public boolean checkPermission(Activity activity, Context context, int requestCode, String...stringPermission) {
        boolean flag = false;
        //已有权限
        if (ContextCompat.checkSelfPermission(context, stringPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            flag = true;
        } else {
            //申请权限
            ActivityCompat.requestPermissions(activity, stringPermission, requestCode);
        }
        return flag;
    }
}

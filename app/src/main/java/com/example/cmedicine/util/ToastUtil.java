package com.example.cmedicine.util;

import android.content.Context;
import android.widget.Toast;

import java.net.ContentHandler;

/**
 * Created by 41850 on 2018/3/4.
 */

public class ToastUtil {
    private static Toast sToast = null;
    public static boolean isShow = true;

    public static void showToast(Context context, String msg) {
        if (!isShow ) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        else {
            sToast.setText(msg);
        }
        sToast.show();
    }

    public static void showToast(Context context,int resId) {
        if (!isShow) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(resId);
        }
        sToast.show();
    }

    public static void showToastLong( Context context,String msg) {
        if (!isShow) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            sToast.setText(msg);
        }
        sToast.show();
    }

    public static void showToastLong(Context context, int resId) {
        if (!isShow) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
        } else {
            sToast.setText(resId);
        }
        sToast.show();
    }

    // 主要针对需要在某个时候，取消提示
    public static void cancelToast() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }
}

package com.joker.api.apply;

import android.app.Activity;
import android.content.Intent;

import com.joker.api.Permissions4M;
import com.joker.api.apply.util.SupportUtil;
import com.joker.api.support.PermissionsPageManager;
import com.joker.api.wrapper.AnnotationWrapper;
import com.joker.api.wrapper.ListenerWrapper;
import com.joker.api.wrapper.PermissionWrapper;
import com.joker.api.wrapper.Wrapper;


/**
 * Created by joker on 2017/8/10.
 */

public class NormalApplyPermissions {
    // annotation module ===================================================================================
    @SuppressWarnings("unchecked")
    public static void grantedWithAnnotation(PermissionWrapper wrapper) {
        wrapper.getProxy(wrapper.getContext().getClass().getName())
                .granted(wrapper.getContext(), wrapper.getRequestCode());
    }

    @SuppressWarnings("unchecked")
    public static void deniedWithAnnotation(PermissionWrapper wrapper) {
        AnnotationWrapper.PermissionsProxy proxy = wrapper.getProxy(wrapper.getContext().getClass().getName());

        proxy.denied(wrapper.getContext(), wrapper.getRequestCode());

        if (SupportUtil.nonShowRationale(wrapper)) {
            boolean androidPage = wrapper.getPageType() == Permissions4M.PageType
                    .ANDROID_SETTING_PAGE;
            Intent intent = androidPage ? PermissionsPageManager.getSettingIntent(getActivity(wrapper)) :
                    PermissionsPageManager.getIntent(getActivity(wrapper));

            proxy.intent(wrapper.getContext(), wrapper.getRequestCode(), intent);
        }
    }

    // listener module ===================================================================================
    public static void grantedWithListener(PermissionWrapper wrapper) {
        if (wrapper.getPermissionRequestListener() != null) {
            wrapper.getPermissionRequestListener().permissionGranted(wrapper.getRequestCode());
        }
    }

    public static void deniedOnResultWithListener(PermissionWrapper wrapper) {
        ListenerWrapper.PermissionRequestListener requestListener = wrapper.getPermissionRequestListener();
        if (requestListener != null) {
            requestListener.permissionDenied(wrapper.getRequestCode());
        }

        if (SupportUtil.pageListenerNonNull(wrapper) && SupportUtil.nonShowRationale(wrapper)) {
            Activity activity = getActivity(wrapper);

            boolean androidPage = wrapper.getPageType() == Permissions4M.PageType
                    .ANDROID_SETTING_PAGE;
            Intent intent = androidPage ? PermissionsPageManager.getSettingIntent(getActivity(wrapper)) :
                    PermissionsPageManager.getIntent(activity);
            wrapper.getPermissionPageListener().pageIntent(intent);
        }
    }

    private static Activity getActivity(Wrapper wrapper) {
        Activity activity;
        if (wrapper.getContext() instanceof android.app.Fragment) {
            activity = ((android.app.Fragment) wrapper.getContext()).getActivity();
        } else if (wrapper.getContext() instanceof android.support.v4.app.Fragment) {
            activity = ((android.support.v4.app.Fragment) wrapper.getContext()).getActivity();
        } else {
            activity = (Activity) wrapper.getContext();
        }

        return activity;
    }
}

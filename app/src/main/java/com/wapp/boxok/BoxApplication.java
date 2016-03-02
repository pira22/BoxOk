package com.wapp.boxok;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

/**
 * Created by MacBook on 14/07/2015.
 */
@ReportsCrashes(
        formUri = "https://khaled.cloudant.com/acra-app/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "ablogionswerequenlyedows",
        formUriBasicAuthPassword = "qlj3wD4fU3xygyEugOY5TNQ0",
        //formKey = " d:d,s;", // This is required for backward compatibility but not used
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.toast_crash
)
public class BoxApplication extends Application {

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        super.onCreate();
        ACRA.init(this);
    }
}

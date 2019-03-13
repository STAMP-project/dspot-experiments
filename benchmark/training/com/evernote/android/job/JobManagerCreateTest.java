package com.evernote.android.job;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.Collections;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Copyright 2017 Evernote Corporation. All rights reserved.
 *
 * Created by rwondratschek on 12.05.17.
 */
@FixMethodOrder(MethodSorters.JVM)
public class JobManagerCreateTest {
    @Test(expected = JobManagerCreateException.class)
    public void verifyJobManagerCrashesWithoutSupportedApi() {
        JobManager.create(mockContext());
    }

    @Test
    public void verifyCreateSuccessful() {
        PackageManager packageManager = Mockito.mock(PackageManager.class);
        Mockito.when(packageManager.queryIntentServices(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt())).thenReturn(Collections.singletonList(new ResolveInfo()));
        Mockito.when(packageManager.queryBroadcastReceivers(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt())).thenReturn(Collections.singletonList(new ResolveInfo()));
        Context context = mockContext();
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        JobManager.create(context);
    }

    @Test
    public void verifyForceAllowApi14() {
        JobConfig.setForceAllowApi14(true);
        Context context = mockContext();
        PackageManager packageManager = Mockito.mock(PackageManager.class);
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        JobManager.create(context);
    }
}


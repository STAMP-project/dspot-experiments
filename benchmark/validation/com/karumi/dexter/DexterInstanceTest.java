/**
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.karumi.dexter;


import PackageManager.PERMISSION_DENIED;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DexterInstanceTest {
    private static final String ANY_PERMISSION = "noissimrep yna";

    private static final Thread THREAD = new DexterInstanceTest.TestThread();

    @Mock
    AndroidPermissionService androidPermissionService;

    @Mock
    Context context;

    @Mock
    Intent intent;

    @Mock
    Activity activity;

    @Mock
    MultiplePermissionsListener multiplePermissionsListener;

    @Mock
    PermissionListener permissionListener;

    private DexterInstance dexter;

    private AsyncExecutor asyncExecutor;

    @Test(expected = DexterException.class)
    public void onNoPermissionCheckedThenThrowException() {
        dexter.checkPermissions(multiplePermissionsListener, Collections.<String>emptyList(), DexterInstanceTest.THREAD);
    }

    @Test(expected = DexterException.class)
    public void onCheckPermissionMoreThanOnceThenThrowException() {
        givenPermissionIsAlreadyDenied(DexterInstanceTest.ANY_PERMISSION);
        dexter.checkPermission(permissionListener, DexterInstanceTest.ANY_PERMISSION, DexterInstanceTest.THREAD);
        dexter.checkPermission(permissionListener, DexterInstanceTest.ANY_PERMISSION, DexterInstanceTest.THREAD);
    }

    @Test
    public void onPermissionAlreadyGrantedThenNotifiesListener() {
        givenPermissionIsAlreadyGranted(DexterInstanceTest.ANY_PERMISSION);
        dexter.checkPermission(permissionListener, DexterInstanceTest.ANY_PERMISSION, DexterInstanceTest.THREAD);
        thenPermissionIsGranted(DexterInstanceTest.ANY_PERMISSION);
    }

    @Test
    public void onShouldShowRationaleThenNotifiesListener() {
        givenPermissionIsAlreadyDenied(DexterInstanceTest.ANY_PERMISSION);
        givenShouldShowRationaleForPermission(DexterInstanceTest.ANY_PERMISSION);
        whenCheckPermission(permissionListener, DexterInstanceTest.ANY_PERMISSION);
        thenShouldShowRationaleForPermission(DexterInstanceTest.ANY_PERMISSION);
    }

    @Test
    public void onPermissionDeniedThenNotifiesListener() {
        givenPermissionIsAlreadyDenied(DexterInstanceTest.ANY_PERMISSION);
        givenShouldShowRationaleForPermission(DexterInstanceTest.ANY_PERMISSION);
        whenCheckPermission(permissionListener, DexterInstanceTest.ANY_PERMISSION);
        dexter.onPermissionRequestDenied(Collections.singletonList(DexterInstanceTest.ANY_PERMISSION));
        thenPermissionIsDenied(DexterInstanceTest.ANY_PERMISSION);
    }

    @Test
    public void onPermissionDeniedDoSequentialCheckPermissionThenNotifiesListener() throws InterruptedException {
        givenPermissionIsAlreadyDenied(DexterInstanceTest.ANY_PERMISSION);
        givenShouldShowRationaleForPermission(DexterInstanceTest.ANY_PERMISSION);
        PermissionListener checkPermissionOnDeniedPermissionListener = givenARetryCheckPermissionOnDeniedPermissionListener(permissionListener);
        whenCheckPermission(checkPermissionOnDeniedPermissionListener, DexterInstanceTest.ANY_PERMISSION);
        dexter.onPermissionRequestDenied(Collections.singletonList(DexterInstanceTest.ANY_PERMISSION));
        asyncExecutor.waitForExecution();
        thenPermissionIsDenied(DexterInstanceTest.ANY_PERMISSION);
    }

    @Test
    public void onPermissionPermanentlyDeniedThenNotifiesListener() {
        givenPermissionIsAlreadyDenied(DexterInstanceTest.ANY_PERMISSION);
        givenShouldNotShowRationaleForPermission(DexterInstanceTest.ANY_PERMISSION);
        whenCheckPermission(permissionListener, DexterInstanceTest.ANY_PERMISSION);
        dexter.onPermissionRequestDenied(Collections.singletonList(DexterInstanceTest.ANY_PERMISSION));
        thenPermissionIsPermanentlyDenied(DexterInstanceTest.ANY_PERMISSION);
    }

    @Test
    public void onPermissionFailedByRuntimeExceptionThenNotifiesListener() {
        givenPermissionIsChecked(DexterInstanceTest.ANY_PERMISSION, PERMISSION_DENIED);
        givenARuntimeExceptionIsThrownWhenPermissionIsChecked(DexterInstanceTest.ANY_PERMISSION);
        givenShouldShowRationaleForPermission(DexterInstanceTest.ANY_PERMISSION);
        whenCheckPermission(permissionListener, DexterInstanceTest.ANY_PERMISSION);
        dexter.onPermissionRequestDenied(Collections.singletonList(DexterInstanceTest.ANY_PERMISSION));
        thenPermissionIsDenied(DexterInstanceTest.ANY_PERMISSION);
    }

    private static class IntentMockProvider extends IntentProvider {
        private final Intent intent;

        IntentMockProvider(Intent intent) {
            this.intent = intent;
        }

        @Override
        public Intent get(Context context, Class<?> clazz) {
            return intent;
        }
    }

    private class CheckPermissionWithOnActivityReadyInBackground implements RetryCheckPermissionOnDeniedPermissionListener.CheckPermissionAction {
        @Override
        public void check(final PermissionListener listener, final String permission) {
            dexter.checkPermission(listener, permission, DexterInstanceTest.THREAD);
            asyncExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    dexter.onActivityReady(activity);
                    dexter.onPermissionRequestDenied(Collections.singletonList(permission));
                }
            });
        }
    }

    private static class TestThread implements Thread {
        @Override
        public void execute(Runnable runnable) {
            runnable.run();
        }

        @Override
        public void loop() {
        }
    }
}


/**
 * Copyright 2016 Google, Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.firebase.jobdispatcher;


import JobService.ACTION_EXECUTE;
import Trigger.NOW;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.provider.ContactsContract;
import com.firebase.jobdispatcher.JobTrigger.ContentUriTrigger;
import com.firebase.jobdispatcher.ObservedUri.Flags;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static RetryStrategy.RETRY_POLICY_EXPONENTIAL;
import static RetryStrategy.RETRY_POLICY_LINEAR;


/**
 * Tests for the {@link DefaultJobValidator}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 23)
public class DefaultJobValidatorTest {
    @Mock
    private Context mockContext;

    @Mock
    private PackageManager packageManagerMock;

    @Captor
    private ArgumentCaptor<Intent> intentCaptor;

    private DefaultJobValidator validator;

    @SuppressWarnings("WrongConstant")
    @Test
    public void testValidate_retryStrategy() throws Exception {
        Map<RetryStrategy, List<String>> testCases = new HashMap<>();
        testCases.put(/* bad policy */
        new RetryStrategy(0, 30, 3600), Collections.singletonList("Unknown retry policy provided"));
        testCases.put(new RetryStrategy(RETRY_POLICY_LINEAR, 15, 3600), Collections.singletonList("Initial backoff must be at least 30s"));
        testCases.put(new RetryStrategy(RETRY_POLICY_EXPONENTIAL, 15, 3600), Collections.singletonList("Initial backoff must be at least 30s"));
        testCases.put(new RetryStrategy(RETRY_POLICY_LINEAR, 30, 60), Collections.singletonList("Maximum backoff must be greater than 300s (5 minutes)"));
        testCases.put(new RetryStrategy(RETRY_POLICY_EXPONENTIAL, 30, 60), Collections.singletonList("Maximum backoff must be greater than 300s (5 minutes)"));
        testCases.put(new RetryStrategy(RETRY_POLICY_LINEAR, 301, 300), Collections.singletonList("Maximum backoff must be greater than or equal to initial backoff"));
        testCases.put(new RetryStrategy(RETRY_POLICY_EXPONENTIAL, 301, 300), Collections.singletonList("Maximum backoff must be greater than or equal to initial backoff"));
        for (Map.Entry<RetryStrategy, List<String>> testCase : testCases.entrySet()) {
            List<String> validationErrors = validator.validate(testCase.getKey());
            Assert.assertNotNull("Expected validation errors, but got null", validationErrors);
            for (String expected : testCase.getValue()) {
                Assert.assertTrue((("Expected validation errors to contain \"" + expected) + "\""), validationErrors.contains(expected));
            }
        }
    }

    @Test
    public void testValidate_trigger() throws Exception {
        Map<JobTrigger, String> testCases = new HashMap<>();
        testCases.put(NOW, null);
        testCases.put(Trigger.executionWindow(0, 100), null);
        ContentUriTrigger contentUriTrigger = Trigger.contentUriTrigger(Arrays.asList(new ObservedUri(ContactsContract.AUTHORITY_URI, Flags.FLAG_NOTIFY_FOR_DESCENDANTS)));
        testCases.put(contentUriTrigger, null);
        for (Map.Entry<JobTrigger, String> testCase : testCases.entrySet()) {
            List<String> validationErrors = validator.validate(testCase.getKey());
            if ((testCase.getValue()) == null) {
                Assert.assertNull("Expected no validation errors for trigger", validationErrors);
            } else {
                Assert.assertTrue((("Expected validation errors to contain \"" + (testCase.getValue())) + "\""), validationErrors.contains(testCase.getValue()));
            }
        }
    }

    @Test
    public void validateService_null() {
        List<String> errors = validator.validateService(null);
        Assert.assertTrue(errors.contains("Service can't be empty"));
    }

    @Test
    public void validateService_empty() {
        List<String> errors = validator.validateService("");
        Assert.assertTrue(errors.contains("Service can't be empty"));
    }

    @Test
    public void validateService_packageManagerIsNotAvailable() {
        Mockito.when(mockContext.getPackageManager()).thenReturn(null);
        List<String> errors = validator.validateService("service");
        Assert.assertTrue(errors.contains("PackageManager is null, can't validate service"));
    }

    @Test
    public void validateService_noSuchService() {
        Mockito.when(mockContext.getPackageManager()).thenReturn(packageManagerMock);
        Mockito.when(packageManagerMock.queryIntentServices(ArgumentMatchers.any(Intent.class), ArgumentMatchers.eq(0))).thenReturn(Collections.<ResolveInfo>emptyList());
        Mockito.when(mockContext.getPackageName()).thenReturn("package");
        List<String> errors = validator.validateService("service");
        Assert.assertNull(errors);
        Mockito.verify(packageManagerMock).queryIntentServices(intentCaptor.capture(), ArgumentMatchers.eq(0));
        Intent intent = intentCaptor.getValue();
        Assert.assertEquals(ACTION_EXECUTE, intent.getAction());
        Assert.assertEquals(new ComponentName("package", "service"), intent.getComponent());
    }

    @Test
    public void validateService_noActiveService() {
        Mockito.when(mockContext.getPackageManager()).thenReturn(packageManagerMock);
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.serviceInfo = new ServiceInfo();
        resolveInfo.serviceInfo.enabled = false;
        Mockito.when(packageManagerMock.queryIntentServices(ArgumentMatchers.any(Intent.class), ArgumentMatchers.eq(0))).thenReturn(Arrays.asList(resolveInfo));
        Mockito.when(mockContext.getPackageName()).thenReturn("package");
        List<String> errors = validator.validateService("service");
        Assert.assertTrue(errors.contains("service is disabled."));
    }

    @Test
    public void validateService() {
        Mockito.when(mockContext.getPackageManager()).thenReturn(packageManagerMock);
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.serviceInfo = new ServiceInfo();
        resolveInfo.serviceInfo.enabled = true;
        Mockito.when(packageManagerMock.queryIntentServices(ArgumentMatchers.any(Intent.class), ArgumentMatchers.eq(0))).thenReturn(Arrays.asList(resolveInfo));
        Mockito.when(mockContext.getPackageName()).thenReturn("package");
        List<String> errors = validator.validateService("service");
        Assert.assertNull(errors);
    }
}


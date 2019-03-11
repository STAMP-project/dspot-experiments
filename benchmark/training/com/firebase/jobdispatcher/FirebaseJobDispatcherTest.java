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


import FirebaseJobDispatcher.CANCEL_RESULT_NO_DRIVER_AVAILABLE;
import FirebaseJobDispatcher.CANCEL_RESULT_SUCCESS;
import FirebaseJobDispatcher.SCHEDULE_RESULT_NO_DRIVER_AVAILABLE;
import FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS;
import RetryStrategy.RETRY_POLICY_EXPONENTIAL;
import com.firebase.jobdispatcher.FirebaseJobDispatcher.ScheduleFailedException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static FirebaseJobDispatcher.SCHEDULE_RESULT_BAD_SERVICE;
import static FirebaseJobDispatcher.SCHEDULE_RESULT_NO_DRIVER_AVAILABLE;
import static FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS;
import static FirebaseJobDispatcher.SCHEDULE_RESULT_UNKNOWN_ERROR;
import static FirebaseJobDispatcher.SCHEDULE_RESULT_UNSUPPORTED_TRIGGER;


/**
 * Tests for the {@link FirebaseJobDispatcher} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 23)
public class FirebaseJobDispatcherTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Driver driver;

    @Mock
    private JobValidator validator;

    private FirebaseJobDispatcher dispatcher;

    @Test
    public void testSchedule_passThrough() throws Exception {
        final int[] possibleResults = new int[]{ SCHEDULE_RESULT_SUCCESS, SCHEDULE_RESULT_NO_DRIVER_AVAILABLE, SCHEDULE_RESULT_BAD_SERVICE, SCHEDULE_RESULT_UNKNOWN_ERROR, SCHEDULE_RESULT_UNSUPPORTED_TRIGGER };
        for (int result : possibleResults) {
            Mockito.when(driver.schedule(null)).thenReturn(result);
            Assert.assertEquals(result, dispatcher.schedule(null));
        }
        Mockito.verify(driver, Mockito.times(possibleResults.length)).schedule(null);
    }

    @Test
    public void testSchedule_unavailable() throws Exception {
        setDriverAvailability(false);
        Assert.assertEquals(SCHEDULE_RESULT_NO_DRIVER_AVAILABLE, dispatcher.schedule(null));
        Mockito.verify(driver, Mockito.never()).schedule(null);
    }

    @Test
    public void testCancelJob() throws Exception {
        final String tag = "foo";
        // simulate success
        Mockito.when(driver.cancel(tag)).thenReturn(CANCEL_RESULT_SUCCESS);
        Assert.assertEquals("Expected dispatcher to pass the result of Driver#cancel(String, Class) through", CANCEL_RESULT_SUCCESS, dispatcher.cancel(tag));
        // verify the driver was indeed called
        Mockito.verify(driver).cancel(tag);
    }

    @Test
    public void testCancelJob_unavailable() throws Exception {
        setDriverAvailability(false);// driver is unavailable

        Assert.assertEquals(CANCEL_RESULT_NO_DRIVER_AVAILABLE, dispatcher.cancel("foo"));
        // verify the driver was never even consulted
        Mockito.verify(driver, Mockito.never()).cancel("foo");
    }

    @Test
    public void testCancelAllJobs() throws Exception {
        Mockito.when(driver.cancelAll()).thenReturn(CANCEL_RESULT_SUCCESS);
        Assert.assertEquals(CANCEL_RESULT_SUCCESS, dispatcher.cancelAll());
        Mockito.verify(driver).cancelAll();
    }

    @Test
    public void testCancelAllJobs_unavailable() throws Exception {
        setDriverAvailability(false);// driver is unavailable

        Assert.assertEquals(CANCEL_RESULT_NO_DRIVER_AVAILABLE, dispatcher.cancelAll());
        Mockito.verify(driver, Mockito.never()).cancelAll();
    }

    @Test
    public void testMustSchedule_success() throws Exception {
        Mockito.when(driver.schedule(null)).thenReturn(SCHEDULE_RESULT_SUCCESS);
        /* assert no exception is thrown */
        dispatcher.mustSchedule(null);
    }

    @Test
    public void testMustSchedule_unavailable() throws Exception {
        setDriverAvailability(false);// driver is unavailable

        expectedException.expect(ScheduleFailedException.class);
        dispatcher.mustSchedule(null);
    }

    @Test
    public void testMustSchedule_failure() throws Exception {
        final int[] possibleErrors = new int[]{ SCHEDULE_RESULT_NO_DRIVER_AVAILABLE, SCHEDULE_RESULT_BAD_SERVICE, SCHEDULE_RESULT_UNKNOWN_ERROR, SCHEDULE_RESULT_UNSUPPORTED_TRIGGER };
        for (int scheduleError : possibleErrors) {
            Mockito.when(driver.schedule(null)).thenReturn(scheduleError);
            try {
                dispatcher.mustSchedule(null);
                Assert.fail((("Expected mustSchedule() with error code " + scheduleError) + " to fail"));
            } catch (ScheduleFailedException expected) {
                /* expected */
            }
        }
        Mockito.verify(driver, Mockito.times(possibleErrors.length)).schedule(null);
    }

    @Test
    public void testNewRetryStrategyBuilder() {
        // custom validator that only approves strategies where initialbackoff == 30s
        Mockito.when(validator.validate(ArgumentMatchers.any(RetryStrategy.class))).thenAnswer(new Answer<List<String>>() {
            @Override
            public List<String> answer(InvocationOnMock invocation) throws Throwable {
                RetryStrategy rs = ((RetryStrategy) (invocation.getArguments()[0]));
                // only succeed if initialBackoff == 30s
                return (rs.getInitialBackoff()) == 30 ? null : Arrays.asList("foo", "bar");
            }
        });
        try {
            dispatcher.newRetryStrategy(RETRY_POLICY_EXPONENTIAL, 0, 30);
            Assert.fail("Expected initial backoff != 30s to fail using custom validator");
        } catch (Exception unused) {
            /* unused */
        }
        try {
            dispatcher.newRetryStrategy(RETRY_POLICY_EXPONENTIAL, 30, 30);
        } catch (Exception e) {
            throw new AssertionError("Expected initial backoff == 30s not to fail using custom validator", e);
        }
    }
}


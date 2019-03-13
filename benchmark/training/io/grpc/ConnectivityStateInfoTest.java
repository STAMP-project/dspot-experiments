/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc;


import Status.INTERNAL;
import Status.OK;
import Status.UNAVAILABLE;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ConnectivityStateInfo}.
 */
@RunWith(JUnit4.class)
public class ConnectivityStateInfoTest {
    @Test
    public void forNonError() {
        ConnectivityStateInfo info = ConnectivityStateInfo.forNonError(ConnectivityState.IDLE);
        Assert.assertEquals(ConnectivityState.IDLE, info.getState());
        Assert.assertEquals(OK, info.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void forNonErrorInvalid() {
        ConnectivityStateInfo.forNonError(ConnectivityState.TRANSIENT_FAILURE);
    }

    @Test
    public void forTransientFailure() {
        ConnectivityStateInfo info = ConnectivityStateInfo.forTransientFailure(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, info.getState());
        Assert.assertEquals(UNAVAILABLE, info.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void forTransientFailureInvalid() {
        ConnectivityStateInfo.forTransientFailure(OK);
    }

    @Test
    public void equality() {
        ConnectivityStateInfo info1 = ConnectivityStateInfo.forNonError(ConnectivityState.IDLE);
        ConnectivityStateInfo info2 = ConnectivityStateInfo.forNonError(ConnectivityState.CONNECTING);
        ConnectivityStateInfo info3 = ConnectivityStateInfo.forNonError(ConnectivityState.IDLE);
        ConnectivityStateInfo info4 = ConnectivityStateInfo.forTransientFailure(UNAVAILABLE);
        ConnectivityStateInfo info5 = ConnectivityStateInfo.forTransientFailure(INTERNAL);
        ConnectivityStateInfo info6 = ConnectivityStateInfo.forTransientFailure(INTERNAL);
        Assert.assertEquals(info1, info3);
        Assert.assertNotSame(info1, info3);
        Assert.assertEquals(info1.hashCode(), info3.hashCode());
        Assert.assertEquals(info5, info6);
        Assert.assertEquals(info5.hashCode(), info6.hashCode());
        Assert.assertNotSame(info5, info6);
        Assert.assertNotEquals(info1, info2);
        Assert.assertNotEquals(info1, info4);
        Assert.assertNotEquals(info4, info6);
        Assert.assertFalse(info1.equals(null));
        // Extra cast to avoid ErrorProne EqualsIncompatibleType failure
        Assert.assertFalse(((Object) (info1)).equals(this));
    }
}


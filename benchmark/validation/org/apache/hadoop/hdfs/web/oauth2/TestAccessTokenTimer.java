/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hdfs.web.oauth2;


import org.apache.hadoop.util.Timer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestAccessTokenTimer {
    @Test
    public void expireConversionWorks() {
        Timer mockTimer = Mockito.mock(Timer.class);
        Mockito.when(mockTimer.now()).thenReturn(5L);
        AccessTokenTimer timer = new AccessTokenTimer(mockTimer);
        timer.setExpiresIn("3");
        Assert.assertEquals(3005, timer.getNextRefreshMSSinceEpoch());
        Assert.assertTrue(timer.shouldRefresh());
    }

    @Test
    public void shouldRefreshIsCorrect() {
        Timer mockTimer = Mockito.mock(Timer.class);
        Mockito.when(mockTimer.now()).thenReturn(500L).thenReturn((1000000L + 500L));
        AccessTokenTimer timer = new AccessTokenTimer(mockTimer);
        timer.setExpiresInMSSinceEpoch("1000000");
        Assert.assertFalse(timer.shouldRefresh());
        Assert.assertTrue(timer.shouldRefresh());
        Mockito.verify(mockTimer, Mockito.times(2)).now();
    }
}


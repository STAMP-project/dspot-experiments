/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.datagen;


import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class SessionManagerTest {
    @Test
    public void sessionShouldForceTokenReUseWhenMaxedOut() {
        final SessionManager sm = new SessionManager();
        sm.setMaxSessionDurationSeconds(1);
        sm.setMaxSessions(5);
        final Set<String> expectedSet = new HashSet<String>(Arrays.asList("0", "1", "2", "3", "4"));
        /**
         * FillActiveSessions
         */
        for (int i = 0; i < 5; i++) {
            final String token = sm.getToken(Integer.toString(i));
            Assert.assertTrue(("Got Token:" + token), expectedSet.contains(token));
            sm.newSession(token);
        }
    }

    @Test
    public void sessionShouldExpireAndReuse() throws InterruptedException {
        final SessionManager sm = new SessionManager();
        sm.setMaxSessionDuration(Duration.ofMillis(10));
        sm.setMaxSessions(5);
        /* FillActiveSessions */
        for (int i = 0; i < 5; i++) {
            sm.newSession(Integer.toString(i));
        }
        /* Expire them all */
        Thread.sleep(11);
        /* reuse tokens */
        for (int i = 0; i < 5; i++) {
            // force expiration & check
            final boolean active = sm.isActiveAndExpire(Integer.toString(i));
            Assert.assertFalse(active);
            // want to re-use the oldest-existing session if we havent seen this before
            final boolean isRecycled = sm.isExpiredSession(Integer.toString(i));
            Assert.assertTrue(("Should be recycled session: " + i), isRecycled);
            final String oldest = sm.recycleOldestExpired();
            Assert.assertNotNull(oldest);
            sm.newSession(Integer.toString(i));
        }
    }

    @Test
    public void isReturningOldestExpiredSession() throws InterruptedException {
        final SessionManager sm = new SessionManager();
        sm.setMaxSessionDuration(Duration.ofMillis(10));
        sm.newSession("1");
        Thread.sleep(2);
        sm.newSession("2");
        Thread.sleep(11);
        sm.isActiveAndExpire("1");
        sm.isActiveAndExpire("2");
        Assert.assertEquals("1", sm.recycleOldestExpired());
    }

    @Test
    public void isActiveThenAddSession() throws InterruptedException {
        final SessionManager sm = new SessionManager();
        final String sessionToken = "not-active";
        Assert.assertFalse(sm.isActiveAndExpire(sessionToken));
        sm.newSession(sessionToken);
        Assert.assertTrue(sm.isActiveAndExpire(sessionToken));
    }

    @Test
    public void doesSessionExpire() throws InterruptedException {
        final SessionManager sm = new SessionManager();
        sm.setMaxSessionDuration(Duration.ofMillis(10));
        final String sessionToken = "active";
        sm.newSession(sessionToken);
        Assert.assertTrue(sm.isActiveAndExpire(sessionToken));
        Thread.sleep(11);
        Assert.assertFalse(sm.isActiveAndExpire(sessionToken));
    }
}


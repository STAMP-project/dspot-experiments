/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.session.mgt;


import AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT;
import java.io.Serializable;
import org.apache.shiro.session.ExpiredSessionException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for the {@link DelegatingSession} class.
 */
public class DelegatingSessionTest {
    DelegatingSession session = null;

    DefaultSessionManager sm = null;

    @Test
    public void testTimeout() {
        Serializable origId = session.getId();
        Assert.assertEquals(session.getTimeout(), DEFAULT_GLOBAL_SESSION_TIMEOUT);
        session.touch();
        session.setTimeout(100);
        Assert.assertEquals(100, session.getTimeout());
        sleep(150);
        try {
            session.getTimeout();
            Assert.fail("Session should have expired.");
        } catch (ExpiredSessionException expected) {
        }
    }
}


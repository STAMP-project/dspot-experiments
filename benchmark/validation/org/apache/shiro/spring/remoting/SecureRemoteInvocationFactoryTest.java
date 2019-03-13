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
package org.apache.shiro.spring.remoting;


import SecureRemoteInvocationFactory.SESSION_ID_KEY;
import java.lang.reflect.Method;
import java.util.UUID;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.remoting.support.RemoteInvocation;


/**
 * //TODO - Class JavaDoc!
 */
/* @Test
public void testNonSessionManagerCall() throws Exception {

SecureRemoteInvocationFactory factory = new SecureRemoteInvocationFactory();

MethodInvocation mi = createMock(MethodInvocation.class);
Method method = getMethod("login", SecurityManager.class);
expect(mi.getMethod()).andReturn(method).anyTimes();
}
 */
public class SecureRemoteInvocationFactoryTest {
    @Test
    public void testSessionManagerProxyStartRemoteInvocation() throws Exception {
        SecureRemoteInvocationFactory factory = new SecureRemoteInvocationFactory();
        MethodInvocation mi = createMock(MethodInvocation.class);
        Method startMethod = getMethod("start", SessionManager.class);
        expect(mi.getMethod()).andReturn(startMethod).anyTimes();
        Object[] args = new Object[]{ "localhost" };
        expect(mi.getArguments()).andReturn(args).anyTimes();
        replay(mi);
        RemoteInvocation ri = factory.createRemoteInvocation(mi);
        verify(mi);
        Assert.assertNull(ri.getAttribute(SESSION_ID_KEY));
    }

    @Test
    public void testSessionManagerProxyNonStartRemoteInvocation() throws Exception {
        SecureRemoteInvocationFactory factory = new SecureRemoteInvocationFactory();
        MethodInvocation mi = createMock(MethodInvocation.class);
        Method method = getMethod("getSession", SessionManager.class);
        expect(mi.getMethod()).andReturn(method).anyTimes();
        String dummySessionId = UUID.randomUUID().toString();
        SessionKey sessionKey = new DefaultSessionKey(dummySessionId);
        Object[] args = new Object[]{ sessionKey };
        expect(mi.getArguments()).andReturn(args).anyTimes();
        replay(mi);
        RemoteInvocation ri = factory.createRemoteInvocation(mi);
        verify(mi);
        Assert.assertEquals(dummySessionId, ri.getAttribute(SESSION_ID_KEY));
    }
}


/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal;


import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.geode.distributed.DistributedMember;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static MBeanJMXAdapter.mbeanServer;


/**
 * Unique tests for {@link MBeanJMXAdapter}.
 */
public class MBeanJMXAdapterTest {
    private static final String UNIQUE_ID = "unique-id";

    private ObjectName objectName;

    private MBeanServer mockMBeanServer;

    private DistributedMember distMember;

    @Test
    public void unregisterMBeanInstanceNotFoundMessageLogged() throws Exception {
        // This mocks the race condition where the server indicates that the object is registered,
        // but when we go to unregister it, it has already been unregistered.
        Mockito.when(mockMBeanServer.isRegistered(objectName)).thenReturn(true);
        // Mock unregisterMBean to throw the InstanceNotFoundException, indicating that the MBean
        // has already been unregistered
        Mockito.doThrow(new InstanceNotFoundException()).when(mockMBeanServer).unregisterMBean(objectName);
        MBeanJMXAdapter mBeanJMXAdapter = Mockito.spy(new MBeanJMXAdapter(distMember));
        mbeanServer = mockMBeanServer;
        mBeanJMXAdapter.unregisterMBean(objectName);
        // InstanceNotFoundException should just log a debug message as it is essentially a no-op
        // during unregistration
        Mockito.verify(mBeanJMXAdapter, Mockito.times(1)).logRegistrationWarning(ArgumentMatchers.any(ObjectName.class), ArgumentMatchers.eq(false));
    }

    @Test
    public void registerMBeanProxyInstanceNotFoundMessageLogged() throws Exception {
        // This mocks the race condition where the server indicates that the object is unregistered,
        // but when we go to register it, it has already been register.
        Mockito.when(mockMBeanServer.isRegistered(objectName)).thenReturn(false);
        // Mock unregisterMBean to throw the InstanceAlreadyExistsException, indicating that the MBean
        // has already been unregistered
        Mockito.doThrow(new InstanceAlreadyExistsException()).when(mockMBeanServer).registerMBean(ArgumentMatchers.any(Object.class), ArgumentMatchers.eq(objectName));
        MBeanJMXAdapter mBeanJMXAdapter = Mockito.spy(new MBeanJMXAdapter(distMember));
        mbeanServer = mockMBeanServer;
        mBeanJMXAdapter.registerMBeanProxy(objectName, objectName);
        // InstanceNotFoundException should just log a debug message as it is essentially a no-op
        // during registration
        Mockito.verify(mBeanJMXAdapter, Mockito.times(1)).logRegistrationWarning(ArgumentMatchers.any(ObjectName.class), ArgumentMatchers.eq(true));
    }

    @Test
    public void getMemberNameOrUniqueIdReturnsNameIfProvided() {
        String memberName = "member-name";
        Mockito.when(distMember.getName()).thenReturn(memberName);
        String result = MBeanJMXAdapter.getMemberNameOrUniqueId(distMember);
        assertThat(result).isEqualTo(memberName);
    }

    @Test
    public void getMemberNameOrUniqueIdReturnsUniqueIdIfNameIsNotProvided() {
        Mockito.when(distMember.getName()).thenReturn("");
        String result = MBeanJMXAdapter.getMemberNameOrUniqueId(distMember);
        assertThat(result).isEqualTo(MBeanJMXAdapterTest.UNIQUE_ID);
    }
}


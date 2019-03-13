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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.serveraction.kerberos;


import KDCType.ACTIVE_DIRECTORY;
import KDCType.MIT_KDC;
import KDCType.NONE;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Test;


public class KerberosOperationHandlerFactoryTest {
    private static Injector injector;

    @Test
    public void testForAD() {
        Assert.assertEquals(MITKerberosOperationHandler.class, KerberosOperationHandlerFactoryTest.injector.getInstance(KerberosOperationHandlerFactory.class).getKerberosOperationHandler(MIT_KDC).getClass());
    }

    @Test
    public void testForMIT() {
        Assert.assertEquals(ADKerberosOperationHandler.class, KerberosOperationHandlerFactoryTest.injector.getInstance(KerberosOperationHandlerFactory.class).getKerberosOperationHandler(ACTIVE_DIRECTORY).getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNone() {
        Assert.assertNull(KerberosOperationHandlerFactoryTest.injector.getInstance(KerberosOperationHandlerFactory.class).getKerberosOperationHandler(NONE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNull() {
        Assert.assertNull(KerberosOperationHandlerFactoryTest.injector.getInstance(KerberosOperationHandlerFactory.class).getKerberosOperationHandler(null));
    }
}


/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package org.kaaproject.kaa.server.node.service.thrift;


import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.node.service.initialization.InitializationService;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class KaaNodeThriftServiceImplTest {
    private static InitializationService kaaNodeInitializationService;

    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.thrift.KaaNodeThriftServiceImpl#getServerShortName()}.
     */
    @Test
    public void testGetServerShortName() {
        KaaNodeThriftServiceImpl b = new KaaNodeThriftServiceImpl();
        Assert.assertNotNull(b);
        Assert.assertEquals("kaa-node", b.getServerShortName());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.thrift.KaaNodeThriftServiceImpl#shutdown()}.
     */
    @Test
    public void testShutdown() {
        KaaNodeThriftServiceImpl b = new KaaNodeThriftServiceImpl();
        ReflectionTestUtils.setField(b, "kaaNodeInitializationService", KaaNodeThriftServiceImplTest.kaaNodeInitializationService);
        try {
            b.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Mockito.verify(KaaNodeThriftServiceImplTest.kaaNodeInitializationService, Mockito.timeout(10000)).stop();
    }
}


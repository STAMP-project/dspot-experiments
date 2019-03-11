/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.ssl;


import UnpooledByteBufAllocator.DEFAULT;
import org.junit.Assert;
import org.junit.Test;


public class OpenSslCachingKeyMaterialProviderTest extends OpenSslKeyMaterialProviderTest {
    @Test
    public void testMaterialCached() throws Exception {
        OpenSslKeyMaterialProvider provider = newMaterialProvider(newKeyManagerFactory(), OpenSslKeyMaterialProviderTest.PASSWORD);
        OpenSslKeyMaterial material = provider.chooseKeyMaterial(DEFAULT, OpenSslKeyMaterialProviderTest.EXISTING_ALIAS);
        Assert.assertNotNull(material);
        Assert.assertNotEquals(0, material.certificateChainAddress());
        Assert.assertNotEquals(0, material.privateKeyAddress());
        Assert.assertEquals(2, material.refCnt());
        OpenSslKeyMaterial material2 = provider.chooseKeyMaterial(DEFAULT, OpenSslKeyMaterialProviderTest.EXISTING_ALIAS);
        Assert.assertNotNull(material2);
        Assert.assertEquals(material.certificateChainAddress(), material2.certificateChainAddress());
        Assert.assertEquals(material.privateKeyAddress(), material2.privateKeyAddress());
        Assert.assertEquals(3, material.refCnt());
        Assert.assertEquals(3, material2.refCnt());
        Assert.assertFalse(material.release());
        Assert.assertFalse(material2.release());
        // After this the material should have been released.
        provider.destroy();
        Assert.assertEquals(0, material.refCnt());
        Assert.assertEquals(0, material2.refCnt());
    }
}


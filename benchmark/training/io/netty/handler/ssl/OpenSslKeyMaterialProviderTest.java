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


public class OpenSslKeyMaterialProviderTest {
    static final String PASSWORD = "example";

    static final String EXISTING_ALIAS = "1";

    private static final String NON_EXISTING_ALIAS = "nonexisting";

    @Test
    public void testChooseKeyMaterial() throws Exception {
        OpenSslKeyMaterialProvider provider = newMaterialProvider(newKeyManagerFactory(), OpenSslKeyMaterialProviderTest.PASSWORD);
        OpenSslKeyMaterial nonExistingMaterial = provider.chooseKeyMaterial(DEFAULT, OpenSslKeyMaterialProviderTest.NON_EXISTING_ALIAS);
        Assert.assertNull(nonExistingMaterial);
        OpenSslKeyMaterial material = provider.chooseKeyMaterial(DEFAULT, OpenSslKeyMaterialProviderTest.EXISTING_ALIAS);
        Assert.assertNotNull(material);
        Assert.assertNotEquals(0, material.certificateChainAddress());
        Assert.assertNotEquals(0, material.privateKeyAddress());
        assertRelease(material);
        provider.destroy();
    }
}


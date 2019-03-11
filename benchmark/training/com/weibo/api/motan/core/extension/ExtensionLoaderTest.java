/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.core.extension;


import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 *
 * @author maijunsheng
 * @version ?????2013-5-29
 */
public class ExtensionLoaderTest extends TestCase {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testExtensionNormal() {
        // ?????????????
        Assert.assertEquals(1, ExtensionLoader.getExtensionLoader(SpiTestInterface.class).getExtension("spitest").spiHello());
        Assert.assertEquals(1, ExtensionLoader.getExtensionLoader(SpiTestInterface.class).getExtension("spitest").spiHello());
        // ??????????????????
        Assert.assertEquals(1, ExtensionLoader.getExtensionLoader(SpiPrototypeInterface.class).getExtension("spiPrototypeTest").spiHello());
        Assert.assertEquals(2, ExtensionLoader.getExtensionLoader(SpiPrototypeInterface.class).getExtension("spiPrototypeTest").spiHello());
        // ???????
        Assert.assertEquals(1, ExtensionLoader.getExtensionLoader(SpiPrototypeInterface.class).getExtensions("").size());
        ExtensionLoader loader = ExtensionLoader.getExtensionLoader(SpiPrototypeInterface.class);
        loader.addExtensionClass(SpiPrototypeTestImpl2.class);
        // ???????
        ExtensionLoader.initExtensionLoader(SpiPrototypeInterface.class);
        Assert.assertEquals(1, ExtensionLoader.getExtensionLoader(SpiTestInterface.class).getExtensions("").size());
        Assert.assertEquals(2, ExtensionLoader.getExtensionLoader(SpiPrototypeInterface.class).getExtensions("").size());
    }

    @Test
    public void testExtensionAbNormal() {
        // ????spi?????????
        try {
            ExtensionLoader.getExtensionLoader(ExtensionLoaderTest.NotSpiInterface.class);
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("without @Spi annotation"));
        }
        // ?????????
        try {
            ExtensionLoader.getExtensionLoader(SpiTestImpl.class);
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("is not interface"));
        }
        Assert.assertNull(ExtensionLoader.getExtensionLoader(ExtensionLoaderTest.SpiWithoutImpl.class).getExtension("default"));
    }

    // not spi
    public interface NotSpiInterface {}

    // not impl
    @Spi
    public interface SpiWithoutImpl {}
}


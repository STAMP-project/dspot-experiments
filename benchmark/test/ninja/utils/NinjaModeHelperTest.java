/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.utils;


import NinjaConstant.MODE_DEV;
import NinjaConstant.MODE_KEY_NAME;
import NinjaConstant.MODE_PROD;
import NinjaConstant.MODE_TEST;
import NinjaMode.dev;
import NinjaMode.prod;
import NinjaMode.test;
import org.junit.Assert;
import org.junit.Test;


/**
 * CookieDataCodec and CookieDataCodecTest are imported from Play Framework.
 *
 * Enables us to use the same sessions as Play Framework if
 * the secret is the same.
 *
 * Also really important because we want to make sure that our client
 * side session mechanism is widely used and stable.
 * We don't want to reinvent
 * the wheel of securely encoding / decoding and signing cookie data.
 *
 * All praise goes to Play Framework and their awesome work.
 */
public class NinjaModeHelperTest {
    @Test
    public void testNinjaModeHelperWorksWithNoModeSet() {
        Assert.assertEquals(false, NinjaModeHelper.determineModeFromSystemProperties().isPresent());
        Assert.assertEquals(prod, NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
    }

    @Test
    public void testNinjaModeHelperWorksWithTestSet() {
        System.setProperty(MODE_KEY_NAME, MODE_TEST);
        Assert.assertEquals(test, NinjaModeHelper.determineModeFromSystemProperties().get());
        Assert.assertEquals(test, NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
        System.clearProperty(MODE_KEY_NAME);
    }

    @Test
    public void testNinjaModeHelperWorksWithDevSet() {
        System.setProperty(MODE_KEY_NAME, MODE_DEV);
        Assert.assertEquals(dev, NinjaModeHelper.determineModeFromSystemProperties().get());
        Assert.assertEquals(dev, NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
        System.clearProperty(MODE_KEY_NAME);
    }

    @Test
    public void testNinjaModeHelperWorksWithProdSet() {
        System.setProperty(MODE_KEY_NAME, MODE_PROD);
        Assert.assertEquals(prod, NinjaModeHelper.determineModeFromSystemProperties().get());
        Assert.assertEquals(prod, NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
        System.clearProperty(MODE_KEY_NAME);
    }
}


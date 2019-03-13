/**
 * Copyright (C) 2016 Square, Inc.
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
package okhttp3.internal.platform;


import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class Jdk9PlatformTest {
    @Test
    public void buildsWhenJdk9() {
        Assume.assumeTrue(PlatformTest.getPlatform().equals("jdk9"));
        Assert.assertNotNull(Jdk9Platform.buildIfSupported());
    }

    @Test
    public void findsAlpnMethods() {
        Assume.assumeTrue(PlatformTest.getPlatform().equals("jdk9"));
        Jdk9Platform platform = Jdk9Platform.buildIfSupported();
        Assert.assertEquals("getApplicationProtocol", platform.getProtocolMethod.getName());
        Assert.assertEquals("setApplicationProtocols", platform.setProtocolMethod.getName());
    }

    @Test
    public void testToStringIsClassname() {
        Assert.assertEquals("Jdk9Platform", new Jdk9Platform(null, null).toString());
    }
}


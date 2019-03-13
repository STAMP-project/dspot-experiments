/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JsonNodeConfigurationFactoryProviderTest {
    @Test
    public void testGet_SingleConfig() {
        JsonNodeConfigurationFactoryProviderTest.Bean1 b1 = JsonNodeConfigurationFactoryProviderTest.factory("s: SS\ni: 55").config(JsonNodeConfigurationFactoryProviderTest.Bean1.class, "");
        Assert.assertNotNull(b1);
        Assert.assertEquals("SS", b1.s);
        Assert.assertEquals(55, b1.i);
    }

    @Test
    public void testGet_MultiConfig() {
        JsonNodeConfigurationFactoryProviderTest.Bean1 b1 = JsonNodeConfigurationFactoryProviderTest.factory("s: SS\ni: 55", "l: 12345\ni: 56").config(JsonNodeConfigurationFactoryProviderTest.Bean1.class, "");
        Assert.assertNotNull(b1);
        Assert.assertEquals("SS", b1.s);
        Assert.assertEquals(56, b1.i);
        Assert.assertEquals(12345L, b1.l);
    }

    @Test
    public void testGet_SingleConfig_PropOverride() {
        Map<String, String> overrides = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
                put("s", "SS");
                put("i", "55");
            }
        };
        JsonNodeConfigurationFactoryProviderTest.Bean1 b1 = JsonNodeConfigurationFactoryProviderTest.factory(overrides, "s: replace_me\ni: 5").config(JsonNodeConfigurationFactoryProviderTest.Bean1.class, "");
        Assert.assertNotNull(b1);
        Assert.assertEquals("SS", b1.s);
        Assert.assertEquals(55, b1.i);
    }

    @Test
    public void testGet_SingleConfig_PropOverride_Nested() {
        Map<String, String> overrides = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
                put("b1.s", "SS");
                put("b1.i", "55");
            }
        };
        JsonNodeConfigurationFactoryProviderTest.Bean1 b1 = JsonNodeConfigurationFactoryProviderTest.factory(overrides, "b1:\n  s: replace_me\n  i: 6").config(JsonNodeConfigurationFactoryProviderTest.Bean1.class, "b1");
        Assert.assertNotNull(b1);
        Assert.assertEquals("SS", b1.s);
        Assert.assertEquals(55, b1.i);
    }

    public static class Bean1 {
        private String s;

        private int i;

        private long l;

        public String getS() {
            return s;
        }

        public int getI() {
            return i;
        }

        public long getL() {
            return l;
        }
    }
}


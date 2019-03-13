/**
 * Copyright 2016 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.maven.docker.config;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 12/02/16
 */
public class NetworkingConfigTest {
    @Test
    public void simple() {
        Object[] data = new Object[]{ bridge, null, "BRiDge", "bridge", "true", "false", null, null, host, null, "host", "host", "true", "false", null, null, container, "alpha", "container:alpha", "container:containerId", "true", "false", "alpha", null, null, "blubber", "blubber", "custom", "false", "true", null, "blubber", custom, "blubber", "blubber", "custom", "false", "true", null, "blubber", none, null, "None", "none", "true", "false", null, null };
        for (int i = 0; i < (data.length); i += 8) {
            for (NetworkConfig config : new NetworkConfig[]{ new NetworkConfig(((NetworkConfig.Mode) (data[i])), ((String) (data[(i + 1)]))), new NetworkConfig(((String) (data[(i + 2)]))) }) {
                if (config.isStandardNetwork()) {
                    Assert.assertEquals(data[(i + 3)], config.getStandardMode("containerId"));
                } else {
                    try {
                        config.getStandardMode("fail");
                        Assert.fail(("Test " + (i % 8)));
                    } catch (IllegalArgumentException exp) {
                        // expected
                    }
                }
                Assert.assertEquals(Boolean.parseBoolean(((String) (data[(i + 4)]))), config.isStandardNetwork());
                Assert.assertEquals(Boolean.parseBoolean(((String) (data[(i + 5)]))), config.isCustomNetwork());
                Assert.assertEquals(data[(i + 6)], config.getContainerAlias());
                Assert.assertEquals(data[(i + 7)], config.getCustomNetwork());
            }
        }
    }

    @Test
    public void empty() {
        for (String str : new String[]{ null, "" }) {
            NetworkConfig config = new NetworkConfig(str);
            Assert.assertFalse(config.isStandardNetwork());
            Assert.assertFalse(config.isCustomNetwork());
            Assert.assertNull(config.getContainerAlias());
            Assert.assertNull(config.getCustomNetwork());
        }
    }

    @Test
    public void builder() {
        NetworkConfig config = new NetworkConfig.Builder().build();
        Assert.assertNull(config);
        config = new NetworkConfig.Builder().name("hello").aliases(Arrays.asList("alias1", "alias2")).build();
        Assert.assertTrue(config.isCustomNetwork());
        Assert.assertEquals("hello", config.getCustomNetwork());
        Assert.assertEquals(2, config.getAliases().size());
    }
}


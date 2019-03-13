/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.jsr107;


import java.net.URI;
import java.net.URL;
import java.util.Properties;
import javax.cache.CacheException;
import org.junit.Assert;
import org.junit.Test;


public class DefaultConfigurationResolverTest {
    @Test
    public void testCacheManagerProps() throws Exception {
        URI uri = DefaultConfigurationResolverTest.makeURI();
        Properties props = new Properties();
        props.put(DefaultConfigurationResolver.DEFAULT_CONFIG_PROPERTY_NAME, uri);
        URI resolved = DefaultConfigurationResolver.resolveConfigURI(props);
        Assert.assertSame(uri, resolved);
    }

    @Test
    public void testSystemProperty() throws Exception {
        URI uri = DefaultConfigurationResolverTest.makeURI();
        System.getProperties().put(DefaultConfigurationResolver.DEFAULT_CONFIG_PROPERTY_NAME, uri);
        URI resolved = DefaultConfigurationResolver.resolveConfigURI(new Properties());
        Assert.assertSame(uri, resolved);
    }

    @Test
    public void testCacheManagerPropertiesOverridesSystemProperty() throws Exception {
        URI uri1 = DefaultConfigurationResolverTest.makeURI();
        URI uri2 = DefaultConfigurationResolverTest.makeURI();
        Assert.assertFalse(uri1.equals(uri2));
        Properties props = new Properties();
        props.put(DefaultConfigurationResolver.DEFAULT_CONFIG_PROPERTY_NAME, uri1);
        System.getProperties().put(DefaultConfigurationResolver.DEFAULT_CONFIG_PROPERTY_NAME, uri2);
        URI resolved = DefaultConfigurationResolver.resolveConfigURI(props);
        Assert.assertSame(uri1, resolved);
    }

    @Test
    public void testDefault() throws Exception {
        URI resolved = DefaultConfigurationResolver.resolveConfigURI(new Properties());
        Assert.assertNull(resolved);
    }

    @Test
    public void testURL() throws Exception {
        URL url = new URL("http://www.cheese.com/asiago");
        Properties props = new Properties();
        props.put(DefaultConfigurationResolver.DEFAULT_CONFIG_PROPERTY_NAME, url);
        URI resolved = DefaultConfigurationResolver.resolveConfigURI(props);
        Assert.assertEquals(url.toURI(), resolved);
    }

    @Test
    public void testString() throws Exception {
        String string = "http://www.cheese.com/armenian-string-cheese/";
        Properties props = new Properties();
        props.put(DefaultConfigurationResolver.DEFAULT_CONFIG_PROPERTY_NAME, string);
        URI resolved = DefaultConfigurationResolver.resolveConfigURI(props);
        Assert.assertEquals(new URI(string), resolved);
    }

    @Test
    public void testInvalidType() throws Exception {
        Properties props = new Properties();
        props.put(DefaultConfigurationResolver.DEFAULT_CONFIG_PROPERTY_NAME, this);
        try {
            DefaultConfigurationResolver.resolveConfigURI(props);
            Assert.fail();
        } catch (CacheException ce) {
            // expected
        }
    }
}


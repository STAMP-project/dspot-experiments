/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.compress;


import java.util.Collection;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.compress.gzip.GZIPCompressionProvider;
import org.pentaho.di.core.compress.hadoopsnappy.HadoopSnappyCompressionProvider;
import org.pentaho.di.core.compress.snappy.SnappyCompressionProvider;
import org.pentaho.di.core.compress.zip.ZIPCompressionProvider;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class CompressionProviderFactoryTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public CompressionProviderFactory factory = null;

    @Test
    public void testGetInstance() {
        Assert.assertNotNull(factory);
    }

    @Test
    public void testCreateCoreProviders() {
        CompressionProvider provider = factory.createCompressionProviderInstance("None");
        Assert.assertNotNull(provider);
        Assert.assertTrue(provider.getClass().isAssignableFrom(NoneCompressionProvider.class));
        Assert.assertEquals("None", provider.getName());
        Assert.assertEquals("No compression", provider.getDescription());
        provider = factory.createCompressionProviderInstance("Zip");
        Assert.assertNotNull(provider);
        Assert.assertTrue(provider.getClass().isAssignableFrom(ZIPCompressionProvider.class));
        Assert.assertEquals("Zip", provider.getName());
        Assert.assertEquals("ZIP compression", provider.getDescription());
        provider = factory.createCompressionProviderInstance("GZip");
        Assert.assertNotNull(provider);
        Assert.assertTrue(provider.getClass().isAssignableFrom(GZIPCompressionProvider.class));
        Assert.assertEquals("GZip", provider.getName());
        Assert.assertEquals("GZIP compression", provider.getDescription());
        provider = factory.createCompressionProviderInstance("Snappy");
        Assert.assertNotNull(provider);
        Assert.assertTrue(provider.getClass().isAssignableFrom(SnappyCompressionProvider.class));
        Assert.assertEquals("Snappy", provider.getName());
        Assert.assertEquals("Snappy compression", provider.getDescription());
        provider = factory.createCompressionProviderInstance("Hadoop-snappy");
        Assert.assertNotNull(provider);
        Assert.assertTrue(provider.getClass().isAssignableFrom(HadoopSnappyCompressionProvider.class));
        Assert.assertEquals("Hadoop-snappy", provider.getName());
        Assert.assertEquals("Hadoop Snappy compression", provider.getDescription());
    }

    /**
     * Test that all core compression plugins' expected names (None, Zip, GZip) are available via the factory
     */
    @Test
    public void getCoreProviderNames() {
        @SuppressWarnings("serial")
        final HashMap<String, Boolean> foundProvider = new HashMap<String, Boolean>() {
            {
                put("None", false);
                put("Zip", false);
                put("GZip", false);
                put("Snappy", false);
                put("Hadoop-snappy", false);
            }
        };
        String[] providers = factory.getCompressionProviderNames();
        Assert.assertNotNull(providers);
        for (String provider : providers) {
            Assert.assertNotNull(foundProvider.get(provider));
            foundProvider.put(provider, true);
        }
        boolean foundAllProviders = true;
        for (Boolean b : foundProvider.values()) {
            foundAllProviders = foundAllProviders && b;
        }
        Assert.assertTrue(foundAllProviders);
    }

    /**
     * Test that all core compression plugins (None, Zip, GZip) are available via the factory
     */
    @Test
    public void getCoreProviders() {
        @SuppressWarnings("serial")
        final HashMap<String, Boolean> foundProvider = new HashMap<String, Boolean>() {
            {
                put("None", false);
                put("Zip", false);
                put("GZip", false);
                put("Snappy", false);
                put("Hadoop-snappy", false);
            }
        };
        Collection<CompressionProvider> providers = factory.getCompressionProviders();
        Assert.assertNotNull(providers);
        for (CompressionProvider provider : providers) {
            Assert.assertNotNull(foundProvider.get(provider.getName()));
            foundProvider.put(provider.getName(), true);
        }
        boolean foundAllProviders = true;
        for (Boolean b : foundProvider.values()) {
            foundAllProviders = foundAllProviders && b;
        }
        Assert.assertTrue(foundAllProviders);
    }

    @Test
    public void getNonExistentProvider() {
        CompressionProvider provider = factory.createCompressionProviderInstance("Fake");
        Assert.assertNull(provider);
        provider = factory.getCompressionProviderByName(null);
        Assert.assertNull(provider);
        provider = factory.getCompressionProviderByName("Invalid");
        Assert.assertNull(provider);
    }
}


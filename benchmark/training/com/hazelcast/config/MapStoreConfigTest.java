/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;


import MapStoreConfig.DEFAULT_WRITE_COALESCING;
import Warning.NONFINAL_FIELDS;
import Warning.NULL_FIELDS;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Properties;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapStoreConfigTest extends HazelcastTestSupport {
    MapStoreConfig defaultCfg = new MapStoreConfig();

    MapStoreConfig cfgNotEnabled = new MapStoreConfig().setEnabled(false);

    MapStoreConfig cfgNotWriteCoalescing = new MapStoreConfig().setWriteCoalescing(false);

    MapStoreConfig cfgNonDefaultWriteDelaySeconds = new MapStoreConfig().setWriteDelaySeconds(((MapStoreConfig.DEFAULT_WRITE_DELAY_SECONDS) + 1));

    MapStoreConfig cfgNonDefaultWriteBatchSize = new MapStoreConfig().setWriteBatchSize(((MapStoreConfig.DEFAULT_WRITE_BATCH_SIZE) + 1));

    MapStoreConfig cfgNonNullClassName = new MapStoreConfig().setClassName("some.class");

    MapStoreConfig cfgNonNullOtherClassName = new MapStoreConfig().setClassName("some.class.other");

    MapStoreConfig cfgNonNullFactoryClassName = new MapStoreConfig().setFactoryClassName("factoryClassName");

    MapStoreConfig cfgNonNullOtherFactoryClassName = new MapStoreConfig().setFactoryClassName("some.class.other");

    MapStoreConfig cfgNonNullImplementation = new MapStoreConfig().setImplementation(new Object());

    MapStoreConfig cfgNonNullOtherImplementation = new MapStoreConfig().setImplementation(new Object());

    MapStoreConfig cfgNonNullFactoryImplementation = new MapStoreConfig().setFactoryImplementation(new Object());

    MapStoreConfig cfgNonNullOtherFactoryImplementation = new MapStoreConfig().setFactoryImplementation(new Object());

    MapStoreConfig cfgWithProperties = new MapStoreConfig().setProperty("a", "b");

    MapStoreConfig cfgEagerMode = new MapStoreConfig().setInitialLoadMode(InitialLoadMode.EAGER);

    MapStoreConfig cfgNullMode = new MapStoreConfig().setInitialLoadMode(null);

    @Test
    public void getAsReadOnly() {
        MapStoreConfigReadOnly readOnlyCfg = cfgNonNullClassName.getAsReadOnly();
        Assert.assertEquals("some.class", readOnlyCfg.getClassName());
        Assert.assertEquals(cfgNonNullClassName, readOnlyCfg);
        // also test returning cached read only instance
        Assert.assertEquals(readOnlyCfg, cfgNonNullClassName.getAsReadOnly());
    }

    @Test
    public void getClassName() {
        Assert.assertNull(new MapStoreConfig().getClassName());
    }

    @Test
    public void setClassName() {
        Assert.assertEquals("some.class", cfgNonNullClassName.getClassName());
        Assert.assertEquals(new MapStoreConfig().setClassName("some.class"), cfgNonNullClassName);
    }

    @Test
    public void getFactoryClassName() {
        Assert.assertNull(new MapStoreConfig().getFactoryClassName());
    }

    @Test
    public void setFactoryClassName() {
        Assert.assertEquals("factoryClassName", cfgNonNullFactoryClassName.getFactoryClassName());
        Assert.assertEquals(new MapStoreConfig().setFactoryClassName("factoryClassName"), cfgNonNullFactoryClassName);
    }

    @Test
    public void getWriteDelaySeconds() {
        Assert.assertEquals(MapStoreConfig.DEFAULT_WRITE_DELAY_SECONDS, new MapStoreConfig().getWriteDelaySeconds());
    }

    @Test
    public void setWriteDelaySeconds() {
        Assert.assertEquals(((MapStoreConfig.DEFAULT_WRITE_DELAY_SECONDS) + 1), cfgNonDefaultWriteDelaySeconds.getWriteDelaySeconds());
        Assert.assertEquals(new MapStoreConfig().setWriteDelaySeconds(((MapStoreConfig.DEFAULT_WRITE_DELAY_SECONDS) + 1)), cfgNonDefaultWriteDelaySeconds);
    }

    @Test
    public void getWriteBatchSize() {
        Assert.assertEquals(MapStoreConfig.DEFAULT_WRITE_BATCH_SIZE, new MapStoreConfig().getWriteBatchSize());
    }

    @Test
    public void setWriteBatchSize() {
        Assert.assertEquals(((MapStoreConfig.DEFAULT_WRITE_BATCH_SIZE) + 1), cfgNonDefaultWriteBatchSize.getWriteBatchSize());
        Assert.assertEquals(new MapStoreConfig().setWriteBatchSize(((MapStoreConfig.DEFAULT_WRITE_BATCH_SIZE) + 1)), cfgNonDefaultWriteBatchSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setWriteBatchSize_whenLessThanOne() {
        MapStoreConfig cfg = new MapStoreConfig().setWriteBatchSize((-15));
    }

    @Test
    public void isEnabled() {
        Assert.assertTrue(new MapStoreConfig().isEnabled());
    }

    @Test
    public void setEnabled() {
        Assert.assertFalse(cfgNotEnabled.isEnabled());
        Assert.assertEquals(new MapStoreConfig().setEnabled(false), cfgNotEnabled);
    }

    @Test
    public void setImplementation() {
        Object mapStoreImpl = new Object();
        MapStoreConfig cfg = new MapStoreConfig().setImplementation(mapStoreImpl);
        Assert.assertEquals(mapStoreImpl, cfg.getImplementation());
        Assert.assertEquals(new MapStoreConfig().setImplementation(mapStoreImpl), cfg);
    }

    @Test
    public void getImplementation() {
        Assert.assertNull(new MapStoreConfig().getImplementation());
    }

    @Test
    public void setFactoryImplementation() {
        Object mapStoreFactoryImpl = new Object();
        MapStoreConfig cfg = new MapStoreConfig().setFactoryImplementation(mapStoreFactoryImpl);
        Assert.assertEquals(mapStoreFactoryImpl, cfg.getFactoryImplementation());
        Assert.assertEquals(new MapStoreConfig().setFactoryImplementation(mapStoreFactoryImpl), cfg);
    }

    @Test
    public void getFactoryImplementation() {
        Assert.assertNull(new MapStoreConfig().getFactoryImplementation());
    }

    @Test
    public void setProperty() {
        MapStoreConfig cfg = new MapStoreConfig().setProperty("a", "b");
        Assert.assertEquals("b", cfg.getProperty("a"));
        Assert.assertEquals(new MapStoreConfig().setProperty("a", "b"), cfg);
    }

    @Test
    public void getProperty() {
        Assert.assertNull(new MapStoreConfig().getProperty("a"));
    }

    @Test
    public void getProperties() {
        Assert.assertEquals(new Properties(), new MapStoreConfig().getProperties());
    }

    @Test
    public void setProperties() {
        Properties properties = new Properties();
        properties.put("a", "b");
        MapStoreConfig cfg = new MapStoreConfig().setProperties(properties);
        Assert.assertEquals(properties, cfg.getProperties());
        Assert.assertEquals("b", cfg.getProperty("a"));
        Properties otherProperties = new Properties();
        otherProperties.put("a", "b");
        Assert.assertEquals(new MapStoreConfig().setProperties(otherProperties), cfg);
    }

    @Test
    public void getInitialLoadMode() {
        Assert.assertEquals(InitialLoadMode.LAZY, new MapStoreConfig().getInitialLoadMode());
    }

    @Test
    public void setInitialLoadMode() {
        MapStoreConfig cfg = new MapStoreConfig().setInitialLoadMode(InitialLoadMode.EAGER);
        Assert.assertEquals(InitialLoadMode.EAGER, cfg.getInitialLoadMode());
        Assert.assertEquals(new MapStoreConfig().setInitialLoadMode(InitialLoadMode.EAGER), cfg);
    }

    @Test
    public void isWriteCoalescing() {
        Assert.assertEquals(DEFAULT_WRITE_COALESCING, new MapStoreConfig().isWriteCoalescing());
    }

    @Test
    public void setWriteCoalescing() {
        MapStoreConfig cfg = new MapStoreConfig();
        cfg.setWriteCoalescing(false);
        Assert.assertFalse(cfg.isWriteCoalescing());
        MapStoreConfig otherCfg = new MapStoreConfig();
        otherCfg.setWriteCoalescing(false);
        Assert.assertEquals(otherCfg, cfg);
    }

    @Test
    public void equals_whenNull() {
        MapStoreConfig cfg = new MapStoreConfig();
        Assert.assertFalse(cfg.equals(null));
    }

    @Test
    public void equals_whenSame() {
        MapStoreConfig cfg = new MapStoreConfig();
        Assert.assertTrue(cfg.equals(cfg));
    }

    @Test
    public void equals_whenOtherClass() {
        MapStoreConfig cfg = new MapStoreConfig();
        Assert.assertFalse(cfg.equals(new Object()));
    }

    @Test
    public void testEquals() {
        Assert.assertFalse(defaultCfg.equals(cfgNotEnabled));
        Assert.assertFalse(defaultCfg.equals(cfgNotWriteCoalescing));
        Assert.assertFalse(defaultCfg.equals(cfgNonDefaultWriteDelaySeconds));
        Assert.assertFalse(defaultCfg.equals(cfgNonDefaultWriteBatchSize));
        // class name branches
        Assert.assertFalse(defaultCfg.equals(cfgNonNullClassName));
        Assert.assertFalse(cfgNonNullClassName.equals(cfgNonNullOtherClassName));
        Assert.assertFalse(cfgNonNullClassName.equals(defaultCfg));
        // factory class name branches
        Assert.assertFalse(defaultCfg.equals(cfgNonNullFactoryClassName));
        Assert.assertFalse(cfgNonNullFactoryClassName.equals(cfgNonNullOtherFactoryClassName));
        Assert.assertFalse(cfgNonNullFactoryClassName.equals(defaultCfg));
        // implementation
        Assert.assertFalse(defaultCfg.equals(cfgNonNullImplementation));
        Assert.assertFalse(cfgNonNullImplementation.equals(cfgNonNullOtherImplementation));
        Assert.assertFalse(cfgNonNullImplementation.equals(defaultCfg));
        // factory implementation
        Assert.assertFalse(defaultCfg.equals(cfgNonNullFactoryImplementation));
        Assert.assertFalse(cfgNonNullFactoryImplementation.equals(cfgNonNullOtherFactoryImplementation));
        Assert.assertFalse(cfgNonNullFactoryImplementation.equals(defaultCfg));
        Assert.assertFalse(defaultCfg.equals(cfgWithProperties));
        Assert.assertFalse(defaultCfg.equals(cfgEagerMode));
    }

    @Test
    public void testHashCode() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(defaultCfg.hashCode(), cfgNotEnabled.hashCode());
        Assert.assertNotEquals(defaultCfg.hashCode(), cfgNotWriteCoalescing.hashCode());
        Assert.assertNotEquals(defaultCfg.hashCode(), cfgNonNullClassName.hashCode());
        Assert.assertNotEquals(defaultCfg.hashCode(), cfgNonNullFactoryClassName.hashCode());
        Assert.assertNotEquals(defaultCfg.hashCode(), cfgNonNullImplementation.hashCode());
        Assert.assertNotEquals(defaultCfg.hashCode(), cfgNonNullFactoryImplementation.hashCode());
        Assert.assertNotEquals(defaultCfg.hashCode(), cfgEagerMode.hashCode());
        Assert.assertNotEquals(defaultCfg.hashCode(), cfgNullMode.hashCode());
    }

    @Test
    public void testToString() {
        HazelcastTestSupport.assertContains(defaultCfg.toString(), "MapStoreConfig");
    }

    @Test
    public void testEqualsAndHashCode() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        EqualsVerifier.forClass(MapStoreConfig.class).allFieldsShouldBeUsedExcept("readOnly").suppress(NONFINAL_FIELDS, NULL_FIELDS).withPrefabValues(MapStoreConfigReadOnly.class, new MapStoreConfigReadOnly(cfgNotEnabled), new MapStoreConfigReadOnly(cfgNonNullClassName)).verify();
    }
}


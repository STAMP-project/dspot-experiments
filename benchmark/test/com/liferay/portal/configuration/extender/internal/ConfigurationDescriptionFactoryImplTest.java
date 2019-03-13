/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.configuration.extender.internal;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Dictionary;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Carlos Sierra Andr?s
 */
public class ConfigurationDescriptionFactoryImplTest {
    @Test
    public void testCreateFactoryConfiguration() {
        ConfigurationDescriptionFactory configurationDescriptionFactory = new ConfigurationDescriptionFactoryImpl();
        String configurationContent = "key=value\nanotherKey=anotherValue";
        ConfigurationDescription configurationDescription = configurationDescriptionFactory.create(new PropertiesFileNamedConfigurationContent("factory.pid-config.pid", new ByteArrayInputStream(configurationContent.getBytes(Charset.forName("UTF-8")))));
        Assert.assertTrue((configurationDescription instanceof FactoryConfigurationDescription));
        FactoryConfigurationDescription factoryConfigurationDescription = ((FactoryConfigurationDescription) (configurationDescription));
        Assert.assertEquals("factory.pid", factoryConfigurationDescription.getFactoryPid());
        Assert.assertEquals("config.pid", factoryConfigurationDescription.getPid());
        Supplier<Dictionary<String, Object>> propertiesSupplier = factoryConfigurationDescription.getPropertiesSupplier();
        Dictionary<String, Object> properties = propertiesSupplier.get();
        Assert.assertEquals("value", properties.get("key"));
        Assert.assertEquals("anotherValue", properties.get("anotherKey"));
    }

    @Test
    public void testCreateReturnsNullWhenNotPropertiesFileNamedConfigurationContent() {
        ConfigurationDescriptionFactory configurationDescriptionFactory = new ConfigurationDescriptionFactoryImpl();
        ConfigurationDescription configurationDescription = configurationDescriptionFactory.create(new NamedConfigurationContent() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public String getName() {
                return "aName";
            }
        });
        Assert.assertNull(configurationDescription);
    }

    @Test
    public void testCreateSingleConfiguration() {
        ConfigurationDescriptionFactory configurationDescriptionFactory = new ConfigurationDescriptionFactoryImpl();
        String configurationContent = "key=value\nanotherKey=anotherValue";
        ConfigurationDescription configurationDescription = configurationDescriptionFactory.create(new PropertiesFileNamedConfigurationContent("config.pid", new ByteArrayInputStream(configurationContent.getBytes(Charset.forName("UTF-8")))));
        Assert.assertTrue((configurationDescription instanceof SingleConfigurationDescription));
        SingleConfigurationDescription singleConfigurationDescription = ((SingleConfigurationDescription) (configurationDescription));
        Assert.assertEquals("config.pid", singleConfigurationDescription.getPid());
        Supplier<Dictionary<String, Object>> propertiesSupplier = singleConfigurationDescription.getPropertiesSupplier();
        Dictionary<String, Object> properties = propertiesSupplier.get();
        Assert.assertEquals("value", properties.get("key"));
        Assert.assertEquals("anotherValue", properties.get("anotherKey"));
    }
}


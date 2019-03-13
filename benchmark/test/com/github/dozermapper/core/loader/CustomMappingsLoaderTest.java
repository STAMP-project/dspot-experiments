/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.loader;


import CustomMappingsLoader.ByReferenceConverter;
import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.converters.CustomConverterDescription;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class CustomMappingsLoaderTest extends AbstractDozerTest {
    CustomMappingsLoader loader;

    ArrayList<MappingFileData> data;

    @Test(expected = MappingException.class)
    public void testLoad_MultipleGlobalConfigsFound() {
        data.add(createMappingData(true));
        data.add(createMappingData(true));
        loader.load(data);
        Assert.fail("should have thrown exception");
    }

    @Test
    public void testLoad_DoesNotAddDefaultUUIDConverter_IfCustomConverterSpecified() {
        // Arrange: The user has already configured a custom converter for mapping an UUID to UUID
        Configuration configuration = new Configuration();
        CustomConverterDescription userSpecifiedConverter = new CustomConverterDescription();
        userSpecifiedConverter.setClassA(UUID.class);
        userSpecifiedConverter.setClassB(UUID.class);
        userSpecifiedConverter.setType(CustomConverter.class);
        configuration.getCustomConverters().addConverter(userSpecifiedConverter);
        MappingFileData mappingFileData = new MappingFileData();
        mappingFileData.setConfiguration(configuration);
        data.add(mappingFileData);
        // Act
        loader.load(data);
        // Assert
        List<CustomConverterDescription> customConverters = configuration.getCustomConverters().getConverters();
        Assert.assertEquals("User-specified converter should be in list of converters.", 1, customConverters.size());
        Assert.assertFalse(("User-specified converter should override default by reference " + "converter for UUID to UUID mapping."), customConverters.get(0).getType().equals(ByReferenceConverter.class));
    }
}


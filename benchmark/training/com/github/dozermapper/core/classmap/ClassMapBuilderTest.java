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
package com.github.dozermapper.core.classmap;


import ClassMapBuilder.CollectionMappingGenerator;
import ClassMapBuilder.MapMappingGenerator;
import DozerConstants.SELF_KEYWORD;
import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.classmap.generator.BeanMappingGenerator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ClassMapBuilderTest extends AbstractDozerTest {
    private CollectionMappingGenerator collectionMappingGenerator;

    private MapMappingGenerator mapMappingGenerator;

    private BeanMappingGenerator beanMappingGenerator;

    private Configuration configuration;

    @Test
    public void shouldPrepareMappingsForCollection() {
        ClassMap classMap = new ClassMap(null);
        collectionMappingGenerator.apply(classMap, configuration);
        List<FieldMap> fieldMaps = classMap.getFieldMaps();
        Assert.assertEquals(1, fieldMaps.size());
        Assert.assertEquals(SELF_KEYWORD, fieldMaps.get(0).getSrcFieldName());
        Assert.assertEquals(SELF_KEYWORD, fieldMaps.get(0).getDestFieldName());
    }
}


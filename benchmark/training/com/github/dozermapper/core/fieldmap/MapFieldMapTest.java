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
package com.github.dozermapper.core.fieldmap;


import MappingDirection.ONE_WAY;
import RelationshipType.NON_CUMULATIVE;
import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MapFieldMapTest extends AbstractDozerTest {
    private BeanContainer beanContainer = new BeanContainer();

    private DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);

    private PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory();

    @Test
    public void testConstructor() {
        FieldMap fieldMap = Mockito.mock(FieldMap.class);
        MapFieldMap source = new MapFieldMap(fieldMap, beanContainer, destBeanCreator, propertyDescriptorFactory);
        source.setCopyByReference(true);
        source.setCustomConverter("converter");
        source.setCustomConverterId("coverterId");
        source.setCustomConverterParam("param");
        source.setDestField(new DozerField("name", "type"));
        source.setDestHintContainer(new HintContainer(beanContainer));
        source.setDestDeepIndexHintContainer(new HintContainer(beanContainer));
        source.setMapId("mapId");
        source.setRelationshipType(NON_CUMULATIVE);
        source.setRemoveOrphans(true);
        source.setSrcField(new DozerField("name", "type"));
        source.setSrcHintContainer(new HintContainer(beanContainer));
        source.setSrcDeepIndexHintContainer(new HintContainer(beanContainer));
        source.setType(ONE_WAY);
        MapFieldMap result = new MapFieldMap(source, beanContainer, destBeanCreator, propertyDescriptorFactory);
        Assert.assertEquals(source.isCopyByReference(), result.isCopyByReference());
        Assert.assertEquals(source.getCustomConverter(), result.getCustomConverter());
        Assert.assertEquals(source.getCustomConverterId(), result.getCustomConverterId());
        Assert.assertEquals(source.getCustomConverterParam(), result.getCustomConverterParam());
        Assert.assertEquals(source.getDestField(), result.getDestField());
        Assert.assertEquals(source.getDestHintContainer(), result.getDestHintContainer());
        Assert.assertEquals(source.getDestDeepIndexHintContainer(), result.getDestDeepIndexHintContainer());
        Assert.assertEquals(source.getMapId(), result.getMapId());
        Assert.assertEquals(source.getRelationshipType(), result.getRelationshipType());
        Assert.assertEquals(source.isRemoveOrphans(), result.isRemoveOrphans());
        Assert.assertEquals(source.getSrcField(), result.getSrcField());
        Assert.assertEquals(source.getSrcHintContainer(), result.getSrcHintContainer());
        Assert.assertEquals(source.getSrcDeepIndexHintContainer(), result.getSrcDeepIndexHintContainer());
        Assert.assertEquals(source.getType(), result.getType());
    }
}


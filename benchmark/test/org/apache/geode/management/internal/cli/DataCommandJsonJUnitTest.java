/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.geode.management.internal.cli.dto.Car;
import org.apache.geode.management.internal.cli.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;


public class DataCommandJsonJUnitTest {
    @Test
    public void testCollectionTypesInJson() {
        String json = "{'attributes':{'power':'90hp'},'make':'502.1825','model':'502.1825','colors':['red','white','blue'],'attributeSet':['red','white','blue'], 'attributeArray':['red','white','blue']}";
        Car car = ((Car) (JsonUtil.jsonToObject(json, Car.class)));
        Assert.assertNotNull(car.getAttributeSet());
        Assert.assertTrue(((car.getAttributeSet()) instanceof HashSet));
        Assert.assertEquals(3, car.getAttributeSet().size());
        Assert.assertNotNull(car.getColors());
        Assert.assertTrue(((car.getColors()) instanceof ArrayList));
        Assert.assertEquals(3, car.getColors().size());
        Assert.assertNotNull(car.getAttributes());
        Assert.assertTrue(((car.getAttributes()) instanceof HashMap));
        Assert.assertEquals(1, car.getAttributes().size());
        Assert.assertTrue(car.getAttributes().containsKey("power"));
        Assert.assertNotNull(car.getAttributeArray());
        Assert.assertTrue(((car.getAttributeArray()) instanceof String[]));
        Assert.assertEquals(3, car.getAttributeArray().length);
    }
}


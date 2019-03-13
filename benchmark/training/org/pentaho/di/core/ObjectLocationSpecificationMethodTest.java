/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.core;


import ObjectLocationSpecificationMethod.FILENAME;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ObjectLocationSpecificationMethodTest {
    @Test
    public void testClass() {
        ObjectLocationSpecificationMethod[] values = ObjectLocationSpecificationMethod.values();
        List<String> descriptions = Arrays.asList(FILENAME.getDescriptions());
        for (ObjectLocationSpecificationMethod method : values) {
            Assert.assertNotNull(method.getCode());
            Assert.assertFalse(method.getCode().isEmpty());
            Assert.assertNotNull(method.getDescription());
            Assert.assertFalse(method.getDescription().isEmpty());
            Assert.assertTrue(descriptions.contains(method.getDescription()));
            Assert.assertEquals(ObjectLocationSpecificationMethod.getSpecificationMethodByCode(method.getCode()), method);
            Assert.assertEquals(ObjectLocationSpecificationMethod.getSpecificationMethodByDescription(method.getDescription()), method);
        }
        Assert.assertEquals(values.length, descriptions.size());
        Assert.assertNull(ObjectLocationSpecificationMethod.getSpecificationMethodByCode("Invalid code"));
        Assert.assertNull(ObjectLocationSpecificationMethod.getSpecificationMethodByDescription("Invalid description"));
    }
}


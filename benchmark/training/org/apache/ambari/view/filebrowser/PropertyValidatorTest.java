/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.view.filebrowser;


import PropertyValidator.InvalidPropertyValidationResult;
import PropertyValidator.WEBHDFS_URL;
import ValidationResult.SUCCESS;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.view.ViewInstanceDefinition;
import org.apache.ambari.view.validation.ValidationResult;
import org.junit.Assert;
import org.junit.Test;


public class PropertyValidatorTest {
    @Test
    public void testValidatePropertyWithValidWebhdfsURI() {
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put(WEBHDFS_URL, "webhdfs://host:1234/");
        ViewInstanceDefinition instanceDefinition = getInstanceDef(propertyMap);
        ValidationResult result = new PropertyValidator().validateProperty(WEBHDFS_URL, instanceDefinition, null);
        Assert.assertEquals(result, SUCCESS);
        Assert.assertEquals(result.isValid(), true);
    }

    @Test
    public void testValidatePropertyWithValidHdfsURI() {
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put(WEBHDFS_URL, "hdfs://host:1234/");
        ViewInstanceDefinition instanceDefinition = getInstanceDef(propertyMap);
        ValidationResult result = new PropertyValidator().validateProperty(WEBHDFS_URL, instanceDefinition, null);
        Assert.assertEquals(result, SUCCESS);
        Assert.assertEquals(result.isValid(), true);
    }

    @Test
    public void testValidatePropertyWithLocalFileURI() {
        Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put(WEBHDFS_URL, "file:///");
        ViewInstanceDefinition instanceDefinition = getInstanceDef(propertyMap);
        ValidationResult result = new PropertyValidator().validateProperty(WEBHDFS_URL, instanceDefinition, null);
        Assert.assertEquals(result.getClass(), InvalidPropertyValidationResult.class);
        Assert.assertEquals(result.isValid(), false);
        Assert.assertEquals(result.getDetail(), "Must be valid URL");
    }
}


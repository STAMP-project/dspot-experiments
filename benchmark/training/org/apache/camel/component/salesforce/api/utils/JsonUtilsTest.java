/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce.api.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import org.apache.camel.component.salesforce.api.dto.SObjectDescription;
import org.apache.camel.component.salesforce.dto.generated.Account;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit test for {@link JsonUtils}
 */
public class JsonUtilsTest {
    public static final Logger LOG = LoggerFactory.getLogger(JsonUtilsTest.class);

    @Test
    public void getBasicApiJsonSchema() throws Exception {
        // create basic api dto schema
        JsonUtilsTest.LOG.info("Basic Api Schema...");
        String basicApiJsonSchema = JsonUtils.getBasicApiJsonSchema();
        JsonUtilsTest.LOG.info(basicApiJsonSchema);
        // parse schema to validate
        ObjectMapper objectMapper = JsonUtils.createObjectMapper();
        JsonSchema jsonSchema = objectMapper.readValue(basicApiJsonSchema, JsonSchema.class);
        Assert.assertTrue(jsonSchema.isObjectSchema());
        Assert.assertFalse(getOneOf().isEmpty());
    }

    @Test
    public void getSObjectJsonSchema() throws Exception {
        // create sobject dto schema
        SObjectDescription description = new Account().description();
        JsonUtilsTest.LOG.info("SObject Schema...");
        String sObjectJsonSchema = JsonUtils.getSObjectJsonSchema(description);
        JsonUtilsTest.LOG.info(sObjectJsonSchema);
        // parse schema to validate
        ObjectMapper objectMapper = JsonUtils.createObjectMapper();
        JsonSchema jsonSchema = objectMapper.readValue(sObjectJsonSchema, JsonSchema.class);
        Assert.assertTrue(jsonSchema.isObjectSchema());
        Assert.assertEquals(2, getOneOf().size());
    }
}


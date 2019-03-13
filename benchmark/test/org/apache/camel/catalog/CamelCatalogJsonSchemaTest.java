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
package org.apache.camel.catalog;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CamelCatalogJsonSchemaTest {
    private static final Logger LOG = LoggerFactory.getLogger(CamelCatalogJsonSchemaTest.class);

    private CamelCatalog catalog = new DefaultCamelCatalog();

    @Test
    public void testValidateJsonComponent() throws Exception {
        for (String name : catalog.findComponentNames()) {
            String json = catalog.componentJSonSchema(name);
            CamelCatalogJsonSchemaTest.LOG.info("Validating {} component", name);
            CamelCatalogJsonSchemaTest.LOG.debug("with JSon: {}", json);
            // validate we can parse the json
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(json);
            Assert.assertNotNull(tree);
            Assert.assertTrue(name, tree.has("component"));
            Assert.assertTrue(name, tree.has("componentProperties"));
            Assert.assertTrue(name, tree.has("properties"));
        }
    }

    @Test
    public void testValidateJsonDataFormats() throws Exception {
        for (String name : catalog.findDataFormatNames()) {
            String json = catalog.dataFormatJSonSchema(name);
            CamelCatalogJsonSchemaTest.LOG.info("Validating {} dataformat", name);
            CamelCatalogJsonSchemaTest.LOG.debug("with JSon: {}", json);
            // validate we can parse the json
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(json);
            Assert.assertNotNull(tree);
            Assert.assertTrue(name, tree.has("dataformat"));
            Assert.assertTrue(name, tree.has("properties"));
        }
    }

    @Test
    public void testValidateJsonLanguages() throws Exception {
        for (String name : catalog.findLanguageNames()) {
            String json = catalog.languageJSonSchema(name);
            CamelCatalogJsonSchemaTest.LOG.info("Validating {} language", name);
            CamelCatalogJsonSchemaTest.LOG.debug("with JSon: {}", json);
            // validate we can parse the json
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(json);
            Assert.assertNotNull(tree);
            Assert.assertTrue(name, tree.has("language"));
            Assert.assertTrue(name, tree.has("properties"));
        }
    }

    @Test
    public void testValidateJsonModels() throws Exception {
        for (String name : catalog.findModelNames()) {
            String json = catalog.modelJSonSchema(name);
            CamelCatalogJsonSchemaTest.LOG.info("Validating {} model", name);
            CamelCatalogJsonSchemaTest.LOG.debug("with JSon: {}", json);
            // validate we can parse the json
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(json);
            Assert.assertNotNull(tree);
            Assert.assertTrue(name, tree.has("model"));
            Assert.assertTrue(name, tree.has("properties"));
        }
    }
}


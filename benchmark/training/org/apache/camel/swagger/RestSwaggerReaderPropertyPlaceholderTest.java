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
package org.apache.camel.swagger;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Swagger;
import java.util.List;
import org.apache.camel.impl.DefaultClassResolver;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Ignore("Does not run well on CI due test uses JMX mbeans")
public class RestSwaggerReaderPropertyPlaceholderTest extends CamelTestSupport {
    @Test
    public void testReaderRead() throws Exception {
        BeanConfig config = new BeanConfig();
        config.setHost("localhost:8080");
        config.setSchemes(new String[]{ "http" });
        config.setBasePath("/api");
        RestSwaggerReader reader = new RestSwaggerReader();
        RestSwaggerSupport support = new RestSwaggerSupport();
        List<RestDefinition> rests = support.getRestDefinitions(context.getName());
        Swagger swagger = reader.read(rests, null, config, context.getName(), new DefaultClassResolver());
        assertNotNull(swagger);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(NON_NULL);
        String json = mapper.writeValueAsString(swagger);
        log.info(json);
        assertTrue(json.contains("\"host\" : \"localhost:8080\""));
        assertTrue(json.contains("\"basePath\" : \"/api\""));
        assertTrue(json.contains("\"/hello/bye\""));
        assertTrue(json.contains("\"summary\" : \"To update the greeting message\""));
        assertTrue(json.contains("\"/hello/bye/{name}\""));
        assertTrue(json.contains("\"/hello/hi/{name}\""));
        assertFalse(json.contains("{foo}"));
        assertFalse(json.contains("{bar}"));
        context.stop();
    }
}


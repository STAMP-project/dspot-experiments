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
package org.apache.hadoop.hbase.rest.model;


import MediaType.APPLICATION_JSON_TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import javax.xml.bind.JAXBContext;
import org.apache.hadoop.hbase.rest.provider.JAXBContextResolver;
import org.junit.Assert;
import org.junit.Test;


public abstract class TestModelBase<T> {
    protected String AS_XML;

    protected String AS_PB;

    protected String AS_JSON;

    protected JAXBContext context;

    protected Class<?> clazz;

    protected ObjectMapper mapper;

    protected TestModelBase(Class<?> clazz) throws Exception {
        super();
        this.clazz = clazz;
        context = new JAXBContextResolver().getContext(clazz);
        mapper = new JacksonJaxbJsonProvider().locateMapper(clazz, APPLICATION_JSON_TYPE);
    }

    @Test
    public void testBuildModel() throws Exception {
        checkModel(buildTestModel());
    }

    @Test
    public void testFromPB() throws Exception {
        checkModel(fromPB(AS_PB));
    }

    @Test
    public void testFromXML() throws Exception {
        checkModel(fromXML(AS_XML));
    }

    @Test
    public void testToXML() throws Exception {
        // Uses fromXML to check model because XML element ordering can be random.
        checkModel(fromXML(toXML(buildTestModel())));
    }

    @Test
    public void testToJSON() throws Exception {
        try {
            ObjectNode expObj = mapper.readValue(AS_JSON, ObjectNode.class);
            ObjectNode actObj = mapper.readValue(toJSON(buildTestModel()), ObjectNode.class);
            Assert.assertEquals(expObj, actObj);
        } catch (Exception e) {
            Assert.assertEquals(AS_JSON, toJSON(buildTestModel()));
        }
    }

    @Test
    public void testFromJSON() throws Exception {
        checkModel(fromJSON(AS_JSON));
    }
}


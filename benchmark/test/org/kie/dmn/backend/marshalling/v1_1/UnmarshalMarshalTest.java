/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.backend.marshalling.v1_1;


import java.util.Arrays;
import org.junit.Test;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.extensions.MyTestRegister;
import org.kie.dmn.backend.marshalling.v1_1.xstream.extensions.DecisionServicesExtensionRegister;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UnmarshalMarshalTest {
    protected static final Logger logger = LoggerFactory.getLogger(UnmarshalMarshalTest.class);

    @Test
    public void test0001() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0001-input-data-string.dmn");
    }

    @Test
    public void test0002() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0002-input-data-number.dmn");
    }

    @Test
    public void test0003() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0003-input-data-string-allowed-values.dmn");
    }

    @Test
    public void test0004() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new DecisionServicesExtensionRegister()));
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0004-decision-services.dmn", marshaller);
    }

    @Test
    public void test0004_ns_other_location() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new DecisionServicesExtensionRegister()));
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "0004-decision-services_ns_other_location.dmn", marshaller);
    }

    @Test
    public void test_hardcoded_java_max_call() throws Exception {
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_1/", "hardcoded-java-max-call.dmn");
    }

    @Test
    public void testDish() throws Exception {
        testRoundTrip("", "dish-decision.xml");
    }

    @Test
    public void testDummyRelation() throws Exception {
        testRoundTrip("", "dummy-relation.xml");
    }

    @Test
    public void testCh11() throws Exception {
        testRoundTrip("", "ch11example.xml");
    }

    @Test
    public void testHello_World_semantic_namespace() throws Exception {
        testRoundTrip("", "Hello_World_semantic_namespace.dmn");
    }

    @Test
    public void testHello_World_semantic_namespace_with_extensions() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new MyTestRegister()));
        testRoundTrip("", "Hello_World_semantic_namespace_with_extensions.dmn", marshaller);
    }

    @Test
    public void testHello_World_semantic_namespace_with_extensions_other_ns_location() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new MyTestRegister()));
        testRoundTrip("", "Hello_World_semantic_namespace_with_extensions_other_ns_location.dmn", marshaller);
    }

    @Test
    public void testSemanticNamespace() throws Exception {
        testRoundTrip("", "semantic-namespace.xml");
    }

    @Test
    public void testQNameSerialization() throws Exception {
        testRoundTrip("", "hardcoded_function_definition.dmn");
    }
}


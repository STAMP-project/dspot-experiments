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
package org.kie.dmn.backend.marshalling.v1_2;


import java.io.InputStreamReader;
import java.util.Arrays;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_2.extensions.MyTestRegister;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.backend.marshalling.v1x.XStreamMarshaller;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.dmndi.DMNShape;
import org.kie.dmn.model.api.dmndi.DMNStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UnmarshalMarshalTest {
    private static final StreamSource DMN12_SCHEMA_SOURCE = new StreamSource(UnmarshalMarshalTest.class.getResource("/DMN12.xsd").getFile());

    private static final DMNMarshaller MARSHALLER = new XStreamMarshaller();

    protected static final Logger logger = LoggerFactory.getLogger(UnmarshalMarshalTest.class);

    @Test
    public void testV12_ch11example() throws Exception {
        testRoundTripV12("org/kie/dmn/backend/marshalling/v1_2/", "ch11example.dmn");
    }

    @Test
    public void testV12_ImportName() throws Exception {
        testRoundTripV12("org/kie/dmn/backend/marshalling/v1_2/", "ImportName.dmn");
    }

    @Test
    public void testV12_DecisionService20180911v12() throws Exception {
        // DROOLS-2987 DMN v1.2 marshaller failing marshalling DecisionService node and dmndi:DMNDecisionServiceDividerLine
        testRoundTripV12("org/kie/dmn/backend/marshalling/v1_2/", "DecisionService20180911v12.dmn");
    }

    @Test
    public void testV12_DiamondWithColors() throws Exception {
        testRoundTripV12("org/kie/dmn/backend/marshalling/v1_2/", "diamondWithColors.dmn");
    }

    @Test
    public void testV12_DMNDIDiagramElementExtension() throws Exception {
        testRoundTripV12("org/kie/dmn/backend/marshalling/v1_2/", "DMNDIDiagramElementExtension.dmn");
    }

    @Test
    public void testV12_DMNDIDiagramElementExtension_withContent() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new MyTestRegister()));
        testRoundTrip("org/kie/dmn/backend/marshalling/v1_2/", "DMNDIDiagramElementExtension_withContent.dmn", marshaller, UnmarshalMarshalTest.DMN12_SCHEMA_SOURCE);
    }

    @Test
    public void test_hardcoded_java_max_call() throws Exception {
        testRoundTripV12("org/kie/dmn/backend/marshalling/v1_2/", "hardcoded-java-max-call.dmn");
    }

    @Test
    public void test_FontSize_sharedStyle() throws Exception {
        testRoundTripV12("org/kie/dmn/backend/marshalling/v1_2/", "test-FontSize-sharedStyle.dmn");
        Definitions definitions = UnmarshalMarshalTest.MARSHALLER.unmarshal(new InputStreamReader(this.getClass().getResourceAsStream("test-FontSize-sharedStyle.dmn")));
        DMNShape shape0 = ((DMNShape) (definitions.getDMNDI().getDMNDiagram().get(0).getDMNDiagramElement().get(0)));
        DMNStyle shape0sharedStyle = ((DMNStyle) (shape0.getDMNLabel().getSharedStyle()));
        Assert.assertEquals("LS_4d396200-362f-4939-830d-32d2b4c87042_0", shape0sharedStyle.getId());
        Assert.assertEquals(21.0, shape0sharedStyle.getFontSize(), 0.0);
    }
}


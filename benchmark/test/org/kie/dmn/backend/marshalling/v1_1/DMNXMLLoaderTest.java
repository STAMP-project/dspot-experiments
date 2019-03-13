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


import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import javax.xml.XMLConstants;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.xstream.extensions.DecisionServicesExtensionRegister;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.InputData;
import org.kie.dmn.model.api.LiteralExpression;


public class DMNXMLLoaderTest {
    @Test
    public void testLoadingDefinitions() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newDefaultMarshaller();
        final InputStream is = this.getClass().getResourceAsStream("0001-input-data-string.dmn");
        final InputStreamReader isr = new InputStreamReader(is);
        final Definitions def = DMNMarshaller.unmarshal(isr);
        Assert.assertThat(def, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(def.getName(), CoreMatchers.is("0001-input-data-string"));
        Assert.assertThat(def.getId(), CoreMatchers.is("_0001-input-data-string"));
        Assert.assertThat(def.getNamespace(), CoreMatchers.is("https://github.com/agilepro/dmn-tck"));
        Assert.assertThat(def.getDrgElement().size(), CoreMatchers.is(2));
        Assert.assertThat(def.getDrgElement().get(0), CoreMatchers.is(CoreMatchers.instanceOf(Decision.class)));
        Decision dec = ((Decision) (def.getDrgElement().get(0)));
        Assert.assertThat(dec.getName(), CoreMatchers.is("Greeting Message"));
        Assert.assertThat(dec.getId(), CoreMatchers.is("d_GreetingMessage"));
        Assert.assertThat(dec.getVariable().getName(), CoreMatchers.is("Greeting Message"));
        Assert.assertThat(dec.getVariable().getTypeRef().getPrefix(), CoreMatchers.is("feel"));
        Assert.assertThat(dec.getVariable().getTypeRef().getLocalPart(), CoreMatchers.is("string"));
        Assert.assertThat(dec.getVariable().getTypeRef().getNamespaceURI(), CoreMatchers.is(XMLConstants.NULL_NS_URI));
        Assert.assertThat(dec.getInformationRequirement().size(), CoreMatchers.is(1));
        Assert.assertThat(dec.getInformationRequirement().get(0).getRequiredInput().getHref(), CoreMatchers.is("#i_FullName"));
        Assert.assertThat(dec.getExpression(), CoreMatchers.is(CoreMatchers.instanceOf(LiteralExpression.class)));
        LiteralExpression le = ((LiteralExpression) (dec.getExpression()));
        Assert.assertThat(le.getText(), CoreMatchers.is("\"Hello \" + Full Name"));
        InputData idata = ((InputData) (def.getDrgElement().get(1)));
        Assert.assertThat(idata.getId(), CoreMatchers.is("i_FullName"));
        Assert.assertThat(idata.getName(), CoreMatchers.is("Full Name"));
        Assert.assertThat(idata.getVariable().getName(), CoreMatchers.is("Full Name"));
        Assert.assertThat(idata.getVariable().getTypeRef().getPrefix(), CoreMatchers.is("feel"));
        Assert.assertThat(idata.getVariable().getTypeRef().getLocalPart(), CoreMatchers.is("string"));
        Assert.assertThat(idata.getVariable().getTypeRef().getNamespaceURI(), CoreMatchers.is(XMLConstants.NULL_NS_URI));
    }

    @Test
    public void testLoadingDecisionServices() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new DecisionServicesExtensionRegister()));
        final InputStream is = this.getClass().getResourceAsStream("0004-decision-services.dmn");
        final InputStreamReader isr = new InputStreamReader(is);
        final Definitions def = DMNMarshaller.unmarshal(isr);
        Assert.assertThat(def.getDecisionService().size(), CoreMatchers.is(2));
        DecisionService decisionService1 = def.getDecisionService().get(0);
        Assert.assertThat(decisionService1.getId(), CoreMatchers.is("_70386614-9838-420b-a2ae-ff901ada63fb"));
        Assert.assertThat(decisionService1.getName(), CoreMatchers.is("A Only Knowing B and C"));
        Assert.assertThat(decisionService1.getDescription(), CoreMatchers.is("Description of A (BC)"));
        Assert.assertThat(decisionService1.getOutputDecision().size(), CoreMatchers.is(1));
        Assert.assertThat(decisionService1.getEncapsulatedDecision().size(), CoreMatchers.is(0));
        Assert.assertThat(decisionService1.getInputDecision().size(), CoreMatchers.is(2));
        Assert.assertThat(decisionService1.getInputData().size(), CoreMatchers.is(0));
        Assert.assertThat(decisionService1.getOutputDecision().get(0).getHref(), CoreMatchers.is("#_c2b44706-d479-4ceb-bb74-73589d26dd04"));
        DecisionService decisionService2 = def.getDecisionService().get(1);
        Assert.assertThat(decisionService2.getId(), CoreMatchers.is("_4620ef13-248a-419e-bc68-6b601b725a03"));
        Assert.assertThat(decisionService2.getName(), CoreMatchers.is("A only as output knowing D and E"));
        Assert.assertThat(decisionService2.getOutputDecision().size(), CoreMatchers.is(1));
        Assert.assertThat(decisionService2.getEncapsulatedDecision().size(), CoreMatchers.is(2));
        Assert.assertThat(decisionService2.getInputDecision().size(), CoreMatchers.is(0));
        Assert.assertThat(decisionService2.getInputData().size(), CoreMatchers.is(2));
        Assert.assertThat(decisionService2.getInputData().get(0).getHref(), CoreMatchers.is("#_bcea16fb-6c19-4bde-b37d-73407002c064"));
        Assert.assertThat(decisionService2.getInputData().get(1).getHref(), CoreMatchers.is("#_207b9195-a441-47f2-9414-2fad64b463f9"));
    }

    @Test
    public void testLoadingWithNoDecisionServices() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new DecisionServicesExtensionRegister()));
        final InputStream is = this.getClass().getResourceAsStream("0001-input-data-string.dmn");
        final InputStreamReader isr = new InputStreamReader(is);
        final Definitions def = DMNMarshaller.unmarshal(isr);
        Assert.assertThat(def.getDecisionService().size(), CoreMatchers.is(0));// check if No DecisionServices in extended v1.1 does not NPE.

    }

    @Test
    public void test0004_multiple_extensions() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList(new DecisionServicesExtensionRegister()));
        final InputStream is = this.getClass().getResourceAsStream("0004-decision-services_multiple_extensions.dmn");
        final InputStreamReader isr = new InputStreamReader(is);
        final Definitions def = marshaller.unmarshal(isr);
        Assert.assertThat(def.getExtensionElements().getAny().size(), CoreMatchers.is(1));
        // if arrived here, means it did not fail with exception while trying to unmarshall unknown rss extension element, hence it just skipped it.
    }
}


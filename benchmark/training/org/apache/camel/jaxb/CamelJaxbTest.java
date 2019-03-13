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
package org.apache.camel.jaxb;


import Exchange.CONTENT_TYPE;
import javax.xml.bind.JAXBElement;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.foo.bar.PersonType;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CamelJaxbTest extends CamelTestSupport {
    @Test
    public void testUnmarshalBadCharsWithFiltering() throws Exception {
        String xml = "<Person><firstName>FOO</firstName><lastName>BAR\b</lastName></Person>";
        PersonType expected = new PersonType();
        expected.setFirstName("FOO");
        expected.setLastName("BAR ");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedBodiesReceived(expected);
        template.sendBody("direct:unmarshalFilteringEnabled", xml);
        resultEndpoint.assertIsSatisfied();
    }

    @Test(expected = CamelExecutionException.class)
    public void testUnmarshalBadCharsNoFiltering() throws Exception {
        String xml = "<Person><firstName>FOO</firstName><lastName>BAR\b</lastName></Person>";
        template.sendBody("direct:getJAXBElementValue", xml);
    }

    @Test
    public void testFilterNonXmlChars() throws Exception {
        String xmlUTF = "<Person><firstName>FOO</firstName><lastName>BAR \u20ac </lastName></Person>";
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlUTF;
        PersonType expected = new PersonType();
        expected.setFirstName("FOO");
        expected.setLastName("BAR \u20ac ");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedBodiesReceived(expected);
        template.sendBody("direct:unmarshalFilteringEnabled", xml);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testMarshalBadCharsWithFiltering() throws Exception {
        PersonType person = new PersonType();
        person.setFirstName("foo\u0004");
        person.setLastName("bar");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(1);
        template.sendBody("direct:marshalFilteringEnabled", person);
        resultEndpoint.assertIsSatisfied();
        String body = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody(String.class);
        assertFalse("Non-xml character wasn't replaced", body.contains("\u0004"));
    }

    @Test
    public void testMarshalBadCharsNoFiltering() throws Exception {
        PersonType person = new PersonType();
        person.setFirstName("foo\u0004");
        person.setLastName("bar");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedHeaderReceived(CONTENT_TYPE, "application/xml");
        template.sendBody("direct:marshal", person);
        resultEndpoint.assertIsSatisfied();
        String body = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody(String.class);
        assertTrue("Non-xml character unexpectedly did not get into marshalled contents", body.contains("\u0004"));
    }

    @Test
    public void testMarshalWithSchemaLocation() throws Exception {
        PersonType person = new PersonType();
        person.setFirstName("foo");
        person.setLastName("bar");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedHeaderReceived(CONTENT_TYPE, "application/xml");
        template.sendBody("direct:marshal", person);
        resultEndpoint.assertIsSatisfied();
        String body = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody(String.class);
        assertTrue("We should get the schemaLocation here", body.contains("schemaLocation=\"person.xsd\""));
    }

    @Test
    public void testMarshalWithoutContentType() throws Exception {
        PersonType person = new PersonType();
        person.setFirstName("foo");
        person.setLastName("bar");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedHeaderReceived(CONTENT_TYPE, null);
        template.sendBody("direct:marshalWithoutContentType", person);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testCustomXmlStreamWriter() throws InterruptedException {
        PersonType person = new PersonType();
        person.setFirstName("foo");
        person.setLastName("bar");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(1);
        template.sendBody("direct:marshalCustomWriter", person);
        resultEndpoint.assertIsSatisfied();
        String body = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody(String.class);
        assertTrue("Body did not get processed correctly by custom filter", body.contains("-Foo"));
    }

    @Test
    public void testCustomXmlStreamWriterAndFiltering() throws InterruptedException {
        PersonType person = new PersonType();
        person.setFirstName("foo\u0004");
        person.setLastName("bar");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(1);
        template.sendBody("direct:marshalCustomWriterAndFiltering", person);
        resultEndpoint.assertIsSatisfied();
        String body = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody(String.class);
        assertFalse("Non-xml character unexpectedly did not get into marshalled contents", body.contains("\u0004"));
        assertTrue("Body did not get processed correctly by custom filter", body.contains("-Foo"));
    }

    @Test
    public void testUnmarshal() throws Exception {
        final String xml = "<Person><firstName>FOO</firstName><lastName>BAR</lastName></Person>";
        PersonType expected = new PersonType();
        expected.setFirstName("FOO");
        expected.setLastName("BAR");
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedBodiesReceived(expected);
        resultEndpoint.expectedHeaderReceived("foo", "bar");
        template.sendBodyAndHeader("direct:getJAXBElementValue", xml, "foo", "bar");
        resultEndpoint.assertIsSatisfied();
        resultEndpoint.reset();
        resultEndpoint.expectedMessageCount(1);
        template.sendBody("direct:getJAXBElement", xml);
        resultEndpoint.assertIsSatisfied();
        assertTrue("We should get the JAXBElement here", ((resultEndpoint.getExchanges().get(0).getIn().getBody()) instanceof JAXBElement));
        resultEndpoint.reset();
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedBodiesReceived(expected);
        template.sendBody("direct:unmarshall", xml);
        resultEndpoint.assertIsSatisfied();
    }
}


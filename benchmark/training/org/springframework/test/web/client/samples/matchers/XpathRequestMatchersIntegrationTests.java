/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.test.web.client.samples.matchers;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Test;
import org.springframework.test.web.Person;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


/**
 * Examples of defining expectations on XML request content with XPath expressions.
 *
 * @author Rossen Stoyanchev
 * @see ContentRequestMatchersIntegrationTests
 * @see XmlContentRequestMatchersIntegrationTests
 */
public class XpathRequestMatchersIntegrationTests {
    private static final Map<String, String> NS = Collections.singletonMap("ns", "http://example.org/music/people");

    private MockRestServiceServer mockServer;

    private RestTemplate restTemplate;

    private XpathRequestMatchersIntegrationTests.PeopleWrapper people;

    @Test
    public void testExists() throws Exception {
        String composer = "/ns:people/composers/composer[%s]";
        String performer = "/ns:people/performers/performer[%s]";
        this.mockServer.expect(requestTo("/composers")).andExpect(content().contentType("application/xml")).andExpect(xpath(composer, XpathRequestMatchersIntegrationTests.NS, 1).exists()).andExpect(xpath(composer, XpathRequestMatchersIntegrationTests.NS, 2).exists()).andExpect(xpath(composer, XpathRequestMatchersIntegrationTests.NS, 3).exists()).andExpect(xpath(composer, XpathRequestMatchersIntegrationTests.NS, 4).exists()).andExpect(xpath(performer, XpathRequestMatchersIntegrationTests.NS, 1).exists()).andExpect(xpath(performer, XpathRequestMatchersIntegrationTests.NS, 2).exists()).andRespond(withSuccess());
        executeAndVerify();
    }

    @Test
    public void testDoesNotExist() throws Exception {
        String composer = "/ns:people/composers/composer[%s]";
        String performer = "/ns:people/performers/performer[%s]";
        this.mockServer.expect(requestTo("/composers")).andExpect(content().contentType("application/xml")).andExpect(xpath(composer, XpathRequestMatchersIntegrationTests.NS, 0).doesNotExist()).andExpect(xpath(composer, XpathRequestMatchersIntegrationTests.NS, 5).doesNotExist()).andExpect(xpath(performer, XpathRequestMatchersIntegrationTests.NS, 0).doesNotExist()).andExpect(xpath(performer, XpathRequestMatchersIntegrationTests.NS, 3).doesNotExist()).andRespond(withSuccess());
        executeAndVerify();
    }

    @Test
    public void testString() throws Exception {
        String composerName = "/ns:people/composers/composer[%s]/name";
        String performerName = "/ns:people/performers/performer[%s]/name";
        // Hamcrest..
        // Hamcrest..
        // Hamcrest..
        this.mockServer.expect(requestTo("/composers")).andExpect(content().contentType("application/xml")).andExpect(xpath(composerName, XpathRequestMatchersIntegrationTests.NS, 1).string("Johann Sebastian Bach")).andExpect(xpath(composerName, XpathRequestMatchersIntegrationTests.NS, 2).string("Johannes Brahms")).andExpect(xpath(composerName, XpathRequestMatchersIntegrationTests.NS, 3).string("Edvard Grieg")).andExpect(xpath(composerName, XpathRequestMatchersIntegrationTests.NS, 4).string("Robert Schumann")).andExpect(xpath(performerName, XpathRequestMatchersIntegrationTests.NS, 1).string("Vladimir Ashkenazy")).andExpect(xpath(performerName, XpathRequestMatchersIntegrationTests.NS, 2).string("Yehudi Menuhin")).andExpect(xpath(composerName, XpathRequestMatchersIntegrationTests.NS, 1).string(equalTo("Johann Sebastian Bach"))).andExpect(xpath(composerName, XpathRequestMatchersIntegrationTests.NS, 1).string(startsWith("Johann"))).andExpect(xpath(composerName, XpathRequestMatchersIntegrationTests.NS, 1).string(notNullValue())).andRespond(withSuccess());
        executeAndVerify();
    }

    @Test
    public void testNumber() throws Exception {
        String composerDouble = "/ns:people/composers/composer[%s]/someDouble";
        // Hamcrest..
        // Hamcrest..
        this.mockServer.expect(requestTo("/composers")).andExpect(content().contentType("application/xml")).andExpect(xpath(composerDouble, XpathRequestMatchersIntegrationTests.NS, 1).number(21.0)).andExpect(xpath(composerDouble, XpathRequestMatchersIntegrationTests.NS, 2).number(0.0025)).andExpect(xpath(composerDouble, XpathRequestMatchersIntegrationTests.NS, 3).number(1.6035)).andExpect(xpath(composerDouble, XpathRequestMatchersIntegrationTests.NS, 4).number(Double.NaN)).andExpect(xpath(composerDouble, XpathRequestMatchersIntegrationTests.NS, 1).number(equalTo(21.0))).andExpect(xpath(composerDouble, XpathRequestMatchersIntegrationTests.NS, 3).number(closeTo(1.6, 0.01))).andRespond(withSuccess());
        executeAndVerify();
    }

    @Test
    public void testBoolean() throws Exception {
        String performerBooleanValue = "/ns:people/performers/performer[%s]/someBoolean";
        this.mockServer.expect(requestTo("/composers")).andExpect(content().contentType("application/xml")).andExpect(xpath(performerBooleanValue, XpathRequestMatchersIntegrationTests.NS, 1).booleanValue(false)).andExpect(xpath(performerBooleanValue, XpathRequestMatchersIntegrationTests.NS, 2).booleanValue(true)).andRespond(withSuccess());
        executeAndVerify();
    }

    @Test
    public void testNodeCount() throws Exception {
        // Hamcrest..
        // Hamcrest..
        this.mockServer.expect(requestTo("/composers")).andExpect(content().contentType("application/xml")).andExpect(xpath("/ns:people/composers/composer", XpathRequestMatchersIntegrationTests.NS).nodeCount(4)).andExpect(xpath("/ns:people/performers/performer", XpathRequestMatchersIntegrationTests.NS).nodeCount(2)).andExpect(xpath("/ns:people/composers/composer", XpathRequestMatchersIntegrationTests.NS).nodeCount(equalTo(4))).andExpect(xpath("/ns:people/performers/performer", XpathRequestMatchersIntegrationTests.NS).nodeCount(equalTo(2))).andRespond(withSuccess());
        executeAndVerify();
    }

    @SuppressWarnings("unused")
    @XmlRootElement(name = "people", namespace = "http://example.org/music/people")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class PeopleWrapper {
        @XmlElementWrapper(name = "composers")
        @XmlElement(name = "composer")
        private List<Person> composers;

        @XmlElementWrapper(name = "performers")
        @XmlElement(name = "performer")
        private List<Person> performers;

        public PeopleWrapper() {
        }

        public PeopleWrapper(List<Person> composers, List<Person> performers) {
            this.composers = composers;
            this.performers = performers;
        }

        public List<Person> getComposers() {
            return this.composers;
        }

        public List<Person> getPerformers() {
            return this.performers;
        }
    }
}


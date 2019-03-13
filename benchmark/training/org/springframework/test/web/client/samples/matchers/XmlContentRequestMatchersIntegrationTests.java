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


import java.util.List;
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
 * Examples of defining expectations on XML request content with XMLUnit.
 *
 * @author Rossen Stoyanchev
 * @see ContentRequestMatchersIntegrationTests
 * @see XpathRequestMatchersIntegrationTests
 */
public class XmlContentRequestMatchersIntegrationTests {
    private static final String PEOPLE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + ((((("<people><composers>" + "<composer><name>Johann Sebastian Bach</name><someBoolean>false</someBoolean><someDouble>21.0</someDouble></composer>") + "<composer><name>Johannes Brahms</name><someBoolean>false</someBoolean><someDouble>0.0025</someDouble></composer>") + "<composer><name>Edvard Grieg</name><someBoolean>false</someBoolean><someDouble>1.6035</someDouble></composer>") + "<composer><name>Robert Schumann</name><someBoolean>false</someBoolean><someDouble>NaN</someDouble></composer>") + "</composers></people>");

    private MockRestServiceServer mockServer;

    private RestTemplate restTemplate;

    private XmlContentRequestMatchersIntegrationTests.PeopleWrapper people;

    @Test
    public void testXmlEqualTo() throws Exception {
        this.mockServer.expect(requestTo("/composers")).andExpect(content().contentType("application/xml")).andExpect(content().xml(XmlContentRequestMatchersIntegrationTests.PEOPLE_XML)).andRespond(withSuccess());
        executeAndVerify();
    }

    @Test
    public void testHamcrestNodeMatcher() throws Exception {
        this.mockServer.expect(requestTo("/composers")).andExpect(content().contentType("application/xml")).andExpect(content().node(hasXPath("/people/composers/composer[1]"))).andRespond(withSuccess());
        executeAndVerify();
    }

    @SuppressWarnings("unused")
    @XmlRootElement(name = "people")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class PeopleWrapper {
        @XmlElementWrapper(name = "composers")
        @XmlElement(name = "composer")
        private List<Person> composers;

        public PeopleWrapper() {
        }

        public PeopleWrapper(List<Person> composers) {
            this.composers = composers;
        }

        public List<Person> getComposers() {
            return this.composers;
        }
    }
}


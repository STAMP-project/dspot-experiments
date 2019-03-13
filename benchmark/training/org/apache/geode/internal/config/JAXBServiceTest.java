/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.internal.config;


import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.RegionConfig;
import org.junit.Test;


public class JAXBServiceTest {
    private String xml;

    private CacheConfig unmarshalled;

    private JAXBService service;

    private JAXBService service2;

    @Test
    public void testCacheMarshall() {
        CacheConfig cacheConfig = new CacheConfig();
        JAXBServiceTest.setBasicValues(cacheConfig);
        xml = service.marshall(cacheConfig);
        System.out.println(xml);
        // cache has the default namespace
        assertThat(xml).contains("xmlns=\"http://geode.apache.org/schema/cache\"");
        assertThat(xml).contains("xsi:schemaLocation=\"http://geode.apache.org/schema/cache http://geode.apache.org/schema/cache/cache-1.0.xsd\"");
        assertThat(xml).contains("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        assertThat(xml).contains("</cache>");
        unmarshalled = service.unMarshall(xml);
    }

    @Test
    public void invalidXmlShouldFail() throws Exception {
        CacheConfig cacheConfig = new CacheConfig();
        // missing version attribute
        assertThatThrownBy(() -> service.marshall(cacheConfig)).hasStackTraceContaining("Attribute 'version' must appear on element 'cache'");
    }

    @Test
    public void testCacheOneMarshall() throws Exception {
        CacheConfig cache = new CacheConfig();
        JAXBServiceTest.setBasicValues(cache);
        cache.getCustomCacheElements().add(new JAXBServiceTest.ElementOne("test"));
        xml = service.marshall(cache);
        System.out.println(xml);
        // cache has the default namespace
        assertThat(xml).contains("xmlns=\"http://geode.apache.org/schema/cache\"");
        assertThat(xml).contains("custom-one>");
    }

    @Test
    public void testMixMarshall() throws Exception {
        CacheConfig cache = new CacheConfig();
        JAXBServiceTest.setBasicValues(cache);
        cache.getCustomCacheElements().add(new JAXBServiceTest.ElementOne("testOne"));
        xml = service.marshall(cache);
        System.out.println(xml);
        assertThat(xml).contains("custom-one>");
        unmarshalled = service2.unMarshall(xml);
        unmarshalled.getCustomCacheElements().add(new JAXBServiceTest.ElementTwo("testTwo"));
        // xml generated wtih CacheConfigTwo has both elements in there.
        xml = service.marshall(unmarshalled);
        System.out.println(xml);
        assertThat(xml).contains("custom-one>");
        assertThat(xml).contains("custom-two>");
        assertThat(xml).containsPattern("xmlns=\"http://geode.apache.org/schema/cache\"");
        assertThat(xml).containsPattern("xmlns:ns\\d=\"http://geode.apache.org/schema/CustomOne\"");
        assertThat(xml).containsPattern("xmlns:ns\\d=\"http://geode.apache.org/schema/CustomTwo\"");
    }

    @Test
    public void xmlWithCustomElementsCanBeUnMarshalledByAnotherService() {
        CacheConfig cache = new CacheConfig();
        JAXBServiceTest.setBasicValues(cache);
        cache.getCustomCacheElements().add(new JAXBServiceTest.ElementOne("test"));
        cache.getCustomCacheElements().add(new JAXBServiceTest.ElementTwo("test"));
        String prettyXml = service.marshall(cache);
        System.out.println(prettyXml);
        CacheConfig cacheConfig = service2.unMarshall(prettyXml);
        List elements = cacheConfig.getCustomCacheElements();
        assertThat(elements.get(0)).isNotInstanceOf(JAXBServiceTest.ElementOne.class);
        assertThat(elements.get(1)).isNotInstanceOf(JAXBServiceTest.ElementTwo.class);
        String uglyXml = service2.marshall(cacheConfig);
        System.out.println(uglyXml);
        assertThat(uglyXml).isNotEqualTo(prettyXml);
        // the xml can be unmarshalled correctly by the first service
        String newXml = service.marshall(service.unMarshall(uglyXml));
        assertThat(newXml).isEqualTo(prettyXml);
    }

    @Test
    public void unmarshallPartialElement() {
        String xml = "<region name=\"one\">\n" + ("        <region-attributes scope=\"distributed-ack\" data-policy=\"replicate\"/>\n" + "    </region>");
        RegionConfig config = service2.unMarshall(xml, RegionConfig.class);
        assertThat(config.getName()).isEqualTo("one");
    }

    @Test
    public void unmarshallAnyElement() {
        String xml = "<region name=\"one\">\n" + (("        <region-attributes scope=\"distributed-ack\" data-policy=\"replicate\"/>\n" + "        <custom:any xmlns:custom=\"http://geode.apache.org/schema/custom\" id=\"any\"/>") + "    </region>");
        RegionConfig config = service2.unMarshall(xml, RegionConfig.class);
        assertThat(config.getName()).isEqualTo("one");
        assertThat(config.getCustomRegionElements()).hasSize(1);
    }

    @Test
    public void unmarshallIgnoresUnknownProperties() {
        // say xml has a type attribute that is removed in the new version
        String existingXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + (((((((((("<cache version=\"1.0\" xsi:schemaLocation=\"http://geode.apache.org/schema/cache " + "http://geode.apache.org/schema/cache/cache-1.0.xsd\" ") + "xmlns=\"http://geode.apache.org/schema/cache\" ") + "xmlns:ns2=\"http://geode.apache.org/schema/CustomOne\" ") + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n") + "    <ns2:custom-one>\n") + "        <ns2:id>one</ns2:id>\n") + "        <ns2:type>onetype</ns2:type>\n") + "        <ns2:value>onevalue</ns2:value>\n") + "    </ns2:custom-one>\n") + "</cache>");
        CacheConfig cacheConfig = service.unMarshall(existingXML);
        List elements = cacheConfig.getCustomCacheElements();
        assertThat(elements.get(0)).isInstanceOf(JAXBServiceTest.ElementOne.class);
    }

    @Test
    public void marshalOlderNameSpace() {
        String xml = "<cache xsi:schemaLocation=\"http://schema.pivotal.io/gemfire/cache http://schema.pivotal.io/gemfire/cache/cache-8.1.xsd\"\n" + (((((("       version=\"8.1\"\n" + "       xmlns=\"http://schema.pivotal.io/gemfire/cache\"\n") + "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n") + "    <region name=\"one\">\n") + "        <region-attributes scope=\"distributed-ack\" data-policy=\"replicate\"/>\n") + "    </region>\n") + "</cache>");
        CacheConfig cacheConfig = service2.unMarshall(xml);
        assertThat(cacheConfig.getRegions()).hasSize(1);
        assertThat(cacheConfig.getRegions().get(0).getName()).isEqualTo("one");
    }

    @Test
    public void marshallNoNameSpace() {
        String xml = "<cache version=\"1.0\">\n" + ((("    <region name=\"one\">\n" + "        <region-attributes scope=\"distributed-ack\" data-policy=\"replicate\"/>\n") + "    </region>\n") + "</cache>");
        CacheConfig cacheConfig = service2.unMarshall(xml);
        assertThat(cacheConfig.getRegions()).hasSize(1);
        assertThat(cacheConfig.getRegions().get(0).getName()).isEqualTo("one");
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "id", "value" })
    @XmlRootElement(name = "custom-one", namespace = "http://geode.apache.org/schema/CustomOne")
    public static class ElementOne implements CacheElement {
        @XmlElement(name = "id", namespace = "http://geode.apache.org/schema/CustomOne")
        private String id;

        @XmlElement(name = "value", namespace = "http://geode.apache.org/schema/CustomOne")
        private String value;

        public ElementOne() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public ElementOne(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(String value) {
            this.id = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "id", "value" })
    @XmlRootElement(name = "custom-two", namespace = "http://geode.apache.org/schema/CustomTwo")
    public static class ElementTwo implements CacheElement {
        @XmlElement(name = "id", namespace = "http://geode.apache.org/schema/CustomTwo")
        private String id;

        @XmlElement(name = "value", namespace = "http://geode.apache.org/schema/CustomTwo")
        private String value;

        public ElementTwo() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public ElementTwo(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(String value) {
            this.id = value;
        }
    }
}


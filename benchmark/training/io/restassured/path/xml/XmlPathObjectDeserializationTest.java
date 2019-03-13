/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.path.xml;


import io.restassured.common.mapper.ObjectDeserializationContext;
import io.restassured.path.xml.config.XmlPathConfig;
import io.restassured.path.xml.mapping.XmlPathObjectDeserializer;
import io.restassured.path.xml.support.CoolGreeting;
import io.restassured.path.xml.support.Greeting;
import io.restassured.path.xml.support.Greetings;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static XmlPath.config;


public class XmlPathObjectDeserializationTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final String COOL_GREETING = "<cool><greeting><firstName>John</firstName>\n" + ("      <lastName>Doe</lastName>\n" + "    </greeting></cool>");

    public static final String GREETINGS = "<greetings>\n" + (((((((((((("\t<greeting>\n" + "\t\t<firstName>John</firstName>\n") + "\t\t<lastName>Doe</lastName>\n") + "\t</greeting>\n") + "\t<greeting>\n") + "\t\t<firstName>Jane</firstName>\n") + "\t\t<lastName>Doe</lastName>\n") + "\t</greeting>\n") + "\t<greeting>\n") + "\t\t<firstName>Some</firstName>\n") + "\t\t<lastName>One</lastName>\n") + "\t</greeting>\n") + "</greetings>");

    @Test
    public void deserializes_single_sub_node_using_jaxb() {
        // When
        final Greeting greeting = XmlPath.from(XmlPathObjectDeserializationTest.COOL_GREETING).getObject("cool.greeting", Greeting.class);
        // Then
        Assert.assertThat(greeting.getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(greeting.getLastName(), Matchers.equalTo("Doe"));
    }

    @Test
    public void deserializes_root_node_using_jaxb() {
        // When
        final CoolGreeting greeting = XmlPath.from(XmlPathObjectDeserializationTest.COOL_GREETING).getObject("cool", CoolGreeting.class);
        // Then
        Assert.assertThat(greeting.getGreeting().getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(greeting.getGreeting().getLastName(), Matchers.equalTo("Doe"));
    }

    @Test
    public void deserializes_another_sub_node_using_jaxb() {
        // When
        final Greeting greeting = XmlPath.from(XmlPathObjectDeserializationTest.GREETINGS).getObject("greetings.greeting[0]", Greeting.class);
        // Then
        Assert.assertThat(greeting.getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(greeting.getLastName(), Matchers.equalTo("Doe"));
    }

    @Test
    public void deserializes_xml_document_including_list_using_jaxb() {
        // When
        final Greetings greetings = XmlPath.from(XmlPathObjectDeserializationTest.GREETINGS).getObject("greetings", Greetings.class);
        // Then
        Assert.assertThat(greetings.getGreeting().size(), Matchers.is(3));
    }

    @Test
    public void cannot_deserialize_list_when_using_getObject() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Failed to convert XML to Java Object. If you're trying convert to a list then use the getList method instead.");
        // When
        final List<Greeting> greetings = XmlPath.from(XmlPathObjectDeserializationTest.GREETINGS).getObject("greetings.greeting", List.class);
        // Then
        Assert.assertThat(greetings.size(), Matchers.is(3));
    }

    @Test
    public void deserializes_list_using_getList() {
        // When
        final List<Greeting> greetings = XmlPath.from(XmlPathObjectDeserializationTest.GREETINGS).getList("greetings.greeting", Greeting.class);
        // Then
        Assert.assertThat(greetings.size(), Matchers.is(3));
    }

    @Test
    public void xml_path_supports_custom_deserializer() {
        // Given
        final AtomicBoolean customDeserializerUsed = new AtomicBoolean(false);
        final XmlPath xmlPath = new XmlPath(XmlPathObjectDeserializationTest.COOL_GREETING).using(XmlPathConfig.xmlPathConfig().defaultObjectDeserializer(new XmlPathObjectDeserializer() {
            public <T> T deserialize(ObjectDeserializationContext ctx) {
                customDeserializerUsed.set(true);
                final String xml = ctx.getDataToDeserialize().asString();
                final Greeting greeting = new Greeting();
                greeting.setFirstName(StringUtils.substringBetween(xml, "<firstName>", "</firstName>"));
                greeting.setLastName(StringUtils.substringBetween(xml, "<lastName>", "</lastName>"));
                return ((T) (greeting));
            }
        }));
        // When
        final Greeting greeting = xmlPath.getObject("", Greeting.class);
        // Then
        Assert.assertThat(greeting.getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(greeting.getLastName(), Matchers.equalTo("Doe"));
        Assert.assertThat(customDeserializerUsed.get(), Matchers.is(true));
    }

    @Test
    public void xml_path_supports_custom_deserializer_using_static_configuration() {
        // Given
        final AtomicBoolean customDeserializerUsed = new AtomicBoolean(false);
        config = XmlPathConfig.xmlPathConfig().defaultObjectDeserializer(new XmlPathObjectDeserializer() {
            public <T> T deserialize(ObjectDeserializationContext ctx) {
                customDeserializerUsed.set(true);
                final String xml = ctx.getDataToDeserialize().asString();
                final Greeting greeting = new Greeting();
                greeting.setFirstName(StringUtils.substringBetween(xml, "<firstName>", "</firstName>"));
                greeting.setLastName(StringUtils.substringBetween(xml, "<lastName>", "</lastName>"));
                return ((T) (greeting));
            }
        });
        // When
        try {
            final XmlPath xmlPath = new XmlPath(XmlPathObjectDeserializationTest.COOL_GREETING);
            final Greeting greeting = xmlPath.getObject("", Greeting.class);
            // Then
            Assert.assertThat(greeting.getFirstName(), Matchers.equalTo("John"));
            Assert.assertThat(greeting.getLastName(), Matchers.equalTo("Doe"));
            Assert.assertThat(customDeserializerUsed.get(), Matchers.is(true));
        } finally {
            XmlPath.reset();
        }
    }
}


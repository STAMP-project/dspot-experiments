/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.configurationdocs;


import org.junit.Test;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;


/**
 *
 *
 * @author Brian Clozel
 */
public class SingleKeyEntryTests {
    private static String NEWLINE = System.lineSeparator();

    @Test
    public void simpleProperty() {
        ConfigurationMetadataProperty property = new ConfigurationMetadataProperty();
        property.setId("spring.test.prop");
        property.setDefaultValue("something");
        property.setDescription("This is a description.");
        property.setType("java.lang.String");
        SingleKeyEntry entry = new SingleKeyEntry(property);
        StringBuilder builder = new StringBuilder();
        entry.writeAsciidoc(builder);
        assertThat(builder.toString()).isEqualTo(((((("|`+spring.test.prop+`" + (SingleKeyEntryTests.NEWLINE)) + "|`+something+`") + (SingleKeyEntryTests.NEWLINE)) + "|+++This is a description.+++") + (SingleKeyEntryTests.NEWLINE)));
    }

    @Test
    public void noDefaultValue() {
        ConfigurationMetadataProperty property = new ConfigurationMetadataProperty();
        property.setId("spring.test.prop");
        property.setDescription("This is a description.");
        property.setType("java.lang.String");
        SingleKeyEntry entry = new SingleKeyEntry(property);
        StringBuilder builder = new StringBuilder();
        entry.writeAsciidoc(builder);
        assertThat(builder.toString()).isEqualTo(((((("|`+spring.test.prop+`" + (SingleKeyEntryTests.NEWLINE)) + "|") + (SingleKeyEntryTests.NEWLINE)) + "|+++This is a description.+++") + (SingleKeyEntryTests.NEWLINE)));
    }

    @Test
    public void defaultValueWithPipes() {
        ConfigurationMetadataProperty property = new ConfigurationMetadataProperty();
        property.setId("spring.test.prop");
        property.setDefaultValue("first|second");
        property.setDescription("This is a description.");
        property.setType("java.lang.String");
        SingleKeyEntry entry = new SingleKeyEntry(property);
        StringBuilder builder = new StringBuilder();
        entry.writeAsciidoc(builder);
        assertThat(builder.toString()).isEqualTo(((((((("|`+spring.test.prop+`" + (SingleKeyEntryTests.NEWLINE)) + "|`+first{vbar}") + (SingleKeyEntryTests.NEWLINE)) + "second+`") + (SingleKeyEntryTests.NEWLINE)) + "|+++This is a description.+++") + (SingleKeyEntryTests.NEWLINE)));
    }

    @Test
    public void defaultValueWithBackslash() {
        ConfigurationMetadataProperty property = new ConfigurationMetadataProperty();
        property.setId("spring.test.prop");
        property.setDefaultValue("first\\second");
        property.setDescription("This is a description.");
        property.setType("java.lang.String");
        SingleKeyEntry entry = new SingleKeyEntry(property);
        StringBuilder builder = new StringBuilder();
        entry.writeAsciidoc(builder);
        assertThat(builder.toString()).isEqualTo(((((("|`+spring.test.prop+`" + (SingleKeyEntryTests.NEWLINE)) + "|`+first\\\\second+`") + (SingleKeyEntryTests.NEWLINE)) + "|+++This is a description.+++") + (SingleKeyEntryTests.NEWLINE)));
    }

    @Test
    public void mapProperty() {
        ConfigurationMetadataProperty property = new ConfigurationMetadataProperty();
        property.setId("spring.test.prop");
        property.setDescription("This is a description.");
        property.setType("java.util.Map<java.lang.String,java.lang.String>");
        SingleKeyEntry entry = new SingleKeyEntry(property);
        StringBuilder builder = new StringBuilder();
        entry.writeAsciidoc(builder);
        assertThat(builder.toString()).isEqualTo(((((("|`+spring.test.prop.*+`" + (SingleKeyEntryTests.NEWLINE)) + "|") + (SingleKeyEntryTests.NEWLINE)) + "|+++This is a description.+++") + (SingleKeyEntryTests.NEWLINE)));
    }

    @Test
    public void listProperty() {
        String[] defaultValue = new String[]{ "first", "second", "third" };
        ConfigurationMetadataProperty property = new ConfigurationMetadataProperty();
        property.setId("spring.test.prop");
        property.setDescription("This is a description.");
        property.setType("java.util.List<java.lang.String>");
        property.setDefaultValue(defaultValue);
        SingleKeyEntry entry = new SingleKeyEntry(property);
        StringBuilder builder = new StringBuilder();
        entry.writeAsciidoc(builder);
        assertThat(builder.toString()).isEqualTo(((((((((("|`+spring.test.prop+`" + (SingleKeyEntryTests.NEWLINE)) + "|`+first,") + (SingleKeyEntryTests.NEWLINE)) + "second,") + (SingleKeyEntryTests.NEWLINE)) + "third+`") + (SingleKeyEntryTests.NEWLINE)) + "|+++This is a description.+++") + (SingleKeyEntryTests.NEWLINE)));
    }
}


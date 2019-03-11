/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util.xml;


import java.math.BigDecimal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;


/**
 * Tests for <code>{@link XmlStringPrettyFormatter#xmlPrettyFormat(String)}</code>.
 *
 * @author Joel Costigliola
 */
public class XmlStringPrettyFormatter_prettyFormat_Test {
    private final BigDecimal javaVersion = new BigDecimal(System.getProperty("java.specification.version"));

    private String expected_formatted_xml;

    @Test
    public void should_format_xml_string_prettily() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel><title>Java Tutorials and Examples 1</title><language>en-us</language></channel></rss>";
        Assertions.assertThat(XmlStringPrettyFormatter.xmlPrettyFormat(xmlString)).isEqualTo(expected_formatted_xml);
    }

    @Test
    public void should_format_xml_string_without_xml_declaration_prettily() {
        String xmlString = "<rss version=\"2.0\"><channel><title>Java Tutorials and Examples 1</title><language>en-us</language></channel></rss>";
        if ((javaVersion.compareTo(new BigDecimal("9"))) >= 0) {
            Assertions.assertThat(XmlStringPrettyFormatter.xmlPrettyFormat(xmlString)).isEqualTo(expected_formatted_xml.substring("<?xml version='1.0' encoding='UTF-8'?>".length()));
        } else {
            Assertions.assertThat(XmlStringPrettyFormatter.xmlPrettyFormat(xmlString)).isEqualTo(expected_formatted_xml.substring("<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n".length()));
        }
    }

    @Test
    public void should_format_xml_string_with_space_and_newline_prettily() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel>  <title>Java Tutorials and Examples 1</title>  \n\n<language>en-us</language>  </channel></rss>";
        Assertions.assertThat(XmlStringPrettyFormatter.xmlPrettyFormat(xmlString)).isEqualTo(expected_formatted_xml);
    }

    @Test
    public void should_throw_error_when_xml_string_is_null() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> xmlPrettyFormat(null)).withMessageStartingWith("Expecting XML String not to be null");
    }

    @Test
    public void should_throw_error_when_xml_string_is_not_valid() {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel><title>Java Tutorials and Examples 1</title><language>en-us</language></chnel></rss>";
        try {
            XmlStringPrettyFormatter.xmlPrettyFormat(xmlString);
        } catch (Exception e) {
            Assertions.assertThat(e).isInstanceOf(RuntimeException.class).hasMessageStartingWith("Unable to format XML string");
            Assertions.assertThat(e).hasRootCauseInstanceOf(SAXParseException.class);
            Assertions.assertThat(e.getCause()).hasMessageContaining("The element type \"channel\" must be terminated by the matching end-tag \"</channel>\"");
        }
    }
}


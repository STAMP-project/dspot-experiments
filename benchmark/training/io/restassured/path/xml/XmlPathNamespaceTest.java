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


import io.restassured.path.xml.config.XmlPathConfig;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class XmlPathNamespaceTest {
    @Test
    public void xml_path_supports_namespaces_when_declared_correctly() {
        // Given
        String xml = "<foo xmlns:ns=\"http://localhost/\">\n" + (("      <bar>sudo </bar>\n" + "      <ns:bar>make me a sandwich!</ns:bar>\n") + "    </foo>");
        // When
        XmlPath xmlPath = new XmlPath(xml).using(XmlPathConfig.xmlPathConfig().declaredNamespace("ns", "http://localhost/"));
        // Then
        Assert.assertThat(xmlPath.getString("foo.bar.text()"), Matchers.equalTo("sudo make me a sandwich!"));
        Assert.assertThat(xmlPath.getString(":foo.:bar.text()"), Matchers.equalTo("sudo "));
        Assert.assertThat(xmlPath.getString(":foo.ns:bar.text()"), Matchers.equalTo("make me a sandwich!"));
    }

    @Test
    public void xml_path_doesnt_support_namespaces_when_not_declared() {
        // Given
        String xml = "<foo xmlns:ns=\"http://localhost/\">\n" + (("      <bar>sudo </bar>\n" + "      <ns:bar>make me a sandwich!</ns:bar>\n") + "    </foo>");
        // When
        XmlPath xmlPath = new XmlPath(xml);
        // Then
        Assert.assertThat(xmlPath.getString("foo.bar.text()"), Matchers.equalTo("sudo make me a sandwich!"));
        Assert.assertThat(xmlPath.getString(":foo.:bar.text()"), Matchers.equalTo("sudo "));
        Assert.assertThat(xmlPath.getString(":foo.ns:bar.text()"), Matchers.equalTo(""));
    }

    @Test
    public void xml_path_supports_declared_namespaces() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((((((((((("<x:response xmlns:x=\"http://something.com/test\" note=\"something\">\n" + "    <x:container cont_id=\"some_id\">\n") + "        <x:item id=\"i_1\">\n") + "            <x:name>first</x:name>\n") + "        </x:item>\n") + "        <x:item id=\"i_2\">\n") + "            <x:name>second</x:name>\n") + "        </x:item>\n") + "        <x:item id=\"i_3\">\n") + "            <x:name>third</x:name>\n") + "        </x:item>\n") + "        <item id=\"i_4\">\n") + "            <name>fourth</name>\n") + "        </item>\n") + "    </x:container>\n") + "</x:response>");
        // When
        XmlPath xmlPath = new XmlPath(xml).using(XmlPathConfig.xmlPathConfig().declaredNamespace("x", "http://something.com/test"));
        // Then
        Assert.assertThat(xmlPath.getString("x:response.'x:container'.'x:item'[0].x:name"), Matchers.equalTo("first"));
        Assert.assertThat(xmlPath.getString("response.container.':item'[0].name"), Matchers.equalTo("fourth"));
        Assert.assertThat(xmlPath.getString("x:response.'x:container'.'x:item'[3].x:name"), Matchers.isEmptyOrNullString());
        Assert.assertThat(xmlPath.getString("'x:response'.'x:container'.'x:item'[3].x:name"), Matchers.isEmptyOrNullString());
    }

    @Test
    public void xml_path_supports_declared_namespaces_without_manual_escaping() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((((((((((("<x:response xmlns:x=\"http://something.com/test\" note=\"something\">\n" + "    <x:container cont_id=\"some_id\">\n") + "        <x:item id=\"i_1\">\n") + "            <x:name>first</x:name>\n") + "        </x:item>\n") + "        <x:item id=\"i_2\">\n") + "            <x:name>second</x:name>\n") + "        </x:item>\n") + "        <x:item id=\"i_3\">\n") + "            <x:name>third</x:name>\n") + "        </x:item>\n") + "        <item id=\"i_4\">\n") + "            <name>fourth</name>\n") + "        </item>\n") + "    </x:container>\n") + "</x:response>");
        // When
        XmlPath xmlPath = new XmlPath(xml).using(XmlPathConfig.xmlPathConfig().declaredNamespace("x", "http://something.com/test"));
        // Then
        Assert.assertThat(xmlPath.getString("x:response.x:container.x:item[0].x:name"), Matchers.equalTo("first"));
        Assert.assertThat(xmlPath.getString("response.container.:item[0].name"), Matchers.equalTo("fourth"));
        Assert.assertThat(xmlPath.getString("x:response.x:container.x:item[3].x:name"), Matchers.isEmptyOrNullString());
        Assert.assertThat(xmlPath.getString("'x:response'.x:container.x:item[3].x:name"), Matchers.isEmptyOrNullString());
    }

    @Test
    public void can_configure_namespace_aware_to_false() {
        // Given
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" + (((((((((("  <soapenv:Body>\n" + "    <ns1:getBankResponse xmlns:ns1=\"http://thomas-bayer.com/blz/\">\n") + "      <ns1:details>\n") + "        <ns1:bezeichnung>ABK-Kreditbank</ns1:bezeichnung>\n") + "        <ns1:bic>ABKBDEB1XXX</ns1:bic>\n") + "        <ns1:ort>Berlin</ns1:ort>\n") + "        <ns1:plz>10789</ns1:plz>\n") + "      </ns1:details>\n") + "    </ns1:getBankResponse>\n") + "  </soapenv:Body>\n") + "</soapenv:Envelope>");
        // When
        XmlPath xmlPath = new XmlPath(xml).using(XmlPathConfig.xmlPathConfig().namespaceAware(false));
        Assert.assertThat(xmlPath.getString("soapenv:Envelope.soapenv:Body.ns1:getBankResponse.@xmlns:ns1"), Matchers.equalTo("http://thomas-bayer.com/blz/"));
    }
}


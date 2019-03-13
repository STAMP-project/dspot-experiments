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
package io.restassured.itest.java;


import io.restassured.itest.java.support.WithJetty;
import io.restassured.path.xml.XmlPath;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;


public class NamespaceResponseParsingITest extends WithJetty {
    @Test
    public void takes_namespaces_into_account_when_correct_namespace_is_declared() {
        XmlPath xmlPath = given().config(xmlConfig(xmlConfig().declareNamespace("ns", "http://localhost/"))).when().get("/namespace-example").xmlPath();
        Assert.assertThat(xmlPath.getString("foo.bar.text()"), Matchers.equalTo("sudo make me a sandwich!"));
        Assert.assertThat(xmlPath.getString(":foo.:bar.text()"), Matchers.equalTo("sudo "));
        Assert.assertThat(xmlPath.getString("foo.ns:bar.text()"), Matchers.equalTo("make me a sandwich!"));
    }

    @Test
    public void doesnt_take_namespaces_into_account_when_no_namespace_is_declared() {
        XmlPath xmlPath = get("/namespace-example").xmlPath();
        Assert.assertThat(xmlPath.getString("foo.bar.text()"), Matchers.equalTo("sudo make me a sandwich!"));
        Assert.assertThat(xmlPath.getString(":foo.:bar.text()"), Matchers.equalTo("sudo "));
        Assert.assertThat(xmlPath.getString("foo.ns:bar.text()"), Matchers.equalTo(""));
    }

    @Test
    public void takes_namespaces_into_when_passing_xml_path_config_to_xml_path_method_in_response_object() {
        final XmlPath xmlPath = get("/namespace-example").xmlPath(io.restassured.path.xml.config.XmlPathConfig.xmlPathConfig().with().declaredNamespace("ns", "http://localhost/"));
        Assert.assertThat(xmlPath.getString("foo.bar.text()"), Matchers.equalTo("sudo make me a sandwich!"));
        Assert.assertThat(xmlPath.getString(":foo.:bar.text()"), Matchers.equalTo("sudo "));
        Assert.assertThat(xmlPath.getString("foo.ns:bar.text()"), Matchers.equalTo("make me a sandwich!"));
    }
}


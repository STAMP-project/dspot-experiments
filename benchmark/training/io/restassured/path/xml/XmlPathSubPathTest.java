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


import io.restassured.path.xml.element.Node;
import io.restassured.path.xml.element.NodeChildren;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class XmlPathSubPathTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final String XML = "<shopping>\n" + ((((((((((((((((((((((((((((("      <category type=\"groceries\">\n" + "        <item>\n") + "\t   <name>Chocolate</name>\n") + "           <price>10</") + "price>\n") + "") + "   ") + "\t</item>\n") + "        <item>\n") + "\t   <name>Coffee</name>\n") + "           <price>20</price>\n") + "\t</item>\n") + "      </category>\n") + "      <category type=\"supplies\">\n") + "        <item>\n") + "\t   <name>Paper</name>\n") + "           <price>5</price>\n") + "\t</item>\n") + "        <item quantity=\"4\">\n") + "           <name>Pens</name>\n") + "           <price>15.5</price>\n") + "\t</item>\n") + "      </category>\n") + "      <category type=\"present\">\n") + "        <item when=\"Aug 10\">\n") + "           <name>Kathryn\'s Birthday</name>\n") + "           <price>200</price>\n") + "        </item>\n") + "      </category>\n") + "</shopping>");

    @Test
    public void subpath_works_for_lists() {
        Node category = XmlPath.with(XmlPathSubPathTest.XML).get("shopping");
        final NodeChildren names = category.getPath("category[0].item.name");
        Assert.assertThat(names, Matchers.hasItems("Chocolate", "Coffee"));
    }

    @Test
    public void subpath_with_explicit_type() {
        Node category = XmlPath.with(XmlPathSubPathTest.XML).get("shopping");
        final float firstPrice = category.getPath("category[0].item.price[0]", float.class);
        Assert.assertThat(firstPrice, Matchers.is(10.0F));
    }

    @Test
    public void error_messages_on_invalid_subpath_looks_ok() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(String.format(("Invalid path:%n" + (((("unexpected token: [ @ line 1, column 49.%n" + "   category[0].item.price.[0]%n") + "                          ^%n") + "%n") + "1 error"))));
        Node category = XmlPath.with(XmlPathSubPathTest.XML).get("shopping");
        final float firstPrice = category.getPath("category[0].item.price.[0]", float.class);
        Assert.assertThat(firstPrice, Matchers.is(10.0F));
    }
}


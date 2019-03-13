/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.util;


import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.junit.Assert;
import org.junit.Test;


public class NameHelperTest {
    private static final ObjectNode NODE = JsonNodeFactory.instance.objectNode();

    private final NameHelper nameHelper = new NameHelper(new DefaultGenerationConfig());

    @Test
    public void testGetterNamedCorrectly() {
        Assert.assertThat(nameHelper.getGetterName("foo", new JCodeModel().BOOLEAN, NameHelperTest.NODE), is("isFoo"));
        Assert.assertThat(nameHelper.getGetterName("foo", new JCodeModel().INT, NameHelperTest.NODE), is("getFoo"));
        Assert.assertThat(nameHelper.getGetterName("oAuth2State", new JCodeModel().INT, NameHelperTest.NODE), is("getoAuth2State"));
        Assert.assertThat(nameHelper.getGetterName("URL", new JCodeModel().INT, NameHelperTest.NODE), is("getUrl"));
    }

    @Test
    public void testSetterNamedCorrectly() {
        Assert.assertThat(nameHelper.getSetterName("foo", NameHelperTest.NODE), is("setFoo"));
        Assert.assertThat(nameHelper.getSetterName("oAuth2State", NameHelperTest.NODE), is("setoAuth2State"));
        Assert.assertThat(nameHelper.getSetterName("URL", NameHelperTest.NODE), is("setUrl"));
    }

    @Test
    public void testBuilderNamedCorrectly() {
        Assert.assertThat(nameHelper.getBuilderName("foo", NameHelperTest.NODE), is("withFoo"));
        Assert.assertThat(nameHelper.getBuilderName("oAuth2State", NameHelperTest.NODE), is("withoAuth2State"));
        Assert.assertThat(nameHelper.getBuilderName("URL", NameHelperTest.NODE), is("withUrl"));
    }

    @Test
    public void testClassNameCorrectly() {
        Assert.assertThat(nameHelper.getClassName("foo", NameHelperTest.NODE), is("foo"));
        Assert.assertThat(nameHelper.getClassName("foo", node("title", "bar")), is("foo"));
        Assert.assertThat(nameHelper.getClassName("foo", node("javaName", "bar")), is("bar"));
        Assert.assertThat(nameHelper.getClassName("foo", node("javaName", "bar").put("title", "abc")), is("bar"));
        // TITLE_ATTRIBUTE
        NameHelper nameHelper = helper(true);
        Assert.assertThat(nameHelper.getClassName("foo", node("title", "bar")), is("Bar"));
        Assert.assertThat(nameHelper.getClassName("foo", node("title", "i am bar")), is("IAmBar"));
        Assert.assertThat(nameHelper.getClassName("foo", node("javaName", "bar")), is("bar"));
        Assert.assertThat(nameHelper.getClassName("foo", node("javaName", "bar").put("title", "abc")), is("bar"));
    }
}


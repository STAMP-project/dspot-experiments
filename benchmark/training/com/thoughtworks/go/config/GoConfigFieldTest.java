/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.config;


import com.thoughtworks.go.util.FileUtil;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import org.hamcrest.Matchers;
import org.jdom2.Element;
import org.junit.Assert;
import org.junit.Test;


public class GoConfigFieldTest {
    public SystemEnvironment systemEnvironment;

    private ConfigCache configCache = new ConfigCache();

    @Test
    public void shouldConvertFromXmlToJavaObjectCorrectly() throws Exception {
        final GoConfigFieldTest.Foo object = new GoConfigFieldTest.Foo();
        final GoConfigFieldWriter field = new GoConfigFieldWriter(GoConfigFieldTest.Foo.class.getDeclaredField("number"), object, configCache, null);
        final Element element = new Element("foo");
        element.setAttribute("number", "100");
        field.setValueIfNotNull(element, object);
        Assert.assertThat(object.number, Matchers.is(100L));
    }

    @Test
    public void shouldConvertFileCorrectly() throws Exception {
        final GoConfigFieldTest.Foo object = new GoConfigFieldTest.Foo();
        final GoConfigFieldWriter field = new GoConfigFieldWriter(GoConfigFieldTest.Foo.class.getDeclaredField("directory"), object, configCache, null);
        final Element element = new Element("foo");
        element.setAttribute("directory", (("foo" + (FileUtil.fileseparator())) + "dir"));
        field.setValueIfNotNull(element, object);
        Assert.assertThat(object.directory.getPath(), Matchers.is((("foo" + (FileUtil.fileseparator())) + "dir")));
    }

    @Test
    public void shouldSetFileToNullifValueIsNotSpecified() throws Exception {
        final GoConfigFieldTest.Foo object = new GoConfigFieldTest.Foo();
        final GoConfigFieldWriter field = new GoConfigFieldWriter(GoConfigFieldTest.Foo.class.getDeclaredField("directory"), object, configCache, null);
        final Element element = new Element("foo");
        field.setValueIfNotNull(element, object);
        Assert.assertThat(object.directory, Matchers.is(Matchers.nullValue()));
    }

    @Test(expected = RuntimeException.class)
    public void shouldValidateAndConvertOnlyIfAppropriate() throws NoSuchFieldException {
        final GoConfigFieldTest.Foo object = new GoConfigFieldTest.Foo();
        final GoConfigFieldWriter field = new GoConfigFieldWriter(GoConfigFieldTest.Foo.class.getDeclaredField("number"), object, configCache, null);
        final Element element = new Element("foo");
        element.setAttribute("number", "anything");
        field.setValueIfNotNull(element, object);
    }

    private class Foo {
        @ConfigAttribute("number")
        private Long number;

        @ConfigAttribute(value = "directory", allowNull = true)
        private File directory;
    }
}


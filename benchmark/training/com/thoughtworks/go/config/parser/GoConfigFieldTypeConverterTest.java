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
package com.thoughtworks.go.config.parser;


import com.thoughtworks.go.util.ReflectionUtil;
import java.beans.PropertyEditor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GoConfigFieldTypeConverterTest {
    @Test
    public void shouldOnlyCreateOneInstanceOfCustomizedFileEditorAndUseIt() throws Exception {
        GoConfigFieldTypeConverter converter = new GoConfigFieldTypeConverter();
        Field expectedField = converter.getClass().getDeclaredField("propertyEditor");
        int modifier = expectedField.getModifiers();
        Assert.assertThat(Modifier.isStatic(modifier), Matchers.is(true));
        Assert.assertThat(Modifier.isFinal(modifier), Matchers.is(true));
        PropertyEditor actual = converter.findCustomEditor(File.class, null);
        Assert.assertThat(actual, Matchers.is(ReflectionUtil.getStaticField(converter.getClass(), "propertyEditor")));
    }
}


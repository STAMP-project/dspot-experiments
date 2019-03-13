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
package org.jsonschema2pojo.integration.ref;


import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class ClasspathRefIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    private static Class<?> classpathRefsClass;

    @Test
    public void refToClasspathResourceUsingClasspathProtocolIsReadSuccessfully() throws NoSuchMethodException {
        Class<?> aClass = ClasspathRefIT.classpathRefsClass.getMethod("getPropertyClasspathRef").getReturnType();
        Assert.assertThat(aClass.getName(), is("com.example.A"));
        Assert.assertThat(aClass.getMethods(), hasItemInArray(hasProperty("name", equalTo("getPropertyOfA"))));
    }

    @Test
    public void refToClasspathResourceUsingResourceProtocolIsReadSuccessfully() throws NoSuchMethodException {
        Class<?> aClass = ClasspathRefIT.classpathRefsClass.getMethod("getPropertyResourceRef").getReturnType();
        Assert.assertThat(aClass.getName(), is("com.example.Title"));
        Assert.assertThat(aClass.getMethods(), hasItemInArray(hasProperty("name", equalTo("getTitle"))));
    }

    @Test
    public void refToClasspathResourceUsingJavaProtocolIsReadSuccessfully() throws NoSuchMethodException {
        Class<?> aClass = ClasspathRefIT.classpathRefsClass.getMethod("getPropertyJavaRef").getReturnType();
        Assert.assertThat(aClass.getName(), is("com.example.Description"));
        Assert.assertThat(aClass.getMethods(), hasItemInArray(hasProperty("name", equalTo("getDescription"))));
    }

    @Test
    public void relativeRefToClasspathResourceWithinClasspathResource() throws NoSuchMethodException {
        Class<?> aClass = ClasspathRefIT.classpathRefsClass.getMethod("getTransitiveRelativeClasspathRef").getReturnType();
        Assert.assertThat(aClass.getName(), is("com.example.ClasspathRefs2"));
        Assert.assertThat(aClass.getMethods(), hasItemInArray(hasProperty("name", equalTo("getPropertyRelativeRef"))));
        Class<?> bClass = aClass.getMethod("getPropertyRelativeRef").getReturnType();
        Assert.assertThat(bClass.getName(), is("com.example.ClasspathRefs3"));
        Assert.assertThat(bClass.getMethods(), hasItemInArray(hasProperty("name", equalTo("getTransitive"))));
    }
}


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


import com.sun.codemodel.JCodeModel;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.apache.commons.io.IOUtils;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class SelfRefIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private static Class<?> selfRefsClass;

    @Test
    public void selfRefUsedInAPropertyIsReadSuccessfully() throws NoSuchMethodException {
        Class<?> aClass = SelfRefIT.selfRefsClass.getMethod("getChildOfSelf").getReturnType();
        Assert.assertThat(aClass.getName(), is("com.example.SelfRefs"));
    }

    @Test
    public void selfEmbeddedRefUsedInAPropertyIsReadSuccessfully() throws NoSuchMethodException {
        Class<?> aClass = SelfRefIT.selfRefsClass.getMethod("getEmbeddedInSelf").getReturnType();
        Assert.assertThat(aClass.getName(), is("com.example.Embedded"));
        Class<?> embedded2Class = aClass.getMethod("getEmbeddedProp").getReturnType();
        Assert.assertThat(embedded2Class.getName(), is("com.example.Embedded2"));
        Class<?> otherEmbeddedClass = embedded2Class.getMethod("getEmbeddedProp2").getReturnType();
        Assert.assertThat(otherEmbeddedClass.getName(), is("com.example.SelfRefs"));
    }

    @Test
    public void selfRefUsedAsArrayItemsIsReadSuccessfully() throws NoSuchMethodException {
        Type listOfAType = SelfRefIT.selfRefsClass.getMethod("getArrayOfSelf").getGenericReturnType();
        Class<?> listEntryClass = ((Class<?>) (((ParameterizedType) (listOfAType)).getActualTypeArguments()[0]));
        Assert.assertThat(listEntryClass.getName(), is("com.example.SelfRefs"));
    }

    @Test
    public void selfRefUsedForAdditionalPropertiesIsReadSuccessfully() throws NoSuchMethodException {
        Type additionalPropertiesType = SelfRefIT.selfRefsClass.getMethod("getAdditionalProperties").getGenericReturnType();
        Class<?> mapEntryClass = ((Class<?>) (((ParameterizedType) (additionalPropertiesType)).getActualTypeArguments()[1]));
        Assert.assertThat(mapEntryClass.getName(), is("com.example.SelfRefs"));
    }

    @Test
    public void nestedSelfRefsInStringContentWithoutParentFile() throws IOException, ClassNotFoundException, NoSuchMethodException {
        String schemaContents = IOUtils.toString(CodeGenerationHelper.class.getResource("/schema/ref/nestedSelfRefsReadAsString.json"));
        JCodeModel codeModel = new JCodeModel();
        new SchemaMapper().generate(codeModel, "NestedSelfRefsInString", "com.example", schemaContents);
        codeModel.build(schemaRule.getGenerateDir());
        ClassLoader classLoader = schemaRule.compile();
        Class<?> nestedSelfRefs = classLoader.loadClass("com.example.NestedSelfRefsInString");
        Assert.assertThat(nestedSelfRefs.getMethod("getThings").getReturnType().getSimpleName(), equalTo("List"));
        Class<?> listEntryType = ((Class<?>) (((ParameterizedType) (nestedSelfRefs.getMethod("getThings").getGenericReturnType())).getActualTypeArguments()[0]));
        Assert.assertThat(listEntryType.getName(), equalTo("com.example.Thing"));
        Class<?> thingClass = classLoader.loadClass("com.example.Thing");
        Assert.assertThat(thingClass.getMethod("getNamespace").getReturnType().getSimpleName(), equalTo("String"));
        Assert.assertThat(thingClass.getMethod("getName").getReturnType().getSimpleName(), equalTo("String"));
        Assert.assertThat(thingClass.getMethod("getVersion").getReturnType().getSimpleName(), equalTo("String"));
    }
}


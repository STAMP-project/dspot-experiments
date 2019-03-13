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
package org.jsonschema2pojo.integration.yaml;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import org.jsonschema2pojo.integration.util.CodeGenerationHelper;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class YamlTypeIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    private static Class<?> classWithManyTypes;

    @Test
    public void booleanTypeProducesBooleans() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getBooleanProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Boolean"));
    }

    @Test
    public void stringTypeProducesStrings() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getStringProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.String"));
    }

    @Test
    public void integerTypeProducesInts() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getIntegerProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Integer"));
    }

    @Test
    public void numberTypeProducesDouble() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getNumberProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Double"));
    }

    @Test
    public void arrayTypeProducesCollection() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getArrayProperty");
        Assert.assertThat(Collection.class.isAssignableFrom(getterMethod.getReturnType()), is(true));
    }

    @Test
    public void nullTypeProducesObject() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getNullProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Object"));
    }

    @Test
    public void anyTypeProducesObject() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getAnyProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Object"));
    }

    @Test
    public void presenceOfPropertiesImpliesTypeObject() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getImpliedObjectProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("com.example.ImpliedObjectProperty"));
    }

    @Test
    public void objectTypeProducesNewType() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getObjectProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("com.example.ObjectProperty"));
        Assert.assertThat(getterMethod.getReturnType().getMethod("getProperty"), is(notNullValue()));
    }

    @Test
    public void defaultTypeProducesObject() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getDefaultProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Object"));
    }

    @Test
    public void reusingTypeFromClasspathProducesNoNewType() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getReusedClasspathType");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.util.Locale"));
        Assert.assertThat(YamlTypeIT.classSchemaRule.generated("java/util/Locale.java").exists(), is(false));
    }

    @Test
    public void reusingTypeFromGeneratedTypesProducesNoNewType() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getReusedGeneratedType");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("com.example.ObjectProperty"));
        Assert.assertThat(getterMethod.getReturnType().getMethod("getProperty"), is(notNullValue()));
    }

    @Test
    public void javaTypeSupportsPrimitiveTypes() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getPrimitiveJavaType");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("long"));
    }

    @Test
    public void correctTypeIsChosenForNullableType() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getNullableStringProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.String"));
    }

    @Test
    public void javaTypeCanBeUsedForAnyShemaType() throws NoSuchMethodException {
        Assert.assertThat(YamlTypeIT.classWithManyTypes.getMethod("getIntegerWithJavaType").getReturnType().getName(), is("java.math.BigDecimal"));
        Assert.assertThat(YamlTypeIT.classWithManyTypes.getMethod("getNumberWithJavaType").getReturnType().getName(), is("java.util.UUID"));
        Assert.assertThat(YamlTypeIT.classWithManyTypes.getMethod("getStringWithJavaType").getReturnType().getName(), is("java.lang.Boolean"));
        Assert.assertThat(YamlTypeIT.classWithManyTypes.getMethod("getBooleanWithJavaType").getReturnType().getName(), is("long"));
        Assert.assertThat(YamlTypeIT.classWithManyTypes.getMethod("getDateWithJavaType").getReturnType().getName(), is("int"));
    }

    @Test
    public void maximumGreaterThanIntegerMaxCausesIntegersToBecomeLongs() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithLongProperty = schemaRule.generateAndCompile("/schema/yaml/type/integerWithLongMaximumAsLong.yaml", "com.example", CodeGenerationHelper.config("sourceType", "yamlschema")).loadClass("com.example.IntegerWithLongMaximumAsLong");
        Method getterMethod = classWithLongProperty.getMethod("getLongProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Long"));
    }

    @Test
    public void maximumGreaterThanIntegerMaxCausesIntegersToBecomePrimitiveLongs() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithLongProperty = schemaRule.generateAndCompile("/schema/yaml/type/integerWithLongMaximumAsLong.yaml", "com.example", CodeGenerationHelper.config("usePrimitives", true, "sourceType", "yamlschema")).loadClass("com.example.IntegerWithLongMaximumAsLong");
        Method getterMethod = classWithLongProperty.getMethod("getLongProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("long"));
    }

    @Test
    public void minimumLessThanIntegerMinCausesIntegersToBecomeLongs() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithLongProperty = schemaRule.generateAndCompile("/schema/yaml/type/integerWithLongMinimumAsLong.yaml", "com.example", CodeGenerationHelper.config("sourceType", "yamlschema")).loadClass("com.example.IntegerWithLongMinimumAsLong");
        Method getterMethod = classWithLongProperty.getMethod("getLongProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Long"));
    }

    @Test
    public void minimumLessThanIntegerMinCausesIntegersToBecomePrimitiveLongs() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithLongProperty = schemaRule.generateAndCompile("/schema/yaml/type/integerWithLongMinimumAsLong.yaml", "com.example", CodeGenerationHelper.config("usePrimitives", true, "sourceType", "yamlschema")).loadClass("com.example.IntegerWithLongMinimumAsLong");
        Method getterMethod = classWithLongProperty.getMethod("getLongProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("long"));
    }

    @Test
    public void useLongIntegersParameterCausesIntegersToBecomeLongs() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithLongProperty = schemaRule.generateAndCompile("/schema/yaml/type/integerAsLong.yaml", "com.example", CodeGenerationHelper.config("useLongIntegers", true, "sourceType", "yamlschema")).loadClass("com.example.IntegerAsLong");
        Method getterMethod = classWithLongProperty.getMethod("getLongProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Long"));
    }

    @Test
    public void useLongIntegersParameterCausesPrimitiveIntsToBecomeLongs() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithLongProperty = schemaRule.generateAndCompile("/schema/yaml/type/integerAsLong.yaml", "com.example", CodeGenerationHelper.config("useLongIntegers", true, "usePrimitives", true, "sourceType", "yamlschema")).loadClass("com.example.IntegerAsLong");
        Method getterMethod = classWithLongProperty.getMethod("getLongProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("long"));
    }

    @Test
    public void useDoubleNumbersFalseCausesNumbersToBecomeFloats() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithDoubleProperty = schemaRule.generateAndCompile("/schema/yaml/type/numberAsFloat.yaml", "com.example", CodeGenerationHelper.config("useDoubleNumbers", false, "sourceType", "yamlschema")).loadClass("com.example.NumberAsFloat");
        Method getterMethod = classWithDoubleProperty.getMethod("getFloatProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("java.lang.Float"));
    }

    @Test
    public void useDoubleNumbersFalseCausesPrimitiveNumbersToBecomeFloats() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithDoubleProperty = schemaRule.generateAndCompile("/schema/yaml/type/numberAsFloat.yaml", "com.example", CodeGenerationHelper.config("useDoubleNumbers", false, "usePrimitives", true, "sourceType", "yamlschema")).loadClass("com.example.NumberAsFloat");
        Method getterMethod = classWithDoubleProperty.getMethod("getFloatProperty");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("float"));
    }

    @Test
    public void unionTypesChooseFirstTypePresent() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithUnionProperties = schemaRule.generateAndCompile("/schema/yaml/type/unionTypes.yaml", "com.example", CodeGenerationHelper.config("sourceType", "yamlschema")).loadClass("com.example.UnionTypes");
        Method booleanGetter = classWithUnionProperties.getMethod("getBooleanProperty");
        Assert.assertThat(booleanGetter.getReturnType().getName(), is("java.lang.Boolean"));
        Method stringGetter = classWithUnionProperties.getMethod("getStringProperty");
        Assert.assertThat(stringGetter.getReturnType().getName(), is("java.lang.String"));
        Method integerGetter = classWithUnionProperties.getMethod("getIntegerProperty");
        Assert.assertThat(integerGetter.getReturnType().getName(), is("java.lang.Integer"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void typeNameConflictDoesNotCauseTypeReuse() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?> classWithNameConflict = schemaRule.generateAndCompile("/schema/yaml/type/typeNameConflict.yaml", "com.example", CodeGenerationHelper.config("sourceType", "yamlschema")).loadClass("com.example.TypeNameConflict");
        Method getterMethod = classWithNameConflict.getMethod("getTypeNameConflict");
        Assert.assertThat(((Class) (getterMethod.getReturnType())), is(not(((Class) (classWithNameConflict)))));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void typeImplementsAdditionalJavaInterfaces() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getTypeWithInterfaces");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("com.example.TypeWithInterfaces"));
        Assert.assertThat(getterMethod.getReturnType().getInterfaces().length, is(2));
        Assert.assertThat(((Class[]) (getterMethod.getReturnType().getInterfaces())), hasItemInArray(((Class) (Cloneable.class))));
        Assert.assertThat(((Class[]) (getterMethod.getReturnType().getInterfaces())), hasItemInArray(((Class) (Serializable.class))));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void typeImplementsInterfacesWithGenericArgsCorrectly() throws NoSuchMethodException, SecurityException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getTypeWithGenericInterface");
        Assert.assertThat(getterMethod.getReturnType().getName(), is("com.example.TypeWithGenericInterface"));
        Assert.assertThat(getterMethod.getReturnType().getInterfaces().length, is(1));
        Assert.assertThat(((Class[]) (getterMethod.getReturnType().getInterfaces())), hasItemInArray(((Class) (YamlTypeIT.InterfaceWithGenerics.class))));
    }

    public interface InterfaceWithGenerics<T, U, V> {}

    @Test
    public void typeExtendsJavaClass() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getTypeWithInheritedClass");
        final Class<?> generatedClass = getterMethod.getReturnType();
        Assert.assertThat(generatedClass.getName(), is("com.example.TypeWithInheritedClass"));
        Assert.assertThat(generatedClass.getSuperclass().equals(YamlTypeIT.InheritedClass.class), equalTo(true));
    }

    public static class InheritedClass {}

    @Test
    public void typeExtendsJavaClassWithGenerics() throws NoSuchMethodException {
        Method getterMethod = YamlTypeIT.classWithManyTypes.getMethod("getTypeWithInheritedClassWithGenerics");
        final Class<?> generatedClass = getterMethod.getReturnType();
        Assert.assertThat(generatedClass.getName(), is("com.example.TypeWithInheritedClassWithGenerics"));
        Assert.assertThat(generatedClass.getSuperclass().equals(YamlTypeIT.InheritedClassWithGenerics.class), equalTo(true));
        Assert.assertThat(((ParameterizedType) (generatedClass.getGenericSuperclass())).getActualTypeArguments(), equalTo(new Type[]{ String.class, Integer.class, Boolean.class }));
    }

    public static class InheritedClassWithGenerics<X, Y, Z> {}
}


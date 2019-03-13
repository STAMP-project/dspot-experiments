/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.core;


import ResolvableType.NONE;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ResolvableType.VariableResolver;
import org.springframework.util.MultiValueMap;

import static ResolvableType.NONE;


/**
 * Tests for {@link ResolvableType}.
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 */
@SuppressWarnings("rawtypes")
@RunWith(MockitoJUnitRunner.class)
public class ResolvableTypeTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Captor
    private ArgumentCaptor<TypeVariable<?>> typeVariableCaptor;

    @Test
    public void noneReturnValues() throws Exception {
        ResolvableType none = NONE;
        Assert.assertThat(none.as(Object.class), equalTo(NONE));
        Assert.assertThat(none.asCollection(), equalTo(NONE));
        Assert.assertThat(none.asMap(), equalTo(NONE));
        Assert.assertThat(none.getComponentType(), equalTo(NONE));
        Assert.assertThat(none.getGeneric(0), equalTo(NONE));
        Assert.assertThat(none.getGenerics().length, equalTo(0));
        Assert.assertThat(none.getInterfaces().length, equalTo(0));
        Assert.assertThat(none.getSuperType(), equalTo(NONE));
        Assert.assertThat(none.getType(), equalTo(ResolvableType.EmptyType.INSTANCE));
        Assert.assertThat(none.hasGenerics(), equalTo(false));
        Assert.assertThat(none.isArray(), equalTo(false));
        Assert.assertThat(none.resolve(), nullValue());
        Assert.assertThat(none.resolve(String.class), equalTo(((Class) (String.class))));
        Assert.assertThat(none.resolveGeneric(0), nullValue());
        Assert.assertThat(none.resolveGenerics().length, equalTo(0));
        Assert.assertThat(none.toString(), equalTo("?"));
        Assert.assertThat(none.hasUnresolvableGenerics(), equalTo(false));
        Assert.assertThat(none.isAssignableFrom(ResolvableType.forClass(Object.class)), equalTo(false));
    }

    @Test
    public void forClass() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class);
        Assert.assertThat(type.getType(), equalTo(((Type) (ResolvableTypeTests.ExtendsList.class))));
        Assert.assertThat(type.getRawClass(), equalTo(ResolvableTypeTests.ExtendsList.class));
        Assert.assertTrue(type.isAssignableFrom(ResolvableTypeTests.ExtendsList.class));
        Assert.assertFalse(type.isAssignableFrom(ArrayList.class));
    }

    @Test
    public void forClassWithNull() throws Exception {
        ResolvableType type = ResolvableType.forClass(null);
        Assert.assertThat(type.getType(), equalTo(((Type) (Object.class))));
        Assert.assertThat(type.getRawClass(), equalTo(Object.class));
        Assert.assertTrue(type.isAssignableFrom(Object.class));
        Assert.assertTrue(type.isAssignableFrom(String.class));
    }

    @Test
    public void forRawClass() throws Exception {
        ResolvableType type = ResolvableType.forRawClass(ResolvableTypeTests.ExtendsList.class);
        Assert.assertThat(type.getType(), equalTo(((Type) (ResolvableTypeTests.ExtendsList.class))));
        Assert.assertThat(type.getRawClass(), equalTo(ResolvableTypeTests.ExtendsList.class));
        Assert.assertTrue(type.isAssignableFrom(ResolvableTypeTests.ExtendsList.class));
        Assert.assertFalse(type.isAssignableFrom(ArrayList.class));
    }

    @Test
    public void forRawClassWithNull() throws Exception {
        ResolvableType type = ResolvableType.forRawClass(null);
        Assert.assertThat(type.getType(), equalTo(((Type) (Object.class))));
        Assert.assertThat(type.getRawClass(), equalTo(Object.class));
        Assert.assertTrue(type.isAssignableFrom(Object.class));
        Assert.assertTrue(type.isAssignableFrom(String.class));
    }

    @Test
    public void forInstanceMustNotBeNull() {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Instance must not be null");
        ResolvableType.forInstance(null);
    }

    @Test
    public void forInstanceNoProvider() {
        ResolvableType type = ResolvableType.forInstance(new Object());
        Assert.assertThat(type.getType(), equalTo(Object.class));
        Assert.assertThat(type.resolve(), equalTo(Object.class));
    }

    @Test
    public void forInstanceProvider() {
        ResolvableType type = ResolvableType.forInstance(new ResolvableTypeTests.MyGenericInterfaceType(String.class));
        Assert.assertThat(type.getRawClass(), equalTo(ResolvableTypeTests.MyGenericInterfaceType.class));
        Assert.assertThat(type.getGeneric().resolve(), equalTo(String.class));
    }

    @Test
    public void forInstanceProviderNull() {
        ResolvableType type = ResolvableType.forInstance(new ResolvableTypeTests.MyGenericInterfaceType<String>(null));
        Assert.assertThat(type.getType(), equalTo(ResolvableTypeTests.MyGenericInterfaceType.class));
        Assert.assertThat(type.resolve(), equalTo(ResolvableTypeTests.MyGenericInterfaceType.class));
    }

    @Test
    public void forField() throws Exception {
        Field field = ResolvableTypeTests.Fields.class.getField("charSequenceList");
        ResolvableType type = ResolvableType.forField(field);
        Assert.assertThat(type.getType(), equalTo(field.getGenericType()));
    }

    @Test
    public void forPrivateField() throws Exception {
        Field field = ResolvableTypeTests.Fields.class.getDeclaredField("privateField");
        ResolvableType type = ResolvableType.forField(field);
        Assert.assertThat(type.getType(), equalTo(field.getGenericType()));
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getSource(), sameInstance(field));
        Field field2 = ResolvableTypeTests.Fields.class.getDeclaredField("otherPrivateField");
        ResolvableType type2 = ResolvableType.forField(field2);
        Assert.assertThat(type2.getType(), equalTo(field2.getGenericType()));
        Assert.assertThat(type2.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type2.getSource(), sameInstance(field2));
        Assert.assertEquals(type, type2);
        Assert.assertEquals(type.hashCode(), type2.hashCode());
    }

    @Test
    public void forFieldMustNotBeNull() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Field must not be null");
        ResolvableType.forField(null);
    }

    @Test
    public void forConstructorParameter() throws Exception {
        Constructor<ResolvableTypeTests.Constructors> constructor = ResolvableTypeTests.Constructors.class.getConstructor(List.class);
        ResolvableType type = ResolvableType.forConstructorParameter(constructor, 0);
        Assert.assertThat(type.getType(), equalTo(constructor.getGenericParameterTypes()[0]));
    }

    @Test
    public void forConstructorParameterMustNotBeNull() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Constructor must not be null");
        ResolvableType.forConstructorParameter(null, 0);
    }

    @Test
    public void forMethodParameterByIndex() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("charSequenceParameter", List.class);
        ResolvableType type = ResolvableType.forMethodParameter(method, 0);
        Assert.assertThat(type.getType(), equalTo(method.getGenericParameterTypes()[0]));
    }

    @Test
    public void forMethodParameterByIndexMustNotBeNull() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Method must not be null");
        ResolvableType.forMethodParameter(null, 0);
    }

    @Test
    public void forMethodParameter() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("charSequenceParameter", List.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(method, 0);
        ResolvableType type = ResolvableType.forMethodParameter(methodParameter);
        Assert.assertThat(type.getType(), equalTo(method.getGenericParameterTypes()[0]));
    }

    @Test
    public void forMethodParameterWithNesting() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("nested", Map.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(method, 0);
        methodParameter.increaseNestingLevel();
        ResolvableType type = ResolvableType.forMethodParameter(methodParameter);
        Assert.assertThat(type.resolve(), equalTo(((Class) (Map.class))));
        Assert.assertThat(type.getGeneric(0).resolve(), equalTo(((Class) (Byte.class))));
        Assert.assertThat(type.getGeneric(1).resolve(), equalTo(((Class) (Long.class))));
    }

    @Test
    public void forMethodParameterWithNestingAndLevels() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("nested", Map.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(method, 0);
        methodParameter.increaseNestingLevel();
        methodParameter.setTypeIndexForCurrentLevel(0);
        ResolvableType type = ResolvableType.forMethodParameter(methodParameter);
        Assert.assertThat(type.resolve(), equalTo(((Class) (Map.class))));
        Assert.assertThat(type.getGeneric(0).resolve(), equalTo(((Class) (String.class))));
        Assert.assertThat(type.getGeneric(1).resolve(), equalTo(((Class) (Integer.class))));
    }

    @Test
    public void forMethodParameterMustNotBeNull() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("MethodParameter must not be null");
        ResolvableType.forMethodParameter(null);
    }

    // SPR-16210
    @Test
    public void forMethodParameterWithSameSignatureAndGenerics() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("list1");
        MethodParameter methodParameter = MethodParameter.forExecutable(method, (-1));
        ResolvableType type = ResolvableType.forMethodParameter(methodParameter);
        Assert.assertThat(getMethod(), equalTo(method));
        method = ResolvableTypeTests.Methods.class.getMethod("list2");
        methodParameter = MethodParameter.forExecutable(method, (-1));
        type = ResolvableType.forMethodParameter(methodParameter);
        Assert.assertThat(getMethod(), equalTo(method));
    }

    @Test
    public void forMethodReturn() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("charSequenceReturn");
        ResolvableType type = ResolvableType.forMethodReturnType(method);
        Assert.assertThat(type.getType(), equalTo(method.getGenericReturnType()));
    }

    @Test
    public void forMethodReturnMustNotBeNull() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Method must not be null");
        ResolvableType.forMethodReturnType(null);
    }

    @Test
    public void classType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("classType"));
        Assert.assertThat(type.getType().getClass(), equalTo(((Class) (Class.class))));
    }

    @Test
    public void paramaterizedType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("parameterizedType"));
        Assert.assertThat(type.getType(), instanceOf(ParameterizedType.class));
    }

    @Test
    public void arrayClassType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("arrayClassType"));
        Assert.assertThat(type.getType(), instanceOf(Class.class));
        Assert.assertThat(((Class) (type.getType())).isArray(), equalTo(true));
    }

    @Test
    public void genericArrayType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("genericArrayType"));
        Assert.assertThat(type.getType(), instanceOf(GenericArrayType.class));
    }

    @Test
    public void wildcardType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("wildcardType"));
        Assert.assertThat(type.getType(), instanceOf(ParameterizedType.class));
        Assert.assertThat(type.getGeneric().getType(), instanceOf(WildcardType.class));
    }

    @Test
    public void typeVariableType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("typeVariableType"));
        Assert.assertThat(type.getType(), instanceOf(TypeVariable.class));
    }

    @Test
    public void getComponentTypeForClassArray() throws Exception {
        Field field = ResolvableTypeTests.Fields.class.getField("arrayClassType");
        ResolvableType type = ResolvableType.forField(field);
        Assert.assertThat(type.isArray(), equalTo(true));
        Assert.assertThat(type.getComponentType().getType(), equalTo(((Type) (((Class) (field.getGenericType())).getComponentType()))));
    }

    @Test
    public void getComponentTypeForGenericArrayType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("genericArrayType"));
        Assert.assertThat(type.isArray(), equalTo(true));
        Assert.assertThat(type.getComponentType().getType(), equalTo(((GenericArrayType) (type.getType())).getGenericComponentType()));
    }

    @Test
    public void getComponentTypeForVariableThatResolvesToGenericArray() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ListOfGenericArray.class).asCollection().getGeneric();
        Assert.assertThat(type.isArray(), equalTo(true));
        Assert.assertThat(type.getType(), instanceOf(TypeVariable.class));
        Assert.assertThat(type.getComponentType().getType().toString(), equalTo("java.util.List<java.lang.String>"));
    }

    @Test
    public void getComponentTypeForNonArray() throws Exception {
        ResolvableType type = ResolvableType.forClass(String.class);
        Assert.assertThat(type.isArray(), equalTo(false));
        Assert.assertThat(type.getComponentType(), equalTo(NONE));
    }

    @Test
    public void asCollection() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class).asCollection();
        Assert.assertThat(type.resolve(), equalTo(((Class) (Collection.class))));
        Assert.assertThat(type.resolveGeneric(), equalTo(((Class) (CharSequence.class))));
    }

    @Test
    public void asMap() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsMap.class).asMap();
        Assert.assertThat(type.resolve(), equalTo(((Class) (Map.class))));
        Assert.assertThat(type.resolveGeneric(0), equalTo(((Class) (String.class))));
        Assert.assertThat(type.resolveGeneric(1), equalTo(((Class) (Integer.class))));
    }

    @Test
    public void asFromInterface() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class).as(List.class);
        Assert.assertThat(type.getType().toString(), equalTo("java.util.List<E>"));
    }

    @Test
    public void asFromInheritedInterface() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class).as(Collection.class);
        Assert.assertThat(type.getType().toString(), equalTo("java.util.Collection<E>"));
    }

    @Test
    public void asFromSuperType() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class).as(ArrayList.class);
        Assert.assertThat(type.getType().toString(), equalTo("java.util.ArrayList<java.lang.CharSequence>"));
    }

    @Test
    public void asFromInheritedSuperType() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class).as(List.class);
        Assert.assertThat(type.getType().toString(), equalTo("java.util.List<E>"));
    }

    @Test
    public void asNotFound() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class).as(Map.class);
        Assert.assertThat(type, sameInstance(NONE));
    }

    @Test
    public void asSelf() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class);
        Assert.assertThat(type.as(ResolvableTypeTests.ExtendsList.class), equalTo(type));
    }

    @Test
    public void getSuperType() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class).getSuperType();
        Assert.assertThat(type.resolve(), equalTo(((Class) (ArrayList.class))));
        type = type.getSuperType();
        Assert.assertThat(type.resolve(), equalTo(((Class) (AbstractList.class))));
        type = type.getSuperType();
        Assert.assertThat(type.resolve(), equalTo(((Class) (AbstractCollection.class))));
        type = type.getSuperType();
        Assert.assertThat(type.resolve(), equalTo(((Class) (Object.class))));
    }

    @Test
    public void getInterfaces() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class);
        Assert.assertThat(type.getInterfaces().length, equalTo(0));
        SortedSet<String> interfaces = new TreeSet<>();
        for (ResolvableType interfaceType : type.getSuperType().getInterfaces()) {
            interfaces.add(interfaceType.toString());
        }
        Assert.assertThat(interfaces.toString(), equalTo(("[" + (((("java.io.Serializable, " + "java.lang.Cloneable, ") + "java.util.List<java.lang.CharSequence>, ") + "java.util.RandomAccess") + "]"))));
    }

    @Test
    public void noSuperType() throws Exception {
        Assert.assertThat(ResolvableType.forClass(Object.class).getSuperType(), equalTo(NONE));
    }

    @Test
    public void noInterfaces() throws Exception {
        Assert.assertThat(ResolvableType.forClass(Object.class).getInterfaces().length, equalTo(0));
    }

    @Test
    public void nested() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("nested"));
        type = type.getNested(2);
        Assert.assertThat(type.resolve(), equalTo(((Class) (Map.class))));
        Assert.assertThat(type.getGeneric(0).resolve(), equalTo(((Class) (Byte.class))));
        Assert.assertThat(type.getGeneric(1).resolve(), equalTo(((Class) (Long.class))));
    }

    @Test
    public void nestedWithIndexes() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("nested"));
        type = type.getNested(2, Collections.singletonMap(2, 0));
        Assert.assertThat(type.resolve(), equalTo(((Class) (Map.class))));
        Assert.assertThat(type.getGeneric(0).resolve(), equalTo(((Class) (String.class))));
        Assert.assertThat(type.getGeneric(1).resolve(), equalTo(((Class) (Integer.class))));
    }

    @Test
    public void nestedWithArray() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("genericArrayType"));
        type = type.getNested(2);
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.resolveGeneric(), equalTo(((Class) (String.class))));
    }

    @Test
    public void getGeneric() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringList"));
        Assert.assertThat(type.getGeneric().getType(), equalTo(((Type) (String.class))));
    }

    @Test
    public void getGenericByIndex() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringIntegerMultiValueMap"));
        Assert.assertThat(type.getGeneric(0).getType(), equalTo(((Type) (String.class))));
        Assert.assertThat(type.getGeneric(1).getType(), equalTo(((Type) (Integer.class))));
    }

    @Test
    public void getGenericOfGeneric() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringListList"));
        Assert.assertThat(type.getGeneric().getType().toString(), equalTo("java.util.List<java.lang.String>"));
        Assert.assertThat(type.getGeneric().getGeneric().getType(), equalTo(((Type) (String.class))));
    }

    @Test
    public void genericOfGenericWithAs() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringListList")).asCollection();
        Assert.assertThat(type.toString(), equalTo("java.util.Collection<java.util.List<java.lang.String>>"));
        Assert.assertThat(type.getGeneric().asCollection().toString(), equalTo("java.util.Collection<java.lang.String>"));
    }

    @Test
    public void getGenericOfGenericByIndexes() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringListList"));
        Assert.assertThat(type.getGeneric(0, 0).getType(), equalTo(((Type) (String.class))));
    }

    @Test
    public void getGenericOutOfBounds() throws Exception {
        ResolvableType type = ResolvableType.forClass(List.class, ResolvableTypeTests.ExtendsList.class);
        Assert.assertThat(type.getGeneric(0), not(equalTo(NONE)));
        Assert.assertThat(type.getGeneric(1), equalTo(NONE));
        Assert.assertThat(type.getGeneric(0, 1), equalTo(NONE));
    }

    @Test
    public void hasGenerics() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class);
        Assert.assertThat(type.hasGenerics(), equalTo(false));
        Assert.assertThat(type.asCollection().hasGenerics(), equalTo(true));
    }

    @Test
    public void getGenericsFromParameterizedType() throws Exception {
        ResolvableType type = ResolvableType.forClass(List.class, ResolvableTypeTests.ExtendsList.class);
        ResolvableType[] generics = type.getGenerics();
        Assert.assertThat(generics.length, equalTo(1));
        Assert.assertThat(generics[0].resolve(), equalTo(((Class) (CharSequence.class))));
    }

    @Test
    public void getGenericsFromClass() throws Exception {
        ResolvableType type = ResolvableType.forClass(List.class);
        ResolvableType[] generics = type.getGenerics();
        Assert.assertThat(generics.length, equalTo(1));
        Assert.assertThat(generics[0].getType().toString(), equalTo("E"));
    }

    @Test
    public void noGetGenerics() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class);
        ResolvableType[] generics = type.getGenerics();
        Assert.assertThat(generics.length, equalTo(0));
    }

    @Test
    public void getResolvedGenerics() throws Exception {
        ResolvableType type = ResolvableType.forClass(List.class, ResolvableTypeTests.ExtendsList.class);
        Class<?>[] generics = type.resolveGenerics();
        Assert.assertThat(generics.length, equalTo(1));
        Assert.assertThat(generics[0], equalTo(((Class) (CharSequence.class))));
    }

    @Test
    public void resolveClassType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("classType"));
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
    }

    @Test
    public void resolveParameterizedType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("parameterizedType"));
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
    }

    @Test
    public void resolveArrayClassType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("arrayClassType"));
        Assert.assertThat(type.resolve(), equalTo(((Class) (List[].class))));
    }

    @Test
    public void resolveGenericArrayType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("genericArrayType"));
        Assert.assertThat(type.resolve(), equalTo(((Class) (List[].class))));
        Assert.assertThat(type.getComponentType().resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getComponentType().getGeneric().resolve(), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveGenericMultiArrayType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("genericMultiArrayType"));
        Assert.assertThat(type.resolve(), equalTo(((Class) (List[][][].class))));
        Assert.assertThat(type.getComponentType().resolve(), equalTo(((Class) (List[][].class))));
    }

    @Test
    public void resolveGenericArrayFromGeneric() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringArrayList"));
        ResolvableType generic = type.asCollection().getGeneric();
        Assert.assertThat(generic.getType().toString(), equalTo("E"));
        Assert.assertThat(generic.isArray(), equalTo(true));
        Assert.assertThat(generic.resolve(), equalTo(((Class) (String[].class))));
    }

    @Test
    public void resolveVariableGenericArray() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("variableTypeGenericArray"), ResolvableTypeTests.TypedFields.class);
        Assert.assertThat(type.getType().toString(), equalTo("T[]"));
        Assert.assertThat(type.isArray(), equalTo(true));
        Assert.assertThat(type.resolve(), equalTo(((Class) (String[].class))));
    }

    @Test
    public void resolveVariableGenericArrayUnknown() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("variableTypeGenericArray"));
        Assert.assertThat(type.getType().toString(), equalTo("T[]"));
        Assert.assertThat(type.isArray(), equalTo(true));
        Assert.assertThat(type.resolve(), nullValue());
    }

    @Test
    public void resolveVariableGenericArrayUnknownWithFallback() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("variableTypeGenericArray"));
        Assert.assertThat(type.getType().toString(), equalTo("T[]"));
        Assert.assertThat(type.isArray(), equalTo(true));
        Assert.assertThat(type.toClass(), equalTo(((Class) (Object.class))));
    }

    @Test
    public void resolveWildcardTypeUpperBounds() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("wildcardType"));
        Assert.assertThat(type.getGeneric().resolve(), equalTo(((Class) (Number.class))));
    }

    @Test
    public void resolveWildcardLowerBounds() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("wildcardSuperType"));
        Assert.assertThat(type.getGeneric().resolve(), equalTo(((Class) (Number.class))));
    }

    @Test
    public void resolveVariableFromFieldType() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringList"));
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getGeneric().resolve(), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveVariableFromFieldTypeUnknown() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("parameterizedType"));
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getGeneric().resolve(), nullValue());
    }

    @Test
    public void resolveVariableFromInheritedField() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringIntegerMultiValueMap")).as(Map.class);
        Assert.assertThat(type.getGeneric(0).resolve(), equalTo(((Class) (String.class))));
        Assert.assertThat(type.getGeneric(1).resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getGeneric(1, 0).resolve(), equalTo(((Class) (Integer.class))));
    }

    @Test
    public void resolveVariableFromInheritedFieldSwitched() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringIntegerMultiValueMapSwitched")).as(Map.class);
        Assert.assertThat(type.getGeneric(0).resolve(), equalTo(((Class) (String.class))));
        Assert.assertThat(type.getGeneric(1).resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getGeneric(1, 0).resolve(), equalTo(((Class) (Integer.class))));
    }

    @Test
    public void doesResolveFromOuterOwner() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("listOfListOfUnknown")).as(Collection.class);
        Assert.assertThat(type.getGeneric(0).resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getGeneric(0).as(Collection.class).getGeneric(0).as(Collection.class).resolve(), nullValue());
    }

    @Test
    public void resolveBoundedTypeVariableResult() throws Exception {
        ResolvableType type = ResolvableType.forMethodReturnType(ResolvableTypeTests.Methods.class.getMethod("boundedTypeVaraibleResult"));
        Assert.assertThat(type.resolve(), equalTo(((Class) (CharSequence.class))));
    }

    @Test
    public void resolveVariableNotFound() throws Exception {
        ResolvableType type = ResolvableType.forMethodReturnType(ResolvableTypeTests.Methods.class.getMethod("typedReturn"));
        Assert.assertThat(type.resolve(), nullValue());
    }

    @Test
    public void resolveTypeVaraibleFromMethodReturn() throws Exception {
        ResolvableType type = ResolvableType.forMethodReturnType(ResolvableTypeTests.Methods.class.getMethod("typedReturn"));
        Assert.assertThat(type.resolve(), nullValue());
    }

    @Test
    public void resolveTypeVaraibleFromMethodReturnWithInstanceClass() throws Exception {
        ResolvableType type = ResolvableType.forMethodReturnType(ResolvableTypeTests.Methods.class.getMethod("typedReturn"), ResolvableTypeTests.TypedMethods.class);
        Assert.assertThat(type.resolve(), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveTypeVaraibleFromSimpleInterfaceType() {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.MySimpleInterfaceType.class).as(ResolvableTypeTests.MyInterfaceType.class);
        Assert.assertThat(type.resolveGeneric(), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveTypeVaraibleFromSimpleCollectionInterfaceType() {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.MyCollectionInterfaceType.class).as(ResolvableTypeTests.MyInterfaceType.class);
        Assert.assertThat(type.resolveGeneric(), equalTo(((Class) (Collection.class))));
        Assert.assertThat(type.resolveGeneric(0, 0), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveTypeVaraibleFromSimpleSuperclassType() {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.MySimpleSuperclassType.class).as(ResolvableTypeTests.MySuperclassType.class);
        Assert.assertThat(type.resolveGeneric(), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveTypeVaraibleFromSimpleCollectionSuperclassType() {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.MyCollectionSuperclassType.class).as(ResolvableTypeTests.MySuperclassType.class);
        Assert.assertThat(type.resolveGeneric(), equalTo(((Class) (Collection.class))));
        Assert.assertThat(type.resolveGeneric(0, 0), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveTypeVariableFromFieldTypeWithImplementsClass() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("parameterizedType"), ResolvableTypeTests.TypedFields.class);
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getGeneric().resolve(), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveTypeVariableFromFieldTypeWithImplementsType() throws Exception {
        ResolvableType implementationType = ResolvableType.forClassWithGenerics(ResolvableTypeTests.Fields.class, Integer.class);
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("parameterizedType"), implementationType);
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.getGeneric().resolve(), equalTo(((Class) (Integer.class))));
    }

    @Test
    public void resolveTypeVariableFromSuperType() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsList.class);
        Assert.assertThat(type.resolve(), equalTo(((Class) (ResolvableTypeTests.ExtendsList.class))));
        Assert.assertThat(type.asCollection().resolveGeneric(), equalTo(((Class) (CharSequence.class))));
    }

    @Test
    public void resolveTypeVariableFromClassWithImplementsClass() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.MySuperclassType.class, ResolvableTypeTests.MyCollectionSuperclassType.class);
        Assert.assertThat(type.resolveGeneric(), equalTo(((Class) (Collection.class))));
        Assert.assertThat(type.resolveGeneric(0, 0), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveTypeVariableFromConstructorParameter() throws Exception {
        Constructor<?> constructor = ResolvableTypeTests.Constructors.class.getConstructor(List.class);
        ResolvableType type = ResolvableType.forConstructorParameter(constructor, 0);
        Assert.assertThat(type.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(type.resolveGeneric(0), equalTo(((Class) (CharSequence.class))));
    }

    @Test
    public void resolveUnknownTypeVariableFromConstructorParameter() throws Exception {
        Constructor<?> constructor = ResolvableTypeTests.Constructors.class.getConstructor(Map.class);
        ResolvableType type = ResolvableType.forConstructorParameter(constructor, 0);
        Assert.assertThat(type.resolve(), equalTo(((Class) (Map.class))));
        Assert.assertThat(type.resolveGeneric(0), nullValue());
    }

    @Test
    public void resolveTypeVariableFromConstructorParameterWithImplementsClass() throws Exception {
        Constructor<?> constructor = ResolvableTypeTests.Constructors.class.getConstructor(Map.class);
        ResolvableType type = ResolvableType.forConstructorParameter(constructor, 0, ResolvableTypeTests.TypedConstructors.class);
        Assert.assertThat(type.resolve(), equalTo(((Class) (Map.class))));
        Assert.assertThat(type.resolveGeneric(0), equalTo(((Class) (String.class))));
    }

    @Test
    public void resolveTypeVariableFromMethodParameter() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("typedParameter", Object.class);
        ResolvableType type = ResolvableType.forMethodParameter(method, 0);
        Assert.assertThat(type.resolve(), nullValue());
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromMethodParameterWithImplementsClass() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("typedParameter", Object.class);
        ResolvableType type = ResolvableType.forMethodParameter(method, 0, ResolvableTypeTests.TypedMethods.class);
        Assert.assertThat(type.resolve(), equalTo(((Class) (String.class))));
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromMethodParameterType() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("typedParameter", Object.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(method, 0);
        ResolvableType type = ResolvableType.forMethodParameter(methodParameter);
        Assert.assertThat(type.resolve(), nullValue());
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromMethodParameterTypeWithImplementsClass() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("typedParameter", Object.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(method, 0);
        methodParameter.setContainingClass(ResolvableTypeTests.TypedMethods.class);
        ResolvableType type = ResolvableType.forMethodParameter(methodParameter);
        Assert.assertThat(type.resolve(), equalTo(((Class) (String.class))));
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromMethodParameterTypeWithImplementsType() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("typedParameter", Object.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(method, 0);
        ResolvableType implementationType = ResolvableType.forClassWithGenerics(ResolvableTypeTests.Methods.class, Integer.class);
        ResolvableType type = ResolvableType.forMethodParameter(methodParameter, implementationType);
        Assert.assertThat(type.resolve(), equalTo(((Class) (Integer.class))));
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromMethodReturn() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("typedReturn");
        ResolvableType type = ResolvableType.forMethodReturnType(method);
        Assert.assertThat(type.resolve(), nullValue());
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromMethodReturnWithImplementsClass() throws Exception {
        Method method = ResolvableTypeTests.Methods.class.getMethod("typedReturn");
        ResolvableType type = ResolvableType.forMethodReturnType(method, ResolvableTypeTests.TypedMethods.class);
        Assert.assertThat(type.resolve(), equalTo(((Class) (String.class))));
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromType() throws Exception {
        Type sourceType = ResolvableTypeTests.Methods.class.getMethod("typedReturn").getGenericReturnType();
        ResolvableType type = ResolvableType.forType(sourceType);
        Assert.assertThat(type.resolve(), nullValue());
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromTypeWithVariableResolver() throws Exception {
        Type sourceType = ResolvableTypeTests.Methods.class.getMethod("typedReturn").getGenericReturnType();
        ResolvableType type = ResolvableType.forType(sourceType, ResolvableType.forClass(ResolvableTypeTests.TypedMethods.class).as(ResolvableTypeTests.Methods.class).asVariableResolver());
        Assert.assertThat(type.resolve(), equalTo(((Class) (String.class))));
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeWithCustomVariableResolver() throws Exception {
        VariableResolver variableResolver = Mockito.mock(VariableResolver.class);
        BDDMockito.given(variableResolver.getSource()).willReturn(this);
        ResolvableType longType = ResolvableType.forClass(Long.class);
        BDDMockito.given(variableResolver.resolveVariable(ArgumentMatchers.any())).willReturn(longType);
        ResolvableType variable = ResolvableType.forType(ResolvableTypeTests.Fields.class.getField("typeVariableType").getGenericType(), variableResolver);
        ResolvableType parameterized = ResolvableType.forType(ResolvableTypeTests.Fields.class.getField("parameterizedType").getGenericType(), variableResolver);
        Assert.assertThat(variable.resolve(), equalTo(((Class) (Long.class))));
        Assert.assertThat(parameterized.resolve(), equalTo(((Class) (List.class))));
        Assert.assertThat(parameterized.resolveGeneric(), equalTo(((Class) (Long.class))));
        Mockito.verify(variableResolver, Mockito.atLeastOnce()).resolveVariable(this.typeVariableCaptor.capture());
        Assert.assertThat(this.typeVariableCaptor.getValue().getName(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromReflectiveParameterizedTypeReference() throws Exception {
        Type sourceType = ResolvableTypeTests.Methods.class.getMethod("typedReturn").getGenericReturnType();
        ResolvableType type = ResolvableType.forType(ParameterizedTypeReference.forType(sourceType));
        Assert.assertThat(type.resolve(), nullValue());
        Assert.assertThat(type.getType().toString(), equalTo("T"));
    }

    @Test
    public void resolveTypeVariableFromDeclaredParameterizedTypeReference() throws Exception {
        Type sourceType = ResolvableTypeTests.Methods.class.getMethod("charSequenceReturn").getGenericReturnType();
        ResolvableType reflectiveType = ResolvableType.forType(sourceType);
        ResolvableType declaredType = ResolvableType.forType(new ParameterizedTypeReference<List<CharSequence>>() {});
        Assert.assertEquals(reflectiveType, declaredType);
    }

    @Test
    public void toStrings() throws Exception {
        Assert.assertThat(NONE.toString(), equalTo("?"));
        assertFieldToStringValue("classType", "java.util.List<?>");
        assertFieldToStringValue("typeVariableType", "?");
        assertFieldToStringValue("parameterizedType", "java.util.List<?>");
        assertFieldToStringValue("arrayClassType", "java.util.List<?>[]");
        assertFieldToStringValue("genericArrayType", "java.util.List<java.lang.String>[]");
        assertFieldToStringValue("genericMultiArrayType", "java.util.List<java.lang.String>[][][]");
        assertFieldToStringValue("wildcardType", "java.util.List<java.lang.Number>");
        assertFieldToStringValue("wildcardSuperType", "java.util.List<java.lang.Number>");
        assertFieldToStringValue("charSequenceList", "java.util.List<java.lang.CharSequence>");
        assertFieldToStringValue("stringList", "java.util.List<java.lang.String>");
        assertFieldToStringValue("stringListList", "java.util.List<java.util.List<java.lang.String>>");
        assertFieldToStringValue("stringArrayList", "java.util.List<java.lang.String[]>");
        assertFieldToStringValue("stringIntegerMultiValueMap", "org.springframework.util.MultiValueMap<java.lang.String, java.lang.Integer>");
        assertFieldToStringValue("stringIntegerMultiValueMapSwitched", ((ResolvableTypeTests.VariableNameSwitch.class.getName()) + "<java.lang.Integer, java.lang.String>"));
        assertFieldToStringValue("listOfListOfUnknown", "java.util.List<java.util.List<?>>");
        assertTypedFieldToStringValue("typeVariableType", "java.lang.String");
        assertTypedFieldToStringValue("parameterizedType", "java.util.List<java.lang.String>");
        Assert.assertThat(ResolvableType.forClass(ResolvableTypeTests.ListOfGenericArray.class).toString(), equalTo(ResolvableTypeTests.ListOfGenericArray.class.getName()));
        Assert.assertThat(ResolvableType.forClass(List.class, ResolvableTypeTests.ListOfGenericArray.class).toString(), equalTo("java.util.List<java.util.List<java.lang.String>[]>"));
    }

    @Test
    public void getSource() throws Exception {
        Class<?> classType = ResolvableTypeTests.MySimpleInterfaceType.class;
        Field basicField = ResolvableTypeTests.Fields.class.getField("classType");
        Field field = ResolvableTypeTests.Fields.class.getField("charSequenceList");
        Method method = ResolvableTypeTests.Methods.class.getMethod("charSequenceParameter", List.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(method, 0);
        Assert.assertThat(ResolvableType.forField(basicField).getSource(), equalTo(((Object) (basicField))));
        Assert.assertThat(ResolvableType.forField(field).getSource(), equalTo(((Object) (field))));
        Assert.assertThat(ResolvableType.forMethodParameter(methodParameter).getSource(), equalTo(((Object) (methodParameter))));
        Assert.assertThat(ResolvableType.forMethodParameter(method, 0).getSource(), equalTo(((Object) (methodParameter))));
        Assert.assertThat(ResolvableType.forClass(classType).getSource(), equalTo(((Object) (classType))));
        Assert.assertThat(ResolvableType.forClass(classType).getSuperType().getSource(), equalTo(((Object) (classType.getGenericSuperclass()))));
    }

    @Test
    public void resolveFromOuterClass() throws Exception {
        Field field = ResolvableTypeTests.EnclosedInParameterizedType.InnerTyped.class.getField("field");
        ResolvableType type = ResolvableType.forField(field, ResolvableTypeTests.TypedEnclosedInParameterizedType.TypedInnerTyped.class);
        Assert.assertThat(type.resolve(), equalTo(((Type) (Integer.class))));
    }

    @Test
    public void resolveFromClassWithGenerics() throws Exception {
        ResolvableType type = ResolvableType.forClassWithGenerics(List.class, ResolvableType.forClassWithGenerics(List.class, String.class));
        Assert.assertThat(type.asCollection().toString(), equalTo("java.util.Collection<java.util.List<java.lang.String>>"));
        Assert.assertThat(type.asCollection().getGeneric().toString(), equalTo("java.util.List<java.lang.String>"));
        Assert.assertThat(type.asCollection().getGeneric().asCollection().toString(), equalTo("java.util.Collection<java.lang.String>"));
        Assert.assertThat(type.toString(), equalTo("java.util.List<java.util.List<java.lang.String>>"));
        Assert.assertThat(type.asCollection().getGeneric().getGeneric().resolve(), equalTo(((Type) (String.class))));
    }

    @Test
    public void isAssignableFromMustNotBeNull() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Type must not be null");
        ResolvableType.forClass(Object.class).isAssignableFrom(((ResolvableType) (null)));
    }

    @Test
    public void isAssignableFromForNone() throws Exception {
        ResolvableType objectType = ResolvableType.forClass(Object.class);
        Assert.assertThat(objectType.isAssignableFrom(NONE), equalTo(false));
        Assert.assertThat(NONE.isAssignableFrom(objectType), equalTo(false));
    }

    @Test
    public void isAssignableFromForClassAndClass() throws Exception {
        ResolvableType objectType = ResolvableType.forClass(Object.class);
        ResolvableType charSequenceType = ResolvableType.forClass(CharSequence.class);
        ResolvableType stringType = ResolvableType.forClass(String.class);
        assertAssignable(objectType, objectType, charSequenceType, stringType).equalTo(true, true, true);
        assertAssignable(charSequenceType, objectType, charSequenceType, stringType).equalTo(false, true, true);
        assertAssignable(stringType, objectType, charSequenceType, stringType).equalTo(false, false, true);
        Assert.assertTrue(objectType.isAssignableFrom(String.class));
        Assert.assertTrue(objectType.isAssignableFrom(StringBuilder.class));
        Assert.assertTrue(charSequenceType.isAssignableFrom(String.class));
        Assert.assertTrue(charSequenceType.isAssignableFrom(StringBuilder.class));
        Assert.assertTrue(stringType.isAssignableFrom(String.class));
        Assert.assertFalse(stringType.isAssignableFrom(StringBuilder.class));
        Assert.assertTrue(objectType.isInstance("a String"));
        Assert.assertTrue(objectType.isInstance(new StringBuilder("a StringBuilder")));
        Assert.assertTrue(charSequenceType.isInstance("a String"));
        Assert.assertTrue(charSequenceType.isInstance(new StringBuilder("a StringBuilder")));
        Assert.assertTrue(stringType.isInstance("a String"));
        Assert.assertFalse(stringType.isInstance(new StringBuilder("a StringBuilder")));
    }

    @Test
    public void isAssignableFromCannotBeResolved() throws Exception {
        ResolvableType objectType = ResolvableType.forClass(Object.class);
        ResolvableType unresolvableVariable = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("o"));
        Assert.assertThat(unresolvableVariable.resolve(), nullValue());
        assertAssignable(objectType, unresolvableVariable).equalTo(true);
        assertAssignable(unresolvableVariable, objectType).equalTo(true);
    }

    @Test
    public void isAssignableFromForClassAndSimpleVariable() throws Exception {
        ResolvableType objectType = ResolvableType.forClass(Object.class);
        ResolvableType charSequenceType = ResolvableType.forClass(CharSequence.class);
        ResolvableType stringType = ResolvableType.forClass(String.class);
        ResolvableType objectVariable = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("o"), ResolvableTypeTests.Assignment.class);
        ResolvableType charSequenceVariable = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("c"), ResolvableTypeTests.Assignment.class);
        ResolvableType stringVariable = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("s"), ResolvableTypeTests.Assignment.class);
        assertAssignable(objectType, objectVariable, charSequenceVariable, stringVariable).equalTo(true, true, true);
        assertAssignable(charSequenceType, objectVariable, charSequenceVariable, stringVariable).equalTo(false, true, true);
        assertAssignable(stringType, objectVariable, charSequenceVariable, stringVariable).equalTo(false, false, true);
        assertAssignable(objectVariable, objectType, charSequenceType, stringType).equalTo(true, true, true);
        assertAssignable(charSequenceVariable, objectType, charSequenceType, stringType).equalTo(false, true, true);
        assertAssignable(stringVariable, objectType, charSequenceType, stringType).equalTo(false, false, true);
        assertAssignable(objectVariable, objectVariable, charSequenceVariable, stringVariable).equalTo(true, true, true);
        assertAssignable(charSequenceVariable, objectVariable, charSequenceVariable, stringVariable).equalTo(false, true, true);
        assertAssignable(stringVariable, objectVariable, charSequenceVariable, stringVariable).equalTo(false, false, true);
    }

    @Test
    public void isAssignableFromForSameClassNonExtendsGenerics() throws Exception {
        ResolvableType objectList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listo"), ResolvableTypeTests.Assignment.class);
        ResolvableType stringList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("lists"), ResolvableTypeTests.Assignment.class);
        assertAssignable(stringList, objectList).equalTo(false);
        assertAssignable(objectList, stringList).equalTo(false);
        assertAssignable(stringList, stringList).equalTo(true);
    }

    @Test
    public void isAssignableFromForSameClassExtendsGenerics() throws Exception {
        // Generic assignment can be a little confusing, given:
        // 
        // List<CharSequence> c1, List<? extends CharSequence> c2, List<String> s;
        // 
        // c2 = s; is allowed and is often used for argument input, for example
        // see List.addAll(). You can get items from c2 but you cannot add items without
        // getting a generic type 'is not applicable for the arguments' error. This makes
        // sense since if you added a StringBuffer to c2 it would break the rules on s.
        // 
        // c1 = s; not allowed. Since there is no '? extends' to cause the generic
        // 'is not applicable for the arguments' error when adding (which would pollute
        // s).
        ResolvableType objectList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listo"), ResolvableTypeTests.Assignment.class);
        ResolvableType charSequenceList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listc"), ResolvableTypeTests.Assignment.class);
        ResolvableType stringList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("lists"), ResolvableTypeTests.Assignment.class);
        ResolvableType extendsObjectList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listxo"), ResolvableTypeTests.Assignment.class);
        ResolvableType extendsCharSequenceList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listxc"), ResolvableTypeTests.Assignment.class);
        ResolvableType extendsStringList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listxs"), ResolvableTypeTests.Assignment.class);
        assertAssignable(objectList, extendsObjectList, extendsCharSequenceList, extendsStringList).equalTo(false, false, false);
        assertAssignable(charSequenceList, extendsObjectList, extendsCharSequenceList, extendsStringList).equalTo(false, false, false);
        assertAssignable(stringList, extendsObjectList, extendsCharSequenceList, extendsStringList).equalTo(false, false, false);
        assertAssignable(extendsObjectList, objectList, charSequenceList, stringList).equalTo(true, true, true);
        assertAssignable(extendsObjectList, extendsObjectList, extendsCharSequenceList, extendsStringList).equalTo(true, true, true);
        assertAssignable(extendsCharSequenceList, extendsObjectList, extendsCharSequenceList, extendsStringList).equalTo(false, true, true);
        assertAssignable(extendsCharSequenceList, objectList, charSequenceList, stringList).equalTo(false, true, true);
        assertAssignable(extendsStringList, extendsObjectList, extendsCharSequenceList, extendsStringList).equalTo(false, false, true);
        assertAssignable(extendsStringList, objectList, charSequenceList, stringList).equalTo(false, false, true);
    }

    @Test
    public void isAssignableFromForDifferentClassesWithGenerics() throws Exception {
        ResolvableType extendsCharSequenceCollection = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("collectionxc"), ResolvableTypeTests.Assignment.class);
        ResolvableType charSequenceCollection = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("collectionc"), ResolvableTypeTests.Assignment.class);
        ResolvableType charSequenceList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listc"), ResolvableTypeTests.Assignment.class);
        ResolvableType extendsCharSequenceList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listxc"), ResolvableTypeTests.Assignment.class);
        ResolvableType extendsStringList = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listxs"), ResolvableTypeTests.Assignment.class);
        assertAssignable(extendsCharSequenceCollection, charSequenceCollection, charSequenceList, extendsCharSequenceList, extendsStringList).equalTo(true, true, true, true);
        assertAssignable(charSequenceCollection, charSequenceList, extendsCharSequenceList, extendsStringList).equalTo(true, false, false);
        assertAssignable(charSequenceList, extendsCharSequenceCollection, charSequenceCollection).equalTo(false, false);
        assertAssignable(extendsCharSequenceList, extendsCharSequenceCollection, charSequenceCollection).equalTo(false, false);
        assertAssignable(extendsStringList, charSequenceCollection, charSequenceList, extendsCharSequenceList).equalTo(false, false, false);
    }

    @Test
    public void isAssignableFromForArrays() throws Exception {
        ResolvableType object = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("o"), ResolvableTypeTests.Assignment.class);
        ResolvableType objectArray = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("oarray"), ResolvableTypeTests.Assignment.class);
        ResolvableType charSequenceArray = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("carray"), ResolvableTypeTests.Assignment.class);
        ResolvableType stringArray = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("sarray"), ResolvableTypeTests.Assignment.class);
        assertAssignable(object, objectArray, charSequenceArray, stringArray).equalTo(true, true, true);
        assertAssignable(objectArray, object, objectArray, charSequenceArray, stringArray).equalTo(false, true, true, true);
        assertAssignable(charSequenceArray, object, objectArray, charSequenceArray, stringArray).equalTo(false, false, true, true);
        assertAssignable(stringArray, object, objectArray, charSequenceArray, stringArray).equalTo(false, false, false, true);
    }

    @Test
    public void isAssignableFromForWildcards() throws Exception {
        ResolvableType object = ResolvableType.forClass(Object.class);
        ResolvableType charSequence = ResolvableType.forClass(CharSequence.class);
        ResolvableType string = ResolvableType.forClass(String.class);
        ResolvableType extendsAnon = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listAnon"), ResolvableTypeTests.Assignment.class).getGeneric();
        ResolvableType extendsObject = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listxo"), ResolvableTypeTests.Assignment.class).getGeneric();
        ResolvableType extendsCharSequence = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listxc"), ResolvableTypeTests.Assignment.class).getGeneric();
        ResolvableType extendsString = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listxs"), ResolvableTypeTests.Assignment.class).getGeneric();
        ResolvableType superObject = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listso"), ResolvableTypeTests.Assignment.class).getGeneric();
        ResolvableType superCharSequence = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listsc"), ResolvableTypeTests.Assignment.class).getGeneric();
        ResolvableType superString = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("listss"), ResolvableTypeTests.Assignment.class).getGeneric();
        // Language Spec 4.5.1. Type Arguments and Wildcards
        // ? extends T <= ? extends S if T <: S
        assertAssignable(extendsCharSequence, extendsObject, extendsCharSequence, extendsString).equalTo(false, true, true);
        assertAssignable(extendsCharSequence, object, charSequence, string).equalTo(false, true, true);
        // ? super T <= ? super S if S <: T
        assertAssignable(superCharSequence, superObject, superCharSequence, superString).equalTo(true, true, false);
        assertAssignable(superCharSequence, object, charSequence, string).equalTo(true, true, false);
        // [Implied] super / extends cannot be mixed
        assertAssignable(superCharSequence, extendsObject, extendsCharSequence, extendsString).equalTo(false, false, false);
        assertAssignable(extendsCharSequence, superObject, superCharSequence, superString).equalTo(false, false, false);
        // T <= T
        assertAssignable(charSequence, object, charSequence, string).equalTo(false, true, true);
        // T <= ? extends T
        assertAssignable(extendsCharSequence, object, charSequence, string).equalTo(false, true, true);
        assertAssignable(charSequence, extendsObject, extendsCharSequence, extendsString).equalTo(false, false, false);
        assertAssignable(extendsAnon, object, charSequence, string).equalTo(true, true, true);
        // T <= ? super T
        assertAssignable(superCharSequence, object, charSequence, string).equalTo(true, true, false);
        assertAssignable(charSequence, superObject, superCharSequence, superString).equalTo(false, false, false);
    }

    @Test
    public void isAssignableFromForComplexWildcards() throws Exception {
        ResolvableType complex1 = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("complexWildcard1"));
        ResolvableType complex2 = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("complexWildcard2"));
        ResolvableType complex3 = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("complexWildcard3"));
        ResolvableType complex4 = ResolvableType.forField(ResolvableTypeTests.AssignmentBase.class.getField("complexWildcard4"));
        assertAssignable(complex1, complex2).equalTo(true);
        assertAssignable(complex2, complex1).equalTo(false);
        assertAssignable(complex3, complex4).equalTo(true);
        assertAssignable(complex4, complex3).equalTo(false);
    }

    @Test
    public void hashCodeAndEquals() throws Exception {
        ResolvableType forClass = ResolvableType.forClass(List.class);
        ResolvableType forFieldDirect = ResolvableType.forField(ResolvableTypeTests.Fields.class.getDeclaredField("stringList"));
        ResolvableType forFieldViaType = ResolvableType.forType(ResolvableTypeTests.Fields.class.getDeclaredField("stringList").getGenericType(), ((VariableResolver) (null)));
        ResolvableType forFieldWithImplementation = ResolvableType.forField(ResolvableTypeTests.Fields.class.getDeclaredField("stringList"), ResolvableTypeTests.TypedFields.class);
        Assert.assertThat(forClass, equalTo(forClass));
        Assert.assertThat(forClass.hashCode(), equalTo(forClass.hashCode()));
        Assert.assertThat(forClass, not(equalTo(forFieldDirect)));
        Assert.assertThat(forClass, not(equalTo(forFieldWithImplementation)));
        Assert.assertThat(forFieldDirect, equalTo(forFieldDirect));
        Assert.assertThat(forFieldDirect, not(equalTo(forFieldViaType)));
        Assert.assertThat(forFieldDirect, not(equalTo(forFieldWithImplementation)));
    }

    @Test
    public void javaDocSample() throws Exception {
        ResolvableType t = ResolvableType.forField(getClass().getDeclaredField("myMap"));
        Assert.assertThat(t.toString(), equalTo("java.util.HashMap<java.lang.Integer, java.util.List<java.lang.String>>"));
        Assert.assertThat(t.getType().getTypeName(), equalTo("java.util.HashMap<java.lang.Integer, java.util.List<java.lang.String>>"));
        Assert.assertThat(t.getType().toString(), equalTo("java.util.HashMap<java.lang.Integer, java.util.List<java.lang.String>>"));
        Assert.assertThat(t.getSuperType().toString(), equalTo("java.util.AbstractMap<java.lang.Integer, java.util.List<java.lang.String>>"));
        Assert.assertThat(t.asMap().toString(), equalTo("java.util.Map<java.lang.Integer, java.util.List<java.lang.String>>"));
        Assert.assertThat(t.getGeneric(0).resolve(), equalTo(Integer.class));
        Assert.assertThat(t.getGeneric(1).resolve(), equalTo(List.class));
        Assert.assertThat(t.getGeneric(1).toString(), equalTo("java.util.List<java.lang.String>"));
        Assert.assertThat(t.resolveGeneric(1, 0), equalTo(String.class));
    }

    @Test
    public void forClassWithGenerics() throws Exception {
        ResolvableType elementType = ResolvableType.forClassWithGenerics(Map.class, Integer.class, String.class);
        ResolvableType listType = ResolvableType.forClassWithGenerics(List.class, elementType);
        Assert.assertThat(listType.toString(), equalTo("java.util.List<java.util.Map<java.lang.Integer, java.lang.String>>"));
        Assert.assertThat(listType.getType().getTypeName(), equalTo("java.util.List<java.util.Map<java.lang.Integer, java.lang.String>>"));
        Assert.assertThat(listType.getType().toString(), equalTo("java.util.List<java.util.Map<java.lang.Integer, java.lang.String>>"));
    }

    @Test
    public void classWithGenericsAs() throws Exception {
        ResolvableType type = ResolvableType.forClassWithGenerics(MultiValueMap.class, Integer.class, String.class);
        Assert.assertThat(type.asMap().toString(), equalTo("java.util.Map<java.lang.Integer, java.util.List<java.lang.String>>"));
    }

    @Test
    public void forClassWithMismatchedGenerics() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Mismatched number of generics specified");
        ResolvableType.forClassWithGenerics(Map.class, Integer.class);
    }

    @Test
    public void forArrayComponent() throws Exception {
        ResolvableType elementType = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringList"));
        ResolvableType type = ResolvableType.forArrayComponent(elementType);
        Assert.assertThat(type.toString(), equalTo("java.util.List<java.lang.String>[]"));
        Assert.assertThat(type.resolve(), equalTo(List[].class));
    }

    @Test
    public void serialize() throws Exception {
        testSerialization(ResolvableType.forClass(List.class));
        testSerialization(ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("charSequenceList")));
        testSerialization(ResolvableType.forMethodParameter(ResolvableTypeTests.Methods.class.getMethod("charSequenceParameter", List.class), 0));
        testSerialization(ResolvableType.forMethodReturnType(ResolvableTypeTests.Methods.class.getMethod("charSequenceReturn")));
        testSerialization(ResolvableType.forConstructorParameter(ResolvableTypeTests.Constructors.class.getConstructor(List.class), 0));
        testSerialization(ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("charSequenceList")).getGeneric());
        ResolvableType deserializedNone = testSerialization(NONE);
        Assert.assertThat(deserializedNone, sameInstance(NONE));
    }

    @Test
    public void canResolveVoid() throws Exception {
        ResolvableType type = ResolvableType.forClass(void.class);
        Assert.assertThat(type.resolve(), equalTo(void.class));
    }

    @Test
    public void narrow() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringList"));
        ResolvableType narrow = ResolvableType.forType(ArrayList.class, type);
        Assert.assertThat(narrow.getGeneric().resolve(), equalTo(String.class));
    }

    @Test
    public void hasUnresolvableGenerics() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.Fields.class.getField("stringList"));
        Assert.assertThat(type.hasUnresolvableGenerics(), equalTo(false));
    }

    @Test
    public void hasUnresolvableGenericsBasedOnOwnGenerics() throws Exception {
        ResolvableType type = ResolvableType.forClass(List.class);
        Assert.assertThat(type.hasUnresolvableGenerics(), equalTo(true));
    }

    @Test
    public void hasUnresolvableGenericsWhenSelfNotResolvable() throws Exception {
        ResolvableType type = ResolvableType.forClass(List.class).getGeneric();
        Assert.assertThat(type.hasUnresolvableGenerics(), equalTo(false));
    }

    @Test
    public void hasUnresolvableGenericsWhenImplementesRawInterface() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.MySimpleInterfaceTypeWithImplementsRaw.class);
        for (ResolvableType generic : type.getGenerics()) {
            Assert.assertThat(generic.resolve(), not(nullValue()));
        }
        Assert.assertThat(type.hasUnresolvableGenerics(), equalTo(true));
    }

    @Test
    public void hasUnresolvableGenericsWhenExtends() throws Exception {
        ResolvableType type = ResolvableType.forClass(ResolvableTypeTests.ExtendsMySimpleInterfaceTypeWithImplementsRaw.class);
        for (ResolvableType generic : type.getGenerics()) {
            Assert.assertThat(generic.resolve(), not(nullValue()));
        }
        Assert.assertThat(type.hasUnresolvableGenerics(), equalTo(true));
    }

    @Test
    public void testSpr11219() throws Exception {
        ResolvableType type = ResolvableType.forField(ResolvableTypeTests.BaseProvider.class.getField("stuff"), ResolvableTypeTests.BaseProvider.class);
        Assert.assertTrue(type.getNested(2).isAssignableFrom(ResolvableType.forClass(ResolvableTypeTests.BaseImplementation.class)));
        Assert.assertEquals("java.util.Collection<org.springframework.core.ResolvableTypeTests$IBase<?>>", type.toString());
    }

    @Test
    public void testSpr12701() throws Exception {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(Callable.class, String.class);
        Type type = resolvableType.getType();
        Assert.assertThat(type, is(instanceOf(ParameterizedType.class)));
        Assert.assertThat(((ParameterizedType) (type)).getRawType(), is(equalTo(Callable.class)));
        Assert.assertThat(((ParameterizedType) (type)).getActualTypeArguments().length, is(equalTo(1)));
        Assert.assertThat(((ParameterizedType) (type)).getActualTypeArguments()[0], is(equalTo(String.class)));
    }

    @Test
    public void testSpr14648() throws Exception {
        ResolvableType collectionClass = ResolvableType.forRawClass(Collection.class);
        ResolvableType setClass = ResolvableType.forRawClass(Set.class);
        ResolvableType fromReturnType = ResolvableType.forMethodReturnType(ResolvableTypeTests.Methods.class.getMethod("wildcardSet"));
        Assert.assertTrue(collectionClass.isAssignableFrom(fromReturnType));
        Assert.assertTrue(setClass.isAssignableFrom(fromReturnType));
    }

    @Test
    public void testSpr16456() throws Exception {
        ResolvableType genericType = ResolvableType.forField(ResolvableTypeTests.UnresolvedWithGenerics.class.getDeclaredField("set")).asCollection();
        ResolvableType type = ResolvableType.forClassWithGenerics(ArrayList.class, genericType.getGeneric());
        Assert.assertThat(type.resolveGeneric(), equalTo(Integer.class));
    }

    @SuppressWarnings("unused")
    private HashMap<Integer, List<String>> myMap;

    private interface AssertAssignbleMatcher {
        void equalTo(boolean... values);
    }

    @SuppressWarnings("serial")
    static class ExtendsList extends ArrayList<CharSequence> {}

    @SuppressWarnings("serial")
    static class ExtendsMap extends HashMap<String, Integer> {}

    static class Fields<T> {
        public List classType;

        public T typeVariableType;

        public List<T> parameterizedType;

        public List[] arrayClassType;

        public List<String>[] genericArrayType;

        public List<String>[][][] genericMultiArrayType;

        public List<? extends Number> wildcardType;

        public List<? super Number> wildcardSuperType = new ArrayList<Object>();

        public List<CharSequence> charSequenceList;

        public List<String> stringList;

        public List<List<String>> stringListList;

        public List<String[]> stringArrayList;

        public MultiValueMap<String, Integer> stringIntegerMultiValueMap;

        public ResolvableTypeTests.VariableNameSwitch<Integer, String> stringIntegerMultiValueMapSwitched;

        public List<List> listOfListOfUnknown;

        @SuppressWarnings("unused")
        private List<String> privateField;

        @SuppressWarnings("unused")
        private List<String> otherPrivateField;

        public Map<Map<String, Integer>, Map<Byte, Long>> nested;

        public T[] variableTypeGenericArray;
    }

    static class TypedFields extends ResolvableTypeTests.Fields<String> {}

    interface Methods<T> {
        List<CharSequence> charSequenceReturn();

        void charSequenceParameter(List<CharSequence> cs);

        <R extends CharSequence & Serializable> R boundedTypeVaraibleResult();

        void nested(Map<Map<String, Integer>, Map<Byte, Long>> p);

        void typedParameter(T p);

        T typedReturn();

        Set<?> wildcardSet();

        List<String> list1();

        List<String> list2();
    }

    static class AssignmentBase<O, C, S> {
        public O o;

        public C c;

        public S s;

        public List<O> listo;

        public List<C> listc;

        public List<S> lists;

        public List<?> listAnon;

        public List<? extends O> listxo;

        public List<? extends C> listxc;

        public List<? extends S> listxs;

        public List<? super O> listso;

        public List<? super C> listsc;

        public List<? super S> listss;

        public O[] oarray;

        public C[] carray;

        public S[] sarray;

        public Collection<C> collectionc;

        public Collection<? extends C> collectionxc;

        public Map<? super Integer, List<String>> complexWildcard1;

        public MultiValueMap<Number, String> complexWildcard2;

        public Collection<? extends Collection<? extends CharSequence>> complexWildcard3;

        public List<List<String>> complexWildcard4;
    }

    static class Assignment extends ResolvableTypeTests.AssignmentBase<Object, CharSequence, String> {}

    interface TypedMethods extends ResolvableTypeTests.Methods<String> {}

    static class Constructors<T> {
        public Constructors(List<CharSequence> p) {
        }

        public Constructors(Map<T, Long> p) {
        }
    }

    static class TypedConstructors extends ResolvableTypeTests.Constructors<String> {
        public TypedConstructors(List<CharSequence> p) {
            super(p);
        }

        public TypedConstructors(Map<String, Long> p) {
            super(p);
        }
    }

    public interface MyInterfaceType<T> {}

    public class MyGenericInterfaceType<T> implements ResolvableTypeProvider , ResolvableTypeTests.MyInterfaceType<T> {
        private final Class<T> type;

        public MyGenericInterfaceType(Class<T> type) {
            this.type = type;
        }

        @Override
        public ResolvableType getResolvableType() {
            if ((this.type) == null) {
                return null;
            }
            return ResolvableType.forClassWithGenerics(getClass(), this.type);
        }
    }

    public class MySimpleInterfaceType implements ResolvableTypeTests.MyInterfaceType<String> {}

    public abstract class MySimpleInterfaceTypeWithImplementsRaw implements List , ResolvableTypeTests.MyInterfaceType<String> {}

    public abstract class ExtendsMySimpleInterfaceTypeWithImplementsRaw extends ResolvableTypeTests.MySimpleInterfaceTypeWithImplementsRaw {}

    public class MyCollectionInterfaceType implements ResolvableTypeTests.MyInterfaceType<Collection<String>> {}

    public abstract class MySuperclassType<T> {}

    public class MySimpleSuperclassType extends ResolvableTypeTests.MySuperclassType<String> {}

    public class MyCollectionSuperclassType extends ResolvableTypeTests.MySuperclassType<Collection<String>> {}

    interface Wildcard<T extends Number> extends List<T> {}

    interface RawExtendsWildcard extends ResolvableTypeTests.Wildcard {}

    interface VariableNameSwitch<V, K> extends MultiValueMap<K, V> {}

    interface ListOfGenericArray extends List<List<String>[]> {}

    static class EnclosedInParameterizedType<T> {
        static class InnerRaw {}

        class InnerTyped<Y> {
            public T field;
        }
    }

    static class TypedEnclosedInParameterizedType extends ResolvableTypeTests.EnclosedInParameterizedType<Integer> {
        class TypedInnerTyped extends ResolvableTypeTests.EnclosedInParameterizedType<Integer>.InnerTyped<Long> {}
    }

    public interface IProvider<P> {}

    public interface IBase<BT extends ResolvableTypeTests.IBase<BT>> {}

    public abstract class AbstractBase<BT extends ResolvableTypeTests.IBase<BT>> implements ResolvableTypeTests.IBase<BT> {}

    public class BaseImplementation extends ResolvableTypeTests.AbstractBase<ResolvableTypeTests.BaseImplementation> {}

    public class BaseProvider<BT extends ResolvableTypeTests.IBase<BT>> implements ResolvableTypeTests.IProvider<ResolvableTypeTests.IBase<BT>> {
        public Collection<ResolvableTypeTests.IBase<BT>> stuff;
    }

    public abstract class UnresolvedWithGenerics {
        Set<Integer> set;
    }
}


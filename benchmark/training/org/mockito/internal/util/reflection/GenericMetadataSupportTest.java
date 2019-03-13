/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;


public class GenericMetadataSupportTest {
    interface GenericsSelfReference<T extends GenericMetadataSupportTest.GenericsSelfReference<T>> {
        T self();
    }

    interface UpperBoundedTypeWithClass<E extends Number & Comparable<E>> {
        E get();
    }

    interface UpperBoundedTypeWithInterfaces<E extends Comparable<E> & Cloneable> {
        E get();
    }

    interface ListOfNumbers extends List<Number> {}

    interface AnotherListOfNumbers extends GenericMetadataSupportTest.ListOfNumbers {}

    abstract class ListOfNumbersImpl implements GenericMetadataSupportTest.ListOfNumbers {}

    abstract class AnotherListOfNumbersImpl extends GenericMetadataSupportTest.ListOfNumbersImpl {}

    interface ListOfAnyNumbers<N extends Number & Cloneable> extends List<N> {}

    interface GenericsNest<K extends Comparable<K> & Cloneable> extends Map<K, Set<Number>> {
        Set<Number> remove(Object key);// override with fixed ParameterizedType


        List<? super Integer> returning_wildcard_with_class_lower_bound();

        List<? super K> returning_wildcard_with_typeVar_lower_bound();

        List<? extends K> returning_wildcard_with_typeVar_upper_bound();

        K returningK();

        <O extends K> List<O> paramType_with_type_params();

        <S, T extends S> T two_type_params();

        <O extends K> O typeVar_with_type_params();
    }

    static class StringList extends ArrayList<String> {}

    public interface TopInterface<T> {
        T generic();
    }

    public interface MiddleInterface<T> extends GenericMetadataSupportTest.TopInterface<T> {}

    public class OwningClassWithDeclaredUpperBounds<T extends List<String> & Comparable<String> & Cloneable> {
        public abstract class AbstractInner implements GenericMetadataSupportTest.MiddleInterface<T> {}
    }

    public class OwningClassWithNoDeclaredUpperBounds<T> {
        public abstract class AbstractInner implements GenericMetadataSupportTest.MiddleInterface<T> {}
    }

    @Test
    public void typeVariable_of_self_type() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsSelfReference.class).resolveGenericReturnType(firstNamedMethod("self", GenericMetadataSupportTest.GenericsSelfReference.class));
        assertThat(genericMetadata.rawType()).isEqualTo(GenericMetadataSupportTest.GenericsSelfReference.class);
    }

    @Test
    public void can_get_raw_type_from_Class() {
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.ListOfAnyNumbers.class).rawType()).isEqualTo(GenericMetadataSupportTest.ListOfAnyNumbers.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.ListOfNumbers.class).rawType()).isEqualTo(GenericMetadataSupportTest.ListOfNumbers.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).rawType()).isEqualTo(GenericMetadataSupportTest.GenericsNest.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.StringList.class).rawType()).isEqualTo(GenericMetadataSupportTest.StringList.class);
    }

    @Test
    public void can_get_raw_type_from_ParameterizedType() {
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.ListOfAnyNumbers.class.getGenericInterfaces()[0]).rawType()).isEqualTo(List.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.ListOfNumbers.class.getGenericInterfaces()[0]).rawType()).isEqualTo(List.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class.getGenericInterfaces()[0]).rawType()).isEqualTo(Map.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.StringList.class.getGenericSuperclass()).rawType()).isEqualTo(ArrayList.class);
    }

    @Test
    public void can_get_type_variables_from_Class() {
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).actualTypeArguments().keySet()).hasSize(1).extracting("name").contains("K");
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.ListOfNumbers.class).actualTypeArguments().keySet()).isEmpty();
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.ListOfAnyNumbers.class).actualTypeArguments().keySet()).hasSize(1).extracting("name").contains("N");
        assertThat(GenericMetadataSupport.inferFrom(Map.class).actualTypeArguments().keySet()).hasSize(2).extracting("name").contains("K", "V");
        assertThat(GenericMetadataSupport.inferFrom(Serializable.class).actualTypeArguments().keySet()).isEmpty();
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.StringList.class).actualTypeArguments().keySet()).isEmpty();
    }

    @Test
    public void can_resolve_type_variables_from_ancestors() throws Exception {
        Method listGet = List.class.getMethod("get", int.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.AnotherListOfNumbers.class).resolveGenericReturnType(listGet).rawType()).isEqualTo(Number.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.AnotherListOfNumbersImpl.class).resolveGenericReturnType(listGet).rawType()).isEqualTo(Number.class);
    }

    @Test
    public void can_get_type_variables_from_ParameterizedType() {
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class.getGenericInterfaces()[0]).actualTypeArguments().keySet()).hasSize(2).extracting("name").contains("K", "V");
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.ListOfAnyNumbers.class.getGenericInterfaces()[0]).actualTypeArguments().keySet()).hasSize(1).extracting("name").contains("E");
        assertThat(GenericMetadataSupport.inferFrom(Integer.class.getGenericInterfaces()[0]).actualTypeArguments().keySet()).hasSize(1).extracting("name").contains("T");
        assertThat(GenericMetadataSupport.inferFrom(StringBuilder.class.getGenericInterfaces()[0]).actualTypeArguments().keySet()).isEmpty();
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.StringList.class).actualTypeArguments().keySet()).isEmpty();
    }

    @Test
    public void typeVariable_return_type_of____iterator____resolved_to_Iterator_and_type_argument_to_String() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.StringList.class).resolveGenericReturnType(firstNamedMethod("iterator", GenericMetadataSupportTest.StringList.class));
        assertThat(genericMetadata.rawType()).isEqualTo(Iterator.class);
        assertThat(genericMetadata.actualTypeArguments().values()).contains(String.class);
    }

    @Test
    public void typeVariable_return_type_of____get____resolved_to_Set_and_type_argument_to_Number() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("get", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(Set.class);
        assertThat(genericMetadata.actualTypeArguments().values()).contains(Number.class);
    }

    @Test
    public void bounded_typeVariable_return_type_of____returningK____resolved_to_Comparable_and_with_BoundedType() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("returningK", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(Comparable.class);
        GenericMetadataSupport extraInterface_0 = GenericMetadataSupport.inferFrom(genericMetadata.extraInterfaces().get(0));
        assertThat(extraInterface_0.rawType()).isEqualTo(Cloneable.class);
    }

    @Test
    public void fixed_ParamType_return_type_of____remove____resolved_to_Set_and_type_argument_to_Number() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("remove", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(Set.class);
        assertThat(genericMetadata.actualTypeArguments().values()).contains(Number.class);
    }

    @Test
    public void paramType_return_type_of____values____resolved_to_Collection_and_type_argument_to_Parameterized_Set() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("values", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(Collection.class);
        GenericMetadataSupport fromTypeVariableE = GenericMetadataSupport.inferFrom(typeVariableValue(genericMetadata.actualTypeArguments(), "E"));
        assertThat(fromTypeVariableE.rawType()).isEqualTo(Set.class);
        assertThat(fromTypeVariableE.actualTypeArguments().values()).contains(Number.class);
    }

    @Test
    public void paramType_with_type_parameters_return_type_of____paramType_with_type_params____resolved_to_Collection_and_type_argument_to_Parameterized_Set() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("paramType_with_type_params", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(List.class);
        Type firstBoundOfE = ((GenericMetadataSupport.TypeVarBoundedType) (typeVariableValue(genericMetadata.actualTypeArguments(), "E"))).firstBound();
        assertThat(GenericMetadataSupport.inferFrom(firstBoundOfE).rawType()).isEqualTo(Comparable.class);
    }

    @Test
    public void typeVariable_with_type_parameters_return_type_of____typeVar_with_type_params____resolved_K_hence_to_Comparable_and_with_BoundedType() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("typeVar_with_type_params", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(Comparable.class);
        GenericMetadataSupport extraInterface_0 = GenericMetadataSupport.inferFrom(genericMetadata.extraInterfaces().get(0));
        assertThat(extraInterface_0.rawType()).isEqualTo(Cloneable.class);
    }

    @Test
    public void class_return_type_of____append____resolved_to_StringBuilder_and_type_arguments() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(StringBuilder.class).resolveGenericReturnType(firstNamedMethod("append", StringBuilder.class));
        assertThat(genericMetadata.rawType()).isEqualTo(StringBuilder.class);
        assertThat(genericMetadata.actualTypeArguments()).isEmpty();
    }

    @Test
    public void paramType_with_wildcard_return_type_of____returning_wildcard_with_class_lower_bound____resolved_to_List_and_type_argument_to_Integer() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("returning_wildcard_with_class_lower_bound", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(List.class);
        GenericMetadataSupport.BoundedType boundedType = ((GenericMetadataSupport.BoundedType) (typeVariableValue(genericMetadata.actualTypeArguments(), "E")));
        assertThat(boundedType.firstBound()).isEqualTo(Integer.class);
        assertThat(boundedType.interfaceBounds()).isEmpty();
    }

    @Test
    public void paramType_with_wildcard_return_type_of____returning_wildcard_with_typeVar_lower_bound____resolved_to_List_and_type_argument_to_Integer() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("returning_wildcard_with_typeVar_lower_bound", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(List.class);
        GenericMetadataSupport.BoundedType boundedType = ((GenericMetadataSupport.BoundedType) (typeVariableValue(genericMetadata.actualTypeArguments(), "E")));
        assertThat(GenericMetadataSupport.inferFrom(boundedType.firstBound()).rawType()).isEqualTo(Comparable.class);
        assertThat(boundedType.interfaceBounds()).contains(Cloneable.class);
    }

    @Test
    public void paramType_with_wildcard_return_type_of____returning_wildcard_with_typeVar_upper_bound____resolved_to_List_and_type_argument_to_Integer() {
        GenericMetadataSupport genericMetadata = GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.GenericsNest.class).resolveGenericReturnType(firstNamedMethod("returning_wildcard_with_typeVar_upper_bound", GenericMetadataSupportTest.GenericsNest.class));
        assertThat(genericMetadata.rawType()).isEqualTo(List.class);
        GenericMetadataSupport.BoundedType boundedType = ((GenericMetadataSupport.BoundedType) (typeVariableValue(genericMetadata.actualTypeArguments(), "E")));
        assertThat(GenericMetadataSupport.inferFrom(boundedType.firstBound()).rawType()).isEqualTo(Comparable.class);
        assertThat(boundedType.interfaceBounds()).contains(Cloneable.class);
    }

    @Test
    public void can_extract_raw_type_from_bounds_on_terminal_typeVariable() {
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.OwningClassWithDeclaredUpperBounds.AbstractInner.class).resolveGenericReturnType(firstNamedMethod("generic", GenericMetadataSupportTest.OwningClassWithDeclaredUpperBounds.AbstractInner.class)).rawType()).isEqualTo(List.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.OwningClassWithNoDeclaredUpperBounds.AbstractInner.class).resolveGenericReturnType(firstNamedMethod("generic", GenericMetadataSupportTest.OwningClassWithNoDeclaredUpperBounds.AbstractInner.class)).rawType()).isEqualTo(Object.class);
    }

    @Test
    public void can_extract_interface_type_from_bounds_on_terminal_typeVariable() {
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.OwningClassWithDeclaredUpperBounds.AbstractInner.class).resolveGenericReturnType(firstNamedMethod("generic", GenericMetadataSupportTest.OwningClassWithDeclaredUpperBounds.AbstractInner.class)).rawExtraInterfaces()).containsExactly(Comparable.class, Cloneable.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.OwningClassWithDeclaredUpperBounds.AbstractInner.class).resolveGenericReturnType(firstNamedMethod("generic", GenericMetadataSupportTest.OwningClassWithDeclaredUpperBounds.AbstractInner.class)).extraInterfaces()).containsExactly(parameterizedTypeOf(Comparable.class, null, String.class), Cloneable.class);
        assertThat(GenericMetadataSupport.inferFrom(GenericMetadataSupportTest.OwningClassWithNoDeclaredUpperBounds.AbstractInner.class).resolveGenericReturnType(firstNamedMethod("generic", GenericMetadataSupportTest.OwningClassWithNoDeclaredUpperBounds.AbstractInner.class)).extraInterfaces()).isEmpty();
    }
}


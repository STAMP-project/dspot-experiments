package net.bytebuddy.implementation;


import EqualsMethod.NaturalOrderComparator;
import EqualsMethod.TypePropertyComparator;
import java.lang.annotation.RetentionPolicy;
import java.util.Comparator;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class EqualsMethodOtherTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test(expected = NullPointerException.class)
    public void testNullableField() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withNonNullableFields(ElementMatchers.named(EqualsMethodOtherTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        type.getDeclaredConstructor().newInstance().equals(type.getDeclaredConstructor().newInstance());
    }

    @Test
    public void testEqualToSelf() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withNonNullableFields(ElementMatchers.named(EqualsMethodOtherTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance, CoreMatchers.is(instance));
    }

    @Test
    public void testEqualToSelfIdentity() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        loaded.getLoaded().getDeclaredField(EqualsMethodOtherTest.FOO).set(instance, new EqualsMethodOtherTest.NonEqualsBase());
        MatcherAssert.assertThat(instance, CoreMatchers.is(instance));
    }

    @Test
    public void testIgnoredField() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withIgnoredFields(ElementMatchers.named(EqualsMethodOtherTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object left = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object right = loaded.getLoaded().getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(EqualsMethodOtherTest.FOO).set(left, EqualsMethodOtherTest.FOO);
        left.getClass().getDeclaredField(EqualsMethodOtherTest.FOO).set(left, EqualsMethodOtherTest.BAR);
        MatcherAssert.assertThat(left, CoreMatchers.is(right));
    }

    @Test
    public void testSuperMethod() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(EqualsMethodOtherTest.EqualsBase.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.requiringSuperClassEquality()).make().load(EqualsMethodOtherTest.EqualsBase.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object left = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object right = loaded.getLoaded().getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(EqualsMethodOtherTest.FOO).set(left, EqualsMethodOtherTest.FOO);
        right.getClass().getDeclaredField(EqualsMethodOtherTest.FOO).set(right, EqualsMethodOtherTest.FOO);
        MatcherAssert.assertThat(left, CoreMatchers.is(right));
    }

    @Test
    public void testSuperClass() throws Exception {
        DynamicType.Loaded<?> superClass = new ByteBuddy().subclass(EqualsMethodOtherTest.EqualsBase.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated()).make().load(EqualsMethodOtherTest.EqualsBase.class.getClassLoader(), WRAPPER);
        DynamicType.Loaded<?> subClass = new ByteBuddy().subclass(superClass.getLoaded()).make().load(superClass.getLoaded().getClassLoader(), WRAPPER);
        Object left = subClass.getLoaded().getDeclaredConstructor().newInstance();
        Object right = subClass.getLoaded().getDeclaredConstructor().newInstance();
        superClass.getLoaded().getDeclaredField(EqualsMethodOtherTest.FOO).set(left, EqualsMethodOtherTest.FOO);
        superClass.getLoaded().getDeclaredField(EqualsMethodOtherTest.FOO).set(right, EqualsMethodOtherTest.FOO);
        MatcherAssert.assertThat(left, CoreMatchers.is(right));
    }

    @Test
    public void testSuperMethodNoMatch() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(EqualsMethodOtherTest.NonEqualsBase.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.requiringSuperClassEquality()).make().load(EqualsMethodOtherTest.NonEqualsBase.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object left = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object right = loaded.getLoaded().getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(EqualsMethodOtherTest.FOO).set(left, EqualsMethodOtherTest.FOO);
        right.getClass().getDeclaredField(EqualsMethodOtherTest.FOO).set(right, EqualsMethodOtherTest.FOO);
        MatcherAssert.assertThat(left, CoreMatchers.not(right));
    }

    @Test
    public void testInstanceOf() throws Exception {
        DynamicType.Loaded<?> superClass = new ByteBuddy().subclass(Object.class).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withSubclassEquality()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        DynamicType.Loaded<?> subClass = new ByteBuddy().subclass(superClass.getLoaded()).make().load(superClass.getLoaded().getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(superClass.getLoaded().getDeclaredConstructor().newInstance(), CoreMatchers.is(subClass.getLoaded().getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(subClass.getLoaded().getDeclaredConstructor().newInstance(), CoreMatchers.is(superClass.getLoaded().getDeclaredConstructor().newInstance()));
    }

    @Test
    public void testTypeOrderForPrimitiveTypedFields() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).defineField(EqualsMethodOtherTest.BAR, int.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withNonNullableFields(ElementMatchers.any()).withPrimitiveTypedFieldsFirst()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(2));
        Object left = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object right = loaded.getLoaded().getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(EqualsMethodOtherTest.BAR).setInt(left, 42);
        right.getClass().getDeclaredField(EqualsMethodOtherTest.BAR).setInt(right, 84);
        MatcherAssert.assertThat(left, CoreMatchers.not(right));
    }

    @Test
    public void testTypeOrderForEnumerationTypedFields() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).defineField(EqualsMethodOtherTest.BAR, RetentionPolicy.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withNonNullableFields(ElementMatchers.any()).withEnumerationTypedFieldsFirst()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(2));
        Object left = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object right = loaded.getLoaded().getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(EqualsMethodOtherTest.BAR).set(left, RetentionPolicy.RUNTIME);
        right.getClass().getDeclaredField(EqualsMethodOtherTest.BAR).set(right, RetentionPolicy.CLASS);
        MatcherAssert.assertThat(left, CoreMatchers.not(right));
    }

    @Test
    public void testTypeOrderForStringTypedFields() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).defineField(EqualsMethodOtherTest.BAR, String.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withNonNullableFields(ElementMatchers.any()).withStringTypedFieldsFirst()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(2));
        Object left = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object right = loaded.getLoaded().getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(EqualsMethodOtherTest.BAR).set(left, EqualsMethodOtherTest.FOO);
        right.getClass().getDeclaredField(EqualsMethodOtherTest.BAR).set(right, EqualsMethodOtherTest.BAR);
        MatcherAssert.assertThat(left, CoreMatchers.not(right));
    }

    @Test
    public void testTypeOrderForPrimitiveWrapperTypes() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).defineField(EqualsMethodOtherTest.FOO, Object.class, Visibility.PUBLIC).defineField(EqualsMethodOtherTest.BAR, Integer.class, Visibility.PUBLIC).method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated().withNonNullableFields(ElementMatchers.any()).withPrimitiveWrapperTypedFieldsFirst()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(2));
        Object left = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Object right = loaded.getLoaded().getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(EqualsMethodOtherTest.BAR).set(left, 42);
        right.getClass().getDeclaredField(EqualsMethodOtherTest.BAR).set(right, 84);
        MatcherAssert.assertThat(left, CoreMatchers.not(right));
    }

    @Test
    public void testNaturalOrderComparator() {
        Comparator<FieldDescription.InDefinedShape> comparator = NaturalOrderComparator.INSTANCE;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(0));
    }

    @Test
    public void testPrimitiveTypeComparatorLeftPrimitive() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_PRIMITIVE_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(leftType.isPrimitive()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is((-1)));
    }

    @Test
    public void testPrimitiveTypeComparatorRightPrimitive() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_PRIMITIVE_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(rightType.isPrimitive()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(1));
    }

    @Test
    public void testPrimitiveTypeComparatorBothPrimitive() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_PRIMITIVE_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(leftType.isPrimitive()).thenReturn(true);
        Mockito.when(rightType.isPrimitive()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(0));
    }

    @Test
    public void testEnumerationTypeComparatorLeftEnumeration() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_ENUMERATION_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(leftType.isEnum()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is((-1)));
    }

    @Test
    public void testEnumerationTypeComparatorRightEnumeration() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_ENUMERATION_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(rightType.isEnum()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(1));
    }

    @Test
    public void testStringTypeComparatorBothEnumeration() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_ENUMERATION_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(leftType.isEnum()).thenReturn(true);
        Mockito.when(rightType.isEnum()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(0));
    }

    @Test
    public void testStringTypeComparatorLeftString() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_STRING_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(leftType.represents(String.class)).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is((-1)));
    }

    @Test
    public void testStringTypeComparatorRightString() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_STRING_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(rightType.represents(String.class)).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(1));
    }

    @Test
    public void testStringTypeComparatorBothString() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_STRING_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        Mockito.when(leftType.represents(String.class)).thenReturn(true);
        Mockito.when(rightType.represents(String.class)).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(0));
    }

    @Test
    public void testPrimitiveWrapperTypeComparatorLeftPrimitiveWrapper() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_PRIMITIVE_WRAPPER_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        TypeDescription leftErasure = Mockito.mock(TypeDescription.class);
        TypeDescription rightErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(leftType.asErasure()).thenReturn(leftErasure);
        Mockito.when(rightType.asErasure()).thenReturn(rightErasure);
        Mockito.when(leftErasure.isPrimitiveWrapper()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is((-1)));
    }

    @Test
    public void testPrimitiveWrapperTypeComparatorRightPrimitiveWrapper() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_PRIMITIVE_WRAPPER_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        TypeDescription leftErasure = Mockito.mock(TypeDescription.class);
        TypeDescription rightErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(leftType.asErasure()).thenReturn(leftErasure);
        Mockito.when(rightType.asErasure()).thenReturn(rightErasure);
        Mockito.when(rightErasure.isPrimitiveWrapper()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(1));
    }

    @Test
    public void testPrimitiveWrapperTypeComparatorBothPrimitiveWrapper() {
        Comparator<FieldDescription.InDefinedShape> comparator = TypePropertyComparator.FOR_PRIMITIVE_WRAPPER_TYPES;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        TypeDescription.Generic leftType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic rightType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(left.getType()).thenReturn(leftType);
        Mockito.when(right.getType()).thenReturn(rightType);
        TypeDescription leftErasure = Mockito.mock(TypeDescription.class);
        TypeDescription rightErasure = Mockito.mock(TypeDescription.class);
        Mockito.when(leftType.asErasure()).thenReturn(leftErasure);
        Mockito.when(rightType.asErasure()).thenReturn(rightErasure);
        Mockito.when(leftErasure.isPrimitiveWrapper()).thenReturn(true);
        Mockito.when(rightErasure.isPrimitiveWrapper()).thenReturn(true);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompoundComparatorNoComparator() {
        Comparator<FieldDescription.InDefinedShape> comparator = new EqualsMethod.CompoundComparator();
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompoundComparatorSingleComparator() {
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        Comparator<FieldDescription.InDefinedShape> delegate = Mockito.mock(Comparator.class);
        Mockito.when(delegate.compare(left, right)).thenReturn(42);
        Comparator<FieldDescription.InDefinedShape> comparator = new EqualsMethod.CompoundComparator(delegate);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(42));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompoundComparatorDoubleComparator() {
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        Comparator<FieldDescription.InDefinedShape> delegate = Mockito.mock(Comparator.class);
        Comparator<FieldDescription.InDefinedShape> first = Mockito.mock(Comparator.class);
        Mockito.when(delegate.compare(left, right)).thenReturn(42);
        Mockito.when(first.compare(left, right)).thenReturn(0);
        Comparator<FieldDescription.InDefinedShape> comparator = new EqualsMethod.CompoundComparator(first, delegate);
        MatcherAssert.assertThat(comparator.compare(left, right), CoreMatchers.is(42));
        Mockito.verify(first).compare(left, right);
    }

    @Test(expected = IllegalStateException.class)
    public void testInterface() throws Exception {
        new ByteBuddy().makeInterface().method(ElementMatchers.isEquals()).intercept(EqualsMethod.isolated()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompatibleReturn() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(EqualsMethodOtherTest.FOO, Object.class).withParameters(Object.class).intercept(EqualsMethod.isolated()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompatibleArgumentLength() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(EqualsMethodOtherTest.FOO, boolean.class).withParameters(Object.class, Object.class).intercept(EqualsMethod.isolated()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompatibleArgumentType() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(EqualsMethodOtherTest.FOO, boolean.class).withParameters(int.class).intercept(EqualsMethod.isolated()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticMethod() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(EqualsMethodOtherTest.FOO, boolean.class, Ownership.STATIC).withParameters(Object.class).intercept(EqualsMethod.isolated()).make();
    }

    public static class EqualsBase {
        @Override
        public boolean equals(Object other) {
            return true;
        }
    }

    public static class NonEqualsBase {
        @Override
        public boolean equals(Object other) {
            return false;
        }
    }
}


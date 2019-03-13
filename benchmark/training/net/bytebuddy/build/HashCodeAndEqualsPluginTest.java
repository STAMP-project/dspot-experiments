package net.bytebuddy.build;


import HashCodeAndEqualsPlugin.AnnotationOrderComparator;
import HashCodeAndEqualsPlugin.Enhance;
import HashCodeAndEqualsPlugin.Enhance.InvokeSuper.ALWAYS;
import HashCodeAndEqualsPlugin.Enhance.InvokeSuper.IF_ANNOTATED;
import HashCodeAndEqualsPlugin.Enhance.InvokeSuper.IF_DECLARED;
import HashCodeAndEqualsPlugin.Enhance.InvokeSuper.NEVER;
import HashCodeAndEqualsPlugin.Sorted;
import HashCodeAndEqualsPlugin.ValueHandling;
import HashCodeAndEqualsPlugin.ValueHandling.Sort;
import java.util.Comparator;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.EqualsMethod;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class HashCodeAndEqualsPluginTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Test
    public void testPluginMatches() throws Exception {
        Plugin plugin = new HashCodeAndEqualsPlugin();
        MatcherAssert.assertThat(plugin.matches(of(HashCodeAndEqualsPluginTest.SimpleSample.class)), Is.is(true));
        MatcherAssert.assertThat(plugin.matches(TypeDescription.OBJECT), Is.is(false));
    }

    @Test
    public void testPluginEnhance() throws Exception {
        Class<?> type = new HashCodeAndEqualsPlugin().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.SimpleSample.class), of(HashCodeAndEqualsPluginTest.SimpleSample.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.SimpleSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance().hashCode(), Is.is(type.getDeclaredConstructor().newInstance().hashCode()));
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), Is.is(type.getDeclaredConstructor().newInstance()));
    }

    @Test
    public void testPluginEnhanceRedundant() throws Exception {
        Class<?> type = new HashCodeAndEqualsPlugin().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.RedundantSample.class), of(HashCodeAndEqualsPluginTest.RedundantSample.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.RedundantSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance().hashCode(), Is.is(42));
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), Is.is(new Object()));
    }

    @Test
    public void testPluginEnhanceIgnore() throws Exception {
        Class<?> type = new HashCodeAndEqualsPlugin().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.IgnoredFieldSample.class), of(HashCodeAndEqualsPluginTest.IgnoredFieldSample.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.IgnoredFieldSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object left = type.getDeclaredConstructor().newInstance();
        Object right = type.getDeclaredConstructor().newInstance();
        type.getDeclaredField(HashCodeAndEqualsPluginTest.FOO).set(left, HashCodeAndEqualsPluginTest.FOO);
        type.getDeclaredField(HashCodeAndEqualsPluginTest.FOO).set(right, HashCodeAndEqualsPluginTest.BAR);
        MatcherAssert.assertThat(left.hashCode(), Is.is(right.hashCode()));
        MatcherAssert.assertThat(left, Is.is(right));
    }

    @Test(expected = NullPointerException.class)
    public void testPluginEnhanceNonNullableHashCode() throws Exception {
        new HashCodeAndEqualsPlugin().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.NonNullableField.class), of(HashCodeAndEqualsPluginTest.NonNullableField.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.NonNullableField.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded().getDeclaredConstructor().newInstance().hashCode();
    }

    @Test(expected = NullPointerException.class)
    public void testPluginEnhanceNonNullableEquals() throws Exception {
        Class<?> type = new HashCodeAndEqualsPlugin().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.NonNullableField.class), of(HashCodeAndEqualsPluginTest.NonNullableField.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.NonNullableField.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        type.getDeclaredConstructor().newInstance().equals(type.getDeclaredConstructor().newInstance());
    }

    @Test
    public void testPluginEnhanceNonNullableReversed() throws Exception {
        Class<?> type = new HashCodeAndEqualsPlugin.WithNonNullableFields().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.NonNullableField.class), of(HashCodeAndEqualsPluginTest.NonNullableField.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.NonNullableField.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object left = type.getDeclaredConstructor().newInstance();
        Object right = type.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(left.hashCode(), Is.is(right.hashCode()));
        MatcherAssert.assertThat(left, Is.is(right));
    }

    @Test(expected = NullPointerException.class)
    public void testPluginEnhanceNonNullableReversedHashCode() throws Exception {
        new HashCodeAndEqualsPlugin.WithNonNullableFields().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.SimpleSample.class), of(HashCodeAndEqualsPluginTest.SimpleSample.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.SimpleSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded().getDeclaredConstructor().newInstance().hashCode();
    }

    @Test(expected = NullPointerException.class)
    public void testPluginEnhanceNonNullableReversedEquals() throws Exception {
        Class<?> type = new HashCodeAndEqualsPlugin.WithNonNullableFields().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.SimpleSample.class), of(HashCodeAndEqualsPluginTest.SimpleSample.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.SimpleSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        type.getDeclaredConstructor().newInstance().equals(type.getDeclaredConstructor().newInstance());
    }

    @Test
    public void testPluginFieldOrder() throws Exception {
        Class<?> type = new HashCodeAndEqualsPlugin.WithNonNullableFields().apply(new ByteBuddy().redefine(HashCodeAndEqualsPluginTest.FieldSortOrderSample.class), of(HashCodeAndEqualsPluginTest.FieldSortOrderSample.class), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of(HashCodeAndEqualsPluginTest.FieldSortOrderSample.class.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object left = type.getDeclaredConstructor().newInstance();
        Object right = type.getDeclaredConstructor().newInstance();
        type.getDeclaredField(HashCodeAndEqualsPluginTest.QUX).set(left, HashCodeAndEqualsPluginTest.FOO);
        type.getDeclaredField(HashCodeAndEqualsPluginTest.QUX).set(right, HashCodeAndEqualsPluginTest.BAR);
        MatcherAssert.assertThat(left.equals(right), Is.is(false));
    }

    @Test
    public void testInvokeSuper() {
        MatcherAssert.assertThat(IF_ANNOTATED.equalsMethod(of(HashCodeAndEqualsPluginTest.SimpleSample.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.isolated()));
        MatcherAssert.assertThat(IF_ANNOTATED.equalsMethod(of(HashCodeAndEqualsPluginTest.SimpleSampleSubclass.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.requiringSuperClassEquality()));
        MatcherAssert.assertThat(IF_DECLARED.equalsMethod(of(HashCodeAndEqualsPluginTest.SimpleSample.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.isolated()));
        MatcherAssert.assertThat(IF_DECLARED.equalsMethod(of(HashCodeAndEqualsPluginTest.SimpleSampleSubclass.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.requiringSuperClassEquality()));
        MatcherAssert.assertThat(IF_DECLARED.equalsMethod(of(HashCodeAndEqualsPluginTest.DeclaredSubclass.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.requiringSuperClassEquality()));
        MatcherAssert.assertThat(ALWAYS.equalsMethod(of(HashCodeAndEqualsPluginTest.SimpleSample.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.requiringSuperClassEquality()));
        MatcherAssert.assertThat(ALWAYS.equalsMethod(of(HashCodeAndEqualsPluginTest.SimpleSampleSubclass.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.requiringSuperClassEquality()));
        MatcherAssert.assertThat(NEVER.equalsMethod(of(HashCodeAndEqualsPluginTest.SimpleSample.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.isolated()));
        MatcherAssert.assertThat(NEVER.equalsMethod(of(HashCodeAndEqualsPluginTest.SimpleSampleSubclass.class)), FieldByFieldComparison.hasPrototype(EqualsMethod.isolated()));
    }

    @Test
    public void testAnnotationComparatorEqualsNoAnnotations() {
        Comparator<FieldDescription.InDefinedShape> comparator = AnnotationOrderComparator.INSTANCE;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        Mockito.when(left.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(right.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        MatcherAssert.assertThat(comparator.compare(left, right), Is.is(0));
    }

    @Test
    public void testAnnotationComparatorEqualsEqualAnnotations() {
        Comparator<FieldDescription.InDefinedShape> comparator = AnnotationOrderComparator.INSTANCE;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        Mockito.when(left.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(ofType(Sorted.class).define("value", 0).build()));
        Mockito.when(right.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(ofType(Sorted.class).define("value", 0).build()));
        MatcherAssert.assertThat(comparator.compare(left, right), Is.is(0));
    }

    @Test
    public void testAnnotationComparatorEqualsLeftBiggerAnnotations() {
        Comparator<FieldDescription.InDefinedShape> comparator = AnnotationOrderComparator.INSTANCE;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        Mockito.when(left.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(ofType(Sorted.class).define("value", 42).build()));
        Mockito.when(right.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(ofType(Sorted.class).define("value", 0).build()));
        MatcherAssert.assertThat(comparator.compare(left, right), Is.is((-1)));
    }

    @Test
    public void testAnnotationComparatorEqualsRightBiggerAnnotations() {
        Comparator<FieldDescription.InDefinedShape> comparator = AnnotationOrderComparator.INSTANCE;
        FieldDescription.InDefinedShape left = Mockito.mock(FieldDescription.InDefinedShape.class);
        FieldDescription.InDefinedShape right = Mockito.mock(FieldDescription.InDefinedShape.class);
        Mockito.when(left.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(ofType(Sorted.class).define("value", 0).build()));
        Mockito.when(right.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(ofType(Sorted.class).define("value", 42).build()));
        MatcherAssert.assertThat(comparator.compare(left, right), Is.is(1));
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class SimpleSample {
        private String foo;
    }

    /* empty */
    @HashCodeAndEqualsPlugin.Enhance
    public static class SimpleSampleSubclass extends HashCodeAndEqualsPluginTest.SimpleSample {}

    public static class Declared {
        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object other) {
            return false;
        }
    }

    /* empty */
    @HashCodeAndEqualsPlugin.Enhance
    public static class DeclaredSubclass extends HashCodeAndEqualsPluginTest.Declared {}

    @HashCodeAndEqualsPlugin.Enhance
    public static class RedundantSample {
        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object other) {
            return true;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class IgnoredFieldSample {
        @HashCodeAndEqualsPlugin.ValueHandling(Sort.IGNORE)
        public String foo;
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class NonNullableField {
        @HashCodeAndEqualsPlugin.ValueHandling(Sort.REVERSE_NULLABILITY)
        public String foo;
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class FieldSortOrderSample {
        public String foo;

        @HashCodeAndEqualsPlugin.Sorted(-1)
        public String bar;

        @HashCodeAndEqualsPlugin.Sorted(1)
        public String qux;
    }
}


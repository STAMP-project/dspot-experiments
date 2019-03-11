package net.bytebuddy.description.annotation;


import OpenedClassReader.ASM_API;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.pool.TypePool;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

import static net.bytebuddy.description.annotation.AnnotationDescription.ForLoadedAnnotation.of;


public abstract class AbstractAnnotationDescriptionTest {
    private static final boolean BOOLEAN = true;

    private static final boolean[] BOOLEAN_ARRAY = new boolean[]{ AbstractAnnotationDescriptionTest.BOOLEAN };

    private static final byte BYTE = 42;

    private static final byte[] BYTE_ARRAY = new byte[]{ AbstractAnnotationDescriptionTest.BYTE };

    private static final short SHORT = 42;

    private static final short[] SHORT_ARRAY = new short[]{ AbstractAnnotationDescriptionTest.SHORT };

    private static final char CHARACTER = 42;

    private static final char[] CHARACTER_ARRAY = new char[]{ AbstractAnnotationDescriptionTest.CHARACTER };

    private static final int INTEGER = 42;

    private static final int[] INTEGER_ARRAY = new int[]{ AbstractAnnotationDescriptionTest.INTEGER };

    private static final long LONG = 42L;

    private static final long[] LONG_ARRAY = new long[]{ AbstractAnnotationDescriptionTest.LONG };

    private static final float FLOAT = 42.0F;

    private static final float[] FLOAT_ARRAY = new float[]{ AbstractAnnotationDescriptionTest.FLOAT };

    private static final double DOUBLE = 42.0;

    private static final double[] DOUBLE_ARRAY = new double[]{ AbstractAnnotationDescriptionTest.DOUBLE };

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String[] STRING_ARRAY = new String[]{ AbstractAnnotationDescriptionTest.FOO };

    private static final AbstractAnnotationDescriptionTest.SampleEnumeration ENUMERATION = AbstractAnnotationDescriptionTest.SampleEnumeration.VALUE;

    private static final AbstractAnnotationDescriptionTest.SampleEnumeration[] ENUMERATION_ARRAY = new AbstractAnnotationDescriptionTest.SampleEnumeration[]{ AbstractAnnotationDescriptionTest.ENUMERATION };

    private static final Class<?> CLASS = Void.class;

    private static final Class<?>[] CLASS_ARRAY = new Class<?>[]{ AbstractAnnotationDescriptionTest.CLASS };

    private static final Class<?> ARRAY_CLASS = Void[].class;

    private static final AbstractAnnotationDescriptionTest.Other ANNOTATION = AbstractAnnotationDescriptionTest.EnumerationCarrier.class.getAnnotation(AbstractAnnotationDescriptionTest.Other.class);

    private static final AbstractAnnotationDescriptionTest.Other[] ANNOTATION_ARRAY = new AbstractAnnotationDescriptionTest.Other[]{ AbstractAnnotationDescriptionTest.ANNOTATION };

    private static final boolean OTHER_BOOLEAN = false;

    private static final boolean[] OTHER_BOOLEAN_ARRAY = new boolean[]{ AbstractAnnotationDescriptionTest.OTHER_BOOLEAN };

    private static final byte OTHER_BYTE = 42 * 2;

    private static final byte[] OTHER_BYTE_ARRAY = new byte[]{ AbstractAnnotationDescriptionTest.OTHER_BYTE };

    private static final short OTHER_SHORT = 42 * 2;

    private static final short[] OTHER_SHORT_ARRAY = new short[]{ AbstractAnnotationDescriptionTest.OTHER_SHORT };

    private static final char OTHER_CHARACTER = 42 * 2;

    private static final char[] OTHER_CHARACTER_ARRAY = new char[]{ AbstractAnnotationDescriptionTest.OTHER_CHARACTER };

    private static final int OTHER_INTEGER = 42 * 2;

    private static final int[] OTHER_INTEGER_ARRAY = new int[]{ AbstractAnnotationDescriptionTest.OTHER_INTEGER };

    private static final long OTHER_LONG = 42L * 2;

    private static final long[] OTHER_LONG_ARRAY = new long[]{ AbstractAnnotationDescriptionTest.OTHER_LONG };

    private static final float OTHER_FLOAT = 42.0F * 2;

    private static final float[] OTHER_FLOAT_ARRAY = new float[]{ AbstractAnnotationDescriptionTest.OTHER_FLOAT };

    private static final double OTHER_DOUBLE = 42.0 * 2;

    private static final double[] OTHER_DOUBLE_ARRAY = new double[]{ AbstractAnnotationDescriptionTest.OTHER_DOUBLE };

    private static final AbstractAnnotationDescriptionTest.SampleEnumeration OTHER_ENUMERATION = AbstractAnnotationDescriptionTest.SampleEnumeration.OTHER;

    private static final AbstractAnnotationDescriptionTest.SampleEnumeration[] OTHER_ENUMERATION_ARRAY = new AbstractAnnotationDescriptionTest.SampleEnumeration[]{ AbstractAnnotationDescriptionTest.OTHER_ENUMERATION };

    private static final Class<?> OTHER_CLASS = Object.class;

    private static final Class<?>[] OTHER_CLASS_ARRAY = new Class<?>[]{ AbstractAnnotationDescriptionTest.OTHER_CLASS };

    private static final Class<?> OTHER_ARRAY_CLASS = Object[].class;

    private static final AbstractAnnotationDescriptionTest.Other OTHER_ANNOTATION = AbstractAnnotationDescriptionTest.OtherEnumerationCarrier.class.getAnnotation(AbstractAnnotationDescriptionTest.Other.class);

    private static final AbstractAnnotationDescriptionTest.Other[] OTHER_ANNOTATION_ARRAY = new AbstractAnnotationDescriptionTest.Other[]{ AbstractAnnotationDescriptionTest.OTHER_ANNOTATION };

    private static final String[] OTHER_STRING_ARRAY = new String[]{ AbstractAnnotationDescriptionTest.BAR };

    private Annotation first;

    private Annotation second;

    private Annotation defaultFirst;

    private Annotation defaultSecond;

    private Annotation explicitTarget;

    private Annotation broken;

    private Class<?> brokenCarrier;

    @Test
    public void testPrecondition() throws Exception {
        MatcherAssert.assertThat(describe(first), CoreMatchers.is(describe(first)));
        MatcherAssert.assertThat(describe(second), CoreMatchers.is(describe(second)));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(describe(second)));
        MatcherAssert.assertThat(describe(first).getAnnotationType(), CoreMatchers.is(describe(second).getAnnotationType()));
        MatcherAssert.assertThat(describe(first).getAnnotationType(), CoreMatchers.not(((TypeDescription) (TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.Other.class)))));
        MatcherAssert.assertThat(describe(second).getAnnotationType(), CoreMatchers.not(((TypeDescription) (TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.Other.class)))));
        MatcherAssert.assertThat(describe(first).getAnnotationType().represents(first.annotationType()), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(second).getAnnotationType().represents(second.annotationType()), CoreMatchers.is(true));
    }

    @Test
    public void assertToString() throws Exception {
        assertToString(describe(first).toString(), first);
        assertToString(describe(second).toString(), second);
    }

    @Test
    public void testHashCode() throws Exception {
        MatcherAssert.assertThat(describe(first).hashCode(), CoreMatchers.is(describe(first).hashCode()));
        MatcherAssert.assertThat(describe(second).hashCode(), CoreMatchers.is(describe(second).hashCode()));
        MatcherAssert.assertThat(describe(first).hashCode(), CoreMatchers.not(describe(second).hashCode()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEquals() throws Exception {
        AnnotationDescription identical = describe(first);
        MatcherAssert.assertThat(identical, CoreMatchers.is(identical));
        AnnotationDescription equalFirst = Mockito.mock(AnnotationDescription.class);
        Mockito.when(equalFirst.getAnnotationType()).thenReturn(TypeDescription.ForLoadedType.of(first.annotationType()));
        Mockito.when(equalFirst.getValue(Mockito.any(MethodDescription.InDefinedShape.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MethodDescription.InDefinedShape method = ((MethodDescription.InDefinedShape) (invocation.getArguments()[0]));
                return of(first).getValue(method);
            }
        });
        MatcherAssert.assertThat(describe(first), CoreMatchers.is(equalFirst));
        AnnotationDescription equalSecond = Mockito.mock(AnnotationDescription.class);
        Mockito.when(equalSecond.getAnnotationType()).thenReturn(TypeDescription.ForLoadedType.of(first.annotationType()));
        Mockito.when(equalSecond.getValue(Mockito.any(MethodDescription.InDefinedShape.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MethodDescription.InDefinedShape method = ((MethodDescription.InDefinedShape) (invocation.getArguments()[0]));
                return of(second).getValue(method);
            }
        });
        MatcherAssert.assertThat(describe(second), CoreMatchers.is(equalSecond));
        AnnotationDescription equalFirstTypeOnly = Mockito.mock(AnnotationDescription.class);
        Mockito.when(equalFirstTypeOnly.getAnnotationType()).thenReturn(TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.Other.class));
        Mockito.when(equalFirstTypeOnly.getValue(Mockito.any(MethodDescription.InDefinedShape.class))).then(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MethodDescription.InDefinedShape method = ((MethodDescription.InDefinedShape) (invocation.getArguments()[0]));
                return of(first).getValue(method);
            }
        });
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(equalFirstTypeOnly));
        AnnotationDescription equalFirstNameOnly = Mockito.mock(AnnotationDescription.class);
        Mockito.when(equalFirstNameOnly.getAnnotationType()).thenReturn(TypeDescription.ForLoadedType.of(first.annotationType()));
        AnnotationValue<?, ?> annotationValue = Mockito.mock(AnnotationValue.class);
        Mockito.when(annotationValue.resolve()).thenReturn(null);
        Mockito.when(equalFirstNameOnly.getValue(Mockito.any(MethodDescription.InDefinedShape.class))).thenReturn(((AnnotationValue) (annotationValue)));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(equalFirstNameOnly));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(equalSecond));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(CoreMatchers.equalTo(null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalMethod() throws Exception {
        describe(first).getValue(new MethodDescription.ForLoadedMethod(Object.class.getMethod("toString")));
    }

    @Test
    public void testLoadedEquals() throws Exception {
        MatcherAssert.assertThat(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load(), CoreMatchers.is(first));
        MatcherAssert.assertThat(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load(), CoreMatchers.is(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load()));
        MatcherAssert.assertThat(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load(), CoreMatchers.not(describe(second).prepare(AbstractAnnotationDescriptionTest.Sample.class).load()));
        MatcherAssert.assertThat(describe(second).prepare(AbstractAnnotationDescriptionTest.Sample.class).load(), CoreMatchers.is(second));
        MatcherAssert.assertThat(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load(), CoreMatchers.not(second));
    }

    @Test
    public void testLoadedHashCode() throws Exception {
        MatcherAssert.assertThat(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load().hashCode(), CoreMatchers.is(first.hashCode()));
        MatcherAssert.assertThat(describe(second).prepare(AbstractAnnotationDescriptionTest.Sample.class).load().hashCode(), CoreMatchers.is(second.hashCode()));
        MatcherAssert.assertThat(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load().hashCode(), CoreMatchers.not(second.hashCode()));
    }

    @Test
    public void testLoadedToString() throws Exception {
        assertToString(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load().toString(), first);
        assertToString(describe(second).prepare(AbstractAnnotationDescriptionTest.Sample.class).load().toString(), second);
    }

    @Test
    public void testToString() throws Exception {
        assertToString(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).toString(), first);
        assertToString(describe(second).prepare(AbstractAnnotationDescriptionTest.Sample.class).toString(), second);
    }

    @Test
    public void testLoadedAnnotationType() throws Exception {
        MatcherAssert.assertThat(describe(first).prepare(AbstractAnnotationDescriptionTest.Sample.class).load().annotationType(), CoreMatchers.<Class<?>>is(AbstractAnnotationDescriptionTest.Sample.class));
        MatcherAssert.assertThat(describe(second).prepare(AbstractAnnotationDescriptionTest.Sample.class).load().annotationType(), CoreMatchers.<Class<?>>is(AbstractAnnotationDescriptionTest.Sample.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPreparation() throws Exception {
        describe(first).prepare(AbstractAnnotationDescriptionTest.Other.class);
    }

    @Test
    public void testValues() throws Exception {
        assertValue(first, "booleanValue", AbstractAnnotationDescriptionTest.BOOLEAN, AbstractAnnotationDescriptionTest.BOOLEAN);
        assertValue(second, "booleanValue", AbstractAnnotationDescriptionTest.BOOLEAN, AbstractAnnotationDescriptionTest.BOOLEAN);
        assertValue(first, "byteValue", AbstractAnnotationDescriptionTest.BYTE, AbstractAnnotationDescriptionTest.BYTE);
        assertValue(second, "byteValue", AbstractAnnotationDescriptionTest.BYTE, AbstractAnnotationDescriptionTest.BYTE);
        assertValue(first, "shortValue", AbstractAnnotationDescriptionTest.SHORT, AbstractAnnotationDescriptionTest.SHORT);
        assertValue(second, "shortValue", AbstractAnnotationDescriptionTest.SHORT, AbstractAnnotationDescriptionTest.SHORT);
        assertValue(first, "charValue", AbstractAnnotationDescriptionTest.CHARACTER, AbstractAnnotationDescriptionTest.CHARACTER);
        assertValue(second, "charValue", AbstractAnnotationDescriptionTest.CHARACTER, AbstractAnnotationDescriptionTest.CHARACTER);
        assertValue(first, "intValue", AbstractAnnotationDescriptionTest.INTEGER, AbstractAnnotationDescriptionTest.INTEGER);
        assertValue(second, "intValue", AbstractAnnotationDescriptionTest.INTEGER, AbstractAnnotationDescriptionTest.INTEGER);
        assertValue(first, "longValue", AbstractAnnotationDescriptionTest.LONG, AbstractAnnotationDescriptionTest.LONG);
        assertValue(second, "longValue", AbstractAnnotationDescriptionTest.LONG, AbstractAnnotationDescriptionTest.LONG);
        assertValue(first, "floatValue", AbstractAnnotationDescriptionTest.FLOAT, AbstractAnnotationDescriptionTest.FLOAT);
        assertValue(second, "floatValue", AbstractAnnotationDescriptionTest.FLOAT, AbstractAnnotationDescriptionTest.FLOAT);
        assertValue(first, "doubleValue", AbstractAnnotationDescriptionTest.DOUBLE, AbstractAnnotationDescriptionTest.DOUBLE);
        assertValue(second, "doubleValue", AbstractAnnotationDescriptionTest.DOUBLE, AbstractAnnotationDescriptionTest.DOUBLE);
        assertValue(first, "stringValue", AbstractAnnotationDescriptionTest.FOO, AbstractAnnotationDescriptionTest.FOO);
        assertValue(second, "stringValue", AbstractAnnotationDescriptionTest.BAR, AbstractAnnotationDescriptionTest.BAR);
        assertValue(first, "classValue", TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.CLASS), AbstractAnnotationDescriptionTest.CLASS);
        assertValue(second, "classValue", TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.CLASS), AbstractAnnotationDescriptionTest.CLASS);
        assertValue(first, "arrayClassValue", TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.ARRAY_CLASS), AbstractAnnotationDescriptionTest.ARRAY_CLASS);
        assertValue(second, "arrayClassValue", TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.ARRAY_CLASS), AbstractAnnotationDescriptionTest.ARRAY_CLASS);
        assertValue(first, "enumValue", new EnumerationDescription.ForLoadedEnumeration(AbstractAnnotationDescriptionTest.ENUMERATION), AbstractAnnotationDescriptionTest.ENUMERATION);
        assertValue(second, "enumValue", new EnumerationDescription.ForLoadedEnumeration(AbstractAnnotationDescriptionTest.ENUMERATION), AbstractAnnotationDescriptionTest.ENUMERATION);
        assertValue(first, "annotationValue", of(AbstractAnnotationDescriptionTest.ANNOTATION), AbstractAnnotationDescriptionTest.ANNOTATION);
        assertValue(second, "annotationValue", of(AbstractAnnotationDescriptionTest.ANNOTATION), AbstractAnnotationDescriptionTest.ANNOTATION);
        assertValue(first, "booleanArrayValue", AbstractAnnotationDescriptionTest.BOOLEAN_ARRAY, AbstractAnnotationDescriptionTest.BOOLEAN_ARRAY);
        assertValue(second, "booleanArrayValue", AbstractAnnotationDescriptionTest.BOOLEAN_ARRAY, AbstractAnnotationDescriptionTest.BOOLEAN_ARRAY);
        assertValue(first, "byteArrayValue", AbstractAnnotationDescriptionTest.BYTE_ARRAY, AbstractAnnotationDescriptionTest.BYTE_ARRAY);
        assertValue(second, "byteArrayValue", AbstractAnnotationDescriptionTest.BYTE_ARRAY, AbstractAnnotationDescriptionTest.BYTE_ARRAY);
        assertValue(first, "shortArrayValue", AbstractAnnotationDescriptionTest.SHORT_ARRAY, AbstractAnnotationDescriptionTest.SHORT_ARRAY);
        assertValue(second, "shortArrayValue", AbstractAnnotationDescriptionTest.SHORT_ARRAY, AbstractAnnotationDescriptionTest.SHORT_ARRAY);
        assertValue(first, "charArrayValue", AbstractAnnotationDescriptionTest.CHARACTER_ARRAY, AbstractAnnotationDescriptionTest.CHARACTER_ARRAY);
        assertValue(second, "charArrayValue", AbstractAnnotationDescriptionTest.CHARACTER_ARRAY, AbstractAnnotationDescriptionTest.CHARACTER_ARRAY);
        assertValue(first, "intArrayValue", AbstractAnnotationDescriptionTest.INTEGER_ARRAY, AbstractAnnotationDescriptionTest.INTEGER_ARRAY);
        assertValue(second, "intArrayValue", AbstractAnnotationDescriptionTest.INTEGER_ARRAY, AbstractAnnotationDescriptionTest.INTEGER_ARRAY);
        assertValue(first, "longArrayValue", AbstractAnnotationDescriptionTest.LONG_ARRAY, AbstractAnnotationDescriptionTest.LONG_ARRAY);
        assertValue(second, "longArrayValue", AbstractAnnotationDescriptionTest.LONG_ARRAY, AbstractAnnotationDescriptionTest.LONG_ARRAY);
        assertValue(first, "floatArrayValue", AbstractAnnotationDescriptionTest.FLOAT_ARRAY, AbstractAnnotationDescriptionTest.FLOAT_ARRAY);
        assertValue(second, "floatArrayValue", AbstractAnnotationDescriptionTest.FLOAT_ARRAY, AbstractAnnotationDescriptionTest.FLOAT_ARRAY);
        assertValue(first, "doubleArrayValue", AbstractAnnotationDescriptionTest.DOUBLE_ARRAY, AbstractAnnotationDescriptionTest.DOUBLE_ARRAY);
        assertValue(second, "doubleArrayValue", AbstractAnnotationDescriptionTest.DOUBLE_ARRAY, AbstractAnnotationDescriptionTest.DOUBLE_ARRAY);
        assertValue(first, "stringArrayValue", AbstractAnnotationDescriptionTest.STRING_ARRAY, AbstractAnnotationDescriptionTest.STRING_ARRAY);
        assertValue(second, "stringArrayValue", AbstractAnnotationDescriptionTest.STRING_ARRAY, AbstractAnnotationDescriptionTest.STRING_ARRAY);
        assertValue(first, "classArrayValue", new TypeDescription[]{ TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.CLASS) }, AbstractAnnotationDescriptionTest.CLASS_ARRAY);
        assertValue(second, "classArrayValue", new TypeDescription[]{ TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.CLASS) }, AbstractAnnotationDescriptionTest.CLASS_ARRAY);
        assertValue(first, "enumArrayValue", new EnumerationDescription[]{ new EnumerationDescription.ForLoadedEnumeration(AbstractAnnotationDescriptionTest.ENUMERATION) }, AbstractAnnotationDescriptionTest.ENUMERATION_ARRAY);
        assertValue(second, "enumArrayValue", new EnumerationDescription[]{ new EnumerationDescription.ForLoadedEnumeration(AbstractAnnotationDescriptionTest.ENUMERATION) }, AbstractAnnotationDescriptionTest.ENUMERATION_ARRAY);
        assertValue(first, "annotationArrayValue", new AnnotationDescription[]{ of(AbstractAnnotationDescriptionTest.ANNOTATION) }, AbstractAnnotationDescriptionTest.ANNOTATION_ARRAY);
        assertValue(second, "annotationArrayValue", new AnnotationDescription[]{ of(AbstractAnnotationDescriptionTest.ANNOTATION) }, AbstractAnnotationDescriptionTest.ANNOTATION_ARRAY);
    }

    @Test
    public void testValuesDefaults() throws Exception {
        assertValue(defaultFirst, "booleanValue", AbstractAnnotationDescriptionTest.BOOLEAN, AbstractAnnotationDescriptionTest.BOOLEAN);
        assertValue(defaultSecond, "booleanValue", AbstractAnnotationDescriptionTest.OTHER_BOOLEAN, AbstractAnnotationDescriptionTest.OTHER_BOOLEAN);
        assertValue(defaultFirst, "byteValue", AbstractAnnotationDescriptionTest.BYTE, AbstractAnnotationDescriptionTest.BYTE);
        assertValue(defaultSecond, "byteValue", AbstractAnnotationDescriptionTest.OTHER_BYTE, AbstractAnnotationDescriptionTest.OTHER_BYTE);
        assertValue(defaultFirst, "shortValue", AbstractAnnotationDescriptionTest.SHORT, AbstractAnnotationDescriptionTest.SHORT);
        assertValue(defaultSecond, "shortValue", AbstractAnnotationDescriptionTest.OTHER_SHORT, AbstractAnnotationDescriptionTest.OTHER_SHORT);
        assertValue(defaultFirst, "charValue", AbstractAnnotationDescriptionTest.CHARACTER, AbstractAnnotationDescriptionTest.CHARACTER);
        assertValue(defaultSecond, "charValue", AbstractAnnotationDescriptionTest.OTHER_CHARACTER, AbstractAnnotationDescriptionTest.OTHER_CHARACTER);
        assertValue(defaultFirst, "intValue", AbstractAnnotationDescriptionTest.INTEGER, AbstractAnnotationDescriptionTest.INTEGER);
        assertValue(defaultSecond, "intValue", AbstractAnnotationDescriptionTest.OTHER_INTEGER, AbstractAnnotationDescriptionTest.OTHER_INTEGER);
        assertValue(defaultFirst, "longValue", AbstractAnnotationDescriptionTest.LONG, AbstractAnnotationDescriptionTest.LONG);
        assertValue(defaultSecond, "longValue", AbstractAnnotationDescriptionTest.OTHER_LONG, AbstractAnnotationDescriptionTest.OTHER_LONG);
        assertValue(defaultFirst, "floatValue", AbstractAnnotationDescriptionTest.FLOAT, AbstractAnnotationDescriptionTest.FLOAT);
        assertValue(defaultSecond, "floatValue", AbstractAnnotationDescriptionTest.OTHER_FLOAT, AbstractAnnotationDescriptionTest.OTHER_FLOAT);
        assertValue(defaultFirst, "doubleValue", AbstractAnnotationDescriptionTest.DOUBLE, AbstractAnnotationDescriptionTest.DOUBLE);
        assertValue(defaultSecond, "doubleValue", AbstractAnnotationDescriptionTest.OTHER_DOUBLE, AbstractAnnotationDescriptionTest.OTHER_DOUBLE);
        assertValue(defaultFirst, "stringValue", AbstractAnnotationDescriptionTest.FOO, AbstractAnnotationDescriptionTest.FOO);
        assertValue(defaultSecond, "stringValue", AbstractAnnotationDescriptionTest.BAR, AbstractAnnotationDescriptionTest.BAR);
        assertValue(defaultFirst, "classValue", TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.CLASS), AbstractAnnotationDescriptionTest.CLASS);
        assertValue(defaultSecond, "classValue", TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.OTHER_CLASS), AbstractAnnotationDescriptionTest.OTHER_CLASS);
        assertValue(defaultFirst, "arrayClassValue", TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.ARRAY_CLASS), AbstractAnnotationDescriptionTest.ARRAY_CLASS);
        assertValue(defaultSecond, "arrayClassValue", TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.OTHER_ARRAY_CLASS), AbstractAnnotationDescriptionTest.OTHER_ARRAY_CLASS);
        assertValue(defaultFirst, "enumValue", new EnumerationDescription.ForLoadedEnumeration(AbstractAnnotationDescriptionTest.ENUMERATION), AbstractAnnotationDescriptionTest.ENUMERATION);
        assertValue(defaultSecond, "enumValue", new EnumerationDescription.ForLoadedEnumeration(AbstractAnnotationDescriptionTest.OTHER_ENUMERATION), AbstractAnnotationDescriptionTest.OTHER_ENUMERATION);
        assertValue(defaultFirst, "annotationValue", of(AbstractAnnotationDescriptionTest.ANNOTATION), AbstractAnnotationDescriptionTest.ANNOTATION);
        assertValue(defaultSecond, "annotationValue", of(AbstractAnnotationDescriptionTest.OTHER_ANNOTATION), AbstractAnnotationDescriptionTest.OTHER_ANNOTATION);
        assertValue(defaultFirst, "booleanArrayValue", AbstractAnnotationDescriptionTest.BOOLEAN_ARRAY, AbstractAnnotationDescriptionTest.BOOLEAN_ARRAY);
        assertValue(defaultSecond, "booleanArrayValue", AbstractAnnotationDescriptionTest.OTHER_BOOLEAN_ARRAY, AbstractAnnotationDescriptionTest.OTHER_BOOLEAN_ARRAY);
        assertValue(defaultFirst, "byteArrayValue", AbstractAnnotationDescriptionTest.BYTE_ARRAY, AbstractAnnotationDescriptionTest.BYTE_ARRAY);
        assertValue(defaultSecond, "byteArrayValue", AbstractAnnotationDescriptionTest.OTHER_BYTE_ARRAY, AbstractAnnotationDescriptionTest.OTHER_BYTE_ARRAY);
        assertValue(defaultFirst, "shortArrayValue", AbstractAnnotationDescriptionTest.SHORT_ARRAY, AbstractAnnotationDescriptionTest.SHORT_ARRAY);
        assertValue(defaultSecond, "shortArrayValue", AbstractAnnotationDescriptionTest.OTHER_SHORT_ARRAY, AbstractAnnotationDescriptionTest.OTHER_SHORT_ARRAY);
        assertValue(defaultFirst, "charArrayValue", AbstractAnnotationDescriptionTest.CHARACTER_ARRAY, AbstractAnnotationDescriptionTest.CHARACTER_ARRAY);
        assertValue(defaultSecond, "charArrayValue", AbstractAnnotationDescriptionTest.OTHER_CHARACTER_ARRAY, AbstractAnnotationDescriptionTest.OTHER_CHARACTER_ARRAY);
        assertValue(defaultFirst, "intArrayValue", AbstractAnnotationDescriptionTest.INTEGER_ARRAY, AbstractAnnotationDescriptionTest.INTEGER_ARRAY);
        assertValue(defaultSecond, "intArrayValue", AbstractAnnotationDescriptionTest.OTHER_INTEGER_ARRAY, AbstractAnnotationDescriptionTest.OTHER_INTEGER_ARRAY);
        assertValue(defaultFirst, "longArrayValue", AbstractAnnotationDescriptionTest.LONG_ARRAY, AbstractAnnotationDescriptionTest.LONG_ARRAY);
        assertValue(defaultSecond, "longArrayValue", AbstractAnnotationDescriptionTest.OTHER_LONG_ARRAY, AbstractAnnotationDescriptionTest.OTHER_LONG_ARRAY);
        assertValue(defaultFirst, "floatArrayValue", AbstractAnnotationDescriptionTest.FLOAT_ARRAY, AbstractAnnotationDescriptionTest.FLOAT_ARRAY);
        assertValue(defaultSecond, "floatArrayValue", AbstractAnnotationDescriptionTest.OTHER_FLOAT_ARRAY, AbstractAnnotationDescriptionTest.OTHER_FLOAT_ARRAY);
        assertValue(defaultFirst, "doubleArrayValue", AbstractAnnotationDescriptionTest.DOUBLE_ARRAY, AbstractAnnotationDescriptionTest.DOUBLE_ARRAY);
        assertValue(defaultSecond, "doubleArrayValue", AbstractAnnotationDescriptionTest.OTHER_DOUBLE_ARRAY, AbstractAnnotationDescriptionTest.OTHER_DOUBLE_ARRAY);
        assertValue(defaultFirst, "stringArrayValue", AbstractAnnotationDescriptionTest.STRING_ARRAY, AbstractAnnotationDescriptionTest.STRING_ARRAY);
        assertValue(defaultSecond, "stringArrayValue", AbstractAnnotationDescriptionTest.OTHER_STRING_ARRAY, AbstractAnnotationDescriptionTest.OTHER_STRING_ARRAY);
        assertValue(defaultFirst, "classArrayValue", new TypeDescription[]{ TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.CLASS) }, AbstractAnnotationDescriptionTest.CLASS_ARRAY);
        assertValue(defaultSecond, "classArrayValue", new TypeDescription[]{ TypeDescription.ForLoadedType.of(AbstractAnnotationDescriptionTest.OTHER_CLASS) }, AbstractAnnotationDescriptionTest.OTHER_CLASS_ARRAY);
        assertValue(defaultFirst, "enumArrayValue", new EnumerationDescription[]{ new EnumerationDescription.ForLoadedEnumeration(AbstractAnnotationDescriptionTest.ENUMERATION) }, AbstractAnnotationDescriptionTest.ENUMERATION_ARRAY);
        assertValue(defaultSecond, "enumArrayValue", new EnumerationDescription[]{ new EnumerationDescription.ForLoadedEnumeration(AbstractAnnotationDescriptionTest.OTHER_ENUMERATION) }, AbstractAnnotationDescriptionTest.OTHER_ENUMERATION_ARRAY);
        assertValue(defaultFirst, "annotationArrayValue", new AnnotationDescription[]{ of(AbstractAnnotationDescriptionTest.ANNOTATION) }, AbstractAnnotationDescriptionTest.ANNOTATION_ARRAY);
        assertValue(defaultSecond, "annotationArrayValue", new AnnotationDescription[]{ of(AbstractAnnotationDescriptionTest.OTHER_ANNOTATION) }, AbstractAnnotationDescriptionTest.OTHER_ANNOTATION_ARRAY);
    }

    @Test
    public void testRetention() throws Exception {
        MatcherAssert.assertThat(describe(first).getRetention(), CoreMatchers.is(RetentionPolicy.RUNTIME));
    }

    @Test
    public void testAnnotationTarget() throws Exception {
        MatcherAssert.assertThat(describe(first).getElementTypes(), CoreMatchers.is(((Set<ElementType>) (new HashSet<ElementType>(Arrays.asList(ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PACKAGE, ElementType.PARAMETER, ElementType.TYPE))))));
        MatcherAssert.assertThat(describe(explicitTarget).getElementTypes(), CoreMatchers.is(Collections.singleton(ElementType.TYPE)));
    }

    @Test
    public void testInheritance() throws Exception {
        MatcherAssert.assertThat(describe(first).isInherited(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(defaultFirst).isInherited(), CoreMatchers.is(true));
    }

    @Test
    public void testDocumented() throws Exception {
        MatcherAssert.assertThat(describe(first).isDocumented(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(defaultFirst).isDocumented(), CoreMatchers.is(true));
    }

    public enum SampleEnumeration {

        VALUE,
        OTHER;}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Sample2 {
        Class<?> foo();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Sample {
        boolean booleanValue();

        byte byteValue();

        short shortValue();

        char charValue();

        int intValue();

        long longValue();

        float floatValue();

        double doubleValue();

        String stringValue();

        Class<?> classValue();

        Class<?> arrayClassValue();

        AbstractAnnotationDescriptionTest.SampleEnumeration enumValue();

        AbstractAnnotationDescriptionTest.Other annotationValue();

        boolean[] booleanArrayValue();

        byte[] byteArrayValue();

        short[] shortArrayValue();

        char[] charArrayValue();

        int[] intArrayValue();

        long[] longArrayValue();

        float[] floatArrayValue();

        double[] doubleArrayValue();

        String[] stringArrayValue();

        Class<?>[] classArrayValue();

        AbstractAnnotationDescriptionTest.SampleEnumeration[] enumArrayValue();

        AbstractAnnotationDescriptionTest.Other[] annotationArrayValue();
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SampleDefault {
        boolean booleanValue() default AbstractAnnotationDescriptionTest.BOOLEAN;

        byte byteValue() default AbstractAnnotationDescriptionTest.BYTE;

        short shortValue() default AbstractAnnotationDescriptionTest.SHORT;

        char charValue() default AbstractAnnotationDescriptionTest.CHARACTER;

        int intValue() default AbstractAnnotationDescriptionTest.INTEGER;

        long longValue() default AbstractAnnotationDescriptionTest.LONG;

        float floatValue() default AbstractAnnotationDescriptionTest.FLOAT;

        double doubleValue() default AbstractAnnotationDescriptionTest.DOUBLE;

        String stringValue() default AbstractAnnotationDescriptionTest.FOO;

        Class<?> classValue() default Void.class;

        Class<?> arrayClassValue() default Void[].class;

        AbstractAnnotationDescriptionTest.SampleEnumeration enumValue() default AbstractAnnotationDescriptionTest.SampleEnumeration.VALUE;

        AbstractAnnotationDescriptionTest.Other annotationValue() default @AbstractAnnotationDescriptionTest.Other;

        boolean[] booleanArrayValue() default AbstractAnnotationDescriptionTest.BOOLEAN;

        byte[] byteArrayValue() default AbstractAnnotationDescriptionTest.BYTE;

        short[] shortArrayValue() default AbstractAnnotationDescriptionTest.SHORT;

        char[] charArrayValue() default AbstractAnnotationDescriptionTest.CHARACTER;

        int[] intArrayValue() default AbstractAnnotationDescriptionTest.INTEGER;

        long[] longArrayValue() default AbstractAnnotationDescriptionTest.LONG;

        float[] floatArrayValue() default AbstractAnnotationDescriptionTest.FLOAT;

        double[] doubleArrayValue() default AbstractAnnotationDescriptionTest.DOUBLE;

        String[] stringArrayValue() default AbstractAnnotationDescriptionTest.FOO;

        Class<?>[] classArrayValue() default Void.class;

        AbstractAnnotationDescriptionTest.SampleEnumeration[] enumArrayValue() default AbstractAnnotationDescriptionTest.SampleEnumeration.VALUE;

        AbstractAnnotationDescriptionTest.Other[] annotationArrayValue() default @AbstractAnnotationDescriptionTest.Other;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Other {
        String value() default AbstractAnnotationDescriptionTest.FOO;
    }

    /* empty */
    @AbstractAnnotationDescriptionTest.Sample(booleanValue = AbstractAnnotationDescriptionTest.BOOLEAN, byteValue = AbstractAnnotationDescriptionTest.BYTE, charValue = AbstractAnnotationDescriptionTest.CHARACTER, shortValue = AbstractAnnotationDescriptionTest.SHORT, intValue = AbstractAnnotationDescriptionTest.INTEGER, longValue = AbstractAnnotationDescriptionTest.LONG, floatValue = AbstractAnnotationDescriptionTest.FLOAT, doubleValue = AbstractAnnotationDescriptionTest.DOUBLE, stringValue = AbstractAnnotationDescriptionTest.FOO, classValue = Void.class, arrayClassValue = Void[].class, enumValue = AbstractAnnotationDescriptionTest.SampleEnumeration.VALUE, annotationValue = @AbstractAnnotationDescriptionTest.Other, booleanArrayValue = AbstractAnnotationDescriptionTest.BOOLEAN, byteArrayValue = AbstractAnnotationDescriptionTest.BYTE, shortArrayValue = AbstractAnnotationDescriptionTest.SHORT, charArrayValue = AbstractAnnotationDescriptionTest.CHARACTER, intArrayValue = AbstractAnnotationDescriptionTest.INTEGER, longArrayValue = AbstractAnnotationDescriptionTest.LONG, floatArrayValue = AbstractAnnotationDescriptionTest.FLOAT, doubleArrayValue = AbstractAnnotationDescriptionTest.DOUBLE, stringArrayValue = AbstractAnnotationDescriptionTest.FOO, classArrayValue = Void.class, enumArrayValue = AbstractAnnotationDescriptionTest.SampleEnumeration.VALUE, annotationArrayValue = @AbstractAnnotationDescriptionTest.Other)
    private static class FooSample {}

    /* empty */
    @AbstractAnnotationDescriptionTest.Sample(booleanValue = AbstractAnnotationDescriptionTest.BOOLEAN, byteValue = AbstractAnnotationDescriptionTest.BYTE, charValue = AbstractAnnotationDescriptionTest.CHARACTER, shortValue = AbstractAnnotationDescriptionTest.SHORT, intValue = AbstractAnnotationDescriptionTest.INTEGER, longValue = AbstractAnnotationDescriptionTest.LONG, floatValue = AbstractAnnotationDescriptionTest.FLOAT, doubleValue = AbstractAnnotationDescriptionTest.DOUBLE, stringValue = AbstractAnnotationDescriptionTest.BAR, classValue = Void.class, arrayClassValue = Void[].class, enumValue = AbstractAnnotationDescriptionTest.SampleEnumeration.VALUE, annotationValue = @AbstractAnnotationDescriptionTest.Other, booleanArrayValue = AbstractAnnotationDescriptionTest.BOOLEAN, byteArrayValue = AbstractAnnotationDescriptionTest.BYTE, shortArrayValue = AbstractAnnotationDescriptionTest.SHORT, charArrayValue = AbstractAnnotationDescriptionTest.CHARACTER, intArrayValue = AbstractAnnotationDescriptionTest.INTEGER, longArrayValue = AbstractAnnotationDescriptionTest.LONG, floatArrayValue = AbstractAnnotationDescriptionTest.FLOAT, doubleArrayValue = AbstractAnnotationDescriptionTest.DOUBLE, stringArrayValue = AbstractAnnotationDescriptionTest.FOO, classArrayValue = Void.class, enumArrayValue = AbstractAnnotationDescriptionTest.SampleEnumeration.VALUE, annotationArrayValue = @AbstractAnnotationDescriptionTest.Other)
    private static class BarSample {}

    /* empty */
    @AbstractAnnotationDescriptionTest.SampleDefault
    private static class DefaultSample {}

    /* empty */
    @AbstractAnnotationDescriptionTest.SampleDefault(booleanValue = !(AbstractAnnotationDescriptionTest.BOOLEAN), byteValue = (AbstractAnnotationDescriptionTest.BYTE) * 2, charValue = (AbstractAnnotationDescriptionTest.CHARACTER) * 2, shortValue = (AbstractAnnotationDescriptionTest.SHORT) * 2, intValue = (AbstractAnnotationDescriptionTest.INTEGER) * 2, longValue = (AbstractAnnotationDescriptionTest.LONG) * 2, floatValue = (AbstractAnnotationDescriptionTest.FLOAT) * 2, doubleValue = (AbstractAnnotationDescriptionTest.DOUBLE) * 2, stringValue = AbstractAnnotationDescriptionTest.BAR, classValue = Object.class, arrayClassValue = Object[].class, enumValue = AbstractAnnotationDescriptionTest.SampleEnumeration.OTHER, annotationValue = @AbstractAnnotationDescriptionTest.Other(AbstractAnnotationDescriptionTest.BAR), booleanArrayValue = !(AbstractAnnotationDescriptionTest.BOOLEAN), byteArrayValue = AbstractAnnotationDescriptionTest.OTHER_BYTE, shortArrayValue = AbstractAnnotationDescriptionTest.OTHER_SHORT, charArrayValue = AbstractAnnotationDescriptionTest.OTHER_CHARACTER, intArrayValue = AbstractAnnotationDescriptionTest.OTHER_INTEGER, longArrayValue = AbstractAnnotationDescriptionTest.OTHER_LONG, floatArrayValue = AbstractAnnotationDescriptionTest.OTHER_FLOAT, doubleArrayValue = AbstractAnnotationDescriptionTest.OTHER_DOUBLE, stringArrayValue = AbstractAnnotationDescriptionTest.BAR, classArrayValue = Object.class, enumArrayValue = AbstractAnnotationDescriptionTest.SampleEnumeration.OTHER, annotationArrayValue = @AbstractAnnotationDescriptionTest.Other(AbstractAnnotationDescriptionTest.BAR))
    private static class NonDefaultSample {}

    /* empty */
    @AbstractAnnotationDescriptionTest.Other
    private static class EnumerationCarrier {}

    /* empty */
    @AbstractAnnotationDescriptionTest.Other(AbstractAnnotationDescriptionTest.BAR)
    private static class OtherEnumerationCarrier {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    protected @interface ExplicitTarget {
        /* empty */
        @AbstractAnnotationDescriptionTest.ExplicitTarget
        class Carrier {}
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface BrokenAnnotation {
        String stringValue();

        AbstractAnnotationDescriptionTest.SampleEnumeration enumValue();

        Class<?> classValue();
    }

    private static class AnnotationValueBreaker extends AsmVisitorWrapper.AbstractBase {
        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            return new AbstractAnnotationDescriptionTest.AnnotationValueBreaker.BreakingClassVisitor(classVisitor);
        }

        private static class BreakingClassVisitor extends ClassVisitor {
            public BreakingClassVisitor(ClassVisitor classVisitor) {
                super(ASM_API, classVisitor);
            }

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access, name, signature, superName, interfaces);
                AnnotationVisitor annotationVisitor = visitAnnotation(Type.getDescriptor(AbstractAnnotationDescriptionTest.BrokenAnnotation.class), true);
                annotationVisitor.visit("stringValue", AbstractAnnotationDescriptionTest.INTEGER);
                annotationVisitor.visitEnum("enumValue", Type.getDescriptor(AbstractAnnotationDescriptionTest.SampleEnumeration.class), AbstractAnnotationDescriptionTest.FOO);
                annotationVisitor.visit("classValue", Type.getType("Lnet/bytebuddy/inexistant/Foo;"));
                annotationVisitor.visitEnd();
            }
        }
    }
}


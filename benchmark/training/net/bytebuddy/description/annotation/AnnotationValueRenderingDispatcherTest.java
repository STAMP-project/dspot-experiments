package net.bytebuddy.description.annotation;


import java.util.Arrays;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.annotation.AnnotationValue.RenderingDispatcher.JAVA_9_CAPABLE_VM;
import static net.bytebuddy.description.annotation.AnnotationValue.RenderingDispatcher.LEGACY_VM;


public class AnnotationValueRenderingDispatcherTest {
    @Test
    public void testBoolean() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(true), CoreMatchers.is("true"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(true), CoreMatchers.is("true"));
    }

    @Test
    public void testByte() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(((byte) (42))), CoreMatchers.is("42"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(((byte) (42))), CoreMatchers.is("42"));
    }

    @Test
    public void testShort() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(((short) (42))), CoreMatchers.is("42"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(((short) (42))), CoreMatchers.is("42"));
    }

    @Test
    public void testCharacter() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString('*'), CoreMatchers.is("*"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString('*'), CoreMatchers.is("'*'"));
        MatcherAssert.assertThat(LEGACY_VM.toSourceString('\''), CoreMatchers.is("'"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString('\''), CoreMatchers.is("\'\\\'\'"));
    }

    @Test
    public void testInteger() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(42), CoreMatchers.is("42"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(42), CoreMatchers.is("42"));
    }

    @Test
    public void testLong() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(42L), CoreMatchers.is("42"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(42L), CoreMatchers.is("42"));
        MatcherAssert.assertThat(LEGACY_VM.toSourceString((1L + (Integer.MAX_VALUE))), CoreMatchers.is(Long.toString(((Integer.MAX_VALUE) + 1L))));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString((1L + (Integer.MAX_VALUE))), CoreMatchers.is(((Long.toString(((Integer.MAX_VALUE) + 1L))) + "L")));
    }

    @Test
    public void testFloat() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(42.0F), CoreMatchers.is("42.0"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(42.0F), CoreMatchers.is("42.0f"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(Float.POSITIVE_INFINITY), CoreMatchers.is("1.0f/0.0f"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(Float.NEGATIVE_INFINITY), CoreMatchers.is("-1.0f/0.0f"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString((0.0F / 0.0F)), CoreMatchers.is("0.0f/0.0f"));
    }

    @Test
    public void testDouble() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(42.0), CoreMatchers.is("42.0"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(42.0), CoreMatchers.is("42.0"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(Double.POSITIVE_INFINITY), CoreMatchers.is("1.0/0.0"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(Double.NEGATIVE_INFINITY), CoreMatchers.is("-1.0/0.0"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString((0.0 / 0.0)), CoreMatchers.is("0.0/0.0"));
    }

    @Test
    public void testString() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString("foo"), CoreMatchers.is("foo"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString("foo"), CoreMatchers.is("\"foo\""));
        MatcherAssert.assertThat(LEGACY_VM.toSourceString("\"foo\""), CoreMatchers.is("\"foo\""));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString("\"foo\""), CoreMatchers.is("\"\\\"foo\\\"\""));
    }

    @Test
    public void testTypeDescription() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(TypeDescription.OBJECT), CoreMatchers.is("class java.lang.Object"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(TypeDescription.OBJECT), CoreMatchers.is("java.lang.Object.class"));
    }

    @Test
    public void testArray() throws Exception {
        MatcherAssert.assertThat(LEGACY_VM.toSourceString(Arrays.asList("foo", "bar")), CoreMatchers.is("[foo, bar]"));
        MatcherAssert.assertThat(JAVA_9_CAPABLE_VM.toSourceString(Arrays.asList("foo", "bar")), CoreMatchers.is("{foo, bar}"));
    }
}


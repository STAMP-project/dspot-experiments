package com.netflix.astyanax.serializers;


import com.google.common.base.Strings;
import com.netflix.astyanax.annotations.Component;
import java.nio.ByteBuffer;
import java.util.Date;
import org.junit.Test;


/**
 * Created with IntelliJ IDEA.
 * User: omar
 * Date: 3/4/13
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotatedCompositeSerializerTest {
    @Test
    public void testOverflow() {
        AnnotatedCompositeSerializer<AnnotatedCompositeSerializerTest.Foo> serializer = new AnnotatedCompositeSerializer<AnnotatedCompositeSerializerTest.Foo>(AnnotatedCompositeSerializerTest.Foo.class);
        AnnotatedCompositeSerializerTest.Foo foo = new AnnotatedCompositeSerializerTest.Foo();
        foo.bar = Strings.repeat("b", 2000);
        foo.bar1 = Strings.repeat("b", 2000);
        foo.bar2 = Strings.repeat("b", 4192);
        ByteBuffer byteBuffer = serializer.toByteBuffer(foo);
    }

    @Test
    public void testOverflow2() {
        AnnotatedCompositeSerializer<AnnotatedCompositeSerializerTest.Foo2> serializer = new AnnotatedCompositeSerializer<AnnotatedCompositeSerializerTest.Foo2>(AnnotatedCompositeSerializerTest.Foo2.class);
        AnnotatedCompositeSerializerTest.Foo2 foo = new AnnotatedCompositeSerializerTest.Foo2();
        foo.bar = Strings.repeat("b", 500);
        foo.test = Strings.repeat("b", 12);
        ByteBuffer byteBuffer = serializer.toByteBuffer(foo);
    }

    public static class Foo2 {
        @Component(ordinal = 0)
        private Date updateTimestamp;

        @Component(ordinal = 1)
        private String bar;

        @Component(ordinal = 2)
        private String test;
    }

    public static class Foo {
        @Component(ordinal = 0)
        private String bar;

        @Component(ordinal = 0)
        private String bar1;

        @Component(ordinal = 0)
        private String bar2;
    }
}


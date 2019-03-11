package io.protostuff.runtime;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Tag;
import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static RuntimeSchema.MAX_TAG_VALUE;


/**
 *
 *
 * @author Kostiantyn Shchepanovskyi
 */
@SuppressWarnings("unused")
public class RuntimeSchemaTagTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Simple serialization/deserialization test
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSerializeDeserialize() throws Exception {
        RuntimeSchema<RuntimeSchemaTagTest.A6> schema = RuntimeSchema.createFrom(RuntimeSchemaTagTest.A6.class);
        RuntimeSchemaTagTest.A6 source = new RuntimeSchemaTagTest.A6(42);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LinkedBuffer buffer = LinkedBuffer.allocate();
        ProtostuffIOUtil.writeTo(outputStream, source, schema, buffer);
        byte[] bytes = outputStream.toByteArray();
        RuntimeSchemaTagTest.A6 newMessage = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, newMessage, schema);
        Assert.assertEquals(source, newMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTag() throws Exception {
        RuntimeSchema.createFrom(RuntimeSchemaTagTest.NegativeTag.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroTag() throws Exception {
        RuntimeSchema.createFrom(RuntimeSchemaTagTest.ZeroTag.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooBigTag() throws Exception {
        RuntimeSchema.createFrom(RuntimeSchemaTagTest.TooBigTag.class);
    }

    /**
     * {@link io.protostuff.runtime.RuntimeSchema#createFrom(Class)} should not throw
     * {@linkplain java.lang.OutOfMemoryError} when tag value is too big
     */
    @Test
    public void testMaxTag() throws Exception {
        RuntimeSchema<RuntimeSchemaTagTest.MaxTag> schema = RuntimeSchema.createFrom(RuntimeSchemaTagTest.MaxTag.class);
        Assert.assertNotNull(schema);
    }

    /**
     * Class and field names should be included in the message when one of fields is not
     * annotated by @Tag (and at least one other field is)
     */
    @Test
    public void testMissingTagException() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("io.protostuff.runtime.RuntimeSchemaTagTest.OneFieldIsNotAnnotated#b is not annotated with @Tag");
        RuntimeSchema.getSchema(RuntimeSchemaTagTest.OneFieldIsNotAnnotated.class);
    }

    static class A1 {
        @Tag(1)
        private int x;

        public A1() {
        }

        public A1(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof RuntimeSchemaTagTest.A1))
                return false;

            RuntimeSchemaTagTest.A1 a1 = ((RuntimeSchemaTagTest.A1) (o));
            return (x) == (a1.x);
        }

        @Override
        public int hashCode() {
            return x;
        }

        @Override
        public String toString() {
            return (("A{" + "x=") + (x)) + '}';
        }
    }

    static class A6 {
        @Tag(1000000)
        private int x;

        public A6() {
        }

        public A6(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof RuntimeSchemaTagTest.A6))
                return false;

            RuntimeSchemaTagTest.A6 a6 = ((RuntimeSchemaTagTest.A6) (o));
            return (x) == (a6.x);
        }

        @Override
        public int hashCode() {
            return x;
        }

        @Override
        public String toString() {
            return (("A{" + "x=") + (x)) + '}';
        }
    }

    static class NegativeTag {
        @Tag(-20)
        private int x;
    }

    static class ZeroTag {
        @Tag(0)
        private int x;
    }

    static class TooBigTag {
        @Tag((MAX_TAG_VALUE) + 1)
        private int x;
    }

    static class MaxTag {
        @Tag((RuntimeSchema.MAX_TAG_VALUE) - 1)
        private int x;
    }

    static class OneFieldIsNotAnnotated {
        @Tag(1)
        private int a;

        private int b;
    }
}


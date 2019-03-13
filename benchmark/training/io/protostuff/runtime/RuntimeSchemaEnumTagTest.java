package io.protostuff.runtime;


import io.protostuff.LinkedBuffer;
import io.protostuff.Output;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Tag;
import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Konstantin Shchepanovskyi
 */
public class RuntimeSchemaEnumTagTest {
    @Test
    public void testWriteNumericEnum() throws Exception {
        RuntimeSchema<RuntimeSchemaEnumTagTest.A> schema = RuntimeSchema.createFrom(RuntimeSchemaEnumTagTest.A.class);
        RuntimeSchemaEnumTagTest.A a = new RuntimeSchemaEnumTagTest.A(RuntimeSchemaEnumTagTest.TaggedEnum.TEN);
        Output output = Mockito.mock(Output.class);
        schema.writeTo(output, a);
        Mockito.verify(output).writeEnum(1, 10, false);
        Mockito.verifyNoMoreInteractions(output);
    }

    @Test
    public void testSerializeDeserializeNumericEnum() throws Exception {
        RuntimeSchema<RuntimeSchemaEnumTagTest.A> schema = RuntimeSchema.createFrom(RuntimeSchemaEnumTagTest.A.class);
        RuntimeSchemaEnumTagTest.A source = new RuntimeSchemaEnumTagTest.A(RuntimeSchemaEnumTagTest.TaggedEnum.TEN);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LinkedBuffer buffer = LinkedBuffer.allocate();
        ProtostuffIOUtil.writeTo(outputStream, source, schema, buffer);
        byte[] bytes = outputStream.toByteArray();
        RuntimeSchemaEnumTagTest.A newMessage = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, newMessage, schema);
        Assert.assertEquals(source, newMessage);
    }

    static enum TaggedEnum {

        @Tag(value = 1, alias = "one")
        ONE,
        @Tag(value = 2, alias = "two")
        TWO,
        @Tag(value = 3, alias = "three")
        THREE,
        @Tag(value = 10, alias = "ten")
        TEN;}

    static class A {
        private RuntimeSchemaEnumTagTest.TaggedEnum x;

        public A() {
        }

        public A(RuntimeSchemaEnumTagTest.TaggedEnum x) {
            this.x = x;
        }

        public RuntimeSchemaEnumTagTest.TaggedEnum getX() {
            return x;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof RuntimeSchemaEnumTagTest.A))
                return false;

            RuntimeSchemaEnumTagTest.A a = ((RuntimeSchemaEnumTagTest.A) (o));
            if ((x) != (a.x))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (x) != null ? x.hashCode() : 0;
        }

        @Override
        public String toString() {
            return (("A{" + "x=") + (x)) + '}';
        }
    }
}


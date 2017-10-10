

package io.protostuff;


public class AmplBufferReuseTest extends io.protostuff.StandardTest {
    private static final java.lang.ThreadLocal<io.protostuff.LinkedBuffer> localBuffer = new java.lang.ThreadLocal<io.protostuff.LinkedBuffer>() {
        @java.lang.Override
        protected io.protostuff.LinkedBuffer initialValue() {
            return io.protostuff.AbstractTest.buf();
        }
    };

    @java.lang.Override
    protected <T> void mergeFrom(byte[] data, int offset, int length, T message, io.protostuff.Schema<T> schema) throws java.io.IOException {
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data, offset, length);
        io.protostuff.ProtostuffIOUtil.mergeFrom(in, message, schema, io.protostuff.AmplBufferReuseTest.localBuffer.get());
    }

    @java.lang.Override
    protected <T> byte[] toByteArray(T message, io.protostuff.Schema<T> schema) {
        final io.protostuff.LinkedBuffer buffer = io.protostuff.AmplBufferReuseTest.localBuffer.get();
        try {
            return io.protostuff.ProtostuffIOUtil.toByteArray(message, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    public void testFooSizeLimited() throws java.lang.Exception {
        final io.protostuff.Foo fooCompare = io.protostuff.SerializableObjects.newFoo(new java.lang.Integer[]{ 90210 , -90210 , 0 }, new java.lang.String[]{ "ab" , "cd" }, new io.protostuff.Bar[]{ io.protostuff.SerializableObjects.bar , io.protostuff.SerializableObjects.negativeBar , io.protostuff.SerializableObjects.bar , io.protostuff.SerializableObjects.negativeBar , io.protostuff.SerializableObjects.bar , io.protostuff.SerializableObjects.negativeBar }, new io.protostuff.Foo.EnumSample[]{ io.protostuff.Foo.EnumSample , io.protostuff.Foo.EnumSample }, new io.protostuff.ByteString[]{ io.protostuff.ByteString.copyFromUtf8("ef") , io.protostuff.ByteString.copyFromUtf8("gh") }, new java.lang.Boolean[]{ true , false }, new java.lang.Float[]{ 1234.4321F , -1234.4321F , 0.0F }, new java.lang.Double[]{ 1.234567887654321E7 , -1.234567887654321E7 , 0.0 }, new java.lang.Long[]{ 7060504030201L , -7060504030201L , 0L });
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        final io.protostuff.LinkedBuffer buffer = io.protostuff.LinkedBuffer.allocate(256);
        try {
            io.protostuff.ProtostuffIOUtil.writeDelimitedTo(out, fooCompare, fooCompare.cachedSchema(), buffer);
        } finally {
            buffer.clear();
        }
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.Foo foo = new io.protostuff.Foo();
        boolean hasException = true;
        try {
            io.protostuff.ProtostuffIOUtil.mergeDelimitedFrom(in, foo, foo.cachedSchema(), buffer);
            hasException = false;
        } catch (io.protostuff.ProtostuffException e) {
            junit.framework.TestCase.assertTrue(e.getMessage().startsWith("size limit exceeded."));
        }
        junit.framework.TestCase.assertTrue(hasException);
    }

    /* amplification of io.protostuff.BufferReuseTest#testFooSizeLimited */
    @org.junit.Test(timeout = 10000)
    public void testFooSizeLimited_add1_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final io.protostuff.Foo fooCompare = io.protostuff.SerializableObjects.newFoo(new java.lang.Integer[]{ 90210 , -90210 , 0 }, new java.lang.String[]{ "ab" , "cd" }, new io.protostuff.Bar[]{ io.protostuff.SerializableObjects.bar , io.protostuff.SerializableObjects.negativeBar , io.protostuff.SerializableObjects.bar , io.protostuff.SerializableObjects.negativeBar , io.protostuff.SerializableObjects.bar , io.protostuff.SerializableObjects.negativeBar }, new io.protostuff.Foo.EnumSample[]{ io.protostuff.Foo.EnumSample.TYPE0 , io.protostuff.Foo.EnumSample.TYPE2 }, new io.protostuff.ByteString[]{ io.protostuff.ByteString.copyFromUtf8("ef") , io.protostuff.ByteString.copyFromUtf8("gh") }, new java.lang.Boolean[]{ true , false }, new java.lang.Float[]{ 1234.4321F , -1234.4321F , 0.0F }, new java.lang.Double[]{ 1.234567887654321E7 , -1.234567887654321E7 , 0.0 }, new java.lang.Long[]{ 7060504030201L , -7060504030201L , 0L });
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            final io.protostuff.LinkedBuffer buffer = io.protostuff.LinkedBuffer.allocate(256);
            try {
                // MethodCallAdder
                io.protostuff.ProtostuffIOUtil.writeDelimitedTo(out, fooCompare, fooCompare.cachedSchema(), buffer);
                io.protostuff.ProtostuffIOUtil.writeDelimitedTo(out, fooCompare, fooCompare.cachedSchema(), buffer);
            } finally {
                buffer.clear();
            }
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            io.protostuff.Foo foo = new io.protostuff.Foo();
            boolean hasException = true;
            try {
                io.protostuff.ProtostuffIOUtil.mergeDelimitedFrom(in, foo, foo.cachedSchema(), buffer);
                hasException = false;
            } catch (io.protostuff.ProtostuffException e) {
                // MethodAssertGenerator build local variable
                Object o_35_0 = e.getMessage().startsWith("size limit exceeded.");
            }
            org.junit.Assert.fail("testFooSizeLimited_add1 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}


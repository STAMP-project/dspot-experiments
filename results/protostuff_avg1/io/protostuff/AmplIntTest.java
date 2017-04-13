

package io.protostuff;


public class AmplIntTest extends io.protostuff.AbstractTest {
    static io.protostuff.PojoWithInts filledPojoWithInts(int i, int ni, long j, long nj) {
        junit.framework.TestCase.assertTrue((i >= 0));
        junit.framework.TestCase.assertTrue((j >= 0));
        io.protostuff.PojoWithInts p = new io.protostuff.PojoWithInts();
        p.setSomeInt32(i);
        p.setSomeUint32(i);
        p.setSomeFixed32(i);
        p.setSomeSint32(ni);
        p.setSomeSfixed32(ni);
        p.setSomeInt64(j);
        p.setSomeUint64(j);
        p.setSomeFixed64(j);
        p.setSomeSint64(nj);
        p.setSomeSfixed64(nj);
        return p;
    }

    public void testProtostuff() throws java.io.IOException {
        io.protostuff.AmplIntTest.verifyProtostuff(io.protostuff.AmplIntTest.filledPojoWithInts(0, 0, 0, 0));
        io.protostuff.AmplIntTest.verifyProtostuff(io.protostuff.AmplIntTest.filledPojoWithInts(1, 1, 1, 1));
        io.protostuff.AmplIntTest.verifyProtostuff(io.protostuff.AmplIntTest.filledPojoWithInts(1, (-1), 1, (-1)));
        io.protostuff.AmplIntTest.verifyProtostuff(io.protostuff.AmplIntTest.filledPojoWithInts(java.lang.Integer.MAX_VALUE, java.lang.Integer.MAX_VALUE, java.lang.Long.MAX_VALUE, java.lang.Long.MAX_VALUE));
        io.protostuff.AmplIntTest.verifyProtostuff(io.protostuff.AmplIntTest.filledPojoWithInts(java.lang.Integer.MAX_VALUE, java.lang.Integer.MIN_VALUE, java.lang.Long.MAX_VALUE, java.lang.Long.MIN_VALUE));
    }

    public void testProtobuf() throws java.io.IOException {
        io.protostuff.AmplIntTest.verifyProtobuf(io.protostuff.AmplIntTest.filledPojoWithInts(0, 0, 0, 0));
        io.protostuff.AmplIntTest.verifyProtobuf(io.protostuff.AmplIntTest.filledPojoWithInts(1, 1, 1, 1));
        io.protostuff.AmplIntTest.verifyProtobuf(io.protostuff.AmplIntTest.filledPojoWithInts(1, (-1), 1, (-1)));
        io.protostuff.AmplIntTest.verifyProtobuf(io.protostuff.AmplIntTest.filledPojoWithInts(java.lang.Integer.MAX_VALUE, java.lang.Integer.MAX_VALUE, java.lang.Long.MAX_VALUE, java.lang.Long.MAX_VALUE));
        io.protostuff.AmplIntTest.verifyProtobuf(io.protostuff.AmplIntTest.filledPojoWithInts(java.lang.Integer.MAX_VALUE, java.lang.Integer.MIN_VALUE, java.lang.Long.MAX_VALUE, java.lang.Long.MIN_VALUE));
    }

    static void verifyProtostuff(io.protostuff.PojoWithInts p) throws java.io.IOException {
        io.protostuff.Schema<io.protostuff.PojoWithInts> schema = io.protostuff.PojoWithInts.getSchema();
        byte[] data = io.protostuff.ProtostuffIOUtil.toByteArray(p, schema, io.protostuff.AbstractTest.buf());
        io.protostuff.PojoWithInts pFromByteArray = new io.protostuff.PojoWithInts();
        io.protostuff.ProtostuffIOUtil.mergeFrom(data, pFromByteArray, schema);
        junit.framework.TestCase.assertEquals(p, pFromByteArray);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        io.protostuff.ProtostuffIOUtil.writeTo(out, p, schema, io.protostuff.AbstractTest.buf());
        byte[] dataFromStream = out.toByteArray();
        junit.framework.TestCase.assertTrue(java.util.Arrays.equals(data, dataFromStream));
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.PojoWithInts pFromStream = new io.protostuff.PojoWithInts();
        io.protostuff.ProtostuffIOUtil.mergeFrom(in, pFromStream, schema);
        junit.framework.TestCase.assertEquals(p, pFromStream);
    }

    static void verifyProtobuf(io.protostuff.PojoWithInts p) throws java.io.IOException {
        io.protostuff.Schema<io.protostuff.PojoWithInts> schema = io.protostuff.PojoWithInts.getSchema();
        byte[] data = io.protostuff.ProtobufIOUtil.toByteArray(p, schema, io.protostuff.AbstractTest.buf());
        io.protostuff.PojoWithInts pFromByteArray = new io.protostuff.PojoWithInts();
        io.protostuff.ProtobufIOUtil.mergeFrom(data, pFromByteArray, schema);
        junit.framework.TestCase.assertEquals(p, pFromByteArray);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        io.protostuff.ProtobufIOUtil.writeTo(out, p, schema, io.protostuff.AbstractTest.buf());
        byte[] dataFromStream = out.toByteArray();
        junit.framework.TestCase.assertTrue(java.util.Arrays.equals(data, dataFromStream));
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        io.protostuff.PojoWithInts pFromStream = new io.protostuff.PojoWithInts();
        io.protostuff.ProtobufIOUtil.mergeFrom(in, pFromStream, schema);
        junit.framework.TestCase.assertEquals(p, pFromStream);
    }
}


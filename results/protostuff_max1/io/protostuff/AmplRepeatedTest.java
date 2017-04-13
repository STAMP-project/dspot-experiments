

package io.protostuff;


public class AmplRepeatedTest extends io.protostuff.AbstractTest {
    public void testPackedRepeatedByteArray() throws java.io.IOException {
        final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
        io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
        test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 0, buffer.offset, false), test);
        verify(test);
    }

    public void testPackedRepeatedByteBuffer() throws java.io.IOException {
        final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
        io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
        test.mergeFrom(new io.protostuff.ByteBufferInput(java.nio.ByteBuffer.wrap(buffer.buffer, 0, buffer.offset), false), test);
        verify(test);
    }

    public void testPackedRepeatedCodedInput() throws java.io.IOException {
        final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
        io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
        test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 0, buffer.offset, false), test);
        verify(test);
    }

    private io.protostuff.LinkedBuffer getProtobufBuffer() throws java.io.IOException {
        final io.protostuff.LinkedBuffer buffer = new io.protostuff.LinkedBuffer(io.protostuff.LinkedBuffer.DEFAULT_BUFFER_SIZE);
        final io.protostuff.ProtobufOutput output = new io.protostuff.ProtobufOutput(buffer);
        output.writeByteRange(false, 1, new byte[]{ ((byte) (3)) , ((byte) (142)) , ((byte) (2)) }, 0, 3, true);
        output.writeFixed64(2, 8, true);
        output.writeInt32(1, 1234, true);
        output.writeByteRange(false, 2, new byte[]{ 9 , 0 , 0 , 0 , 0 , 0 , 0 , 0 }, 0, 8, true);
        output.writeByteRange(false, 1, new byte[]{ ((byte) (158)) , ((byte) (167)) , ((byte) (5)) }, 0, 3, true);
        return buffer;
    }

    private void verify(final io.protostuff.PojoWithRepeated test) {
        junit.framework.TestCase.assertEquals(4, test.getSomeInt32Count());
        junit.framework.TestCase.assertEquals(((java.lang.Integer) (3)), test.getSomeInt32(0));
        junit.framework.TestCase.assertEquals(((java.lang.Integer) (270)), test.getSomeInt32(1));
        junit.framework.TestCase.assertEquals(((java.lang.Integer) (1234)), test.getSomeInt32(2));
        junit.framework.TestCase.assertEquals(((java.lang.Integer) (86942)), test.getSomeInt32(3));
        junit.framework.TestCase.assertEquals(2, test.getSomeFixed64Count());
        junit.framework.TestCase.assertEquals(((java.lang.Long) (8L)), test.getSomeFixed64(0));
        junit.framework.TestCase.assertEquals(((java.lang.Long) (9L)), test.getSomeFixed64(1));
    }

    public void testPackedRepeatedByteArray_literalMutation6_failAssert3() throws java.io.IOException {
        try {
            final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
            io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
            test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 2, buffer.offset, false), test);
            verify(test);
            org.junit.Assert.fail("testPackedRepeatedByteArray_literalMutation6 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedByteArray_add1_failAssert0_literalMutation14_failAssert2() throws java.io.IOException {
        try {
            try {
                final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 2, buffer.offset, false), test);
                test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 0, buffer.offset, false), test);
                verify(test);
                org.junit.Assert.fail("testPackedRepeatedByteArray_add1 should have thrown UnsupportedOperationException");
            } catch (java.lang.UnsupportedOperationException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedByteArray_add1_failAssert0_literalMutation14 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedByteArray_add1_failAssert0_literalMutation17_failAssert4_literalMutation95_failAssert11() throws java.io.IOException {
        try {
            try {
                try {
                    final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                    io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                    test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 2, buffer.offset, false), test);
                    test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, (-1), buffer.offset, false), test);
                    verify(test);
                    org.junit.Assert.fail("testPackedRepeatedByteArray_add1 should have thrown UnsupportedOperationException");
                } catch (java.lang.UnsupportedOperationException eee) {
                }
                org.junit.Assert.fail("testPackedRepeatedByteArray_add1_failAssert0_literalMutation17 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedByteArray_add1_failAssert0_literalMutation17_failAssert4_literalMutation95 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    public void testPackedRepeatedCodedInput_literalMutation155_failAssert2() throws java.io.IOException {
        try {
            final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
            io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
            test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, (-1), buffer.offset, false), test);
            verify(test);
            org.junit.Assert.fail("testPackedRepeatedCodedInput_literalMutation155 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    public void testPackedRepeatedCodedInput_literalMutation157_failAssert3() throws java.io.IOException {
        try {
            final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
            io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
            test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 2, buffer.offset, false), test);
            verify(test);
            org.junit.Assert.fail("testPackedRepeatedCodedInput_literalMutation157 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedCodedInput_add152_failAssert0_literalMutation165_failAssert2() throws java.io.IOException {
        try {
            try {
                final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 2, buffer.offset, false), test);
                test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 0, buffer.offset, false), test);
                verify(test);
                org.junit.Assert.fail("testPackedRepeatedCodedInput_add152 should have thrown UnsupportedOperationException");
            } catch (java.lang.UnsupportedOperationException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedCodedInput_add152_failAssert0_literalMutation165 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedCodedInput_add152_failAssert0_literalMutation163_failAssert1() throws java.io.IOException {
        try {
            try {
                final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, (-1), buffer.offset, false), test);
                test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 0, buffer.offset, false), test);
                verify(test);
                org.junit.Assert.fail("testPackedRepeatedCodedInput_add152 should have thrown UnsupportedOperationException");
            } catch (java.lang.UnsupportedOperationException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedCodedInput_add152_failAssert0_literalMutation163 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedCodedInput_add152_failAssert0_literalMutation170_failAssert5_literalMutation256_failAssert15() throws java.io.IOException {
        try {
            try {
                try {
                    final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                    io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                    test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, (-1), buffer.offset, false), test);
                    test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 2, buffer.offset, false), test);
                    verify(test);
                    org.junit.Assert.fail("testPackedRepeatedCodedInput_add152 should have thrown UnsupportedOperationException");
                } catch (java.lang.UnsupportedOperationException eee) {
                }
                org.junit.Assert.fail("testPackedRepeatedCodedInput_add152_failAssert0_literalMutation170 should have thrown ProtobufException");
            } catch (io.protostuff.ProtobufException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedCodedInput_add152_failAssert0_literalMutation170_failAssert5_literalMutation256 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedCodedInput_add152_failAssert0_literalMutation168_failAssert4_literalMutation246_failAssert11() throws java.io.IOException {
        try {
            try {
                try {
                    final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                    io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                    test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 2, buffer.offset, false), test);
                    test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, (-1), buffer.offset, false), test);
                    verify(test);
                    org.junit.Assert.fail("testPackedRepeatedCodedInput_add152 should have thrown UnsupportedOperationException");
                } catch (java.lang.UnsupportedOperationException eee) {
                }
                org.junit.Assert.fail("testPackedRepeatedCodedInput_add152_failAssert0_literalMutation168 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedCodedInput_add152_failAssert0_literalMutation168_failAssert4_literalMutation246 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }
}




package io.protostuff;


/**
 *
 */
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
        // Generate protobuf with packed repeated fields
        final io.protostuff.LinkedBuffer buffer = new io.protostuff.LinkedBuffer(io.protostuff.LinkedBuffer.DEFAULT_BUFFER_SIZE);
        final io.protostuff.ProtobufOutput output = new io.protostuff.ProtobufOutput(buffer);
        // 03 // first element (varint 3)
        // 8E 02 // second element (varint 270)
        output.writeByteRange(false, 1, new byte[]{ ((byte) (3)) , ((byte) (142)) , ((byte) (2)) }, 0, 3, true);
        // Interleave
        output.writeFixed64(2, 8, true);
        // Non packed
        output.writeInt32(1, 1234, true);
        // Interleave
        output.writeByteRange(false, 2, new byte[]{ 9 , 0 , 0 , 0 , 0 , 0 , 0 , 0 }, 0, 8, true);
        // 9E A7 05 // third element (varint 86942)
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

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteArray */
    public void testPackedRepeatedByteArray_literalMutation4_failAssert2() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
            io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
            test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, -1, buffer.offset, false), test);
            verify(test);
            org.junit.Assert.fail("testPackedRepeatedByteArray_literalMutation4 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteArray */
    public void testPackedRepeatedByteArray_literalMutation6_failAssert3() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
            io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
            test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 2, buffer.offset, false), test);
            verify(test);
            org.junit.Assert.fail("testPackedRepeatedByteArray_literalMutation6 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteArray */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedByteArray_add1_failAssert0_literalMutation12_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                // MethodCallAdder
                test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, -1, buffer.offset, false), test);
                test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 0, buffer.offset, false), test);
                verify(test);
                org.junit.Assert.fail("testPackedRepeatedByteArray_add1 should have thrown UnsupportedOperationException");
            } catch (java.lang.UnsupportedOperationException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedByteArray_add1_failAssert0_literalMutation12 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteArray */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedByteArray_add1_failAssert0_literalMutation14_failAssert2() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                // MethodCallAdder
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

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteArray */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedByteArray_add1_failAssert0_literalMutation17_failAssert4_literalMutation95_failAssert11() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                    io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                    // MethodCallAdder
                    test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 2, buffer.offset, false), test);
                    test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, -1, buffer.offset, false), test);
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

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteArray */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedByteArray_add1_failAssert0_literalMutation19_failAssert5_literalMutation105_failAssert15() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                    io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                    // MethodCallAdder
                    test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, -1, buffer.offset, false), test);
                    test.mergeFrom(new io.protostuff.ByteArrayInput(buffer.buffer, 2, buffer.offset, false), test);
                    verify(test);
                    org.junit.Assert.fail("testPackedRepeatedByteArray_add1 should have thrown UnsupportedOperationException");
                } catch (java.lang.UnsupportedOperationException eee) {
                }
                org.junit.Assert.fail("testPackedRepeatedByteArray_add1_failAssert0_literalMutation19 should have thrown ProtobufException");
            } catch (io.protostuff.ProtobufException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedByteArray_add1_failAssert0_literalMutation19_failAssert5_literalMutation105 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteBuffer */
    public void testPackedRepeatedByteBuffer_literalMutation119_failAssert3() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
            io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
            test.mergeFrom(new io.protostuff.ByteBufferInput(java.nio.ByteBuffer.wrap(buffer.buffer, 2, buffer.offset), false), test);
            verify(test);
            org.junit.Assert.fail("testPackedRepeatedByteBuffer_literalMutation119 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteBuffer */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedByteBuffer_add114_failAssert0_literalMutation127_failAssert2() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                // MethodCallAdder
                test.mergeFrom(new io.protostuff.ByteBufferInput(java.nio.ByteBuffer.wrap(buffer.buffer, 2, buffer.offset), false), test);
                test.mergeFrom(new io.protostuff.ByteBufferInput(java.nio.ByteBuffer.wrap(buffer.buffer, 0, buffer.offset), false), test);
                verify(test);
                org.junit.Assert.fail("testPackedRepeatedByteBuffer_add114 should have thrown UnsupportedOperationException");
            } catch (java.lang.UnsupportedOperationException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedByteBuffer_add114_failAssert0_literalMutation127 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedByteBuffer */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedByteBuffer_add114_failAssert0_literalMutation130_failAssert4_literalMutation208_failAssert11() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                    io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                    // MethodCallAdder
                    test.mergeFrom(new io.protostuff.ByteBufferInput(java.nio.ByteBuffer.wrap(buffer.buffer, 2, buffer.offset), false), test);
                    test.mergeFrom(new io.protostuff.ByteBufferInput(java.nio.ByteBuffer.wrap(buffer.buffer, -1, buffer.offset), false), test);
                    verify(test);
                    org.junit.Assert.fail("testPackedRepeatedByteBuffer_add114 should have thrown UnsupportedOperationException");
                } catch (java.lang.UnsupportedOperationException eee) {
                }
                org.junit.Assert.fail("testPackedRepeatedByteBuffer_add114_failAssert0_literalMutation130 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedByteBuffer_add114_failAssert0_literalMutation130_failAssert4_literalMutation208 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedCodedInput */
    public void testPackedRepeatedCodedInput_literalMutation232_failAssert3() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
            io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
            test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 2, buffer.offset, false), test);
            verify(test);
            org.junit.Assert.fail("testPackedRepeatedCodedInput_literalMutation232 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedCodedInput */
    public void testPackedRepeatedCodedInput_literalMutation230_failAssert2() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
            io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
            test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, -1, buffer.offset, false), test);
            verify(test);
            org.junit.Assert.fail("testPackedRepeatedCodedInput_literalMutation230 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedCodedInput */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedCodedInput_add227_failAssert0_literalMutation238_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                // MethodCallAdder
                test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, -1, buffer.offset, false), test);
                test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 0, buffer.offset, false), test);
                verify(test);
                org.junit.Assert.fail("testPackedRepeatedCodedInput_add227 should have thrown UnsupportedOperationException");
            } catch (java.lang.UnsupportedOperationException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedCodedInput_add227_failAssert0_literalMutation238 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedCodedInput */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedCodedInput_add227_failAssert0_literalMutation240_failAssert2() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                // MethodCallAdder
                test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 2, buffer.offset, false), test);
                test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 0, buffer.offset, false), test);
                verify(test);
                org.junit.Assert.fail("testPackedRepeatedCodedInput_add227 should have thrown UnsupportedOperationException");
            } catch (java.lang.UnsupportedOperationException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedCodedInput_add227_failAssert0_literalMutation240 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedCodedInput */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedCodedInput_add227_failAssert0_literalMutation245_failAssert5_literalMutation331_failAssert15() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                    io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                    // MethodCallAdder
                    test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, -1, buffer.offset, false), test);
                    test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 2, buffer.offset, false), test);
                    verify(test);
                    org.junit.Assert.fail("testPackedRepeatedCodedInput_add227 should have thrown UnsupportedOperationException");
                } catch (java.lang.UnsupportedOperationException eee) {
                }
                org.junit.Assert.fail("testPackedRepeatedCodedInput_add227_failAssert0_literalMutation245 should have thrown ProtobufException");
            } catch (io.protostuff.ProtobufException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedCodedInput_add227_failAssert0_literalMutation245_failAssert5_literalMutation331 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.RepeatedTest#testPackedRepeatedCodedInput */
    @org.junit.Test(timeout = 10000)
    public void testPackedRepeatedCodedInput_add227_failAssert0_literalMutation243_failAssert4_literalMutation321_failAssert11() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    final io.protostuff.LinkedBuffer buffer = getProtobufBuffer();
                    io.protostuff.PojoWithRepeated test = new io.protostuff.PojoWithRepeated();
                    // MethodCallAdder
                    test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, 2, buffer.offset, false), test);
                    test.mergeFrom(new io.protostuff.CodedInput(buffer.buffer, -1, buffer.offset, false), test);
                    verify(test);
                    org.junit.Assert.fail("testPackedRepeatedCodedInput_add227 should have thrown UnsupportedOperationException");
                } catch (java.lang.UnsupportedOperationException eee) {
                }
                org.junit.Assert.fail("testPackedRepeatedCodedInput_add227_failAssert0_literalMutation243 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testPackedRepeatedCodedInput_add227_failAssert0_literalMutation243_failAssert4_literalMutation321 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }
}


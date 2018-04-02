package io.protostuff;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Test;


public class AmplCodedInputTest extends AbstractTest {
    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundarylitNum9_failAssert5() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(2147483647, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundarylitNum9 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundarylitNum19_failAssert8() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = -2147483648;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundarylitNum19 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_remove88_failAssert41() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; 1 <= msgLength; i++) {
            }
            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_remove88 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_replacement6_failAssert3() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new byte[]{ 46, 115, -44, 106 }, -1904675524, -21383048, false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_replacement6 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundarylitNum8_failAssert4() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(0, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundarylitNum8 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_remove87_failAssert40() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_remove87 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd68_failAssert34() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readUInt64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd68 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd67_failAssert33() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readUInt32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd67 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_replacement2_failAssert0() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = 156591366;
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_replacement2 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd49_failAssert16() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readEnum();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd49 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd65_failAssert32() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readString();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd65 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd51_failAssert18() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readFixed64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd51 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd50_failAssert17() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readFixed32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd50 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd61_failAssert28() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readSFixed32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd61 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd52_failAssert19() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readFloat();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd52 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd62_failAssert29() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readSFixed64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd62 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_replacement5_failAssert2() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = new byte[]{ 100, -20, -117 };
            CodedInput ci = new CodedInput(new ByteArrayInputStream(new byte[]{ 100, -20, -117 }), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_replacement5 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd74_failAssert36() throws Exception {
        try {
            int __DSPOT_size_6 = 986475328;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.skipRawBytes(986475328);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd74 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd36_failAssert9() throws Exception {
        try {
            int __DSPOT_value_0 = -19668052;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.checkLastTagWas(-19668052);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd36 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd47_failAssert14() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readBytes();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd47 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd63_failAssert30() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readSInt32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd63 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd55_failAssert22() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readRawByte();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd55 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd48_failAssert15() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readDouble();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd48 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd56_failAssert23() throws Exception {
        try {
            int __DSPOT_size_3 = 2145706595;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readRawBytes(2145706595);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd56 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd64_failAssert31() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readSInt64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd64 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd46_failAssert13() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readByteBuffer();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd46 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd54_failAssert21() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readInt64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd54 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd44_failAssert11() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readBool();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd44 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd45_failAssert12() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readByteArray();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd45 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd53_failAssert20() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.readInt32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd53 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_sd43_failAssert10() throws Exception {
        try {
            int __DSPOT_byteLimit_2 = -1348236471;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                ProtobufOutput.writeRawVarInt32Bytes(out, i);

            ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            byte[] data = out.toByteArray();
            CodedInput ci = new CodedInput(new ByteArrayInputStream(out.toByteArray()), new byte[10], false);
            ci.pushLimit((msgLength + 2));
            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            ci.pushLimit(-1348236471);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd43 should have thrown ProtobufException");
        } catch (ProtobufException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_replacement2_failAssert0_replacement251_failAssert2() throws Exception {
        try {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int tag = 156591366;
                int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    ProtobufOutput.writeRawVarInt32Bytes(out, i);

                ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
                byte[] data = out.toByteArray();
                CodedInput ci = new CodedInput(new byte[]{ 14, -91 }, 221006639, 1045185488, true);
                ci.pushLimit((msgLength + 2));
                ci.readTag();
                ci.skipField(tag);
                ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_replacement2 should have thrown ProtobufException");
            } catch (ProtobufException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_replacement2_failAssert0_replacement251 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testSkipFieldOverTheBufferBoundary_remove88_failAssert41_replacement3904_failAssert3() throws Exception {
        try {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int tag = WireFormat.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; 1 <= msgLength; i++) {
                }
                ProtobufOutput.writeRawVarInt32Bytes(out, WireFormat.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED));
                byte[] data = out.toByteArray();
                CodedInput ci = new CodedInput(new byte[]{ 109, -81, -101 }, 459510170, 643580259, false);
                ci.pushLimit((msgLength + 2));
                ci.readTag();
                ci.skipField(tag);
                ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_remove88 should have thrown ProtobufException");
            } catch (ProtobufException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_remove88_failAssert41_replacement3904 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException eee) {
        }
    }
}


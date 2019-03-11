package com.mcxiaoke.packer.common;


import com.android.apksig.ApkVerifier;
import com.android.apksig.ApkVerifier.Builder;
import com.android.apksig.ApkVerifier.IssueWithParams;
import com.android.apksig.ApkVerifier.Result;
import com.android.apksig.apk.ApkFormatException;
import com.mcxiaoke.packer.support.walle.Support;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


/**
 * User: mcxiaoke
 * Date: 2017/5/17
 * Time: 16:25
 */
public class PayloadTests extends TestCase {
    public void testFileExists() {
        File file = new File("../tools/test.apk");
        TestCase.assertTrue(file.exists());
    }

    public void testFileCopy() throws IOException {
        File f1 = new File("../tools/test.apk");
        File f2 = newTestFile();
        TestCase.assertTrue(f2.exists());
        TestCase.assertTrue(f2.getName().endsWith(".apk"));
        TestCase.assertEquals(f1.length(), f2.length());
        TestCase.assertEquals(f1.getParent(), f2.getParent());
    }

    public void testFileSignature() throws ApkFormatException, IOException, NoSuchAlgorithmException {
        File f = newTestFile();
        checkApkVerified(f);
    }

    public void testOverrideSignature() throws ApkFormatException, IOException, NoSuchAlgorithmException {
        File f = newTestFile();
        // don't write with APK Signature Scheme v2 Block ID 0x7109871a
        PackerCommon.writeString(f, "OverrideSignatureSchemeBlock", 1896449818);
        TestCase.assertEquals("OverrideSignatureSchemeBlock", PackerCommon.readString(f, 1896449818));
        ApkVerifier verifier = new Builder(f).build();
        Result result = verifier.verify();
        final List<IssueWithParams> errors = result.getErrors();
        if ((errors != null) && ((errors.size()) > 0)) {
            for (IssueWithParams error : errors) {
                System.out.println(("testOverrideSignature " + error));
            }
        }
        TestCase.assertTrue(result.containsErrors());
        TestCase.assertFalse(result.isVerified());
        TestCase.assertFalse(result.isVerifiedUsingV1Scheme());
        TestCase.assertFalse(result.isVerifiedUsingV2Scheme());
    }

    public void testBytesWrite1() throws IOException {
        File f = newTestFile();
        byte[] in = "Hello".getBytes();
        Support.writeBlock(f, 74565, in);
        byte[] out = Support.readBytes(f, 74565);
        TestCase.assertTrue(TestUtils.sameBytes(in, out));
        checkApkVerified(f);
    }

    public void testBytesWrite2() throws IOException {
        File f = newTestFile();
        byte[] in = "??????????@#?%??*?????????".getBytes("UTF-8");
        Support.writeBlock(f, 74565, in);
        byte[] out = Support.readBytes(f, 74565);
        TestCase.assertTrue(TestUtils.sameBytes(in, out));
        checkApkVerified(f);
    }

    public void testStringWrite() throws IOException {
        File f = newTestFile();
        PackerCommon.writeString(f, "Test String", 1903851627);
        TestCase.assertEquals("Test String", PackerCommon.readString(f, 1903851627));
        PackerCommon.writeString(f, "??????????@#?%??*?????????", 1903851627);
        TestCase.assertEquals("??????????@#?%??*?????????", PackerCommon.readString(f, 1903851627));
        checkApkVerified(f);
    }

    public void testValuesWrite() throws IOException {
        File f = newTestFile();
        Map<String, String> in = new HashMap<>();
        in.put("Channel", "HelloWorld");
        in.put("??", "??????");
        in.put("!@#$!%^@&*()_+\"?:><", "??Google");
        in.put("12345abcd", "2017");
        PackerCommon.writeValues(f, in, 74565);
        Map<String, String> out = PackerCommon.readValues(f, 74565);
        TestCase.assertNotNull(out);
        TestCase.assertEquals(in.size(), out.size());
        for (Map.Entry<String, String> entry : in.entrySet()) {
            TestCase.assertEquals(entry.getValue(), out.get(entry.getKey()));
        }
        checkApkVerified(f);
    }

    public void testValuesMixedWrite() throws IOException {
        File f = newTestFile();
        Map<String, String> in = new HashMap<>();
        in.put("!@#$!%^@&*()_+\"?:><", "??Google");
        in.put("12345abcd", "2017");
        PackerCommon.writeValues(f, in, 1193046);
        PackerCommon.writeValue(f, "hello", "Mixed", 34952);
        Map<String, String> out = PackerCommon.readValues(f, 1193046);
        TestCase.assertNotNull(out);
        TestCase.assertEquals(in.size(), out.size());
        for (Map.Entry<String, String> entry : in.entrySet()) {
            TestCase.assertEquals(entry.getValue(), out.get(entry.getKey()));
        }
        TestCase.assertEquals("Mixed", PackerCommon.readValue(f, "hello", 34952));
        PackerCommon.writeString(f, "RawValue", 8215);
        TestCase.assertEquals("RawValue", PackerCommon.readString(f, 8215));
        PackerCommon.writeString(f, "OverrideValues", 1193046);
        TestCase.assertEquals("OverrideValues", PackerCommon.readString(f, 1193046));
        checkApkVerified(f);
    }

    public void testByteBuffer() throws IOException {
        byte[] string = "Hello".getBytes();
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(123);
        buf.putChar('z');
        buf.putShort(((short) (2017)));
        buf.putFloat(3.1415F);
        buf.put(string);
        buf.putLong(9876543210L);
        buf.putDouble(3.14159265);
        buf.put(((byte) (5)));
        buf.flip();// important

        // TestUtils.showBuffer(buf);
        TestCase.assertEquals(123, buf.getInt());
        TestCase.assertEquals('z', buf.getChar());
        TestCase.assertEquals(2017, buf.getShort());
        TestCase.assertEquals(3.1415F, buf.getFloat());
        byte[] so = new byte[string.length];
        buf.get(so);
        TestCase.assertTrue(TestUtils.sameBytes(string, so));
        TestCase.assertEquals(9876543210L, buf.getLong());
        TestCase.assertEquals(3.14159265, buf.getDouble());
        TestCase.assertEquals(((byte) (5)), buf.get());
    }

    public void testBufferWrite() throws IOException {
        File f = newTestFile();
        byte[] string = "Hello".getBytes();
        ByteBuffer in = ByteBuffer.allocate(1024);
        in.order(ByteOrder.LITTLE_ENDIAN);
        in.putInt(123);
        in.putChar('z');
        in.putShort(((short) (2017)));
        in.putFloat(3.1415F);
        in.putLong(9876543210L);
        in.putDouble(3.14159265);
        in.put(((byte) (5)));
        in.put(string);
        in.flip();// important

        // TestUtils.showBuffer(in);
        Support.writeBlock(f, 1193046, in);
        ByteBuffer out = Support.readBlock(f, 1193046);
        TestCase.assertNotNull(out);
        // TestUtils.showBuffer(out);
        TestCase.assertEquals(123, out.getInt());
        TestCase.assertEquals('z', out.getChar());
        TestCase.assertEquals(2017, out.getShort());
        TestCase.assertEquals(3.1415F, out.getFloat());
        TestCase.assertEquals(9876543210L, out.getLong());
        TestCase.assertEquals(3.14159265, out.getDouble());
        TestCase.assertEquals(((byte) (5)), out.get());
        byte[] so = new byte[string.length];
        out.get(so);
        TestCase.assertTrue(TestUtils.sameBytes(string, so));
        checkApkVerified(f);
    }

    public void testChannelWriteRead() throws IOException {
        File f = newTestFile();
        PackerCommon.writeChannel(f, "Hello");
        TestCase.assertEquals("Hello", PackerCommon.readChannel(f));
        PackerCommon.writeChannel(f, "??");
        TestCase.assertEquals("??", PackerCommon.readChannel(f));
        PackerCommon.writeChannel(f, "?? C");
        TestCase.assertEquals("?? C", PackerCommon.readChannel(f));
        checkApkVerified(f);
    }
}


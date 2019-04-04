package com.google.gson;


import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public final class AmplJavaSerializationTest {
    private final Gson gson = new Gson();

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add25_add973_literalMutationString3518_failAssert65() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.1_,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            Number o_testNumberIsSerializable_add25__11 = serialized.get(0);
            double o_testNumberIsSerializable_add25__12 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add25__14 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add25_add973__22 = serialized.get(2);
            double o_testNumberIsSerializable_add25__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add25_add973_literalMutationString3518 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializablenull33_failAssert10_literalMutationString712_failAssert46() throws Exception {
        try {
            try {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = this.gson.fromJson("[1j3.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(null);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                org.junit.Assert.fail("testNumberIsSerializablenull33 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testNumberIsSerializablenull33_failAssert10_literalMutationString712 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected_1) {
            Assert.assertEquals("Expecting number, got: STRING", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add27_add648_literalMutationString3709_failAssert72() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.14,6.673^-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add27__11 = serialized.get(0).doubleValue();
            Number o_testNumberIsSerializable_add27__13 = serialized.get(1);
            double o_testNumberIsSerializable_add27__14 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add27_add648__22 = serialized.get(2);
            double o_testNumberIsSerializable_add27__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add27_add648_literalMutationString3709 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add27_add648_literalMutationString3700_failAssert57() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1U,3.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add27__11 = serialized.get(0).doubleValue();
            Number o_testNumberIsSerializable_add27__13 = serialized.get(1);
            double o_testNumberIsSerializable_add27__14 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add27_add648__22 = serialized.get(2);
            double o_testNumberIsSerializable_add27__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add27_add648_literalMutationString3700 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add29_add821_literalMutationString4053_failAssert75() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,C.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            Number o_testNumberIsSerializable_add29_add821__22 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add29_add821_literalMutationString4053 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add29_literalMutationString454_failAssert12() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.14,6y.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add29_literalMutationString454 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add29_literalMutationString532_failAssert16() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3&14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add29_literalMutationString532 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add29_add821_literalMutationString4042_failAssert56() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,t3.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            Number o_testNumberIsSerializable_add29_add821__22 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add29_add821_literalMutationString4042 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add29_add786_literalMutationString2112_failAssert54() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.14,_.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29_add786__19 = serialized.get(2);
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add29_add786_literalMutationString2112 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializablenull33_failAssert10_literalMutationString704_failAssert47() throws Exception {
        try {
            try {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = this.gson.fromJson("[1,3.14,6.673xe-11]", type);
                List<Number> serialized = serializedCopy(null);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                org.junit.Assert.fail("testNumberIsSerializablenull33 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testNumberIsSerializablenull33_failAssert10_literalMutationString704 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected_1) {
            Assert.assertEquals("Expecting number, got: STRING", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add29_add786_literalMutationString2091_failAssert53() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.14,6.6)73e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29_add786__19 = serialized.get(2);
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add29_add786_literalMutationString2091 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add27_literalMutationString222_failAssert26() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.146.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add27__11 = serialized.get(0).doubleValue();
            Number o_testNumberIsSerializable_add27__13 = serialized.get(1);
            double o_testNumberIsSerializable_add27__14 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_add27__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add27_literalMutationString222 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add29_add786_literalMutationString2123_failAssert70() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29_add786__19 = serialized.get(2);
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add29_add786_literalMutationString2123 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add25_add973_literalMutationString3524_failAssert66() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.14,6.&673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            Number o_testNumberIsSerializable_add25__11 = serialized.get(0);
            double o_testNumberIsSerializable_add25__12 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add25__14 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add25_add973__22 = serialized.get(2);
            double o_testNumberIsSerializable_add25__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add25_add973_literalMutationString3524 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_literalMutationString4_failAssert3() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.67?e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_literalMutationString4 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add27_literalMutationString323_failAssert22() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.14,6.6$73e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add27__11 = serialized.get(0).doubleValue();
            Number o_testNumberIsSerializable_add27__13 = serialized.get(1);
            double o_testNumberIsSerializable_add27__14 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_add27__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add27_literalMutationString323 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add29_literalMutationString471_failAssert19() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.146.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add29_literalMutationString471 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_literalMutationString5_failAssert4() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.673e -11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_literalMutationString6_failAssert5() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[-Td]z^b ndZQzekGw", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testNumberIsSerializable_add25_literalMutationString778_failAssert30() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = this.gson.fromJson("[1,3.14,6.A673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            Number o_testNumberIsSerializable_add25__11 = serialized.get(0);
            double o_testNumberIsSerializable_add25__12 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add25__14 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_add25__16 = serialized.get(2).doubleValue();
            org.junit.Assert.fail("testNumberIsSerializable_add25_literalMutationString778 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            Assert.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T serializedCopy(T object) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytesOut);
        out.writeObject(object);
        out.close();
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bytesIn);
        return ((T) (in.readObject()));
    }
}


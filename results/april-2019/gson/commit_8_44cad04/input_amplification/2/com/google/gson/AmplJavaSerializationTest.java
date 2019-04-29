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


public final class AmplJavaSerializationTest extends TestCase {
    private final Gson gson = new Gson();

    public void testNumberIsSerializable_literalMutationString6_failAssert0null1218_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(null);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0null1218 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString1_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.6O3e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString1 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber17_failAssert0_literalMutationString638_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6X.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(4).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber17 should have thrown IndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber17_failAssert0_literalMutationString638 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString4_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.A673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString1_failAssert0_literalMutationNumber788_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.6O3e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(0).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString1 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString1_failAssert0_literalMutationNumber788 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString4_failAssert0_add1033_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.A673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4_failAssert0_add1033 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString6_failAssert0_add1050_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,.14,6.673e-11]", type);
                serializedCopy(list);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_add1050 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString1_failAssert0_add1109_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                gson.fromJson("[1,3.14,6.6O3e-11]", type);
                List<Number> list = gson.fromJson("[1,3.14,6.6O3e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString1 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString1_failAssert0_add1109 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_add25_literalMutationString364_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,%.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            Number o_testNumberIsSerializable_add25__11 = serialized.get(0);
            double o_testNumberIsSerializable_add25__12 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add25__14 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_add25__16 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_add25_literalMutationString364 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString6_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString4_failAssert0_literalMutationNumber626_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.A673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(2).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4_failAssert0_literalMutationNumber626 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString4_failAssert0null1212_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.A673e-11]", type);
                List<Number> serialized = serializedCopy(null);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4_failAssert0null1212 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_add29_literalMutationString391_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.673e -11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add29__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add29__13 = serialized.get(1).doubleValue();
            Number o_testNumberIsSerializable_add29__15 = serialized.get(2);
            double o_testNumberIsSerializable_add29__16 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_add29_literalMutationString391 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString6_failAssert0_literalMutationNumber671_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(0).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_literalMutationNumber671 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString6_failAssert0_add1054_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1);
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_add1054 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
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


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

    public void testNumberIsSerializable_literalMutationString5_failAssert0_add1095_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,D3.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1095 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString4_failAssert0_literalMutationNumber723_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.x73e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(0).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4_failAssert0_literalMutationNumber723 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString4_failAssert0_literalMutationNumber721_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.x73e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(2).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4_failAssert0_literalMutationNumber721 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_add26_literalMutationString367_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.673p-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add26__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add26__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_add26__15 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_add26__17 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_add26_literalMutationString367 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0_literalMutationString734_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,D.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_literalMutationString734 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,D3.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString4_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.x73e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString4 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber19_literalMutationString222_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.146.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber19__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber19__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber19__15 = serialized.get(0).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber19_literalMutationString222 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber16_literalMutationString144_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,D6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber16__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber16__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber16__15 = serialized.get(1).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber16_literalMutationString144 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber18_literalMutationString244_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.*14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber18__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber18__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber18__15 = serialized.get(0).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber18_literalMutationString244 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString1_failAssert0_literalMutationString694_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[,3.14,6P673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString1 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString1_failAssert0_literalMutationString694 should have thrown JsonSyntaxException");
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


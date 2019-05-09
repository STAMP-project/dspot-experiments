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

    public void testNumberIsSerializable_literalMutationString6_failAssert0_add1036_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,^6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2);
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_add1036 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString6_failAssert0_add1036_failAssert0_add3369_failAssert0() throws Exception {
        try {
            {
                {
                    Type type = new TypeToken<List<Number>>() {}.getType();
                    List<Number> list = gson.fromJson("[1,3.14,^6.673e-11]", type);
                    List<Number> serialized = serializedCopy(list);
                    serialized.get(0).doubleValue();
                    serialized.get(1);
                    serialized.get(1).doubleValue();
                    serialized.get(2);
                    serialized.get(2).doubleValue();
                    junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
                }
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_add1036 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_add1036_failAssert0_add3369 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString6_failAssert0_literalMutationNumber626_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,^6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(0).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_literalMutationNumber626 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0_add1086_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1P3.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2);
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1086 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber16_literalMutationString164_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.673es-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber16__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber16__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber16__15 = serialized.get(1).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber16_literalMutationString164 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString6_failAssert0_add1036_failAssert0_literalMutationNumber2635_failAssert0() throws Exception {
        try {
            {
                {
                    Type type = new TypeToken<List<Number>>() {}.getType();
                    List<Number> list = gson.fromJson("[1,3.14,^6.673e-11]", type);
                    List<Number> serialized = serializedCopy(list);
                    serialized.get(0).doubleValue();
                    serialized.get(1).doubleValue();
                    serialized.get(1);
                    serialized.get(2).doubleValue();
                    junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
                }
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_add1036 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6_failAssert0_add1036_failAssert0_literalMutationNumber2635 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString6_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,^6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString6 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_add23_literalMutationString459_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.r4,6.673e-11]", type);
            List<Number> o_testNumberIsSerializable_add23__9 = serializedCopy(list);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add23__12 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add23__14 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_add23__16 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_add23_literalMutationString459 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber10_add847_literalMutationString1719_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> o_testNumberIsSerializable_literalMutationNumber10_add847__7 = gson.fromJson("[1,3.14,6.673e-11]", type);
            List<Number> list = gson.fromJson("[1,3.14,6.67Ue-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber10__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber10__14 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber10__16 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber10_add847_literalMutationString1719 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_add23_literalMutationString459_failAssert0_add3439_failAssert0() throws Exception {
        try {
            {
                new TypeToken<List<Number>>() {}.getType();
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.r4,6.673e-11]", type);
                List<Number> o_testNumberIsSerializable_add23__9 = serializedCopy(list);
                List<Number> serialized = serializedCopy(list);
                double o_testNumberIsSerializable_add23__12 = serialized.get(0).doubleValue();
                double o_testNumberIsSerializable_add23__14 = serialized.get(1).doubleValue();
                double o_testNumberIsSerializable_add23__16 = serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_add23_literalMutationString459 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_add23_literalMutationString459_failAssert0_add3439 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1P3.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber17_failAssert0_add1092_failAssert0_literalMutationString2648_failAssert0() throws Exception {
        try {
            {
                {
                    Type type = new TypeToken<List<Number>>() {}.getType();
                    List<Number> list = gson.fromJson("[1,3.14,6.673e:-11]", type);
                    List<Number> serialized = serializedCopy(list);
                    serialized.get(0);
                    serialized.get(0).doubleValue();
                    serialized.get(1).doubleValue();
                    serialized.get(4).doubleValue();
                    junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber17 should have thrown IndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber17_failAssert0_add1092 should have thrown IndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber17_failAssert0_add1092_failAssert0_literalMutationString2648 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_add23_literalMutationString459_failAssert0_literalMutationNumber2791_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.r4,6.673e-11]", type);
                List<Number> o_testNumberIsSerializable_add23__9 = serializedCopy(list);
                List<Number> serialized = serializedCopy(list);
                double o_testNumberIsSerializable_add23__12 = serialized.get(0).doubleValue();
                double o_testNumberIsSerializable_add23__14 = serialized.get(1).doubleValue();
                double o_testNumberIsSerializable_add23__16 = serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_add23_literalMutationString459 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_add23_literalMutationString459_failAssert0_literalMutationNumber2791 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber17_failAssert0_literalMutationString733_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.y673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(4).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber17 should have thrown IndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber17_failAssert0_literalMutationString733 should have thrown JsonSyntaxException");
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


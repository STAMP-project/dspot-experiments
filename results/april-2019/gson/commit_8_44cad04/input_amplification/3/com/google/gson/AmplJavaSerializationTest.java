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

    public void testNumberIsSerializable_literalMutationNumber19_literalMutationString165_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,O6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber19__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber19__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber19__15 = serialized.get(0).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber19_literalMutationString165 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0_add1084_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.6|3e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(1);
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1084 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber11_literalMutationString265_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.6u3e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber11__11 = serialized.get(2).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber11__14 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber11__16 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber11_literalMutationString265 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0_add1084_failAssert0_add5312_failAssert0() throws Exception {
        try {
            {
                {
                    Type type = new TypeToken<List<Number>>() {}.getType();
                    List<Number> list = gson.fromJson("[1,3.14,6.6|3e-11]", type);
                    List<Number> serialized = serializedCopy(list);
                    serialized.get(0).doubleValue();
                    serialized.get(1);
                    serialized.get(1);
                    serialized.get(1).doubleValue();
                    serialized.get(2).doubleValue();
                    junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
                }
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1084 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1084_failAssert0_add5312 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_add22null1209_failAssert0_literalMutationString3050_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> o_testNumberIsSerializable_add22__7 = gson.fromJson("[1,3.14,6.673u-11]", type);
                List<Number> list = gson.fromJson("[1,3.14,6.673e-11]", null);
                List<Number> serialized = serializedCopy(list);
                double o_testNumberIsSerializable_add22__12 = serialized.get(0).doubleValue();
                double o_testNumberIsSerializable_add22__14 = serialized.get(1).doubleValue();
                double o_testNumberIsSerializable_add22__16 = serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_add22null1209 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_add22null1209_failAssert0_literalMutationString3050 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber12_literalMutationString325_failAssert0null5906_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[P,3.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(null);
                double o_testNumberIsSerializable_literalMutationNumber12__11 = serialized.get(0).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber12__13 = serialized.get(2).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber12__16 = serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber12_literalMutationString325 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber12_literalMutationString325_failAssert0null5906 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0_add1081_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.6|3e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(0).doubleValue();
                serialized.get(1).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1081 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber16_add840_literalMutationString2268_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1Q3.14,6.673e-11]", type);
            List<Number> o_testNumberIsSerializable_literalMutationNumber16_add840__9 = serializedCopy(list);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber16__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber16__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber16__15 = serialized.get(1).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber16_add840_literalMutationString2268 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber18_literalMutationString306_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,O3.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber18__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber18__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber18__15 = serialized.get(0).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber18_literalMutationString306 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0_add1084_failAssert0_literalMutationNumber3689_failAssert0() throws Exception {
        try {
            {
                {
                    Type type = new TypeToken<List<Number>>() {}.getType();
                    List<Number> list = gson.fromJson("[1,3.14,6.6|3e-11]", type);
                    List<Number> serialized = serializedCopy(list);
                    serialized.get(0).doubleValue();
                    serialized.get(1);
                    serialized.get(2).doubleValue();
                    serialized.get(2).doubleValue();
                    junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
                }
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1084 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1084_failAssert0_literalMutationNumber3689 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.6|3e-11]", type);
            List<Number> serialized = serializedCopy(list);
            serialized.get(0).doubleValue();
            serialized.get(1).doubleValue();
            serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0_literalMutationNumber730_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.6|3e-11]", type);
                List<Number> serialized = serializedCopy(list);
                serialized.get(0).doubleValue();
                serialized.get(0).doubleValue();
                serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_literalMutationNumber730 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_add21_literalMutationString502_failAssert0() throws Exception {
        try {
            new TypeToken<List<Number>>() {}.getType();
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1O,3.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_add21__16 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_add21__18 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_add21__20 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_add21_literalMutationString502 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString5_failAssert0_add1081_failAssert0_add5551_failAssert0() throws Exception {
        try {
            {
                {
                    Type type = new TypeToken<List<Number>>() {}.getType();
                    List<Number> list = gson.fromJson("[1,3.14,6.6|3e-11]", type);
                    serializedCopy(list);
                    List<Number> serialized = serializedCopy(list);
                    serialized.get(0).doubleValue();
                    serialized.get(0).doubleValue();
                    serialized.get(1).doubleValue();
                    serialized.get(2).doubleValue();
                    junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5 should have thrown JsonSyntaxException");
                }
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1081 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString5_failAssert0_add1081_failAssert0_add5551 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber11_literalMutationString265_failAssert0_literalMutationNumber4046_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.6u3e-11]", type);
                List<Number> serialized = serializedCopy(list);
                double o_testNumberIsSerializable_literalMutationNumber11__11 = serialized.get(2).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber11__14 = serialized.get(0).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber11__16 = serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber11_literalMutationString265 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber11_literalMutationString265_failAssert0_literalMutationNumber4046 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber12_literalMutationString325_failAssert0_add5168_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[P,3.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                double o_testNumberIsSerializable_literalMutationNumber12__11 = serialized.get(0).doubleValue();
                serialized.get(2);
                double o_testNumberIsSerializable_literalMutationNumber12__13 = serialized.get(2).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber12__16 = serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber12_literalMutationString325 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber12_literalMutationString325_failAssert0_add5168 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber8_failAssert0null1235_failAssert0_literalMutationString3300_failAssert0() throws Exception {
        try {
            {
                {
                    Type type = new TypeToken<List<Number>>() {}.getType();
                    List<Number> list = gson.fromJson("[1,3.14l6.673e-11]", type);
                    List<Number> serialized = serializedCopy(null);
                    serialized.get(-1).doubleValue();
                    serialized.get(1).doubleValue();
                    serialized.get(2).doubleValue();
                    junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber8 should have thrown ArrayIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber8_failAssert0null1235 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber8_failAssert0null1235_failAssert0_literalMutationString3300 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationString3_literalMutationString367_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[1,3.14,6.73-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationString3__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationString3__13 = serialized.get(1).doubleValue();
            double o_testNumberIsSerializable_literalMutationString3__15 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationString3_literalMutationString367 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber12_literalMutationString325_failAssert0_literalMutationNumber3405_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[P,3.14,6.673e-11]", type);
                List<Number> serialized = serializedCopy(list);
                double o_testNumberIsSerializable_literalMutationNumber12__11 = serialized.get(0).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber12__13 = serialized.get(2).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber12__16 = serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber12_literalMutationString325 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber12_literalMutationString325_failAssert0_literalMutationNumber3405 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber12_literalMutationString325_failAssert0() throws Exception {
        try {
            Type type = new TypeToken<List<Number>>() {}.getType();
            List<Number> list = gson.fromJson("[P,3.14,6.673e-11]", type);
            List<Number> serialized = serializedCopy(list);
            double o_testNumberIsSerializable_literalMutationNumber12__11 = serialized.get(0).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber12__13 = serialized.get(2).doubleValue();
            double o_testNumberIsSerializable_literalMutationNumber12__16 = serialized.get(2).doubleValue();
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber12_literalMutationString325 should have thrown JsonSyntaxException");
        } catch (JsonSyntaxException expected) {
            TestCase.assertEquals("Expecting number, got: STRING", expected.getMessage());
        }
    }

    public void testNumberIsSerializable_literalMutationNumber11_literalMutationString265_failAssert0_add5494_failAssert0() throws Exception {
        try {
            {
                Type type = new TypeToken<List<Number>>() {}.getType();
                List<Number> list = gson.fromJson("[1,3.14,6.6u3e-11]", type);
                List<Number> serialized = serializedCopy(list);
                double o_testNumberIsSerializable_literalMutationNumber11__11 = serialized.get(2).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber11__14 = serialized.get(1).doubleValue();
                double o_testNumberIsSerializable_literalMutationNumber11__16 = serialized.get(2).doubleValue();
                junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber11_literalMutationString265 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testNumberIsSerializable_literalMutationNumber11_literalMutationString265_failAssert0_add5494 should have thrown JsonSyntaxException");
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


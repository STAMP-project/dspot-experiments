package io.protostuff;


/**
 * From https://groups.google.com/forum/#!topic/protostuff/D7Bb1REf8pQ (Anil Pandge)
 */
public class AmplCodedDataInputTest extends junit.framework.TestCase {
    public static void main(java.lang.String[] args) {
        try {
            io.protostuff.SampleClass _clazz = new io.protostuff.SampleClass();
            java.util.List<java.lang.String> testStrings = new java.util.ArrayList<java.lang.String>();
            for (int i = 0; i < 1800; i++) {
                java.lang.String test = new java.lang.String(("TestingString" + i));
                testStrings.add(test);
                try {
                    _clazz.setTestStringList(testStrings);
                    byte[] serialize = io.protostuff.AmplCodedDataInputTest.serialize(_clazz);
                    java.lang.System.out.println(("Payload Size = " + (serialize.length)));
                    io.protostuff.SampleClass deserialize = io.protostuff.AmplCodedDataInputTest.deserialize(serialize);
                    java.lang.System.out.println(deserialize.getTestStringList().get(i));
                } catch (java.lang.Exception ex) {
                    java.lang.System.out.println("Failed");
                }
            }
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
    }

    public void testIt() throws java.lang.Exception {
        io.protostuff.SampleClass _clazz = new io.protostuff.SampleClass();
        java.util.List<java.lang.String> testStrings = new java.util.ArrayList<java.lang.String>();
        for (int i = 0; i < 1800; i++) {
            java.lang.String test = new java.lang.String(("TestingString" + i));
            testStrings.add(test);
            _clazz.setTestStringList(testStrings);
            byte[] serialize = io.protostuff.AmplCodedDataInputTest.serialize(_clazz);
            junit.framework.TestCase.assertNotNull(io.protostuff.AmplCodedDataInputTest.deserialize(serialize));
        }
    }

    private static byte[] serialize(final io.protostuff.SampleClass t) throws java.lang.Exception {
        java.io.ObjectOutputStream oos = null;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try {
            oos = new java.io.ObjectOutputStream(baos);
            t.writeExternal(oos);
        } catch (java.io.IOException e) {
            throw new java.lang.Exception(e);
        } finally {
            io.protostuff.AmplCodedDataInputTest.tryClose(oos);
        }
        return baos.toByteArray();
    }

    private static io.protostuff.SampleClass deserialize(final byte[] bytes) throws java.lang.Exception {
        final io.protostuff.SampleClass t = new io.protostuff.SampleClass();
        java.io.ObjectInputStream ois = null;
        try {
            ois = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes));
            t.readExternal(ois);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        } finally {
            io.protostuff.AmplCodedDataInputTest.tryClose(ois);
        }
        return t;
    }

    private static void tryClose(java.io.Closeable closeable) throws java.lang.Exception {
        try {
            closeable.close();
        } catch (java.io.IOException e) {
            throw new java.lang.Exception(e);
        }
    }
}


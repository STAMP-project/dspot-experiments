package io.protostuff;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * From https://groups.google.com/forum/#!topic/protostuff/D7Bb1REf8pQ (Anil Pandge)
 */
public class CodedDataInputTest extends TestCase {
    public void testIt() throws Exception {
        SampleClass _clazz = new SampleClass();
        List<String> testStrings = new ArrayList<String>();
        for (int i = 0; i < 1800; i++) {
            String test = new String(("TestingString" + i));
            testStrings.add(test);
            _clazz.setTestStringList(testStrings);
            byte[] serialize = CodedDataInputTest.serialize(_clazz);
            TestCase.assertNotNull(CodedDataInputTest.deserialize(serialize));
        }
    }
}


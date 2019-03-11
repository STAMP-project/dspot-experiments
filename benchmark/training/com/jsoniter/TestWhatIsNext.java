package com.jsoniter;


import ValueType.OBJECT;
import java.io.IOException;
import junit.framework.TestCase;


public class TestWhatIsNext extends TestCase {
    public void test() throws IOException {
        JsonIterator parser = JsonIterator.parse("{}");
        TestCase.assertEquals(OBJECT, parser.whatIsNext());
    }
}


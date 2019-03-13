package com.jsoniter;


import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonProperty;
import java.io.IOException;
import junit.framework.TestCase;


public class TestAnnotation extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
        // JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
    }

    public static class TestObject4 {
        private int field1;

        private TestObject4() {
        }

        @JsonCreator
        public static TestAnnotation.TestObject4 createObject(@JsonProperty("field1")
        int field1) {
            TestAnnotation.TestObject4 obj = new TestAnnotation.TestObject4();
            obj.field1 = field1;
            return obj;
        }
    }

    public void test_static_factory() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestAnnotation.TestObject4 obj = iter.read(TestAnnotation.TestObject4.class);
        TestCase.assertEquals(100, obj.field1);
    }

    public static class TestObject5 {
        private int field1;

        public void setField1(int field1) {
            this.field1 = field1;
        }
    }

    public void test_single_param_setter() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestAnnotation.TestObject5 obj = iter.read(TestAnnotation.TestObject5.class);
        TestCase.assertEquals(100, obj.field1);
    }

    public static class TestObject8 {
        @JsonCreator
        public TestObject8(@JsonProperty(required = true)
        int param1) {
        }
    }

    public static class TestObject17 {
        public int field1;

        public void setField1(int field1) {
            this.field1 = field1;
        }

        @JsonCreator
        public void initialize(@JsonProperty("field1")
        int field1) {
        }
    }

    public void test_name_conflict() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        TestCase.assertNotNull(iter.read(TestAnnotation.TestObject17.class));
    }

    public interface TestObject18Interface<A> {
        void setHello(A val);
    }

    public static class TestObject18 implements TestAnnotation.TestObject18Interface<Integer> {
        public int _val;

        @Override
        public void setHello(Integer val) {
            _val = val;
        }
    }

    public void test_inherited_setter_is_not_duplicate() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"hello\":1}");
        TestAnnotation.TestObject18 obj = iter.read(TestAnnotation.TestObject18.class);
        TestCase.assertNotNull(obj);
        TestCase.assertEquals(1, obj._val);
    }
}


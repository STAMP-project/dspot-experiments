package com.apollographql.apollo.api;


import org.junit.Assert;
import org.junit.Test;


public final class InputTest {
    @Test
    public void testInputEqualsOnNotNullValue() {
        String value = "Hello world!";
        Input<String> stringInput = Input.fromNullable(value);
        Input<String> anotherStringInput = Input.fromNullable(value);
        Assert.assertEquals(stringInput, anotherStringInput);
    }

    @Test
    public void testInputNotEqualsOnDifferentValues() {
        String value = "Hello world!";
        String value2 = "Bye world!";
        Input<String> stringInput = Input.fromNullable(value);
        Input<String> anotherStringInput = Input.fromNullable(value2);
        Assert.assertNotEquals(stringInput, anotherStringInput);
    }

    @Test
    public void testInputEqualsOnNullValue() {
        Input<String> stringInput = Input.fromNullable(null);
        Input<String> anotherStringInput = Input.fromNullable(null);
        Assert.assertEquals(stringInput, anotherStringInput);
    }

    @Test
    public void testInputEqualsOnNotNullObjects() {
        InputTest.TestObject object = new InputTest.TestObject("Hello world!");
        Input<InputTest.TestObject> aInput = Input.fromNullable(object);
        Input<InputTest.TestObject> anotherInput = Input.fromNullable(object);
        Assert.assertEquals(aInput, anotherInput);
    }

    @Test
    public void testInputEqualsOnEqualObjectsWithDifferentReferences() {
        InputTest.TestObject object1 = new InputTest.TestObject("Hello world!");
        InputTest.TestObject object2 = new InputTest.TestObject("Hello world!");
        Input<InputTest.TestObject> input1 = Input.fromNullable(object1);
        Input<InputTest.TestObject> input2 = Input.fromNullable(object2);
        Assert.assertEquals(input1, input2);
    }

    @Test
    public void testInputNotEqualsOnDifferentObjects() {
        InputTest.TestObject object = new InputTest.TestObject("Hello world!");
        InputTest.TestObject anotherObject = new InputTest.TestObject("Bye world!");
        Input<InputTest.TestObject> aInput = Input.fromNullable(object);
        Input<InputTest.TestObject> anotherInput = Input.fromNullable(anotherObject);
        Assert.assertNotEquals(aInput, anotherInput);
    }

    @Test
    public void testInputEqualsOnObjectsWithNullValue() {
        InputTest.TestObject object = new InputTest.TestObject(null);
        Input<InputTest.TestObject> aInput = Input.fromNullable(object);
        Input<InputTest.TestObject> anotherInput = Input.fromNullable(object);
        Assert.assertEquals(aInput, anotherInput);
    }

    @Test
    public void testInputNotEqualsWhenAnObjectIsNull() {
        InputTest.TestObject object = new InputTest.TestObject(null);
        Input<InputTest.TestObject> aInput = Input.fromNullable(object);
        Input<InputTest.TestObject> anotherInput = Input.fromNullable(null);
        Assert.assertNotEquals(aInput, anotherInput);
    }

    // ==================================================================
    // ==================================================================
    class TestObject {
        private String value;

        TestObject(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof InputTest.TestObject)) {
                return false;
            }
            InputTest.TestObject input = ((InputTest.TestObject) (o));
            return (((value) != null) && (value.equals(input.value))) || (((value) == null) && ((input.value) == null));
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}


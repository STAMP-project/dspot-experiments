package org.junit.runners.parameterized;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.TestClass;


public class TestWithParametersTest {
    private static final String DUMMY_NAME = "dummy name";

    private static final TestClass DUMMY_TEST_CLASS = new TestClass(TestWithParametersTest.DummyClass.class);

    private static final List<Object> DUMMY_PARAMETERS = Arrays.<Object>asList("a", "b");

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void cannotBeCreatedWithoutAName() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The name is missing.");
        new TestWithParameters(null, TestWithParametersTest.DUMMY_TEST_CLASS, TestWithParametersTest.DUMMY_PARAMETERS);
    }

    @Test
    public void cannotBeCreatedWithoutTestClass() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The test class is missing.");
        new TestWithParameters(TestWithParametersTest.DUMMY_NAME, null, TestWithParametersTest.DUMMY_PARAMETERS);
    }

    @Test
    public void cannotBeCreatedWithoutParameters() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The parameters are missing.");
        new TestWithParameters(TestWithParametersTest.DUMMY_NAME, TestWithParametersTest.DUMMY_TEST_CLASS, ((List<Object>) (null)));
    }

    @Test
    public void doesNotAllowToModifyProvidedParameters() {
        TestWithParameters test = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, TestWithParametersTest.DUMMY_TEST_CLASS, TestWithParametersTest.DUMMY_PARAMETERS);
        thrown.expect(UnsupportedOperationException.class);
        test.getParameters().set(0, "another parameter");
    }

    @Test
    public void doesNotConsiderParametersWhichChangedAfterTestInstantiation() {
        List<Object> parameters = Arrays.<Object>asList("dummy parameter");
        TestWithParameters test = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, TestWithParametersTest.DUMMY_TEST_CLASS, parameters);
        parameters.set(0, "another parameter");
        Assert.assertEquals(Arrays.asList("dummy parameter"), test.getParameters());
    }

    @Test
    public void isEqualToTestWithSameNameAndTestClassAndParameters() {
        TestWithParameters firstTest = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, new TestClass(TestWithParametersTest.DummyClass.class), Arrays.<Object>asList("a", "b"));
        TestWithParameters secondTest = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, new TestClass(TestWithParametersTest.DummyClass.class), Arrays.<Object>asList("a", "b"));
        Assert.assertEquals(firstTest, secondTest);
    }

    @Test
    public void isNotEqualToTestWithDifferentName() {
        TestWithParameters firstTest = new TestWithParameters("name", TestWithParametersTest.DUMMY_TEST_CLASS, TestWithParametersTest.DUMMY_PARAMETERS);
        TestWithParameters secondTest = new TestWithParameters("another name", TestWithParametersTest.DUMMY_TEST_CLASS, TestWithParametersTest.DUMMY_PARAMETERS);
        Assert.assertNotEquals(firstTest, secondTest);
    }

    @Test
    public void isNotEqualToTestWithDifferentTestClass() {
        TestWithParameters firstTest = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, new TestClass(TestWithParametersTest.DummyClass.class), TestWithParametersTest.DUMMY_PARAMETERS);
        TestWithParameters secondTest = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, new TestClass(TestWithParametersTest.AnotherDummyClass.class), TestWithParametersTest.DUMMY_PARAMETERS);
        Assert.assertNotEquals(firstTest, secondTest);
    }

    @Test
    public void isNotEqualToTestWithDifferentParameters() {
        TestWithParameters firstTest = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, TestWithParametersTest.DUMMY_TEST_CLASS, Arrays.<Object>asList("a"));
        TestWithParameters secondTest = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, TestWithParametersTest.DUMMY_TEST_CLASS, Arrays.<Object>asList("b"));
        Assert.assertNotEquals(firstTest, secondTest);
    }

    @Test
    public void isNotEqualToObjectWithDifferentClass() {
        TestWithParameters test = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, TestWithParametersTest.DUMMY_TEST_CLASS, TestWithParametersTest.DUMMY_PARAMETERS);
        Assert.assertNotEquals(test, new Integer(3));
    }

    @Test
    public void hasSameHashCodeAsEqualTest() {
        TestWithParameters firstTest = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, TestWithParametersTest.DUMMY_TEST_CLASS, TestWithParametersTest.DUMMY_PARAMETERS);
        TestWithParameters secondTest = new TestWithParameters(TestWithParametersTest.DUMMY_NAME, TestWithParametersTest.DUMMY_TEST_CLASS, TestWithParametersTest.DUMMY_PARAMETERS);
        Assert.assertEquals(firstTest.hashCode(), secondTest.hashCode());
    }

    @Test
    public void hasMeaningfulToString() {
        TestWithParameters test = new TestWithParameters("name", new TestClass(TestWithParametersTest.DummyClass.class), Arrays.<Object>asList("first parameter", "second parameter"));
        Assert.assertEquals("Wrong toString().", "org.junit.runners.parameterized.TestWithParametersTest$DummyClass 'name' with parameters [first parameter, second parameter]", test.toString());
    }

    private static class DummyClass {}

    private static class AnotherDummyClass {}
}


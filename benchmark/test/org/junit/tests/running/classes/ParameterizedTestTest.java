package org.junit.tests.running.classes;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;


public class ParameterizedTestTest {
    @RunWith(Parameterized.class)
    public static class AdditionTest {
        @Parameterized.Parameters(name = "{index}: {0} + {1} = {2}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{ new Object[]{ 0, 0, 0 }, new Object[]{ 1, 1, 2 }, new Object[]{ 3, 2, 5 }, new Object[]{ 4, 3, 7 } });
        }

        private int firstSummand;

        private int secondSummand;

        private int sum;

        public AdditionTest(int firstSummand, int secondSummand, int sum) {
            this.firstSummand = firstSummand;
            this.secondSummand = secondSummand;
            this.sum = sum;
        }

        @Test
        public void test() {
            Assert.assertEquals(sum, ((firstSummand) + (secondSummand)));
        }
    }

    @Test
    public void countsRuns() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.AdditionTest.class);
        Assert.assertEquals(4, result.getRunCount());
    }

    @Test
    public void countBeforeRun() throws Exception {
        Runner runner = Request.aClass(ParameterizedTestTest.AdditionTest.class).getRunner();
        Assert.assertEquals(4, runner.testCount());
    }

    @Test
    public void plansNamedCorrectly() throws Exception {
        Runner runner = Request.aClass(ParameterizedTestTest.AdditionTest.class).getRunner();
        Description description = runner.getDescription();
        Assert.assertEquals("[2: 3 + 2 = 5]", description.getChildren().get(2).getDisplayName());
    }

    @RunWith(Parameterized.class)
    public static class ThreeFailures {
        @Parameterized.Parameters(name = "{index}: x={0}")
        public static Collection<Integer> data() {
            return Arrays.asList(1, 2, 3);
        }

        @Parameterized.Parameter(0)
        public int unused;

        @Test
        public void testSomething() {
            Assert.fail();
        }
    }

    @Test
    public void countsFailures() throws Exception {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.ThreeFailures.class);
        Assert.assertEquals(3, result.getFailureCount());
    }

    @Test
    public void failuresNamedCorrectly() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.ThreeFailures.class);
        Assert.assertEquals((("testSomething[0: x=1](" + (ParameterizedTestTest.ThreeFailures.class.getName())) + ")"), result.getFailures().get(0).getTestHeader());
    }

    @RunWith(Parameterized.class)
    public static class ParameterizedWithoutSpecialTestname {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{ new Object[]{ 3 }, new Object[]{ 3 } });
        }

        public ParameterizedWithoutSpecialTestname(Object something) {
        }

        @Test
        public void testSomething() {
        }
    }

    @Test
    public void usesIndexAsTestName() {
        Runner runner = Request.aClass(ParameterizedTestTest.ParameterizedWithoutSpecialTestname.class).getRunner();
        Description description = runner.getDescription();
        Assert.assertEquals("[1]", description.getChildren().get(1).getDisplayName());
    }

    @RunWith(Parameterized.class)
    public static class AdditionTestWithAnnotatedFields {
        @Parameterized.Parameters(name = "{index}: {0} + {1} = {2}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{ new Object[]{ 0, 0, 0 }, new Object[]{ 1, 1, 2 }, new Object[]{ 3, 2, 5 }, new Object[]{ 4, 3, 7 } });
        }

        @Parameterized.Parameter(0)
        public int firstSummand;

        @Parameterized.Parameter(1)
        public int secondSummand;

        @Parameterized.Parameter(2)
        public int sum;

        @Test
        public void test() {
            Assert.assertEquals(sum, ((firstSummand) + (secondSummand)));
        }
    }

    @Test
    public void providesDataByAnnotatedFields() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.AdditionTestWithAnnotatedFields.class);
        Assert.assertEquals(4, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }

    @RunWith(Parameterized.class)
    public static class BadIndexForAnnotatedFieldTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{ new Object[]{ 0 } });
        }

        @Parameterized.Parameter(2)
        public int fInput;

        public int fExpected;

        @Test
        public void test() {
            Assert.assertEquals(fExpected, fib(fInput));
        }

        private int fib(int x) {
            return 0;
        }
    }

    @Test
    public void failureOnInitialization() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.BadIndexForAnnotatedFieldTest.class);
        Assert.assertEquals(1, result.getFailureCount());
        List<Failure> failures = result.getFailures();
        MatcherAssert.assertThat(failures.get(0).getException().getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("Invalid @Parameter value: 2. @Parameter fields counted: 1. Please use an index between 0 and 0."), CoreMatchers.containsString("@Parameter(0) is never used.")));
    }

    @RunWith(Parameterized.class)
    public static class BadNumberOfAnnotatedFieldTest {
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{ new Object[]{ 0, 0 } });
        }

        @Parameterized.Parameter(0)
        public int fInput;

        public int fExpected;

        @Test
        public void test() {
            Assert.assertEquals(fExpected, fib(fInput));
        }

        private int fib(int x) {
            return 0;
        }
    }

    @Test
    public void numberOfFieldsAndParametersShouldMatch() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.BadNumberOfAnnotatedFieldTest.class);
        Assert.assertEquals(1, result.getFailureCount());
        List<Failure> failures = result.getFailures();
        Assert.assertTrue(failures.get(0).getException().getMessage().contains("Wrong number of parameters and @Parameter fields. @Parameter fields counted: 1, available parameters: 2."));
    }

    private static String fLog;

    @RunWith(Parameterized.class)
    public static class BeforeAndAfter {
        @BeforeClass
        public static void before() {
            ParameterizedTestTest.fLog += "before ";
        }

        @AfterClass
        public static void after() {
            ParameterizedTestTest.fLog += "after ";
        }

        public BeforeAndAfter(int x) {
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{ new Object[]{ 3 } });
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void beforeAndAfterClassAreRun() {
        ParameterizedTestTest.fLog = "";
        JUnitCore.runClasses(ParameterizedTestTest.BeforeAndAfter.class);
        Assert.assertEquals("before after ", ParameterizedTestTest.fLog);
    }

    @RunWith(Parameterized.class)
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class BeforeParamAndAfterParam {
        @BeforeClass
        public static void before() {
            ParameterizedTestTest.fLog += "beforeClass ";
        }

        @Parameterized.BeforeParam
        public static void beforeParam(String x) {
            ParameterizedTestTest.fLog += ("before(" + x) + ") ";
        }

        @Parameterized.AfterParam
        public static void afterParam() {
            ParameterizedTestTest.fLog += "afterParam ";
        }

        @AfterClass
        public static void after() {
            ParameterizedTestTest.fLog += "afterClass ";
        }

        private final String x;

        public BeforeParamAndAfterParam(String x) {
            this.x = x;
        }

        @Parameterized.Parameters
        public static Collection<String> data() {
            return Arrays.asList("A", "B");
        }

        @Test
        public void first() {
            ParameterizedTestTest.fLog += ("first(" + (x)) + ") ";
        }

        @Test
        public void second() {
            ParameterizedTestTest.fLog += ("second(" + (x)) + ") ";
        }
    }

    @Test
    public void beforeParamAndAfterParamAreRun() {
        ParameterizedTestTest.fLog = "";
        Result result = JUnitCore.runClasses(ParameterizedTestTest.BeforeParamAndAfterParam.class);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertEquals(("beforeClass before(A) first(A) second(A) afterParam " + "before(B) first(B) second(B) afterParam afterClass "), ParameterizedTestTest.fLog);
    }

    @RunWith(Parameterized.class)
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class MultipleBeforeParamAndAfterParam {
        @Parameterized.BeforeParam
        public static void before1() {
            ParameterizedTestTest.fLog += "before1() ";
        }

        @Parameterized.BeforeParam
        public static void before2(String x) {
            ParameterizedTestTest.fLog += ("before2(" + x) + ") ";
        }

        @Parameterized.AfterParam
        public static void after2() {
            ParameterizedTestTest.fLog += "after2() ";
        }

        @Parameterized.AfterParam
        public static void after1(String x) {
            ParameterizedTestTest.fLog += ("after1(" + x) + ") ";
        }

        private final String x;

        public MultipleBeforeParamAndAfterParam(String x) {
            this.x = x;
        }

        @Parameterized.Parameters
        public static Collection<String> data() {
            return Arrays.asList("A", "B");
        }

        @Test
        public void first() {
            ParameterizedTestTest.fLog += ("first(" + (x)) + ") ";
        }

        @Test
        public void second() {
            ParameterizedTestTest.fLog += ("second(" + (x)) + ") ";
        }
    }

    @Test
    public void multipleBeforeParamAndAfterParamAreRun() {
        ParameterizedTestTest.fLog = "";
        Result result = JUnitCore.runClasses(ParameterizedTestTest.MultipleBeforeParamAndAfterParam.class);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertEquals(("before1() before2(A) first(A) second(A) after1(A) after2() " + "before1() before2(B) first(B) second(B) after1(B) after2() "), ParameterizedTestTest.fLog);
    }

    @RunWith(Parameterized.class)
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class MultipleParametersBeforeParamAndAfterParam {
        @Parameterized.BeforeParam
        public static void before(String x, int y) {
            ParameterizedTestTest.fLog += ((("before(" + x) + ",") + y) + ") ";
        }

        @Parameterized.AfterParam
        public static void after(String x, int y) {
            ParameterizedTestTest.fLog += ((("after(" + x) + ",") + y) + ") ";
        }

        private final String x;

        private final int y;

        public MultipleParametersBeforeParamAndAfterParam(String x, int y) {
            this.x = x;
            this.y = y;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[]{ "A", 1 }, new Object[]{ "B", 2 });
        }

        @Test
        public void first() {
            ParameterizedTestTest.fLog += ((("first(" + (x)) + ",") + (y)) + ") ";
        }

        @Test
        public void second() {
            ParameterizedTestTest.fLog += ((("second(" + (x)) + ",") + (y)) + ") ";
        }
    }

    @Test
    public void multipleParametersBeforeParamAndAfterParamAreRun() {
        ParameterizedTestTest.fLog = "";
        Result result = JUnitCore.runClasses(ParameterizedTestTest.MultipleParametersBeforeParamAndAfterParam.class);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertEquals(("before(A,1) first(A,1) second(A,1) after(A,1) " + "before(B,2) first(B,2) second(B,2) after(B,2) "), ParameterizedTestTest.fLog);
    }

    @RunWith(Parameterized.class)
    public static class BeforeParamAndAfterParamError {
        @Parameterized.BeforeParam
        public void beforeParam(String x) {
        }

        @Parameterized.AfterParam
        private static void afterParam() {
        }

        public BeforeParamAndAfterParamError(String x) {
        }

        @Parameterized.Parameters
        public static Collection<String> data() {
            return Arrays.asList("A", "B");
        }

        @Test
        public void test() {
        }
    }

    @Test
    public void beforeParamAndAfterParamValidation() {
        ParameterizedTestTest.fLog = "";
        Result result = JUnitCore.runClasses(ParameterizedTestTest.BeforeParamAndAfterParamError.class);
        Assert.assertEquals(1, result.getFailureCount());
        List<Failure> failures = result.getFailures();
        MatcherAssert.assertThat(failures.get(0).getMessage(), CoreMatchers.containsString("beforeParam() should be static"));
        MatcherAssert.assertThat(failures.get(0).getMessage(), CoreMatchers.containsString("afterParam() should be public"));
    }

    @RunWith(Parameterized.class)
    public static class BeforeParamAndAfterParamErrorNumberOfParameters {
        @Parameterized.BeforeParam
        public static void beforeParam(String x, String y) {
        }

        @Parameterized.AfterParam
        public static void afterParam(String x, String y, String z) {
        }

        public BeforeParamAndAfterParamErrorNumberOfParameters(String x) {
        }

        @Parameterized.Parameters
        public static Collection<String> data() {
            return Arrays.asList("A", "B", "C", "D");
        }

        @Test
        public void test() {
        }
    }

    @Test
    public void beforeParamAndAfterParamValidationNumberOfParameters() {
        ParameterizedTestTest.fLog = "";
        Result result = JUnitCore.runClasses(ParameterizedTestTest.BeforeParamAndAfterParamErrorNumberOfParameters.class);
        Assert.assertEquals(1, result.getFailureCount());
        List<Failure> failures = result.getFailures();
        MatcherAssert.assertThat(failures.get(0).getMessage(), CoreMatchers.containsString("Method beforeParam() should have 0 or 1 parameter(s)"));
        MatcherAssert.assertThat(failures.get(0).getMessage(), CoreMatchers.containsString("Method afterParam() should have 0 or 1 parameter(s)"));
    }

    @RunWith(Parameterized.class)
    public static class EmptyTest {
        @BeforeClass
        public static void before() {
            ParameterizedTestTest.fLog += "before ";
        }

        @AfterClass
        public static void after() {
            ParameterizedTestTest.fLog += "after ";
        }
    }

    @Test
    public void validateClassCatchesNoParameters() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.EmptyTest.class);
        Assert.assertEquals(1, result.getFailureCount());
    }

    @RunWith(Parameterized.class)
    public static class IncorrectTest {
        @Test
        public int test() {
            return 0;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Collections.singletonList(new Object[]{ 1 });
        }
    }

    @Test
    public void failuresAddedForBadTestMethod() throws Exception {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.IncorrectTest.class);
        Assert.assertEquals(1, result.getFailureCount());
    }

    @RunWith(Parameterized.class)
    public static class ProtectedParametersTest {
        @Parameterized.Parameters
        protected static Collection<Object[]> data() {
            return Collections.emptyList();
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void meaningfulFailureWhenParametersNotPublic() {
        assertTestCreatesSingleFailureWithMessage(ParameterizedTestTest.ProtectedParametersTest.class, ("No public static parameters method on class " + (ParameterizedTestTest.ProtectedParametersTest.class.getName())));
    }

    @RunWith(Parameterized.class)
    public static class ParametersNotIterable {
        @Parameterized.Parameters
        public static String data() {
            return "foo";
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void meaningfulFailureWhenParametersAreNotAnIterable() {
        MatcherAssert.assertThat(PrintableResult.testResult(ParameterizedTestTest.ParametersNotIterable.class).toString(), CoreMatchers.containsString("ParametersNotIterable.data() must return an Iterable of arrays."));
    }

    @RunWith(Parameterized.class)
    public static class PrivateConstructor {
        private PrivateConstructor(int x) {
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{ new Object[]{ 3 } });
        }

        @Test
        public void aTest() {
        }
    }

    @Test(expected = InitializationError.class)
    public void exceptionWhenPrivateConstructor() throws Throwable {
        new Parameterized(ParameterizedTestTest.PrivateConstructor.class);
    }

    @RunWith(Parameterized.class)
    public static class AdditionTestWithArray {
        @Parameterized.Parameters(name = "{index}: {0} + {1} = {2}")
        public static Object[][] data() {
            return new Object[][]{ new Object[]{ 0, 0, 0 }, new Object[]{ 1, 1, 2 }, new Object[]{ 3, 2, 5 }, new Object[]{ 4, 3, 7 } };
        }

        @Parameterized.Parameter(0)
        public int firstSummand;

        @Parameterized.Parameter(1)
        public int secondSummand;

        @Parameterized.Parameter(2)
        public int sum;

        @Test
        public void test() {
            Assert.assertEquals(sum, ((firstSummand) + (secondSummand)));
        }
    }

    @Test
    public void runsEveryTestOfArray() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.AdditionTestWithArray.class);
        Assert.assertEquals(4, result.getRunCount());
    }

    @RunWith(Parameterized.class)
    public static class SingleArgumentTestWithArray {
        @Parameterized.Parameters
        public static Object[] data() {
            return new Object[]{ "first test", "second test" };
        }

        public SingleArgumentTestWithArray(Object argument) {
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void runsForEverySingleArgumentOfArray() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.SingleArgumentTestWithArray.class);
        Assert.assertEquals(2, result.getRunCount());
    }

    @RunWith(Parameterized.class)
    public static class SingleArgumentTestWithIterable {
        private static final AtomicBoolean dataCalled = new AtomicBoolean(false);

        @Parameterized.Parameters
        public static Iterable<? extends Object> data() {
            if (!(ParameterizedTestTest.SingleArgumentTestWithIterable.dataCalled.compareAndSet(false, true))) {
                Assert.fail("Should not call @Parameters method more than once");
            }
            return new ParameterizedTestTest.OneShotIterable<String>(Arrays.asList("first test", "second test"));
        }

        public SingleArgumentTestWithIterable(Object argument) {
        }

        @Test
        public void aTest() {
        }
    }

    private static class OneShotIterable<T> implements Iterable<T> {
        private final Iterable<T> delegate;

        private final AtomicBoolean iterated = new AtomicBoolean(false);

        OneShotIterable(Iterable<T> delegate) {
            this.delegate = delegate;
        }

        public Iterator<T> iterator() {
            if (iterated.compareAndSet(false, true)) {
                return delegate.iterator();
            }
            throw new IllegalStateException("Cannot call iterator() more than once");
        }
    }

    @Test
    public void runsForEverySingleArgumentOfIterable() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.SingleArgumentTestWithIterable.class);
        Assert.assertEquals(2, result.getRunCount());
    }

    @RunWith(Parameterized.class)
    public static class SingleArgumentTestWithCollection {
        @Parameterized.Parameters
        public static Iterable<? extends Object> data() {
            return Collections.unmodifiableCollection(Arrays.asList("first test", "second test"));
        }

        public SingleArgumentTestWithCollection(Object argument) {
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void runsForEverySingleArgumentOfCollection() {
        Result result = JUnitCore.runClasses(ParameterizedTestTest.SingleArgumentTestWithCollection.class);
        Assert.assertEquals(2, result.getRunCount());
    }

    public static class ExceptionThrowingRunnerFactory implements ParametersRunnerFactory {
        public Runner createRunnerForTestWithParameters(TestWithParameters test) throws InitializationError {
            throw new InitializationError("Called ExceptionThrowingRunnerFactory.");
        }
    }

    @RunWith(Parameterized.class)
    @Parameterized.UseParametersRunnerFactory(ParameterizedTestTest.ExceptionThrowingRunnerFactory.class)
    public static class TestWithUseParametersRunnerFactoryAnnotation {
        @Parameterized.Parameters
        public static Iterable<? extends Object> data() {
            return Arrays.asList("single test");
        }

        public TestWithUseParametersRunnerFactoryAnnotation(Object argument) {
        }

        @Test
        public void aTest() {
        }
    }

    @Test
    public void usesParametersRunnerFactoryThatWasSpecifiedByAnnotation() {
        assertTestCreatesSingleFailureWithMessage(ParameterizedTestTest.TestWithUseParametersRunnerFactoryAnnotation.class, "Called ExceptionThrowingRunnerFactory.");
    }

    @RunWith(Parameterized.class)
    @Parameterized.UseParametersRunnerFactory(ParameterizedTestTest.ExceptionThrowingRunnerFactory.class)
    public abstract static class UseParameterizedFactoryAbstractTest {
        @Parameterized.Parameters
        public static Iterable<? extends Object> data() {
            return Arrays.asList("single test");
        }
    }

    public static class UseParameterizedFactoryTest extends ParameterizedTestTest.UseParameterizedFactoryAbstractTest {
        public UseParameterizedFactoryTest(String parameter) {
        }

        @Test
        public void parameterizedTest() {
        }
    }

    @Test
    public void usesParametersRunnerFactoryThatWasSpecifiedByAnnotationInSuperClass() {
        assertTestCreatesSingleFailureWithMessage(ParameterizedTestTest.UseParameterizedFactoryTest.class, "Called ExceptionThrowingRunnerFactory.");
    }

    @RunWith(Parameterized.class)
    public static class AssumptionInParametersMethod {
        static boolean assumptionFails;

        @Parameterized.Parameters
        public static Iterable<String> data() {
            Assume.assumeFalse(ParameterizedTestTest.AssumptionInParametersMethod.assumptionFails);
            return Collections.singletonList("foobar");
        }

        public AssumptionInParametersMethod(String parameter) {
        }

        @Test
        public void test1() {
        }

        @Test
        public void test2() {
        }
    }

    @Test
    public void testsAreExecutedWhenAssumptionInParametersMethodDoesNotFail() {
        ParameterizedTestTest.AssumptionInParametersMethod.assumptionFails = false;
        Result result = JUnitCore.runClasses(ParameterizedTestTest.AssumptionInParametersMethod.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(0, getAssumptionFailureCount());
        Assert.assertEquals(0, result.getIgnoreCount());
        Assert.assertEquals(2, result.getRunCount());
    }

    @Test
    public void testsAreNotExecutedWhenAssumptionInParametersMethodFails() {
        ParameterizedTestTest.AssumptionInParametersMethod.assumptionFails = true;
        Result result = JUnitCore.runClasses(ParameterizedTestTest.AssumptionInParametersMethod.class);
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(1, getAssumptionFailureCount());
        Assert.assertEquals(0, result.getIgnoreCount());
        Assert.assertEquals(0, result.getRunCount());
    }
}


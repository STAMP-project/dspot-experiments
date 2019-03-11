package org.junit.tests.running.classes;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;

import static org.junit.experimental.categories.Categories.CategoryFilter.exclude;


/**
 * Tests verifying that class-level fixtures ({@link BeforeClass} and
 * {@link AfterClass}) and rules ({@link ClassRule}) are not executed when there
 * are no test methods to be run in a test class because they have been ignored.
 */
public class ClassLevelMethodsWithIgnoredTestsTest {
    private static final String FAILURE_MESSAGE = "This should not have happened!";

    public static class BeforeClassWithIgnoredTest {
        @BeforeClass
        public static void beforeClass() {
            Assert.fail(ClassLevelMethodsWithIgnoredTestsTest.FAILURE_MESSAGE);
        }

        @Ignore
        @Test
        public void test() throws Exception {
            Assert.fail("test() should not run");
        }
    }

    @Test
    public void beforeClassShouldNotRunWhenAllTestsAreIgnored() {
        runClassAndVerifyNoFailures(ClassLevelMethodsWithIgnoredTestsTest.BeforeClassWithIgnoredTest.class, "BeforeClass should not have been executed because the test method is ignored!");
    }

    @Ignore
    public static class BeforeClassWithIgnoredClass {
        @BeforeClass
        public static void beforeClass() {
            Assert.fail(ClassLevelMethodsWithIgnoredTestsTest.FAILURE_MESSAGE);
        }

        @Test
        public void test() throws Exception {
            Assert.fail("test() should not run");
        }
    }

    @Test
    public void beforeClassShouldNotRunWhenWholeClassIsIgnored() {
        runClassAndVerifyNoFailures(ClassLevelMethodsWithIgnoredTestsTest.BeforeClassWithIgnoredClass.class, "BeforeClass should not have been executed because the whole test class is ignored!");
    }

    public static class AfterClassWithIgnoredTest {
        @Ignore
        @Test
        public void test() throws Exception {
            Assert.fail("test() should not run");
        }

        @AfterClass
        public static void afterClass() {
            Assert.fail(ClassLevelMethodsWithIgnoredTestsTest.FAILURE_MESSAGE);
        }
    }

    @Test
    public void afterClassShouldNotRunWhenAllTestsAreIgnored() {
        runClassAndVerifyNoFailures(ClassLevelMethodsWithIgnoredTestsTest.AfterClassWithIgnoredTest.class, "AfterClass should not have been executed because the test method is ignored!");
    }

    public interface FilteredTests {}

    public static class BeforeClassWithFilteredTest {
        @BeforeClass
        public static void setUpClass() {
            Assert.fail(ClassLevelMethodsWithIgnoredTestsTest.FAILURE_MESSAGE);
        }

        @Category(ClassLevelMethodsWithIgnoredTestsTest.FilteredTests.class)
        @Test
        public void test() throws Exception {
            Assert.fail("test() should not run");
        }
    }

    public static class HasUnfilteredTest {
        @Test
        public void unfilteredTest() {
            // to prevent errors when all other tests have been filtered
        }
    }

    @Test
    public void beforeClassShouldNotRunWhenAllTestsAreFiltered() {
        Result result = new JUnitCore().run(Request.classes(ClassLevelMethodsWithIgnoredTestsTest.BeforeClassWithFilteredTest.class, ClassLevelMethodsWithIgnoredTestsTest.HasUnfilteredTest.class).filterWith(exclude(ClassLevelMethodsWithIgnoredTestsTest.FilteredTests.class)));
        analyseResult(result, "BeforeClass should not have been executed because the test method is filtered!");
    }

    public static class BrokenRule implements TestRule {
        public Statement apply(Statement base, Description description) {
            throw new RuntimeException("this rule is broken");
        }
    }

    public static class ClassRuleWithIgnoredTest {
        @ClassRule
        public static ClassLevelMethodsWithIgnoredTestsTest.BrokenRule brokenRule = new ClassLevelMethodsWithIgnoredTestsTest.BrokenRule();

        @Ignore
        @Test
        public void test() throws Exception {
            Assert.fail("test() should not be run");
        }
    }

    @Test
    public void classRuleShouldNotBeAppliedWhenAllTestsAreIgnored() {
        runClassAndVerifyNoFailures(ClassLevelMethodsWithIgnoredTestsTest.ClassRuleWithIgnoredTest.class, "The class rule should have been applied because the test method is ignored!");
    }
}


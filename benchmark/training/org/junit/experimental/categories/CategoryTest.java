package org.junit.experimental.categories;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import static org.junit.experimental.categories.Categories.CategoryFilter.<init>;
import static org.junit.experimental.categories.Categories.CategoryFilter.categoryFilter;
import static org.junit.experimental.categories.Categories.CategoryFilter.exclude;
import static org.junit.experimental.categories.Categories.CategoryFilter.include;


public class CategoryTest {
    // category marker
    public interface FastTests {}

    // category marker
    public interface SlowTests {}

    // category marker
    public interface ReallySlowTests {}

    public static class OneOfEach {
        @Category(CategoryTest.FastTests.class)
        @Test
        public void a() {
        }

        @Category(CategoryTest.SlowTests.class)
        @Test
        public void b() {
        }

        @Category(CategoryTest.ReallySlowTests.class)
        @Test
        public void c() {
        }
    }

    public static class A {
        @Test
        public void a() {
            Assert.fail();
        }

        @Category(CategoryTest.SlowTests.class)
        @Test
        public void b() {
        }
    }

    @Category(CategoryTest.SlowTests.class)
    public static class B {
        @Test
        public void c() {
        }
    }

    public static class C {
        @Test
        public void d() {
            Assert.fail();
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoryTest.SlowTests.class)
    @Suite.SuiteClasses({ CategoryTest.A.class, CategoryTest.B.class, CategoryTest.C.class })
    public static class SlowTestSuite {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoryTest.SlowTests.class)
    @Suite.SuiteClasses({ CategoryTest.A.class })
    public static class JustA {}

    @Test
    public void testCountOnJustA() {
        Assert.assertThat(PrintableResult.testResult(CategoryTest.JustA.class), ResultMatchers.isSuccessful());
    }

    @Test
    public void testCount() {
        Assert.assertThat(PrintableResult.testResult(CategoryTest.SlowTestSuite.class), ResultMatchers.isSuccessful());
    }

    public static class Category1 {}

    public static class Category2 {}

    public static class SomeAreSlow {
        @Test
        public void noCategory() {
        }

        @Category(CategoryTest.Category1.class)
        @Test
        public void justCategory1() {
        }

        @Category(CategoryTest.Category2.class)
        @Test
        public void justCategory2() {
        }

        @Category({ CategoryTest.Category1.class, CategoryTest.Category2.class })
        @Test
        public void both() {
        }

        @Category({ CategoryTest.Category2.class, CategoryTest.Category1.class })
        @Test
        public void bothReversed() {
        }
    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory(CategoryTest.Category1.class)
    @Suite.SuiteClasses({ CategoryTest.SomeAreSlow.class })
    public static class SomeAreSlowSuite {}

    @Test
    public void testCountOnAWithoutSlowTests() {
        Result result = JUnitCore.runClasses(CategoryTest.SomeAreSlowSuite.class);
        Assert.assertThat(PrintableResult.testResult(CategoryTest.SomeAreSlowSuite.class), ResultMatchers.isSuccessful());
        Assert.assertEquals(2, result.getRunCount());
        Assert.assertTrue(result.wasSuccessful());
    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory(CategoryTest.Category1.class)
    @Categories.IncludeCategory(CategoryTest.Category2.class)
    @Suite.SuiteClasses({ CategoryTest.SomeAreSlow.class })
    public static class IncludeAndExcludeSuite {}

    @Test
    public void testsThatAreBothIncludedAndExcludedAreExcluded() {
        Result result = JUnitCore.runClasses(CategoryTest.IncludeAndExcludeSuite.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertTrue(result.wasSuccessful());
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({ CategoryTest.A.class, CategoryTest.B.class, CategoryTest.C.class })
    public static class TestSuiteWithNoCategories {}

    @Test
    public void testCountWithExplicitIncludeFilter() throws Throwable {
        Categories.CategoryFilter include = Categories.CategoryFilter.include(CategoryTest.SlowTests.class);
        Request baseRequest = Request.aClass(CategoryTest.TestSuiteWithNoCategories.class);
        Result result = new JUnitCore().run(baseRequest.filterWith(include));
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(2, result.getRunCount());
    }

    @Test
    public void testCountWithExplicitExcludeFilter() throws Throwable {
        Categories.CategoryFilter include = exclude(CategoryTest.SlowTests.class);
        Request baseRequest = Request.aClass(CategoryTest.TestSuiteWithNoCategories.class);
        Result result = new JUnitCore().run(baseRequest.filterWith(include));
        Assert.assertEquals(2, result.getFailureCount());
        Assert.assertEquals(2, result.getRunCount());
    }

    @Test
    public void testCountWithExplicitExcludeFilter_usingConstructor() throws Throwable {
        Categories.CategoryFilter include = new Categories.CategoryFilter(null, CategoryTest.SlowTests.class);
        Request baseRequest = Request.aClass(CategoryTest.TestSuiteWithNoCategories.class);
        Result result = new JUnitCore().run(baseRequest.filterWith(include));
        Assert.assertEquals(2, result.getFailureCount());
        Assert.assertEquals(2, result.getRunCount());
    }

    @Test
    public void categoryFilterLeavesOnlyMatchingMethods() throws NoTestsRemainException, InitializationError {
        Categories.CategoryFilter filter = Categories.CategoryFilter.include(CategoryTest.SlowTests.class);
        BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(CategoryTest.A.class);
        filter.apply(runner);
        Assert.assertEquals(1, runner.testCount());
    }

    @Test
    public void categoryFilterLeavesOnlyMatchingMethods_usingConstructor() throws NoTestsRemainException, InitializationError {
        Categories.CategoryFilter filter = new Categories.CategoryFilter(CategoryTest.SlowTests.class, null);
        BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(CategoryTest.A.class);
        filter.apply(runner);
        Assert.assertEquals(1, runner.testCount());
    }

    public static class OneFastOneSlow {
        @Category(CategoryTest.FastTests.class)
        @Test
        public void a() {
        }

        @Category(CategoryTest.SlowTests.class)
        @Test
        public void b() {
        }
    }

    @Test
    public void categoryFilterRejectsIncompatibleCategory() throws NoTestsRemainException, InitializationError {
        Categories.CategoryFilter filter = Categories.CategoryFilter.include(CategoryTest.SlowTests.class);
        BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(CategoryTest.OneFastOneSlow.class);
        filter.apply(runner);
        Assert.assertEquals(1, runner.testCount());
    }

    public static class OneFast {
        @Category(CategoryTest.FastTests.class)
        @Test
        public void a() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoryTest.SlowTests.class)
    @Suite.SuiteClasses({ CategoryTest.OneFast.class })
    public static class OneFastSuite {}

    @Test
    public void ifNoTestsToRunUseErrorRunner() {
        Result result = JUnitCore.runClasses(CategoryTest.OneFastSuite.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertFalse(result.wasSuccessful());
    }

    @Test
    public void describeACategoryFilter() {
        Categories.CategoryFilter filter = Categories.CategoryFilter.include(CategoryTest.SlowTests.class);
        Assert.assertEquals((("categories [" + (CategoryTest.SlowTests.class)) + "]"), filter.describe());
    }

    @Test
    public void describeMultipleCategoryFilter() {
        Categories.CategoryFilter filter = Categories.CategoryFilter.include(CategoryTest.FastTests.class, CategoryTest.SlowTests.class);
        String d1 = String.format("categories [%s, %s]", CategoryTest.FastTests.class, CategoryTest.SlowTests.class);
        String d2 = String.format("categories [%s, %s]", CategoryTest.SlowTests.class, CategoryTest.FastTests.class);
        Assert.assertThat(filter.describe(), Is.is(AnyOf.anyOf(IsEqual.equalTo(d1), IsEqual.equalTo(d2))));
    }

    public static class OneThatIsBothFastAndSlow {
        @Category({ CategoryTest.FastTests.class, CategoryTest.SlowTests.class })
        @Test
        public void a() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoryTest.SlowTests.class)
    @Suite.SuiteClasses({ CategoryTest.OneThatIsBothFastAndSlow.class })
    public static class ChooseSlowFromBoth {}

    @Test
    public void runMethodWithTwoCategories() {
        Assert.assertThat(PrintableResult.testResult(CategoryTest.ChooseSlowFromBoth.class), ResultMatchers.isSuccessful());
    }

    public interface VerySlowTests extends CategoryTest.SlowTests {}

    public static class OneVerySlowTest {
        @Category(CategoryTest.VerySlowTests.class)
        @Test
        public void a() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoryTest.SlowTests.class)
    @Suite.SuiteClasses({ CategoryTest.OneVerySlowTest.class })
    public static class RunSlowFromVerySlow {}

    @Test
    public void subclassesOfIncludedCategoriesAreRun() {
        Assert.assertThat(PrintableResult.testResult(CategoryTest.RunSlowFromVerySlow.class), ResultMatchers.isSuccessful());
    }

    public interface MultiA {}

    public interface MultiB {}

    public interface MultiC {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory(value = { CategoryTest.MultiA.class, CategoryTest.MultiB.class }, matchAny = false)
    @Suite.SuiteClasses(CategoryTest.AllIncludedMustMatched.class)
    public static class AllIncludedMustBeMatchedSuite {}

    public static class AllIncludedMustMatched {
        @Test
        @Category({ CategoryTest.MultiA.class, CategoryTest.MultiB.class })
        public void a() {
        }

        @Test
        @Category(CategoryTest.MultiB.class)
        public void b() {
            Assert.fail(("When multiple categories are included in a Suite, " + "@Test method must match all include categories"));
        }
    }

    @Test
    public void allIncludedSuiteCategoriesMustBeMatched() {
        Result result = JUnitCore.runClasses(CategoryTest.AllIncludedMustBeMatchedSuite.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory({ CategoryTest.MultiA.class, CategoryTest.MultiB.class })
    @Categories.ExcludeCategory(CategoryTest.MultiC.class)
    @Suite.SuiteClasses(CategoryTest.MultipleIncludesAndExcludeOnMethod.class)
    public static class MultiIncludeWithExcludeCategorySuite {}

    public static class MultipleIncludesAndExcludeOnMethod {
        @Test
        @Category({ CategoryTest.MultiA.class, CategoryTest.MultiB.class })
        public void a() {
        }

        @Test
        @Category({ CategoryTest.MultiA.class, CategoryTest.MultiB.class, CategoryTest.MultiC.class })
        public void b() {
            Assert.fail(("When multiple categories are included and excluded in a Suite, " + "@Test method must match all include categories and contain non of the excluded"));
        }
    }

    @Test
    public void anyMethodWithExcludedCategoryWillBeExcluded() {
        Result result = JUnitCore.runClasses(CategoryTest.MultiIncludeWithExcludeCategorySuite.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }

    public static class ClassAsCategory {}

    public static class OneMoreTest {
        @Category(CategoryTest.ClassAsCategory.class)
        @Test
        public void a() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoryTest.ClassAsCategory.class)
    @Suite.SuiteClasses({ CategoryTest.OneMoreTest.class })
    public static class RunClassAsCategory {}

    @Test
    public void classesCanBeCategories() {
        Assert.assertThat(PrintableResult.testResult(CategoryTest.RunClassAsCategory.class), ResultMatchers.isSuccessful());
    }

    @Category(CategoryTest.SlowTests.class)
    public abstract static class Ancestor {}

    public static class Inherited extends CategoryTest.Ancestor {
        @Test
        public void a() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CategoryTest.SlowTests.class)
    @Suite.SuiteClasses(CategoryTest.Inherited.class)
    public interface InheritanceSuite {}

    @Test
    public void testInheritance() {
        Result result = JUnitCore.runClasses(CategoryTest.InheritanceSuite.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertTrue(result.wasSuccessful());
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(Runnable.class)
    @Categories.ExcludeCategory(Runnable.class)
    @Suite.SuiteClasses({  })
    public static class EmptyCategoriesSuite {}

    @Test
    public void emptyCategoriesSuite() {
        Assert.assertThat(PrintableResult.testResult(CategoryTest.EmptyCategoriesSuite.class), ResultMatchers.failureCountIs(1));
    }

    @Category(Runnable.class)
    public static class NoTest {}

    @Category(Runnable.class)
    public static class IgnoredTest {
        @Ignore
        @Test
        public void test() {
            Assert.fail();
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(Runnable.class)
    @Suite.SuiteClasses({ CategoryTest.NoTest.class, CategoryTest.IgnoredTest.class })
    public static class IgnoredTestCategoriesSuite {}

    @Test
    public void ignoredTest() {
        // behaves same as Suite
        Result result = JUnitCore.runClasses(CategoryTest.IgnoredTestCategoriesSuite.class);
        Assert.assertFalse(result.wasSuccessful());
        Assert.assertThat(result.getRunCount(), Is.is(1));
        Assert.assertThat(result.getFailureCount(), Is.is(1));
        Assert.assertThat(result.getIgnoreCount(), Is.is(1));
    }

    @Category(Runnable.class)
    public static class ExcludedTest1 {
        @Test
        public void test() {
            Assert.fail();
        }
    }

    @Category(Runnable.class)
    public static class ExcludedTest2 {
        @Test
        @Category(Runnable.class)
        public void test() {
            Assert.fail();
        }
    }

    public static class IncludedTest {
        @Test
        @Category(Object.class)
        public void test() {
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory({ Runnable.class, Object.class })
    @Categories.ExcludeCategory(Runnable.class)
    @Suite.SuiteClasses({ CategoryTest.ExcludedTest1.class, CategoryTest.ExcludedTest2.class, CategoryTest.IncludedTest.class })
    public static class IncludedExcludedSameSuite {}

    @Test
    public void oneRunnableOthersAvoided() {
        Result result = JUnitCore.runClasses(CategoryTest.IncludedExcludedSameSuite.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertTrue(result.wasSuccessful());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCountWithMultipleExcludeFilter() throws Throwable {
        Set<Class<?>> exclusions = new HashSet<Class<?>>(2);
        Collections.addAll(exclusions, CategoryTest.SlowTests.class, CategoryTest.FastTests.class);
        Categories.CategoryFilter exclude = categoryFilter(true, null, true, exclusions);
        Request baseRequest = Request.aClass(CategoryTest.OneOfEach.class);
        Result result = new JUnitCore().run(baseRequest.filterWith(exclude));
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(1, result.getRunCount());
    }

    @Test
    public void testCountWithMultipleIncludeFilter() throws Throwable {
        Categories.CategoryFilter exclude = include(true, CategoryTest.SlowTests.class, CategoryTest.FastTests.class);
        Request baseRequest = Request.aClass(CategoryTest.OneOfEach.class);
        Result result = new JUnitCore().run(baseRequest.filterWith(exclude));
        Assert.assertTrue(result.wasSuccessful());
        Assert.assertEquals(2, result.getRunCount());
    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory(String.class)
    @Suite.SuiteClasses(CategoryTest.NoIncludeCategoryAnnotationTest.class)
    public static class NoIncludeCategoryAnnotationSuite {}

    @Category(CharSequence.class)
    public static class NoIncludeCategoryAnnotationTest {
        @Test
        public void test2() {
        }

        @Test
        @Category(String.class)
        public void test1() {
        }
    }

    @Test
    public void noIncludeCategoryAnnotation() {
        Result testResult = JUnitCore.runClasses(CategoryTest.NoIncludeCategoryAnnotationSuite.class);
        Assert.assertTrue(testResult.wasSuccessful());
        Assert.assertEquals(1, testResult.getRunCount());
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(CharSequence.class)
    @Categories.ExcludeCategory(String.class)
    @Suite.SuiteClasses(CategoryTest.NoIncludeCategoryAnnotationTest.class)
    public static class SameAsNoIncludeCategoryAnnotationSuite {}

    @Test
    public void sameAsNoIncludeCategoryAnnotation() {
        Result testResult = JUnitCore.runClasses(CategoryTest.SameAsNoIncludeCategoryAnnotationSuite.class);
        Assert.assertTrue(testResult.wasSuccessful());
        Assert.assertEquals(1, testResult.getRunCount());
    }
}


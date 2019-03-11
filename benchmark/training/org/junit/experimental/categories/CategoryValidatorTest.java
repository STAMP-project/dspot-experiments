package org.junit.experimental.categories;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;


public class CategoryValidatorTest {
    public static class SampleCategory {}

    public static class CategoryTest {
        @BeforeClass
        @Category(CategoryValidatorTest.SampleCategory.class)
        public static void methodWithCategoryAndBeforeClass() {
        }

        @AfterClass
        @Category(CategoryValidatorTest.SampleCategory.class)
        public static void methodWithCategoryAndAfterClass() {
        }

        @Before
        @Category(CategoryValidatorTest.SampleCategory.class)
        public static void methodWithCategoryAndBefore() {
        }

        @After
        @Category(CategoryValidatorTest.SampleCategory.class)
        public static void methodWithCategoryAndAfter() {
        }

        @Category(CategoryValidatorTest.SampleCategory.class)
        public static void methodWithCategory() {
        }
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithBeforeClass() {
        FrameworkMethod method = new TestClass(CategoryValidatorTest.CategoryTest.class).getAnnotatedMethods(BeforeClass.class).get(0);
        testAndAssertErrorMessage(method, "@BeforeClass can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithAfterClass() {
        FrameworkMethod method = new TestClass(CategoryValidatorTest.CategoryTest.class).getAnnotatedMethods(AfterClass.class).get(0);
        testAndAssertErrorMessage(method, "@AfterClass can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithBefore() {
        FrameworkMethod method = new TestClass(CategoryValidatorTest.CategoryTest.class).getAnnotatedMethods(Before.class).get(0);
        testAndAssertErrorMessage(method, "@Before can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithAfter() {
        FrameworkMethod method = new TestClass(CategoryValidatorTest.CategoryTest.class).getAnnotatedMethods(After.class).get(0);
        testAndAssertErrorMessage(method, "@After can not be combined with @Category");
    }

    @Test
    public void errorIsNotAddedWhenCategoryIsNotCombinedWithIllegalCombination() throws NoSuchMethodException {
        FrameworkMethod method = new FrameworkMethod(CategoryValidatorTest.CategoryTest.class.getMethod("methodWithCategory"));
        List<Exception> errors = new CategoryValidator().validateAnnotatedMethod(method);
        Assert.assertThat(errors.size(), CoreMatchers.is(0));
    }
}


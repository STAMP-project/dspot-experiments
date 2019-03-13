package org.junit.experimental.categories;


import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 *
 *
 * @author tibor17
 * @version 4.12
 * @since 4.12
 */
public final class MultiCategoryTest {
    public interface A {}

    public interface B {}

    public interface C {}

    /**
     * This test is mentioned in {@code Categories} and any changes
     * must be reflected.
     */
    @Test
    public void runSuite() {
        // Targeting Test:
        Result testResult = JUnitCore.runClasses(MultiCategoryTest.MultiCategorySuite.class);
        Assert.assertThat("unexpected run count", testResult.getRunCount(), Is.is(IsEqual.equalTo(2)));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), Is.is(IsEqual.equalTo(0)));
        Assert.assertThat("unexpected failure count", testResult.getIgnoreCount(), Is.is(IsEqual.equalTo(0)));
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory({ MultiCategoryTest.A.class, MultiCategoryTest.B.class })
    @Categories.ExcludeCategory(MultiCategoryTest.C.class)
    @Suite.SuiteClasses({ MultiCategoryTest.CategoriesTest.class })
    public static final class MultiCategorySuite {}

    public static final class CategoriesTest {
        @Test
        @Category(MultiCategoryTest.A.class)
        public void a() {
        }

        @Test
        @Category(MultiCategoryTest.B.class)
        public void b() {
        }

        @Test
        @Category(MultiCategoryTest.C.class)
        public void c() {
            Assert.fail();
        }

        @Test
        public void anything() {
            Assert.fail();
        }
    }

    @Test
    public void inheritanceAnyIncluded() {
        Result testResult = JUnitCore.runClasses(MultiCategoryTest.InheritanceAny.class);
        Assert.assertThat("unexpected run count", testResult.getRunCount(), Is.is(IsEqual.equalTo(3)));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), Is.is(IsEqual.equalTo(0)));
        Assert.assertThat("unexpected failure count", testResult.getIgnoreCount(), Is.is(IsEqual.equalTo(0)));
    }

    @Test
    public void inheritanceAllIncluded() {
        Result testResult = JUnitCore.runClasses(MultiCategoryTest.InheritanceAll.class);
        Assert.assertThat("unexpected run count", testResult.getRunCount(), Is.is(IsEqual.equalTo(1)));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), Is.is(IsEqual.equalTo(0)));
        Assert.assertThat("unexpected failure count", testResult.getIgnoreCount(), Is.is(IsEqual.equalTo(0)));
    }

    @Test
    public void inheritanceAnyAll() {
        // any included, all excluded
        Result testResult = JUnitCore.runClasses(MultiCategoryTest.InheritanceAnyAll.class);
        Assert.assertThat("unexpected run count", testResult.getRunCount(), Is.is(IsEqual.equalTo(3)));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), Is.is(IsEqual.equalTo(0)));
        Assert.assertThat("unexpected failure count", testResult.getIgnoreCount(), Is.is(IsEqual.equalTo(0)));
    }

    @Test
    public void inheritanceAllAny() {
        // all included, any excluded
        Result testResult = JUnitCore.runClasses(MultiCategoryTest.InheritanceAllAny.class);
        Assert.assertThat("unexpected run count", testResult.getRunCount(), Is.is(IsEqual.equalTo(1)));
        Assert.assertThat("unexpected failure count", testResult.getFailureCount(), Is.is(IsEqual.equalTo(1)));
        Assert.assertThat("unexpected failure count", testResult.getIgnoreCount(), Is.is(IsEqual.equalTo(0)));
        Assert.assertFalse(testResult.wasSuccessful());
    }

    public static class X implements MultiCategoryTest.A {}

    public static class Y implements MultiCategoryTest.B {}

    public static class Z implements MultiCategoryTest.A , MultiCategoryTest.B {}

    public static class W implements MultiCategoryTest.A , MultiCategoryTest.B , MultiCategoryTest.C {}

    public static class Q implements MultiCategoryTest.A , MultiCategoryTest.C {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory({ MultiCategoryTest.A.class, MultiCategoryTest.B.class })
    @Categories.ExcludeCategory(MultiCategoryTest.C.class)
    @Suite.SuiteClasses({ MultiCategoryTest.InheritanceAnyTest.class })
    public static final class InheritanceAny {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory(value = { MultiCategoryTest.A.class, MultiCategoryTest.B.class }, matchAny = false)
    @Categories.ExcludeCategory(MultiCategoryTest.C.class)
    @Suite.SuiteClasses({ MultiCategoryTest.InheritanceAllTest.class })
    public static final class InheritanceAll {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory({ MultiCategoryTest.A.class, MultiCategoryTest.B.class })
    @Categories.ExcludeCategory(value = { MultiCategoryTest.A.class, MultiCategoryTest.C.class }, matchAny = false)
    @Suite.SuiteClasses({ MultiCategoryTest.InheritanceAnyAllTest.class })
    public static final class InheritanceAnyAll {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory(value = { MultiCategoryTest.A.class, MultiCategoryTest.B.class }, matchAny = false)
    @Categories.ExcludeCategory({ MultiCategoryTest.A.class, MultiCategoryTest.C.class })
    @Suite.SuiteClasses({ MultiCategoryTest.InheritanceAllAnyTest.class })
    public static final class InheritanceAllAny {}

    public static final class InheritanceAnyTest {
        @Test
        @Category(MultiCategoryTest.X.class)
        public void x() {
        }

        @Test
        @Category(MultiCategoryTest.Y.class)
        public void y() {
        }

        @Test
        @Category(MultiCategoryTest.Z.class)
        public void z() {
        }

        @Test
        @Category(MultiCategoryTest.W.class)
        public void w() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.Q.class)
        public void q() {
            Assert.fail();
        }

        @Test
        @Category(Runnable.class)
        public void runnable() {
            Assert.fail();
        }

        @Test
        public void t() {
            Assert.fail();
        }
    }

    public static final class InheritanceAllTest {
        @Test
        @Category(MultiCategoryTest.X.class)
        public void x() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.Y.class)
        public void y() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.Z.class)
        public void z() {
        }

        @Test
        @Category(MultiCategoryTest.W.class)
        public void w() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.Q.class)
        public void q() {
            Assert.fail();
        }

        @Test
        @Category(Runnable.class)
        public void runnable() {
            Assert.fail();
        }

        @Test
        public void t() {
            Assert.fail();
        }
    }

    public static final class InheritanceAnyAllTest {
        @Test
        @Category(MultiCategoryTest.X.class)
        public void x() {
        }

        @Test
        @Category(MultiCategoryTest.Y.class)
        public void y() {
        }

        @Test
        @Category(MultiCategoryTest.Z.class)
        public void z() {
        }

        @Test
        @Category(MultiCategoryTest.W.class)
        public void w() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.Q.class)
        public void q() {
            Assert.fail();
        }

        @Test
        @Category(Runnable.class)
        public void runnable() {
            Assert.fail();
        }

        @Test
        public void t() {
            Assert.fail();
        }
    }

    public static final class InheritanceAllAnyTest {
        @Test
        @Category(MultiCategoryTest.X.class)
        public void x() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.Y.class)
        public void y() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.Z.class)
        public void z() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.W.class)
        public void w() {
            Assert.fail();
        }

        @Test
        @Category(MultiCategoryTest.Q.class)
        public void q() {
            Assert.fail();
        }

        @Test
        @Category(Runnable.class)
        public void runnable() {
            Assert.fail();
        }

        @Test
        public void t() {
            Assert.fail();
        }
    }
}


package org.junit.runners.parameterized;


import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Dmitry Baev charlie@yandex-team.ru
Date: 03.05.14
 */
public class ParameterizedNamesTest {
    @RunWith(Parameterized.class)
    public static class ParametrizedWithSpecialCharsInName {
        public ParametrizedWithSpecialCharsInName(String s) {
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[]{ "\n" }, new Object[]{ "\r\n" }, new Object[]{ "\r" }, new Object[]{ "\u0085" }, new Object[]{ "\u2028" }, new Object[]{ "\u2029" });
        }

        @Test
        public void test() {
        }
    }

    @Test
    public void parameterizedTestsWithSpecialCharsInName() {
        Request request = Request.aClass(ParameterizedNamesTest.ParametrizedWithSpecialCharsInName.class);
        for (Description parent : request.getRunner().getDescription().getChildren()) {
            for (Description description : parent.getChildren()) {
                Assert.assertEquals(("test" + (parent.getDisplayName())), description.getMethodName());
            }
        }
    }
}


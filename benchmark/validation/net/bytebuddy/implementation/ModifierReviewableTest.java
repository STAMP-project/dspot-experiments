package net.bytebuddy.implementation;


import java.lang.reflect.Method;
import net.bytebuddy.description.ModifierReviewable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ModifierReviewableTest {
    private final ModifierReviewableTest.SimpleModifierReviewable simpleModifierReviewable;

    private final Method method;

    private final Object expected;

    public ModifierReviewableTest(int modifiers, String methodName, Object expected) throws Exception {
        simpleModifierReviewable = new ModifierReviewableTest.SimpleModifierReviewable(modifiers);
        method = ModifierReviewable.AbstractBase.class.getMethod(methodName);
        this.expected = expected;
    }

    @Test
    public void testModifierProperty() throws Exception {
        MatcherAssert.assertThat(method.invoke(simpleModifierReviewable), CoreMatchers.is(expected));
    }

    private static class SimpleModifierReviewable extends ModifierReviewable.AbstractBase {
        private final int modifiers;

        private SimpleModifierReviewable(int modifiers) {
            this.modifiers = modifiers;
        }

        public int getModifiers() {
            return modifiers;
        }
    }
}


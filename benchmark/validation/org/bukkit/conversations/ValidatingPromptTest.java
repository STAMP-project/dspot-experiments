package org.bukkit.conversations;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ValidatingPromptTest {
    @Test
    public void TestBooleanPrompt() {
        ValidatingPromptTest.TestBooleanPrompt prompt = new ValidatingPromptTest.TestBooleanPrompt();
        Assert.assertTrue(isInputValid(null, "true"));
        Assert.assertFalse(isInputValid(null, "bananas"));
        acceptInput(null, "true");
        Assert.assertTrue(prompt.result);
        acceptInput(null, "no");
        Assert.assertFalse(prompt.result);
    }

    @Test
    public void TestFixedSetPrompt() {
        ValidatingPromptTest.TestFixedSetPrompt prompt = new ValidatingPromptTest.TestFixedSetPrompt("foo", "bar");
        Assert.assertTrue(isInputValid(null, "foo"));
        Assert.assertFalse(isInputValid(null, "cheese"));
        acceptInput(null, "foo");
        Assert.assertEquals("foo", prompt.result);
    }

    @Test
    public void TestNumericPrompt() {
        ValidatingPromptTest.TestNumericPrompt prompt = new ValidatingPromptTest.TestNumericPrompt();
        Assert.assertTrue(isInputValid(null, "1010220"));
        Assert.assertFalse(isInputValid(null, "tomato"));
        acceptInput(null, "1010220");
        Assert.assertEquals(1010220, prompt.result);
    }

    @Test
    public void TestRegexPrompt() {
        ValidatingPromptTest.TestRegexPrompt prompt = new ValidatingPromptTest.TestRegexPrompt("a.c");
        Assert.assertTrue(isInputValid(null, "abc"));
        Assert.assertTrue(isInputValid(null, "axc"));
        Assert.assertFalse(isInputValid(null, "xyz"));
        acceptInput(null, "abc");
        Assert.assertEquals("abc", prompt.result);
    }

    // TODO: TestPlayerNamePrompt()
    private class TestBooleanPrompt extends BooleanPrompt {
        public boolean result;

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
            result = input;
            return null;
        }

        public String getPromptText(ConversationContext context) {
            return null;
        }
    }

    private class TestFixedSetPrompt extends FixedSetPrompt {
        public String result;

        public TestFixedSetPrompt(String... fixedSet) {
            super(fixedSet);
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            result = input;
            return null;
        }

        public String getPromptText(ConversationContext context) {
            return null;
        }
    }

    private class TestNumericPrompt extends NumericPrompt {
        public Number result;

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
            result = input;
            return null;
        }

        public String getPromptText(ConversationContext context) {
            return null;
        }
    }

    private class TestRegexPrompt extends RegexPrompt {
        public String result;

        public TestRegexPrompt(String pattern) {
            super(pattern);
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            result = input;
            return null;
        }

        public String getPromptText(ConversationContext context) {
            return null;
        }
    }
}


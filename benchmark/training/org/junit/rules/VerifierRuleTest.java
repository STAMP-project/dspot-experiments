package org.junit.rules;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;


public class VerifierRuleTest {
    private static String sequence;

    public static class UsesVerifier {
        @Rule
        public Verifier collector = new Verifier() {
            @Override
            protected void verify() {
                VerifierRuleTest.sequence += "verify ";
            }
        };

        @Test
        public void example() {
            VerifierRuleTest.sequence += "test ";
        }
    }

    @Test
    public void verifierRunsAfterTest() {
        VerifierRuleTest.sequence = "";
        Assert.assertThat(PrintableResult.testResult(VerifierRuleTest.UsesVerifier.class), ResultMatchers.isSuccessful());
        Assert.assertEquals("test verify ", VerifierRuleTest.sequence);
    }
}


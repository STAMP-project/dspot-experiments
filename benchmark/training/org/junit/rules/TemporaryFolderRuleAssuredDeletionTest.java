package org.junit.rules;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;


public class TemporaryFolderRuleAssuredDeletionTest {
    public static class TestClass {
        static TemporaryFolder injectedRule;

        @Rule
        public TemporaryFolder folder = TemporaryFolderRuleAssuredDeletionTest.TestClass.injectedRule;

        @Test
        public void alwaysPassesButDeletesRootFolder() {
            // we delete the folder in the test so that it cannot be deleted by
            // the rule
            folder.getRoot().delete();
        }
    }

    @Test
    public void testFailsWhenCreatedFolderCannotBeDeletedButDeletionIsAssured() {
        TemporaryFolderRuleAssuredDeletionTest.TestClass.injectedRule = builder().assureDeletion().build();
        PrintableResult result = PrintableResult.testResult(TemporaryFolderRuleAssuredDeletionTest.TestClass.class);
        Assert.assertThat(result, ResultMatchers.failureCountIs(1));
        Assert.assertThat(result.toString(), CoreMatchers.containsString("Unable to clean up temporary folder"));
    }

    @Test
    public void byDefaultTestDoesNotFailWhenCreatedFolderCannotBeDeleted() {
        TemporaryFolderRuleAssuredDeletionTest.TestClass.injectedRule = new TemporaryFolder();
        PrintableResult result = PrintableResult.testResult(TemporaryFolderRuleAssuredDeletionTest.TestClass.class);
        Assert.assertThat(result, ResultMatchers.isSuccessful());
    }
}


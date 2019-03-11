package org.embulk.standards;


import org.embulk.config.ConfigException;
import org.embulk.exec.PartialExecutionException;
import org.embulk.test.TestingEmbulk;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestRemoveColumnsFilterPlugin {
    private static final String RESOURCE_NAME_PREFIX = "org/embulk/standards/remove_columns/test/";

    @Rule
    public TestingEmbulk embulk = TestingEmbulk.builder().build();

    @Test
    public void useKeepOption() throws Exception {
        TestRemoveColumnsFilterPlugin.assertRecordsByResource(embulk, "test_keep_in.yml", "test_keep_filter.yml", "test_keep.csv", "test_keep_expected.csv");
    }

    @Test
    public void useKeepWithAcceptUnmatched() throws Exception {
        TestRemoveColumnsFilterPlugin.assertRecordsByResource(embulk, "test_keep_in.yml", "test_keep_with_unmatched_filter.yml", "test_keep.csv", "test_keep_expected.csv");
    }

    @Test
    public void useKeepWithoutAcceptUnmatched() throws Exception {
        try {
            TestRemoveColumnsFilterPlugin.assertRecordsByResource(embulk, "test_keep_in.yml", "test_keep_without_unmatched_filter.yml", "test_keep.csv", "test_keep_expected.csv");
            Assert.fail();
        } catch (PartialExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof ConfigException));
        }
    }

    @Test
    public void useKeepWithDuplicatedColumnNames() throws Exception {
        TestRemoveColumnsFilterPlugin.assertRecordsByResource(embulk, "test_keep_with_duplicated_column_names_in.yml", "test_keep_with_duplicated_column_names.yml", "test_keep_with_duplicated_column_names.csv", "test_keep_with_duplicated_column_names_expected.csv");
    }

    @Test
    public void useRemove() throws Exception {
        TestRemoveColumnsFilterPlugin.assertRecordsByResource(embulk, "test_remove_in.yml", "test_remove_filter.yml", "test_remove.csv", "test_remove_expected.csv");
    }

    @Test
    public void useRemoveWithAcceptUnmatched() throws Exception {
        TestRemoveColumnsFilterPlugin.assertRecordsByResource(embulk, "test_remove_in.yml", "test_remove_with_unmatched_filter.yml", "test_remove.csv", "test_remove_expected.csv");
    }

    @Test
    public void useRemoveWithoutAcceptUnmatched() throws Exception {
        try {
            TestRemoveColumnsFilterPlugin.assertRecordsByResource(embulk, "test_remove_in.yml", "test_remove_without_unmatched_filter.yml", "test_remove.csv", "test_remove_expected.csv");
            Assert.fail();
        } catch (PartialExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof ConfigException));
        }
    }
}


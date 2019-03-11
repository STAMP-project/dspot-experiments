package org.embulk.standards.guess;


import com.google.common.collect.ImmutableList;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.DataSource;
import org.embulk.test.TestingEmbulk;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestCsvAllStringsGuessPlugin {
    private static final String RESOURCE_NAME_PREFIX = "org/embulk/standards/guess/csv_all_strings/test/";

    @Rule
    public TestingEmbulk embulk = TestingEmbulk.builder().build();

    @Test
    public void testSimple() throws Exception {
        ConfigSource exec = embulk.newConfig().set("guess_plugins", ImmutableList.of("csv_all_strings")).set("exclude_guess_plugins", ImmutableList.of("csv"));
        ConfigDiff guessed = embulk.parserBuilder().exec(exec).inputResource(((TestCsvAllStringsGuessPlugin.RESOURCE_NAME_PREFIX) + "test_simple.csv")).guess();
        Assert.assertThat(guessed, Matchers.is(((DataSource) (embulk.loadYamlResource(((TestCsvAllStringsGuessPlugin.RESOURCE_NAME_PREFIX) + "test_simple_guessed.yml"))))));
    }
}


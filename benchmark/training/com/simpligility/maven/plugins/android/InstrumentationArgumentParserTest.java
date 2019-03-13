package com.simpligility.maven.plugins.android;


import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class InstrumentationArgumentParserTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_return_an_empty_map_for_null_list() {
        Assert.assertTrue(InstrumentationArgumentParser.parse(null).isEmpty());
    }

    @Test
    public void two_flat_args_should_be_parsed_into_two_key_value_pairs() {
        final List<String> flatArgs = Lists.newArrayList("key1 value1", "key2 value2");
        final Map<String, String> parsedArgs = InstrumentationArgumentParser.parse(flatArgs);
        Assert.assertThat(parsedArgs.get("key1"), CoreMatchers.is("value1"));
        Assert.assertThat(parsedArgs.get("key2"), CoreMatchers.is("value2"));
    }

    @Test
    public void should_parse_values_with_space_character() {
        final List<String> flatArgs = Lists.newArrayList("key1 'value with spaces'");
        final Map<String, String> parsedArgs = InstrumentationArgumentParser.parse(flatArgs);
        Assert.assertThat(parsedArgs.get("key1"), CoreMatchers.is("'value with spaces'"));
    }

    @Test
    public void missing_value_should_throw_IllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CoreMatchers.is("Could not separate \"key1\" by a whitespace into two parts"));
        final List<String> flatArgs = Lists.newArrayList("key1");
        InstrumentationArgumentParser.parse(flatArgs);
    }

    @Test
    public void empty_pair_should_throw_IllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CoreMatchers.is("Could not separate \"\" by a whitespace into two parts"));
        final List<String> flatArgs = Lists.newArrayList("");
        InstrumentationArgumentParser.parse(flatArgs);
    }
}


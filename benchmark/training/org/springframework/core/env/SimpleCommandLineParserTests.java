/**
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.env;


import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SimpleCommandLineParserTests {
    @Test
    public void withNoOptions() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        Assert.assertThat(parser.parse().getOptionValues("foo"), CoreMatchers.nullValue());
    }

    @Test
    public void withSingleOptionAndNoValue() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        CommandLineArgs args = parser.parse("--o1");
        Assert.assertThat(args.containsOption("o1"), CoreMatchers.is(true));
        Assert.assertThat(args.getOptionValues("o1"), CoreMatchers.equalTo(Collections.EMPTY_LIST));
    }

    @Test
    public void withSingleOptionAndValue() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        CommandLineArgs args = parser.parse("--o1=v1");
        Assert.assertThat(args.containsOption("o1"), CoreMatchers.is(true));
        Assert.assertThat(args.getOptionValues("o1").get(0), CoreMatchers.equalTo("v1"));
    }

    @Test
    public void withMixOfOptionsHavingValueAndOptionsHavingNoValue() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        CommandLineArgs args = parser.parse("--o1=v1", "--o2");
        Assert.assertThat(args.containsOption("o1"), CoreMatchers.is(true));
        Assert.assertThat(args.containsOption("o2"), CoreMatchers.is(true));
        Assert.assertThat(args.containsOption("o3"), CoreMatchers.is(false));
        Assert.assertThat(args.getOptionValues("o1").get(0), CoreMatchers.equalTo("v1"));
        Assert.assertThat(args.getOptionValues("o2"), CoreMatchers.equalTo(Collections.EMPTY_LIST));
        Assert.assertThat(args.getOptionValues("o3"), CoreMatchers.nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void withEmptyOptionText() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        parser.parse("--");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withEmptyOptionName() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        parser.parse("--=v1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withEmptyOptionValue() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        parser.parse("--o1=");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withEmptyOptionNameAndEmptyOptionValue() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        parser.parse("--=");
    }

    @Test
    public void withNonOptionArguments() {
        SimpleCommandLineArgsParser parser = new SimpleCommandLineArgsParser();
        CommandLineArgs args = parser.parse("--o1=v1", "noa1", "--o2=v2", "noa2");
        Assert.assertThat(args.getOptionValues("o1").get(0), CoreMatchers.equalTo("v1"));
        Assert.assertThat(args.getOptionValues("o2").get(0), CoreMatchers.equalTo("v2"));
        List<String> nonOptions = args.getNonOptionArgs();
        Assert.assertThat(nonOptions.get(0), CoreMatchers.equalTo("noa1"));
        Assert.assertThat(nonOptions.get(1), CoreMatchers.equalTo("noa2"));
        Assert.assertThat(nonOptions.size(), CoreMatchers.equalTo(2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void assertOptionNamesIsUnmodifiable() {
        CommandLineArgs args = new SimpleCommandLineArgsParser().parse();
        args.getOptionNames().add("bogus");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void assertNonOptionArgsIsUnmodifiable() {
        CommandLineArgs args = new SimpleCommandLineArgsParser().parse();
        args.getNonOptionArgs().add("foo");
    }
}


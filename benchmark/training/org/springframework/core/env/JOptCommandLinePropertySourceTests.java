/**
 * Copyright 2002-2013 the original author or authors.
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


import java.util.Arrays;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link JOptCommandLinePropertySource}.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class JOptCommandLinePropertySourceTests {
    @Test
    public void withRequiredArg_andArgIsPresent() {
        OptionParser parser = new OptionParser();
        parser.accepts("foo").withRequiredArg();
        OptionSet options = parser.parse("--foo=bar");
        PropertySource<?> ps = new JOptCommandLinePropertySource(options);
        Assert.assertThat(((String) (ps.getProperty("foo"))), CoreMatchers.equalTo("bar"));
    }

    @Test
    public void withOptionalArg_andArgIsMissing() {
        OptionParser parser = new OptionParser();
        parser.accepts("foo").withOptionalArg();
        OptionSet options = parser.parse("--foo");
        PropertySource<?> ps = new JOptCommandLinePropertySource(options);
        Assert.assertThat(ps.containsProperty("foo"), CoreMatchers.is(true));
        Assert.assertThat(((String) (ps.getProperty("foo"))), CoreMatchers.equalTo(""));
    }

    @Test
    public void withNoArg() {
        OptionParser parser = new OptionParser();
        parser.accepts("o1");
        parser.accepts("o2");
        OptionSet options = parser.parse("--o1");
        PropertySource<?> ps = new JOptCommandLinePropertySource(options);
        Assert.assertThat(ps.containsProperty("o1"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o2"), CoreMatchers.is(false));
        Assert.assertThat(((String) (ps.getProperty("o1"))), CoreMatchers.equalTo(""));
        Assert.assertThat(ps.getProperty("o2"), CoreMatchers.nullValue());
    }

    @Test
    public void withRequiredArg_andMultipleArgsPresent_usingDelimiter() {
        OptionParser parser = new OptionParser();
        parser.accepts("foo").withRequiredArg().withValuesSeparatedBy(',');
        OptionSet options = parser.parse("--foo=bar,baz,biz");
        CommandLinePropertySource<?> ps = new JOptCommandLinePropertySource(options);
        Assert.assertEquals(Arrays.asList("bar", "baz", "biz"), ps.getOptionValues("foo"));
        Assert.assertThat(ps.getProperty("foo"), CoreMatchers.equalTo("bar,baz,biz"));
    }

    @Test
    public void withRequiredArg_andMultipleArgsPresent_usingRepeatedOption() {
        OptionParser parser = new OptionParser();
        parser.accepts("foo").withRequiredArg().withValuesSeparatedBy(',');
        OptionSet options = parser.parse("--foo=bar", "--foo=baz", "--foo=biz");
        CommandLinePropertySource<?> ps = new JOptCommandLinePropertySource(options);
        Assert.assertEquals(Arrays.asList("bar", "baz", "biz"), ps.getOptionValues("foo"));
        Assert.assertThat(ps.getProperty("foo"), CoreMatchers.equalTo("bar,baz,biz"));
    }

    @Test
    public void withMissingOption() {
        OptionParser parser = new OptionParser();
        parser.accepts("foo").withRequiredArg().withValuesSeparatedBy(',');
        OptionSet options = parser.parse();// <-- no options whatsoever

        PropertySource<?> ps = new JOptCommandLinePropertySource(options);
        Assert.assertThat(ps.getProperty("foo"), CoreMatchers.nullValue());
    }

    @Test
    public void withDottedOptionName() {
        OptionParser parser = new OptionParser();
        parser.accepts("spring.profiles.active").withRequiredArg();
        OptionSet options = parser.parse("--spring.profiles.active=p1");
        CommandLinePropertySource<?> ps = new JOptCommandLinePropertySource(options);
        Assert.assertThat(ps.getProperty("spring.profiles.active"), CoreMatchers.equalTo("p1"));
    }

    @Test
    public void withDefaultNonOptionArgsNameAndNoNonOptionArgsPresent() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList("o1", "option1")).withRequiredArg();
        parser.accepts("o2");
        OptionSet optionSet = parser.parse("--o1=v1", "--o2");
        EnumerablePropertySource<?> ps = new JOptCommandLinePropertySource(optionSet);
        Assert.assertThat(ps.containsProperty("nonOptionArgs"), CoreMatchers.is(false));
        Assert.assertThat(ps.containsProperty("o1"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o2"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("nonOptionArgs"), CoreMatchers.is(false));
        Assert.assertThat(ps.getProperty("nonOptionArgs"), CoreMatchers.nullValue());
        Assert.assertThat(ps.getPropertyNames().length, CoreMatchers.is(2));
    }

    @Test
    public void withDefaultNonOptionArgsNameAndNonOptionArgsPresent() {
        OptionParser parser = new OptionParser();
        parser.accepts("o1").withRequiredArg();
        parser.accepts("o2");
        OptionSet optionSet = parser.parse("--o1=v1", "noa1", "--o2", "noa2");
        PropertySource<?> ps = new JOptCommandLinePropertySource(optionSet);
        Assert.assertThat(ps.containsProperty("nonOptionArgs"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o1"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o2"), CoreMatchers.is(true));
        String nonOptionArgs = ((String) (ps.getProperty("nonOptionArgs")));
        Assert.assertThat(nonOptionArgs, CoreMatchers.equalTo("noa1,noa2"));
    }

    @Test
    public void withCustomNonOptionArgsNameAndNoNonOptionArgsPresent() {
        OptionParser parser = new OptionParser();
        parser.accepts("o1").withRequiredArg();
        parser.accepts("o2");
        OptionSet optionSet = parser.parse("--o1=v1", "noa1", "--o2", "noa2");
        CommandLinePropertySource<?> ps = new JOptCommandLinePropertySource(optionSet);
        ps.setNonOptionArgsPropertyName("NOA");
        Assert.assertThat(ps.containsProperty("nonOptionArgs"), CoreMatchers.is(false));
        Assert.assertThat(ps.containsProperty("NOA"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o1"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o2"), CoreMatchers.is(true));
        String nonOptionArgs = ps.getProperty("NOA");
        Assert.assertThat(nonOptionArgs, CoreMatchers.equalTo("noa1,noa2"));
    }

    @Test
    public void withRequiredArg_ofTypeEnum() {
        OptionParser parser = new OptionParser();
        parser.accepts("o1").withRequiredArg().ofType(JOptCommandLinePropertySourceTests.OptionEnum.class);
        OptionSet options = parser.parse("--o1=VAL_1");
        PropertySource<?> ps = new JOptCommandLinePropertySource(options);
        Assert.assertThat(ps.getProperty("o1"), CoreMatchers.equalTo("VAL_1"));
    }

    public static enum OptionEnum {

        VAL_1;}
}


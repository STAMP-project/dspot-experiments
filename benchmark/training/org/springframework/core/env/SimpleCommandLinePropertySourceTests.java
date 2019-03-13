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


import CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link SimpleCommandLinePropertySource}.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class SimpleCommandLinePropertySourceTests {
    @Test
    public void withDefaultName() {
        PropertySource<?> ps = new SimpleCommandLinePropertySource();
        Assert.assertThat(ps.getName(), CoreMatchers.equalTo(COMMAND_LINE_PROPERTY_SOURCE_NAME));
    }

    @Test
    public void withCustomName() {
        PropertySource<?> ps = new SimpleCommandLinePropertySource("ps1", new String[0]);
        Assert.assertThat(ps.getName(), CoreMatchers.equalTo("ps1"));
    }

    @Test
    public void withNoArgs() {
        PropertySource<?> ps = new SimpleCommandLinePropertySource();
        Assert.assertThat(ps.containsProperty("foo"), CoreMatchers.is(false));
        Assert.assertThat(ps.getProperty("foo"), CoreMatchers.nullValue());
    }

    @Test
    public void withOptionArgsOnly() {
        CommandLinePropertySource<?> ps = new SimpleCommandLinePropertySource("--o1=v1", "--o2");
        Assert.assertThat(ps.containsProperty("o1"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o2"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o3"), CoreMatchers.is(false));
        Assert.assertThat(ps.getProperty("o1"), CoreMatchers.equalTo("v1"));
        Assert.assertThat(ps.getProperty("o2"), CoreMatchers.equalTo(""));
        Assert.assertThat(ps.getProperty("o3"), CoreMatchers.nullValue());
    }

    @Test
    public void withDefaultNonOptionArgsNameAndNoNonOptionArgsPresent() {
        EnumerablePropertySource<?> ps = new SimpleCommandLinePropertySource("--o1=v1", "--o2");
        Assert.assertThat(ps.containsProperty("nonOptionArgs"), CoreMatchers.is(false));
        Assert.assertThat(ps.containsProperty("o1"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o2"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("nonOptionArgs"), CoreMatchers.is(false));
        Assert.assertThat(ps.getProperty("nonOptionArgs"), CoreMatchers.nullValue());
        Assert.assertThat(ps.getPropertyNames().length, CoreMatchers.is(2));
    }

    @Test
    public void withDefaultNonOptionArgsNameAndNonOptionArgsPresent() {
        CommandLinePropertySource<?> ps = new SimpleCommandLinePropertySource("--o1=v1", "noa1", "--o2", "noa2");
        Assert.assertThat(ps.containsProperty("nonOptionArgs"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o1"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o2"), CoreMatchers.is(true));
        String nonOptionArgs = ps.getProperty("nonOptionArgs");
        Assert.assertThat(nonOptionArgs, CoreMatchers.equalTo("noa1,noa2"));
    }

    @Test
    public void withCustomNonOptionArgsNameAndNoNonOptionArgsPresent() {
        CommandLinePropertySource<?> ps = new SimpleCommandLinePropertySource("--o1=v1", "noa1", "--o2", "noa2");
        ps.setNonOptionArgsPropertyName("NOA");
        Assert.assertThat(ps.containsProperty("nonOptionArgs"), CoreMatchers.is(false));
        Assert.assertThat(ps.containsProperty("NOA"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o1"), CoreMatchers.is(true));
        Assert.assertThat(ps.containsProperty("o2"), CoreMatchers.is(true));
        String nonOptionArgs = ps.getProperty("NOA");
        Assert.assertThat(nonOptionArgs, CoreMatchers.equalTo("noa1,noa2"));
    }

    @Test
    public void covertNonOptionArgsToStringArrayAndList() {
        CommandLinePropertySource<?> ps = new SimpleCommandLinePropertySource("--o1=v1", "noa1", "--o2", "noa2");
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(ps);
        String nonOptionArgs = env.getProperty("nonOptionArgs");
        Assert.assertThat(nonOptionArgs, CoreMatchers.equalTo("noa1,noa2"));
        String[] nonOptionArgsArray = env.getProperty("nonOptionArgs", String[].class);
        Assert.assertThat(nonOptionArgsArray[0], CoreMatchers.equalTo("noa1"));
        Assert.assertThat(nonOptionArgsArray[1], CoreMatchers.equalTo("noa2"));
        @SuppressWarnings("unchecked")
        List<String> nonOptionArgsList = env.getProperty("nonOptionArgs", List.class);
        Assert.assertThat(nonOptionArgsList.get(0), CoreMatchers.equalTo("noa1"));
        Assert.assertThat(nonOptionArgsList.get(1), CoreMatchers.equalTo("noa2"));
    }
}


/**
 * Copyright 2002-2018 the original author or authors.
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
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests covering the extensibility of {@link AbstractEnvironment}.
 *
 * @author Chris Beams
 * @since 3.1
 */
// -- tests relating to customizing property sources -------------------------------
public class CustomEnvironmentTests {
    // -- tests relating to customizing reserved default profiles ----------------------
    @Test
    public void control() {
        Environment env = new AbstractEnvironment() {};
        Assert.assertThat(env.acceptsProfiles(defaultProfile()), CoreMatchers.is(true));
    }

    @Test
    public void withNoReservedDefaultProfile() {
        class CustomEnvironment extends AbstractEnvironment {
            @Override
            protected Set<String> getReservedDefaultProfiles() {
                return Collections.emptySet();
            }
        }
        Environment env = new CustomEnvironment();
        Assert.assertThat(env.acceptsProfiles(defaultProfile()), CoreMatchers.is(false));
    }

    @Test
    public void withSingleCustomReservedDefaultProfile() {
        class CustomEnvironment extends AbstractEnvironment {
            @Override
            protected Set<String> getReservedDefaultProfiles() {
                return Collections.singleton("rd1");
            }
        }
        Environment env = new CustomEnvironment();
        Assert.assertThat(env.acceptsProfiles(defaultProfile()), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(Profiles.of("rd1")), CoreMatchers.is(true));
    }

    @Test
    public void withMultiCustomReservedDefaultProfile() {
        class CustomEnvironment extends AbstractEnvironment {
            @Override
            @SuppressWarnings("serial")
            protected Set<String> getReservedDefaultProfiles() {
                return new HashSet<String>() {
                    {
                        add("rd1");
                        add("rd2");
                    }
                };
            }
        }
        ConfigurableEnvironment env = new CustomEnvironment();
        Assert.assertThat(env.acceptsProfiles(defaultProfile()), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(Profiles.of("rd1 | rd2")), CoreMatchers.is(true));
        // finally, issue additional assertions to cover all combinations of calling these
        // methods, however unlikely.
        env.setDefaultProfiles("d1");
        Assert.assertThat(env.acceptsProfiles(Profiles.of("rd1 | rd2")), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(Profiles.of("d1")), CoreMatchers.is(true));
        env.setActiveProfiles("a1", "a2");
        Assert.assertThat(env.acceptsProfiles(Profiles.of("d1")), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(Profiles.of("a1 | a2")), CoreMatchers.is(true));
        env.setActiveProfiles();
        Assert.assertThat(env.acceptsProfiles(Profiles.of("d1")), CoreMatchers.is(true));
        Assert.assertThat(env.acceptsProfiles(Profiles.of("a1 | a2")), CoreMatchers.is(false));
        env.setDefaultProfiles();
        Assert.assertThat(env.acceptsProfiles(defaultProfile()), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(Profiles.of("rd1 | rd2")), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(Profiles.of("d1")), CoreMatchers.is(false));
        Assert.assertThat(env.acceptsProfiles(Profiles.of("a1 | a2")), CoreMatchers.is(false));
    }
}


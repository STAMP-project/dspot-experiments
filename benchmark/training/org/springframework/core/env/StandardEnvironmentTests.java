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


import StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
import StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.Arrays;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.SpringProperties;
import org.springframework.mock.env.MockPropertySource;


/**
 * Unit tests for {@link StandardEnvironment}.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 */
@SuppressWarnings("deprecation")
public class StandardEnvironmentTests {
    private static final String ALLOWED_PROPERTY_NAME = "theanswer";

    private static final String ALLOWED_PROPERTY_VALUE = "42";

    private static final String DISALLOWED_PROPERTY_NAME = "verboten";

    private static final String DISALLOWED_PROPERTY_VALUE = "secret";

    private static final String STRING_PROPERTY_NAME = "stringPropName";

    private static final String STRING_PROPERTY_VALUE = "stringPropValue";

    private static final Object NON_STRING_PROPERTY_NAME = new Object();

    private static final Object NON_STRING_PROPERTY_VALUE = new Object();

    private final ConfigurableEnvironment environment = new StandardEnvironment();

    @Test
    public void merge() {
        ConfigurableEnvironment child = new StandardEnvironment();
        child.setActiveProfiles("c1", "c2");
        child.getPropertySources().addLast(new MockPropertySource("childMock").withProperty("childKey", "childVal").withProperty("bothKey", "childBothVal"));
        ConfigurableEnvironment parent = new StandardEnvironment();
        parent.setActiveProfiles("p1", "p2");
        parent.getPropertySources().addLast(new MockPropertySource("parentMock").withProperty("parentKey", "parentVal").withProperty("bothKey", "parentBothVal"));
        Assert.assertThat(child.getProperty("childKey"), is("childVal"));
        Assert.assertThat(child.getProperty("parentKey"), nullValue());
        Assert.assertThat(child.getProperty("bothKey"), is("childBothVal"));
        Assert.assertThat(parent.getProperty("childKey"), nullValue());
        Assert.assertThat(parent.getProperty("parentKey"), is("parentVal"));
        Assert.assertThat(parent.getProperty("bothKey"), is("parentBothVal"));
        Assert.assertThat(child.getActiveProfiles(), equalTo(new String[]{ "c1", "c2" }));
        Assert.assertThat(parent.getActiveProfiles(), equalTo(new String[]{ "p1", "p2" }));
        child.merge(parent);
        Assert.assertThat(child.getProperty("childKey"), is("childVal"));
        Assert.assertThat(child.getProperty("parentKey"), is("parentVal"));
        Assert.assertThat(child.getProperty("bothKey"), is("childBothVal"));
        Assert.assertThat(parent.getProperty("childKey"), nullValue());
        Assert.assertThat(parent.getProperty("parentKey"), is("parentVal"));
        Assert.assertThat(parent.getProperty("bothKey"), is("parentBothVal"));
        Assert.assertThat(child.getActiveProfiles(), equalTo(new String[]{ "c1", "c2", "p1", "p2" }));
        Assert.assertThat(parent.getActiveProfiles(), equalTo(new String[]{ "p1", "p2" }));
    }

    @Test
    public void propertySourceOrder() {
        ConfigurableEnvironment env = new StandardEnvironment();
        MutablePropertySources sources = env.getPropertySources();
        Assert.assertThat(sources.precedenceOf(PropertySource.named(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)), equalTo(0));
        Assert.assertThat(sources.precedenceOf(PropertySource.named(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)), equalTo(1));
        Assert.assertThat(sources.size(), is(2));
    }

    @Test
    public void propertySourceTypes() {
        ConfigurableEnvironment env = new StandardEnvironment();
        MutablePropertySources sources = env.getPropertySources();
        Assert.assertThat(sources.get(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME), instanceOf(SystemEnvironmentPropertySource.class));
    }

    @Test
    public void activeProfilesIsEmptyByDefault() {
        Assert.assertThat(environment.getActiveProfiles().length, is(0));
    }

    @Test
    public void defaultProfilesContainsDefaultProfileByDefault() {
        Assert.assertThat(environment.getDefaultProfiles().length, is(1));
        Assert.assertThat(environment.getDefaultProfiles()[0], equalTo("default"));
    }

    @Test
    public void setActiveProfiles() {
        environment.setActiveProfiles("local", "embedded");
        String[] activeProfiles = environment.getActiveProfiles();
        Assert.assertThat(Arrays.asList(activeProfiles), hasItems("local", "embedded"));
        Assert.assertThat(activeProfiles.length, is(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setActiveProfiles_withNullProfileArray() {
        environment.setActiveProfiles(((String[]) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setActiveProfiles_withNullProfile() {
        environment.setActiveProfiles(((String) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setActiveProfiles_withEmptyProfile() {
        environment.setActiveProfiles("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setActiveProfiles_withNotOperator() {
        environment.setActiveProfiles("p1", "!p2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultProfiles_withNullProfileArray() {
        environment.setDefaultProfiles(((String[]) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultProfiles_withNullProfile() {
        environment.setDefaultProfiles(((String) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultProfiles_withEmptyProfile() {
        environment.setDefaultProfiles("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDefaultProfiles_withNotOperator() {
        environment.setDefaultProfiles("d1", "!d2");
    }

    @Test
    public void addActiveProfile() {
        Assert.assertThat(environment.getActiveProfiles().length, is(0));
        environment.setActiveProfiles("local", "embedded");
        Assert.assertThat(Arrays.asList(environment.getActiveProfiles()), hasItems("local", "embedded"));
        Assert.assertThat(environment.getActiveProfiles().length, is(2));
        environment.addActiveProfile("p1");
        Assert.assertThat(Arrays.asList(environment.getActiveProfiles()), hasItems("p1"));
        Assert.assertThat(environment.getActiveProfiles().length, is(3));
        environment.addActiveProfile("p2");
        environment.addActiveProfile("p3");
        Assert.assertThat(Arrays.asList(environment.getActiveProfiles()), hasItems("p2", "p3"));
        Assert.assertThat(environment.getActiveProfiles().length, is(5));
    }

    @Test
    public void addActiveProfile_whenActiveProfilesPropertyIsAlreadySet() {
        ConfigurableEnvironment env = new StandardEnvironment();
        Assert.assertThat(env.getProperty(ACTIVE_PROFILES_PROPERTY_NAME), nullValue());
        env.getPropertySources().addFirst(new MockPropertySource().withProperty(ACTIVE_PROFILES_PROPERTY_NAME, "p1"));
        Assert.assertThat(env.getProperty(ACTIVE_PROFILES_PROPERTY_NAME), equalTo("p1"));
        env.addActiveProfile("p2");
        Assert.assertThat(env.getActiveProfiles(), arrayContaining("p1", "p2"));
    }

    @Test
    public void reservedDefaultProfile() {
        Assert.assertThat(environment.getDefaultProfiles(), equalTo(new String[]{ RESERVED_DEFAULT_PROFILE_NAME }));
        System.setProperty(DEFAULT_PROFILES_PROPERTY_NAME, "d0");
        Assert.assertThat(environment.getDefaultProfiles(), equalTo(new String[]{ "d0" }));
        environment.setDefaultProfiles("d1", "d2");
        Assert.assertThat(environment.getDefaultProfiles(), equalTo(new String[]{ "d1", "d2" }));
        System.getProperties().remove(DEFAULT_PROFILES_PROPERTY_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultProfileWithCircularPlaceholder() {
        System.setProperty(DEFAULT_PROFILES_PROPERTY_NAME, "${spring.profiles.default}");
        try {
            environment.getDefaultProfiles();
        } finally {
            System.getProperties().remove(DEFAULT_PROFILES_PROPERTY_NAME);
        }
    }

    @Test
    public void getActiveProfiles_systemPropertiesEmpty() {
        Assert.assertThat(environment.getActiveProfiles().length, is(0));
        System.setProperty(ACTIVE_PROFILES_PROPERTY_NAME, "");
        Assert.assertThat(environment.getActiveProfiles().length, is(0));
        System.getProperties().remove(ACTIVE_PROFILES_PROPERTY_NAME);
    }

    @Test
    public void getActiveProfiles_fromSystemProperties() {
        System.setProperty(ACTIVE_PROFILES_PROPERTY_NAME, "foo");
        Assert.assertThat(Arrays.asList(environment.getActiveProfiles()), hasItem("foo"));
        System.getProperties().remove(ACTIVE_PROFILES_PROPERTY_NAME);
    }

    @Test
    public void getActiveProfiles_fromSystemProperties_withMultipleProfiles() {
        System.setProperty(ACTIVE_PROFILES_PROPERTY_NAME, "foo,bar");
        Assert.assertThat(Arrays.asList(environment.getActiveProfiles()), hasItems("foo", "bar"));
        System.getProperties().remove(ACTIVE_PROFILES_PROPERTY_NAME);
    }

    @Test
    public void getActiveProfiles_fromSystemProperties_withMulitpleProfiles_withWhitespace() {
        System.setProperty(ACTIVE_PROFILES_PROPERTY_NAME, " bar , baz ");// notice whitespace

        Assert.assertThat(Arrays.asList(environment.getActiveProfiles()), hasItems("bar", "baz"));
        System.getProperties().remove(ACTIVE_PROFILES_PROPERTY_NAME);
    }

    @Test
    public void getDefaultProfiles() {
        Assert.assertThat(environment.getDefaultProfiles(), equalTo(new String[]{ RESERVED_DEFAULT_PROFILE_NAME }));
        environment.getPropertySources().addFirst(new MockPropertySource().withProperty(DEFAULT_PROFILES_PROPERTY_NAME, "pd1"));
        Assert.assertThat(environment.getDefaultProfiles().length, is(1));
        Assert.assertThat(Arrays.asList(environment.getDefaultProfiles()), hasItem("pd1"));
    }

    @Test
    public void setDefaultProfiles() {
        environment.setDefaultProfiles();
        Assert.assertThat(environment.getDefaultProfiles().length, is(0));
        environment.setDefaultProfiles("pd1");
        Assert.assertThat(Arrays.asList(environment.getDefaultProfiles()), hasItem("pd1"));
        environment.setDefaultProfiles("pd2", "pd3");
        Assert.assertThat(Arrays.asList(environment.getDefaultProfiles()), not(hasItem("pd1")));
        Assert.assertThat(Arrays.asList(environment.getDefaultProfiles()), hasItems("pd2", "pd3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void acceptsProfiles_withEmptyArgumentList() {
        environment.acceptsProfiles();
    }

    @Test(expected = IllegalArgumentException.class)
    public void acceptsProfiles_withNullArgumentList() {
        environment.acceptsProfiles(((String[]) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void acceptsProfiles_withNullArgument() {
        environment.acceptsProfiles(((String) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void acceptsProfiles_withEmptyArgument() {
        environment.acceptsProfiles("");
    }

    @Test
    public void acceptsProfiles_activeProfileSetProgrammatically() {
        Assert.assertThat(environment.acceptsProfiles("p1", "p2"), is(false));
        environment.setActiveProfiles("p1");
        Assert.assertThat(environment.acceptsProfiles("p1", "p2"), is(true));
        environment.setActiveProfiles("p2");
        Assert.assertThat(environment.acceptsProfiles("p1", "p2"), is(true));
        environment.setActiveProfiles("p1", "p2");
        Assert.assertThat(environment.acceptsProfiles("p1", "p2"), is(true));
    }

    @Test
    public void acceptsProfiles_activeProfileSetViaProperty() {
        Assert.assertThat(environment.acceptsProfiles("p1"), is(false));
        environment.getPropertySources().addFirst(new MockPropertySource().withProperty(ACTIVE_PROFILES_PROPERTY_NAME, "p1"));
        Assert.assertThat(environment.acceptsProfiles("p1"), is(true));
    }

    @Test
    public void acceptsProfiles_defaultProfile() {
        Assert.assertThat(environment.acceptsProfiles("pd"), is(false));
        environment.setDefaultProfiles("pd");
        Assert.assertThat(environment.acceptsProfiles("pd"), is(true));
        environment.setActiveProfiles("p1");
        Assert.assertThat(environment.acceptsProfiles("pd"), is(false));
        Assert.assertThat(environment.acceptsProfiles("p1"), is(true));
    }

    @Test
    public void acceptsProfiles_withNotOperator() {
        Assert.assertThat(environment.acceptsProfiles("p1"), is(false));
        Assert.assertThat(environment.acceptsProfiles("!p1"), is(true));
        environment.addActiveProfile("p1");
        Assert.assertThat(environment.acceptsProfiles("p1"), is(true));
        Assert.assertThat(environment.acceptsProfiles("!p1"), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void acceptsProfiles_withInvalidNotOperator() {
        environment.acceptsProfiles("p1", "!");
    }

    @Test
    public void acceptsProfiles_withProfileExpression() {
        Assert.assertThat(environment.acceptsProfiles(Profiles.of("p1 & p2")), is(false));
        environment.addActiveProfile("p1");
        Assert.assertThat(environment.acceptsProfiles(Profiles.of("p1 & p2")), is(false));
        environment.addActiveProfile("p2");
        Assert.assertThat(environment.acceptsProfiles(Profiles.of("p1 & p2")), is(true));
    }

    @Test
    public void environmentSubclass_withCustomProfileValidation() {
        ConfigurableEnvironment env = new AbstractEnvironment.AbstractEnvironment() {
            @Override
            protected void validateProfile(String profile) {
                super.validateProfile(profile);
                if (profile.contains("-")) {
                    throw new IllegalArgumentException((("Invalid profile [" + profile) + "]: must not contain dash character"));
                }
            }
        };
        env.addActiveProfile("validProfile");// succeeds

        try {
            env.addActiveProfile("invalid-profile");
            Assert.fail("expected validation exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertThat(ex.getMessage(), equalTo("Invalid profile [invalid-profile]: must not contain dash character"));
        }
    }

    @Test
    public void suppressGetenvAccessThroughSystemProperty() {
        System.setProperty("spring.getenv.ignore", "true");
        Assert.assertTrue(environment.getSystemEnvironment().isEmpty());
        System.clearProperty("spring.getenv.ignore");
    }

    @Test
    public void suppressGetenvAccessThroughSpringProperty() {
        SpringProperties.setProperty("spring.getenv.ignore", "true");
        Assert.assertTrue(environment.getSystemEnvironment().isEmpty());
        SpringProperties.setProperty("spring.getenv.ignore", null);
    }

    @Test
    public void suppressGetenvAccessThroughSpringFlag() {
        SpringProperties.setFlag("spring.getenv.ignore");
        Assert.assertTrue(environment.getSystemEnvironment().isEmpty());
        SpringProperties.setProperty("spring.getenv.ignore", null);
    }

    @Test
    public void getSystemProperties_withAndWithoutSecurityManager() {
        System.setProperty(StandardEnvironmentTests.ALLOWED_PROPERTY_NAME, StandardEnvironmentTests.ALLOWED_PROPERTY_VALUE);
        System.setProperty(StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME, StandardEnvironmentTests.DISALLOWED_PROPERTY_VALUE);
        System.getProperties().put(StandardEnvironmentTests.STRING_PROPERTY_NAME, StandardEnvironmentTests.NON_STRING_PROPERTY_VALUE);
        System.getProperties().put(StandardEnvironmentTests.NON_STRING_PROPERTY_NAME, StandardEnvironmentTests.STRING_PROPERTY_VALUE);
        {
            Map<?, ?> systemProperties = environment.getSystemProperties();
            Assert.assertThat(systemProperties, notNullValue());
            Assert.assertSame(systemProperties, System.getProperties());
            Assert.assertThat(systemProperties.get(StandardEnvironmentTests.ALLOWED_PROPERTY_NAME), equalTo(((Object) (StandardEnvironmentTests.ALLOWED_PROPERTY_VALUE))));
            Assert.assertThat(systemProperties.get(StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME), equalTo(((Object) (StandardEnvironmentTests.DISALLOWED_PROPERTY_VALUE))));
            // non-string keys and values work fine... until the security manager is introduced below
            Assert.assertThat(systemProperties.get(StandardEnvironmentTests.STRING_PROPERTY_NAME), equalTo(StandardEnvironmentTests.NON_STRING_PROPERTY_VALUE));
            Assert.assertThat(systemProperties.get(StandardEnvironmentTests.NON_STRING_PROPERTY_NAME), equalTo(((Object) (StandardEnvironmentTests.STRING_PROPERTY_VALUE))));
        }
        SecurityManager oldSecurityManager = System.getSecurityManager();
        SecurityManager securityManager = new SecurityManager() {
            @Override
            public void checkPropertiesAccess() {
                // see http://download.oracle.com/javase/1.5.0/docs/api/java/lang/System.html#getProperties()
                throw new AccessControlException("Accessing the system properties is disallowed");
            }

            @Override
            public void checkPropertyAccess(String key) {
                // see http://download.oracle.com/javase/1.5.0/docs/api/java/lang/System.html#getProperty(java.lang.String)
                if (StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME.equals(key)) {
                    throw new AccessControlException(String.format("Accessing the system property [%s] is disallowed", StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME));
                }
            }

            @Override
            public void checkPermission(Permission perm) {
                // allow everything else
            }
        };
        System.setSecurityManager(securityManager);
        {
            Map<?, ?> systemProperties = environment.getSystemProperties();
            Assert.assertThat(systemProperties, notNullValue());
            Assert.assertThat(systemProperties, instanceOf(ReadOnlySystemAttributesMap.class));
            Assert.assertThat(((String) (systemProperties.get(StandardEnvironmentTests.ALLOWED_PROPERTY_NAME))), equalTo(StandardEnvironmentTests.ALLOWED_PROPERTY_VALUE));
            Assert.assertThat(systemProperties.get(StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME), equalTo(null));
            // nothing we can do here in terms of warning the user that there was
            // actually a (non-string) value available. By this point, we only
            // have access to calling System.getProperty(), which itself returns null
            // if the value is non-string.  So we're stuck with returning a potentially
            // misleading null.
            Assert.assertThat(systemProperties.get(StandardEnvironmentTests.STRING_PROPERTY_NAME), nullValue());
            // in the case of a non-string *key*, however, we can do better.  Alert
            // the user that under these very special conditions (non-object key +
            // SecurityManager that disallows access to system properties), they
            // cannot do what they're attempting.
            try {
                systemProperties.get(StandardEnvironmentTests.NON_STRING_PROPERTY_NAME);
                Assert.fail("Expected IllegalArgumentException when searching with non-string key against ReadOnlySystemAttributesMap");
            } catch (IllegalArgumentException ex) {
                // expected
            }
        }
        System.setSecurityManager(oldSecurityManager);
        System.clearProperty(StandardEnvironmentTests.ALLOWED_PROPERTY_NAME);
        System.clearProperty(StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME);
        System.getProperties().remove(StandardEnvironmentTests.STRING_PROPERTY_NAME);
        System.getProperties().remove(StandardEnvironmentTests.NON_STRING_PROPERTY_NAME);
    }

    @Test
    public void getSystemEnvironment_withAndWithoutSecurityManager() {
        StandardEnvironmentTests.getModifiableSystemEnvironment().put(StandardEnvironmentTests.ALLOWED_PROPERTY_NAME, StandardEnvironmentTests.ALLOWED_PROPERTY_VALUE);
        StandardEnvironmentTests.getModifiableSystemEnvironment().put(StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME, StandardEnvironmentTests.DISALLOWED_PROPERTY_VALUE);
        {
            Map<String, Object> systemEnvironment = environment.getSystemEnvironment();
            Assert.assertThat(systemEnvironment, notNullValue());
            Assert.assertSame(systemEnvironment, System.getenv());
        }
        SecurityManager oldSecurityManager = System.getSecurityManager();
        SecurityManager securityManager = new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
                // see http://download.oracle.com/javase/1.5.0/docs/api/java/lang/System.html#getenv()
                if ("getenv.*".equals(perm.getName())) {
                    throw new AccessControlException("Accessing the system environment is disallowed");
                }
                // see http://download.oracle.com/javase/1.5.0/docs/api/java/lang/System.html#getenv(java.lang.String)
                if (("getenv." + (StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME)).equals(perm.getName())) {
                    throw new AccessControlException(String.format("Accessing the system environment variable [%s] is disallowed", StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME));
                }
            }
        };
        System.setSecurityManager(securityManager);
        {
            Map<String, Object> systemEnvironment = environment.getSystemEnvironment();
            Assert.assertThat(systemEnvironment, notNullValue());
            Assert.assertThat(systemEnvironment, instanceOf(ReadOnlySystemAttributesMap.class));
            Assert.assertThat(systemEnvironment.get(StandardEnvironmentTests.ALLOWED_PROPERTY_NAME), equalTo(((Object) (StandardEnvironmentTests.ALLOWED_PROPERTY_VALUE))));
            Assert.assertThat(systemEnvironment.get(StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME), nullValue());
        }
        System.setSecurityManager(oldSecurityManager);
        StandardEnvironmentTests.getModifiableSystemEnvironment().remove(StandardEnvironmentTests.ALLOWED_PROPERTY_NAME);
        StandardEnvironmentTests.getModifiableSystemEnvironment().remove(StandardEnvironmentTests.DISALLOWED_PROPERTY_NAME);
    }
}


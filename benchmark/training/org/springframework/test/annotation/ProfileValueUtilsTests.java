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
package org.springframework.test.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;


/**
 * Unit tests for {@link ProfileValueUtils}.
 *
 * @author Sam Brannen
 * @since 3.0
 */
public class ProfileValueUtilsTests {
    private static final String NON_ANNOTATED_METHOD = "nonAnnotatedMethod";

    private static final String ENABLED_ANNOTATED_METHOD = "enabledAnnotatedMethod";

    private static final String DISABLED_ANNOTATED_METHOD = "disabledAnnotatedMethod";

    private static final String NAME = "ProfileValueUtilsTests.profile_value.name";

    private static final String VALUE = "enigma";

    // -------------------------------------------------------------------
    @Test
    public void isTestEnabledInThisEnvironmentForProvidedClass() throws Exception {
        assertClassIsEnabled(ProfileValueUtilsTests.NonAnnotated.class);
        assertClassIsEnabled(ProfileValueUtilsTests.EnabledAnnotatedSingleValue.class);
        assertClassIsEnabled(ProfileValueUtilsTests.EnabledAnnotatedMultiValue.class);
        assertClassIsEnabled(ProfileValueUtilsTests.MetaEnabledClass.class);
        assertClassIsEnabled(ProfileValueUtilsTests.MetaEnabledWithCustomProfileValueSourceClass.class);
        assertClassIsEnabled(ProfileValueUtilsTests.EnabledWithCustomProfileValueSourceOnTestInterface.class);
        assertClassIsDisabled(ProfileValueUtilsTests.DisabledAnnotatedSingleValue.class);
        assertClassIsDisabled(ProfileValueUtilsTests.DisabledAnnotatedSingleValueOnTestInterface.class);
        assertClassIsDisabled(ProfileValueUtilsTests.DisabledAnnotatedMultiValue.class);
        assertClassIsDisabled(ProfileValueUtilsTests.MetaDisabledClass.class);
        assertClassIsDisabled(ProfileValueUtilsTests.MetaDisabledWithCustomProfileValueSourceClass.class);
    }

    @Test
    public void isTestEnabledInThisEnvironmentForProvidedMethodAndClass() throws Exception {
        assertMethodIsEnabled(ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.NonAnnotated.class);
        assertMethodIsEnabled(ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedSingleValue.class);
        assertMethodIsEnabled(ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedSingleValue.class);
        assertMethodIsEnabled(ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.MetaEnabledAnnotatedSingleValue.class);
        assertMethodIsEnabled(ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.MetaEnabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.MetaEnabledAnnotatedSingleValue.class);
        assertMethodIsEnabled(ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedMultiValue.class);
        assertMethodIsEnabled(ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedMultiValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedMultiValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedSingleValueOnTestInterface.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.MetaDisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.MetaDisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.MetaDisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedMultiValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedMultiValue.class);
        assertMethodIsDisabled(ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedMultiValue.class);
    }

    @Test
    public void isTestEnabledInThisEnvironmentForProvidedProfileValueSourceMethodAndClass() throws Exception {
        ProfileValueSource profileValueSource = SystemProfileValueSource.getInstance();
        assertMethodIsEnabled(profileValueSource, ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.NonAnnotated.class);
        assertMethodIsEnabled(profileValueSource, ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedSingleValue.class);
        assertMethodIsEnabled(profileValueSource, ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(profileValueSource, ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedSingleValue.class);
        assertMethodIsEnabled(profileValueSource, ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedMultiValue.class);
        assertMethodIsEnabled(profileValueSource, ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedMultiValue.class);
        assertMethodIsDisabled(profileValueSource, ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.EnabledAnnotatedMultiValue.class);
        assertMethodIsDisabled(profileValueSource, ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(profileValueSource, ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(profileValueSource, ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedSingleValue.class);
        assertMethodIsDisabled(profileValueSource, ProfileValueUtilsTests.NON_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedMultiValue.class);
        assertMethodIsDisabled(profileValueSource, ProfileValueUtilsTests.ENABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedMultiValue.class);
        assertMethodIsDisabled(profileValueSource, ProfileValueUtilsTests.DISABLED_ANNOTATED_METHOD, ProfileValueUtilsTests.DisabledAnnotatedMultiValue.class);
    }

    // -------------------------------------------------------------------
    @SuppressWarnings("unused")
    private static class NonAnnotated {
        public void nonAnnotatedMethod() {
        }
    }

    @SuppressWarnings("unused")
    @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = ProfileValueUtilsTests.VALUE)
    private static class EnabledAnnotatedSingleValue {
        public void nonAnnotatedMethod() {
        }

        @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = ProfileValueUtilsTests.VALUE)
        public void enabledAnnotatedMethod() {
        }

        @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = (ProfileValueUtilsTests.VALUE) + "X")
        public void disabledAnnotatedMethod() {
        }
    }

    @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = (ProfileValueUtilsTests.VALUE) + "X")
    private interface IfProfileValueTestInterface {}

    @SuppressWarnings("unused")
    private static class DisabledAnnotatedSingleValueOnTestInterface implements ProfileValueUtilsTests.IfProfileValueTestInterface {
        public void nonAnnotatedMethod() {
        }
    }

    @SuppressWarnings("unused")
    @IfProfileValue(name = ProfileValueUtilsTests.NAME, values = { "foo", ProfileValueUtilsTests.VALUE, "bar" })
    private static class EnabledAnnotatedMultiValue {
        public void nonAnnotatedMethod() {
        }

        @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = ProfileValueUtilsTests.VALUE)
        public void enabledAnnotatedMethod() {
        }

        @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = (ProfileValueUtilsTests.VALUE) + "X")
        public void disabledAnnotatedMethod() {
        }
    }

    @SuppressWarnings("unused")
    @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = (ProfileValueUtilsTests.VALUE) + "X")
    private static class DisabledAnnotatedSingleValue {
        public void nonAnnotatedMethod() {
        }

        @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = ProfileValueUtilsTests.VALUE)
        public void enabledAnnotatedMethod() {
        }

        @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = (ProfileValueUtilsTests.VALUE) + "X")
        public void disabledAnnotatedMethod() {
        }
    }

    @SuppressWarnings("unused")
    @IfProfileValue(name = ProfileValueUtilsTests.NAME, values = { "foo", "bar" })
    private static class DisabledAnnotatedMultiValue {
        public void nonAnnotatedMethod() {
        }

        @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = ProfileValueUtilsTests.VALUE)
        public void enabledAnnotatedMethod() {
        }

        @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = (ProfileValueUtilsTests.VALUE) + "X")
        public void disabledAnnotatedMethod() {
        }
    }

    @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = ProfileValueUtilsTests.VALUE)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface MetaEnabled {}

    @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = (ProfileValueUtilsTests.VALUE) + "X")
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface MetaDisabled {}

    @ProfileValueUtilsTests.MetaEnabled
    private static class MetaEnabledClass {}

    @ProfileValueUtilsTests.MetaDisabled
    private static class MetaDisabledClass {}

    @SuppressWarnings("unused")
    @ProfileValueUtilsTests.MetaEnabled
    private static class MetaEnabledAnnotatedSingleValue {
        public void nonAnnotatedMethod() {
        }

        @ProfileValueUtilsTests.MetaEnabled
        public void enabledAnnotatedMethod() {
        }

        @ProfileValueUtilsTests.MetaDisabled
        public void disabledAnnotatedMethod() {
        }
    }

    @SuppressWarnings("unused")
    @ProfileValueUtilsTests.MetaDisabled
    private static class MetaDisabledAnnotatedSingleValue {
        public void nonAnnotatedMethod() {
        }

        @ProfileValueUtilsTests.MetaEnabled
        public void enabledAnnotatedMethod() {
        }

        @ProfileValueUtilsTests.MetaDisabled
        public void disabledAnnotatedMethod() {
        }
    }

    public static class HardCodedProfileValueSource implements ProfileValueSource {
        @Override
        public String get(final String key) {
            return key.equals(ProfileValueUtilsTests.NAME) ? "42" : null;
        }
    }

    @ProfileValueSourceConfiguration(ProfileValueUtilsTests.HardCodedProfileValueSource.class)
    @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = "42")
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface MetaEnabledWithCustomProfileValueSource {}

    @ProfileValueSourceConfiguration(ProfileValueUtilsTests.HardCodedProfileValueSource.class)
    @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = "13")
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface MetaDisabledWithCustomProfileValueSource {}

    @ProfileValueUtilsTests.MetaEnabledWithCustomProfileValueSource
    private static class MetaEnabledWithCustomProfileValueSourceClass {}

    @ProfileValueUtilsTests.MetaDisabledWithCustomProfileValueSource
    private static class MetaDisabledWithCustomProfileValueSourceClass {}

    @ProfileValueSourceConfiguration(ProfileValueUtilsTests.HardCodedProfileValueSource.class)
    private interface CustomProfileValueSourceTestInterface {}

    @IfProfileValue(name = ProfileValueUtilsTests.NAME, value = "42")
    private static class EnabledWithCustomProfileValueSourceOnTestInterface implements ProfileValueUtilsTests.CustomProfileValueSourceTestInterface {}
}


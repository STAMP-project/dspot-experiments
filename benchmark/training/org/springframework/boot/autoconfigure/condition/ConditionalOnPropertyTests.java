/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.condition;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;


/**
 * Tests for {@link ConditionalOnProperty}.
 *
 * @author Maciej Walkowiak
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ConditionalOnPropertyTests {
    private ConfigurableApplicationContext context;

    private ConfigurableEnvironment environment = new StandardEnvironment();

    @Test
    public void allPropertiesAreDefined() {
        load(ConditionalOnPropertyTests.MultiplePropertiesRequiredConfiguration.class, "property1=value1", "property2=value2");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void notAllPropertiesAreDefined() {
        load(ConditionalOnPropertyTests.MultiplePropertiesRequiredConfiguration.class, "property1=value1");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void propertyValueEqualsFalse() {
        load(ConditionalOnPropertyTests.MultiplePropertiesRequiredConfiguration.class, "property1=false", "property2=value2");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void propertyValueEqualsFALSE() {
        load(ConditionalOnPropertyTests.MultiplePropertiesRequiredConfiguration.class, "property1=FALSE", "property2=value2");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void relaxedName() {
        load(ConditionalOnPropertyTests.RelaxedPropertiesRequiredConfiguration.class, "spring.theRelaxedProperty=value1");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void prefixWithoutPeriod() {
        load(ConditionalOnPropertyTests.RelaxedPropertiesRequiredConfigurationWithShortPrefix.class, "spring.property=value1");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    // Enabled by default
    @Test
    public void enabledIfNotConfiguredOtherwise() {
        load(ConditionalOnPropertyTests.EnabledIfNotConfiguredOtherwiseConfig.class);
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void enabledIfNotConfiguredOtherwiseWithConfig() {
        load(ConditionalOnPropertyTests.EnabledIfNotConfiguredOtherwiseConfig.class, "simple.myProperty:false");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void enabledIfNotConfiguredOtherwiseWithConfigDifferentCase() {
        load(ConditionalOnPropertyTests.EnabledIfNotConfiguredOtherwiseConfig.class, "simple.my-property:FALSE");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    // Disabled by default
    @Test
    public void disableIfNotConfiguredOtherwise() {
        load(ConditionalOnPropertyTests.DisabledIfNotConfiguredOtherwiseConfig.class);
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void disableIfNotConfiguredOtherwiseWithConfig() {
        load(ConditionalOnPropertyTests.DisabledIfNotConfiguredOtherwiseConfig.class, "simple.myProperty:true");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void disableIfNotConfiguredOtherwiseWithConfigDifferentCase() {
        load(ConditionalOnPropertyTests.DisabledIfNotConfiguredOtherwiseConfig.class, "simple.myproperty:TrUe");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void simpleValueIsSet() {
        load(ConditionalOnPropertyTests.SimpleValueConfig.class, "simple.myProperty:bar");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void caseInsensitive() {
        load(ConditionalOnPropertyTests.SimpleValueConfig.class, "simple.myProperty:BaR");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void defaultValueIsSet() {
        load(ConditionalOnPropertyTests.DefaultValueConfig.class, "simple.myProperty:bar");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void defaultValueIsNotSet() {
        load(ConditionalOnPropertyTests.DefaultValueConfig.class);
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void defaultValueIsSetDifferentValue() {
        load(ConditionalOnPropertyTests.DefaultValueConfig.class, "simple.myProperty:another");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void prefix() {
        load(ConditionalOnPropertyTests.PrefixValueConfig.class, "simple.myProperty:bar");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void relaxedEnabledByDefault() {
        load(ConditionalOnPropertyTests.PrefixValueConfig.class, "simple.myProperty:bar");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void multiValuesAllSet() {
        load(ConditionalOnPropertyTests.MultiValuesConfig.class, "simple.my-property:bar", "simple.my-another-property:bar");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void multiValuesOnlyOneSet() {
        load(ConditionalOnPropertyTests.MultiValuesConfig.class, "simple.my-property:bar");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void usingValueAttribute() {
        load(ConditionalOnPropertyTests.ValueAttribute.class, "some.property");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void nameOrValueMustBeSpecified() {
        assertThatIllegalStateException().isThrownBy(() -> load(.class, "some.property")).satisfies(causeMessageContaining("The name or value attribute of @ConditionalOnProperty must be specified"));
    }

    @Test
    public void nameAndValueMustNotBeSpecified() {
        assertThatIllegalStateException().isThrownBy(() -> load(.class, "some.property")).satisfies(causeMessageContaining("The name and value attributes of @ConditionalOnProperty are exclusive"));
    }

    @Test
    public void metaAnnotationConditionMatchesWhenPropertyIsSet() {
        load(ConditionalOnPropertyTests.MetaAnnotation.class, "my.feature.enabled=true");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Test
    public void metaAnnotationConditionDoesNotMatchWhenPropertyIsNotSet() {
        load(ConditionalOnPropertyTests.MetaAnnotation.class);
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void metaAndDirectAnnotationConditionDoesNotMatchWhenOnlyDirectPropertyIsSet() {
        load(ConditionalOnPropertyTests.MetaAnnotationAndDirectAnnotation.class, "my.other.feature.enabled=true");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void metaAndDirectAnnotationConditionDoesNotMatchWhenOnlyMetaPropertyIsSet() {
        load(ConditionalOnPropertyTests.MetaAnnotationAndDirectAnnotation.class, "my.feature.enabled=true");
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void metaAndDirectAnnotationConditionDoesNotMatchWhenNeitherPropertyIsSet() {
        load(ConditionalOnPropertyTests.MetaAnnotationAndDirectAnnotation.class);
        assertThat(this.context.containsBean("foo")).isFalse();
    }

    @Test
    public void metaAndDirectAnnotationConditionMatchesWhenBothPropertiesAreSet() {
        load(ConditionalOnPropertyTests.MetaAnnotationAndDirectAnnotation.class, "my.feature.enabled=true", "my.other.feature.enabled=true");
        assertThat(this.context.containsBean("foo")).isTrue();
    }

    @Configuration
    @ConditionalOnProperty(name = { "property1", "property2" })
    protected static class MultiplePropertiesRequiredConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "spring.", name = "the-relaxed-property")
    protected static class RelaxedPropertiesRequiredConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "spring", name = "property")
    protected static class RelaxedPropertiesRequiredConfigurationWithShortPrefix {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    // i.e ${simple.myProperty:true}
    @Configuration
    @ConditionalOnProperty(prefix = "simple", name = "my-property", havingValue = "true", matchIfMissing = true)
    static class EnabledIfNotConfiguredOtherwiseConfig {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    // i.e ${simple.myProperty:false}
    @Configuration
    @ConditionalOnProperty(prefix = "simple", name = "my-property", havingValue = "true", matchIfMissing = false)
    static class DisabledIfNotConfiguredOtherwiseConfig {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "simple", name = "my-property", havingValue = "bar")
    static class SimpleValueConfig {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "simple.myProperty", havingValue = "bar", matchIfMissing = true)
    static class DefaultValueConfig {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "simple", name = "my-property", havingValue = "bar")
    static class PrefixValueConfig {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "simple", name = { "my-property", "my-another-property" }, havingValue = "bar")
    static class MultiValuesConfig {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty("some.property")
    protected static class ValueAttribute {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty
    protected static class NoNameOrValueAttribute {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "x", name = "y")
    protected static class NameAndValueAttribute {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnPropertyTests.ConditionalOnMyFeature
    protected static class MetaAnnotation {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnPropertyTests.ConditionalOnMyFeature
    @ConditionalOnProperty(prefix = "my.other.feature", name = "enabled", havingValue = "true", matchIfMissing = false)
    protected static class MetaAnnotationAndDirectAnnotation {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @ConditionalOnProperty(prefix = "my.feature", name = "enabled", havingValue = "true", matchIfMissing = false)
    public @interface ConditionalOnMyFeature {}
}


package org.robolectric.plugins;


import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.config.ConfigurationRegistry;
import org.robolectric.pluginapi.config.Configurer;


@RunWith(JUnit4.class)
public class CustomConfigurerTest {
    @Test
    public void customConfigCanBeAccessedFromWithinSandbox() throws Exception {
        List<String> failures = runAndGetFailures(CustomConfigurerTest.TestWithConfig.class);
        assertThat(failures).containsExactly("someConfig value is the value");
    }

    // ///////////////////
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
    public @interface SomeConfig {
        String value();
    }

    @Ignore
    public static class TestWithConfig {
        @Test
        @CustomConfigurerTest.SomeConfig("the value")
        public void shouldHaveValue() throws Exception {
            CustomConfigurerTest.SomeConfig someConfig = ConfigurationRegistry.get(CustomConfigurerTest.SomeConfig.class);
            Assert.fail(("someConfig value is " + (someConfig.value())));
        }
    }

    static class SomeConfigConfigurer implements Configurer<CustomConfigurerTest.SomeConfig> {
        @Override
        public Class<CustomConfigurerTest.SomeConfig> getConfigClass() {
            return CustomConfigurerTest.SomeConfig.class;
        }

        @Nonnull
        @Override
        public CustomConfigurerTest.SomeConfig defaultConfig() {
            return new CustomConfigurerTest.SomeConfigConfigurer.MySomeConfig();
        }

        @Override
        public CustomConfigurerTest.SomeConfig getConfigFor(@Nonnull
        String packageName) {
            return null;
        }

        @Override
        public CustomConfigurerTest.SomeConfig getConfigFor(@Nonnull
        Class<?> testClass) {
            return testClass.getAnnotation(CustomConfigurerTest.SomeConfig.class);
        }

        @Override
        public CustomConfigurerTest.SomeConfig getConfigFor(@Nonnull
        Method method) {
            return method.getAnnotation(CustomConfigurerTest.SomeConfig.class);
        }

        @Nonnull
        @Override
        public CustomConfigurerTest.SomeConfig merge(@Nonnull
        CustomConfigurerTest.SomeConfig parentConfig, @Nonnull
        CustomConfigurerTest.SomeConfig childConfig) {
            return childConfig;
        }

        @SuppressWarnings("BadAnnotationImplementation")
        private static class MySomeConfig implements CustomConfigurerTest.SomeConfig {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Annotation.class;
            }

            @Override
            public String value() {
                return "default value";
            }
        }
    }
}


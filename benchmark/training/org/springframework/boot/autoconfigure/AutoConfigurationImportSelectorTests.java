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
package org.springframework.boot.autoconfigure;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link AutoConfigurationImportSelector}
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
public class AutoConfigurationImportSelectorTests {
    private final AutoConfigurationImportSelectorTests.TestAutoConfigurationImportSelector importSelector = new AutoConfigurationImportSelectorTests.TestAutoConfigurationImportSelector();

    private final ConfigurableListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    private final MockEnvironment environment = new MockEnvironment();

    private List<AutoConfigurationImportFilter> filters = new ArrayList<>();

    @Test
    public void importsAreSelectedWhenUsingEnableAutoConfiguration() {
        String[] imports = selectImports(AutoConfigurationImportSelectorTests.BasicEnableAutoConfiguration.class);
        assertThat(imports).hasSameSizeAs(SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, getClass().getClassLoader()));
        assertThat(this.importSelector.getLastEvent().getExclusions()).isEmpty();
    }

    @Test
    public void classExclusionsAreApplied() {
        String[] imports = selectImports(AutoConfigurationImportSelectorTests.EnableAutoConfigurationWithClassExclusions.class);
        assertThat(imports).hasSize(((getAutoConfigurationClassNames().size()) - 1));
        assertThat(this.importSelector.getLastEvent().getExclusions()).contains(FreeMarkerAutoConfiguration.class.getName());
    }

    @Test
    public void classExclusionsAreAppliedWhenUsingSpringBootApplication() {
        String[] imports = selectImports(AutoConfigurationImportSelectorTests.SpringBootApplicationWithClassExclusions.class);
        assertThat(imports).hasSize(((getAutoConfigurationClassNames().size()) - 1));
        assertThat(this.importSelector.getLastEvent().getExclusions()).contains(FreeMarkerAutoConfiguration.class.getName());
    }

    @Test
    public void classNamesExclusionsAreApplied() {
        String[] imports = selectImports(AutoConfigurationImportSelectorTests.EnableAutoConfigurationWithClassNameExclusions.class);
        assertThat(imports).hasSize(((getAutoConfigurationClassNames().size()) - 1));
        assertThat(this.importSelector.getLastEvent().getExclusions()).contains(MustacheAutoConfiguration.class.getName());
    }

    @Test
    public void classNamesExclusionsAreAppliedWhenUsingSpringBootApplication() {
        String[] imports = selectImports(AutoConfigurationImportSelectorTests.SpringBootApplicationWithClassNameExclusions.class);
        assertThat(imports).hasSize(((getAutoConfigurationClassNames().size()) - 1));
        assertThat(this.importSelector.getLastEvent().getExclusions()).contains(MustacheAutoConfiguration.class.getName());
    }

    @Test
    public void propertyExclusionsAreApplied() {
        this.environment.setProperty("spring.autoconfigure.exclude", FreeMarkerAutoConfiguration.class.getName());
        String[] imports = selectImports(AutoConfigurationImportSelectorTests.BasicEnableAutoConfiguration.class);
        assertThat(imports).hasSize(((getAutoConfigurationClassNames().size()) - 1));
        assertThat(this.importSelector.getLastEvent().getExclusions()).contains(FreeMarkerAutoConfiguration.class.getName());
    }

    @Test
    public void severalPropertyExclusionsAreApplied() {
        this.environment.setProperty("spring.autoconfigure.exclude", (((FreeMarkerAutoConfiguration.class.getName()) + ",") + (MustacheAutoConfiguration.class.getName())));
        testSeveralPropertyExclusionsAreApplied();
    }

    @Test
    public void severalPropertyExclusionsAreAppliedWithExtraSpaces() {
        this.environment.setProperty("spring.autoconfigure.exclude", ((((FreeMarkerAutoConfiguration.class.getName()) + " , ") + (MustacheAutoConfiguration.class.getName())) + " "));
        testSeveralPropertyExclusionsAreApplied();
    }

    @Test
    public void severalPropertyYamlExclusionsAreApplied() {
        this.environment.setProperty("spring.autoconfigure.exclude[0]", FreeMarkerAutoConfiguration.class.getName());
        this.environment.setProperty("spring.autoconfigure.exclude[1]", MustacheAutoConfiguration.class.getName());
        testSeveralPropertyExclusionsAreApplied();
    }

    @Test
    public void combinedExclusionsAreApplied() {
        this.environment.setProperty("spring.autoconfigure.exclude", ThymeleafAutoConfiguration.class.getName());
        String[] imports = selectImports(AutoConfigurationImportSelectorTests.EnableAutoConfigurationWithClassAndClassNameExclusions.class);
        assertThat(imports).hasSize(((getAutoConfigurationClassNames().size()) - 3));
        assertThat(this.importSelector.getLastEvent().getExclusions()).contains(FreeMarkerAutoConfiguration.class.getName(), MustacheAutoConfiguration.class.getName(), ThymeleafAutoConfiguration.class.getName());
    }

    @Test
    public void nonAutoConfigurationClassExclusionsShouldThrowException() {
        assertThatIllegalStateException().isThrownBy(() -> selectImports(.class));
    }

    @Test
    public void nonAutoConfigurationClassNameExclusionsWhenPresentOnClassPathShouldThrowException() {
        assertThatIllegalStateException().isThrownBy(() -> selectImports(.class));
    }

    @Test
    public void nonAutoConfigurationPropertyExclusionsWhenPresentOnClassPathShouldThrowException() {
        this.environment.setProperty("spring.autoconfigure.exclude", ("org.springframework.boot.autoconfigure." + "AutoConfigurationImportSelectorTests.TestConfiguration"));
        assertThatIllegalStateException().isThrownBy(() -> selectImports(.class));
    }

    @Test
    public void nameAndPropertyExclusionsWhenNotPresentOnClasspathShouldNotThrowException() {
        this.environment.setProperty("spring.autoconfigure.exclude", "org.springframework.boot.autoconfigure.DoesNotExist2");
        selectImports(AutoConfigurationImportSelectorTests.EnableAutoConfigurationWithAbsentClassNameExclude.class);
        assertThat(this.importSelector.getLastEvent().getExclusions()).containsExactlyInAnyOrder("org.springframework.boot.autoconfigure.DoesNotExist1", "org.springframework.boot.autoconfigure.DoesNotExist2");
    }

    @Test
    public void filterShouldFilterImports() {
        String[] defaultImports = selectImports(AutoConfigurationImportSelectorTests.BasicEnableAutoConfiguration.class);
        this.filters.add(new AutoConfigurationImportSelectorTests.TestAutoConfigurationImportFilter(defaultImports, 1));
        this.filters.add(new AutoConfigurationImportSelectorTests.TestAutoConfigurationImportFilter(defaultImports, 3, 4));
        String[] filtered = selectImports(AutoConfigurationImportSelectorTests.BasicEnableAutoConfiguration.class);
        assertThat(filtered).hasSize(((defaultImports.length) - 3));
        assertThat(filtered).doesNotContain(defaultImports[1], defaultImports[3], defaultImports[4]);
    }

    @Test
    public void filterShouldSupportAware() {
        AutoConfigurationImportSelectorTests.TestAutoConfigurationImportFilter filter = new AutoConfigurationImportSelectorTests.TestAutoConfigurationImportFilter(new String[]{  });
        this.filters.add(filter);
        selectImports(AutoConfigurationImportSelectorTests.BasicEnableAutoConfiguration.class);
        assertThat(filter.getBeanFactory()).isEqualTo(this.beanFactory);
    }

    private class TestAutoConfigurationImportSelector extends AutoConfigurationImportSelector {
        private AutoConfigurationImportEvent lastEvent;

        @Override
        protected List<AutoConfigurationImportFilter> getAutoConfigurationImportFilters() {
            return AutoConfigurationImportSelectorTests.this.filters;
        }

        @Override
        protected List<AutoConfigurationImportListener> getAutoConfigurationImportListeners() {
            return Collections.singletonList(( event) -> this.lastEvent = event);
        }

        public AutoConfigurationImportEvent getLastEvent() {
            return this.lastEvent;
        }
    }

    private static class TestAutoConfigurationImportFilter implements BeanFactoryAware , AutoConfigurationImportFilter {
        private final Set<String> nonMatching = new HashSet<>();

        private BeanFactory beanFactory;

        TestAutoConfigurationImportFilter(String[] configurations, int... nonMatching) {
            for (int i : nonMatching) {
                this.nonMatching.add(configurations[i]);
            }
        }

        @Override
        public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
            boolean[] result = new boolean[autoConfigurationClasses.length];
            for (int i = 0; i < (result.length); i++) {
                result[i] = !(this.nonMatching.contains(autoConfigurationClasses[i]));
            }
            return result;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        public BeanFactory getBeanFactory() {
            return this.beanFactory;
        }
    }

    @Configuration
    private class TestConfiguration {}

    @EnableAutoConfiguration
    private class BasicEnableAutoConfiguration {}

    @EnableAutoConfiguration(exclude = FreeMarkerAutoConfiguration.class)
    private class EnableAutoConfigurationWithClassExclusions {}

    @SpringBootApplication(exclude = FreeMarkerAutoConfiguration.class)
    private class SpringBootApplicationWithClassExclusions {}

    @EnableAutoConfiguration(excludeName = "org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration")
    private class EnableAutoConfigurationWithClassNameExclusions {}

    @EnableAutoConfiguration(exclude = MustacheAutoConfiguration.class, excludeName = "org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration")
    private class EnableAutoConfigurationWithClassAndClassNameExclusions {}

    @EnableAutoConfiguration(exclude = AutoConfigurationImportSelectorTests.TestConfiguration.class)
    private class EnableAutoConfigurationWithFaultyClassExclude {}

    @EnableAutoConfiguration(excludeName = "org.springframework.boot.autoconfigure.AutoConfigurationImportSelectorTests.TestConfiguration")
    private class EnableAutoConfigurationWithFaultyClassNameExclude {}

    @EnableAutoConfiguration(excludeName = "org.springframework.boot.autoconfigure.DoesNotExist1")
    private class EnableAutoConfigurationWithAbsentClassNameExclude {}

    @SpringBootApplication(excludeName = "org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration")
    private class SpringBootApplicationWithClassNameExclusions {}
}


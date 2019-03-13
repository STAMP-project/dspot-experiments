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
package org.springframework.security.config.annotation.web;


import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;


/**
 * Tests for {@link AbstractConfiguredSecurityBuilder}.
 *
 * @author Joe Grandja
 */
public class AbstractConfiguredSecurityBuilderTests {
    private AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder builder;

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenObjectPostProcessorIsNullThenThrowIllegalArgumentException() {
        new AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void objectPostProcessorWhenNullThenThrowIllegalArgumentException() {
        objectPostProcessor(null);
    }

    @Test
    public void applyWhenDuplicateConfigurerAddedThenDuplicateConfigurerRemoved() throws Exception {
        this.builder.apply(new AbstractConfiguredSecurityBuilderTests.TestSecurityConfigurer());
        this.builder.apply(new AbstractConfiguredSecurityBuilderTests.TestSecurityConfigurer());
        assertThat(getConfigurers(AbstractConfiguredSecurityBuilderTests.TestSecurityConfigurer.class)).hasSize(1);
    }

    @Test(expected = IllegalStateException.class)
    public void buildWhenBuildTwiceThenThrowIllegalStateException() throws Exception {
        build();
        build();
    }

    @Test(expected = IllegalStateException.class)
    public void getObjectWhenNotBuiltThenThrowIllegalStateException() throws Exception {
        getObject();
    }

    @Test
    public void buildWhenConfigurerAppliesAnotherConfigurerThenObjectStillBuilds() throws Exception {
        AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.CONFIGURER = Mockito.mock(SecurityConfigurer.class);
        apply(new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer());
        build();
        Mockito.verify(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.CONFIGURER).init(this.builder);
        Mockito.verify(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.CONFIGURER).configure(this.builder);
    }

    @Test(expected = IllegalStateException.class)
    public void getConfigurerWhenMultipleConfigurersThenThrowIllegalStateException() throws Exception {
        AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder builder = new AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder(Mockito.mock(ObjectPostProcessor.class), true);
        apply(new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer());
        apply(new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer());
        getConfigurer(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.class);
    }

    @Test(expected = IllegalStateException.class)
    public void removeConfigurerWhenMultipleConfigurersThenThrowIllegalStateException() throws Exception {
        AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder builder = new AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder(Mockito.mock(ObjectPostProcessor.class), true);
        apply(new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer());
        apply(new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer());
        removeConfigurer(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.class);
    }

    @Test
    public void removeConfigurersWhenMultipleConfigurersThenConfigurersRemoved() throws Exception {
        AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer configurer1 = new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer();
        AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer configurer2 = new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer();
        AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder builder = new AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder(Mockito.mock(ObjectPostProcessor.class), true);
        apply(configurer1);
        apply(configurer2);
        List<AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer> removedConfigurers = removeConfigurers(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.class);
        assertThat(removedConfigurers).hasSize(2);
        assertThat(removedConfigurers).containsExactly(configurer1, configurer2);
        assertThat(getConfigurers(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.class)).isEmpty();
    }

    @Test
    public void getConfigurersWhenMultipleConfigurersThenConfigurersReturned() throws Exception {
        AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer configurer1 = new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer();
        AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer configurer2 = new AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer();
        AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder builder = new AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder(Mockito.mock(ObjectPostProcessor.class), true);
        apply(configurer1);
        apply(configurer2);
        List<AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer> configurers = builder.getConfigurers(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.class);
        assertThat(configurers).hasSize(2);
        assertThat(configurers).containsExactly(configurer1, configurer2);
        assertThat(getConfigurers(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.class)).hasSize(2);
    }

    private static class DelegateSecurityConfigurer extends SecurityConfigurerAdapter<Object, AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder> {
        private static SecurityConfigurer<Object, AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder> CONFIGURER;

        @Override
        public void init(AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder builder) throws Exception {
            builder.apply(AbstractConfiguredSecurityBuilderTests.DelegateSecurityConfigurer.CONFIGURER);
        }
    }

    private static class TestSecurityConfigurer extends SecurityConfigurerAdapter<Object, AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder> {}

    private static class TestConfiguredSecurityBuilder extends AbstractConfiguredSecurityBuilder<Object, AbstractConfiguredSecurityBuilderTests.TestConfiguredSecurityBuilder> {
        private TestConfiguredSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
            super(objectPostProcessor);
        }

        private TestConfiguredSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor, boolean allowConfigurersOfSameType) {
            super(objectPostProcessor, allowConfigurersOfSameType);
        }

        public Object performBuild() throws Exception {
            return "success";
        }
    }
}


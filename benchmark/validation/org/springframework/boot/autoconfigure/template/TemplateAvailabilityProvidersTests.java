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
package org.springframework.boot.autoconfigure.template;


import java.util.Collections;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link TemplateAvailabilityProviders}.
 *
 * @author Phillip Webb
 */
public class TemplateAvailabilityProvidersTests {
    private TemplateAvailabilityProviders providers;

    @Mock
    private TemplateAvailabilityProvider provider;

    private String view = "view";

    private ClassLoader classLoader = getClass().getClassLoader();

    private MockEnvironment environment = new MockEnvironment();

    @Mock
    private ResourceLoader resourceLoader;

    @Test
    public void createWhenApplicationContextIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TemplateAvailabilityProviders(((ApplicationContext) (null)))).withMessageContaining("ClassLoader must not be null");
    }

    @Test
    public void createWhenUsingApplicationContextShouldLoadProviders() {
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        BDDMockito.given(applicationContext.getClassLoader()).willReturn(this.classLoader);
        TemplateAvailabilityProviders providers = new TemplateAvailabilityProviders(applicationContext);
        assertThat(providers.getProviders()).isNotEmpty();
        Mockito.verify(applicationContext).getClassLoader();
    }

    @Test
    public void createWhenClassLoaderIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TemplateAvailabilityProviders(((ClassLoader) (null)))).withMessageContaining("ClassLoader must not be null");
    }

    @Test
    public void createWhenUsingClassLoaderShouldLoadProviders() {
        TemplateAvailabilityProviders providers = new TemplateAvailabilityProviders(this.classLoader);
        assertThat(providers.getProviders()).isNotEmpty();
    }

    @Test
    public void createWhenProvidersIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TemplateAvailabilityProviders(((Collection<TemplateAvailabilityProvider>) (null)))).withMessageContaining("Providers must not be null");
    }

    @Test
    public void createWhenUsingProvidersShouldUseProviders() {
        TemplateAvailabilityProviders providers = new TemplateAvailabilityProviders(Collections.singleton(this.provider));
        assertThat(providers.getProviders()).containsOnly(this.provider);
    }

    @Test
    public void getProviderWhenApplicationContextIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.providers.getProvider(this.view, null)).withMessageContaining("ApplicationContext must not be null");
    }

    @Test
    public void getProviderWhenViewIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.providers.getProvider(null, this.environment, this.classLoader, this.resourceLoader)).withMessageContaining("View must not be null");
    }

    @Test
    public void getProviderWhenEnvironmentIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.providers.getProvider(this.view, null, this.classLoader, this.resourceLoader)).withMessageContaining("Environment must not be null");
    }

    @Test
    public void getProviderWhenClassLoaderIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.providers.getProvider(this.view, this.environment, null, this.resourceLoader)).withMessageContaining("ClassLoader must not be null");
    }

    @Test
    public void getProviderWhenResourceLoaderIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.providers.getProvider(this.view, this.environment, this.classLoader, null)).withMessageContaining("ResourceLoader must not be null");
    }

    @Test
    public void getProviderWhenNoneMatchShouldReturnNull() {
        TemplateAvailabilityProvider found = this.providers.getProvider(this.view, this.environment, this.classLoader, this.resourceLoader);
        assertThat(found).isNull();
        Mockito.verify(this.provider).isTemplateAvailable(this.view, this.environment, this.classLoader, this.resourceLoader);
    }

    @Test
    public void getProviderWhenMatchShouldReturnProvider() {
        BDDMockito.given(this.provider.isTemplateAvailable(this.view, this.environment, this.classLoader, this.resourceLoader)).willReturn(true);
        TemplateAvailabilityProvider found = this.providers.getProvider(this.view, this.environment, this.classLoader, this.resourceLoader);
        assertThat(found).isSameAs(this.provider);
    }

    @Test
    public void getProviderShouldCacheMatchResult() {
        BDDMockito.given(this.provider.isTemplateAvailable(this.view, this.environment, this.classLoader, this.resourceLoader)).willReturn(true);
        this.providers.getProvider(this.view, this.environment, this.classLoader, this.resourceLoader);
        this.providers.getProvider(this.view, this.environment, this.classLoader, this.resourceLoader);
        Mockito.verify(this.provider, Mockito.times(1)).isTemplateAvailable(this.view, this.environment, this.classLoader, this.resourceLoader);
    }

    @Test
    public void getProviderShouldCacheNoMatchResult() {
        this.providers.getProvider(this.view, this.environment, this.classLoader, this.resourceLoader);
        this.providers.getProvider(this.view, this.environment, this.classLoader, this.resourceLoader);
        Mockito.verify(this.provider, Mockito.times(1)).isTemplateAvailable(this.view, this.environment, this.classLoader, this.resourceLoader);
    }

    @Test
    public void getProviderWhenCacheDisabledShouldNotUseCache() {
        BDDMockito.given(this.provider.isTemplateAvailable(this.view, this.environment, this.classLoader, this.resourceLoader)).willReturn(true);
        this.environment.setProperty("spring.template.provider.cache", "false");
        this.providers.getProvider(this.view, this.environment, this.classLoader, this.resourceLoader);
        this.providers.getProvider(this.view, this.environment, this.classLoader, this.resourceLoader);
        Mockito.verify(this.provider, Mockito.times(2)).isTemplateAvailable(this.view, this.environment, this.classLoader, this.resourceLoader);
    }
}


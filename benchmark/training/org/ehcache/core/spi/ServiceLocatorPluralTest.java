/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.core.spi;


import ServiceLocator.DependencySet;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests handling of {@link PluralService} by {@link ServiceLocator}.
 */
public class ServiceLocatorPluralTest {
    /**
     * Ensures that multiple instances of a single {@code Service} implementation <i>without</i>
     * the {@link PluralService} annotation can not have more than one instance registered.
     */
    @Test
    public void testMultipleInstanceRegistration() throws Exception {
        final ServiceLocator.DependencySet serviceLocator = ServiceLocator.dependencySet();
        final ConcreteService firstSingleton = new ConcreteService();
        final ConcreteService secondSingleton = new ConcreteService();
        serviceLocator.with(firstSingleton);
        Assert.assertThat(serviceLocator.providersOf(ConcreteService.class), Matchers.contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(AdditionalService.class), Matchers.<AdditionalService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(AggregateService.class), Matchers.<AggregateService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(FooService.class), Matchers.<FooService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(BarService.class), Matchers.<BarService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(FoundationService.class), Matchers.<FoundationService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(AugmentedService.class), Matchers.<AugmentedService>contains(firstSingleton));
        try {
            serviceLocator.with(secondSingleton);
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
            Assert.assertThat(e.getMessage(), Matchers.containsString(ConcreteService.class.getName()));
        }
    }

    /**
     * Ensures that multiple {@code Service} implementations of an interface <i>without</i> the
     * {@link PluralService} annotation can not both be registered.
     */
    @Test
    public void testMultipleImplementationRegistration() throws Exception {
        final ServiceLocator.DependencySet serviceLocator = ServiceLocator.dependencySet();
        final ConcreteService firstSingleton = new ConcreteService();
        final ExtraConcreteService secondSingleton = new ExtraConcreteService();
        serviceLocator.with(firstSingleton);
        Assert.assertThat(serviceLocator.providersOf(ConcreteService.class), Matchers.contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(AdditionalService.class), Matchers.<AdditionalService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(AggregateService.class), Matchers.<AggregateService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(FooService.class), Matchers.<FooService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(BarService.class), Matchers.<BarService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(FoundationService.class), Matchers.<FoundationService>contains(firstSingleton));
        Assert.assertThat(serviceLocator.providersOf(AugmentedService.class), Matchers.<AugmentedService>contains(firstSingleton));
        try {
            serviceLocator.with(secondSingleton);
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
            final String message = e.getMessage();
            Assert.assertThat(message, Matchers.containsString(AdditionalService.class.getName()));
            Assert.assertThat(message, Matchers.containsString(AggregateService.class.getName()));
            Assert.assertThat(message, Matchers.containsString(FooService.class.getName()));
            Assert.assertThat(message, Matchers.containsString(BarService.class.getName()));
            Assert.assertThat(message, Matchers.containsString(FoundationService.class.getName()));
            Assert.assertThat(message, Matchers.containsString(AugmentedService.class.getName()));
        }
    }

    /**
     * Ensures that multiple {@code Service} implementations of an interface <i>with</i> the
     * {@link PluralService} annotation can both be registered.
     */
    @Test
    public void testPluralRegistration() throws Exception {
        final ServiceLocator.DependencySet dependencySet = ServiceLocator.dependencySet();
        final AlphaServiceProviderImpl alphaServiceProvider = new AlphaServiceProviderImpl();
        final BetaServiceProviderImpl betaServiceProvider = new BetaServiceProviderImpl();
        dependencySet.with(alphaServiceProvider);
        Assert.assertThat(dependencySet.providersOf(AlphaServiceProviderImpl.class), Matchers.everyItem(Matchers.isOneOf(alphaServiceProvider)));
        Assert.assertThat(dependencySet.providersOf(AlphaServiceProvider.class), Matchers.everyItem(Matchers.<AlphaServiceProvider>isOneOf(alphaServiceProvider)));
        Assert.assertThat(dependencySet.providersOf(PluralServiceProvider.class), Matchers.everyItem(Matchers.<PluralServiceProvider>isOneOf(alphaServiceProvider)));
        dependencySet.with(betaServiceProvider);
        Assert.assertThat(dependencySet.providersOf(BetaServiceProviderImpl.class), Matchers.everyItem(Matchers.isOneOf(betaServiceProvider)));
        Assert.assertThat(dependencySet.providersOf(BetaServiceProvider.class), Matchers.everyItem(Matchers.<BetaServiceProvider>isOneOf(betaServiceProvider)));
        Assert.assertThat(dependencySet.providersOf(PluralServiceProvider.class), Matchers.everyItem(Matchers.<PluralServiceProvider>isOneOf(alphaServiceProvider, betaServiceProvider)));
    }
}


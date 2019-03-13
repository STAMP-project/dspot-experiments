/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.oauth2.provider.scope.internal.liferay;


import ScopeMapper.PASS_THROUGH_SCOPE_MAPPER;
import com.liferay.oauth2.provider.scope.internal.spi.scope.matcher.StrictScopeMatcherFactory;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopedServiceTrackerMap;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcherFactory;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Stian Sigvartsen
 */
@RunWith(PowerMockRunner.class)
public class ScopeLocatorImplTest extends PowerMockito {
    @Test
    public void testPrefixHandlerFactoryByNameAndCompany() throws Exception {
        String applicationName2 = "com.liferay.test2";
        PrefixHandler defaultPrefixHandler = ( target) -> "default/" + target;
        ScopeFinder scopeFinder = () -> scopesSet1;
        ScopeLocatorImplTest.Builder builder = new ScopeLocatorImplTest.Builder();
        ScopeLocatorImpl scopeLocatorImpl = builder.withPrefixHandlerFactories(( propertyAccessor) -> defaultPrefixHandler, ( registrator) -> {
        }).withScopeFinders(( registrator) -> {
            registrator.register(_COMPANY_ID, _APPLICATION_NAME, scopeFinder);
            registrator.register(_COMPANY_ID, applicationName2, scopeFinder);
        }).build();
        Collection<String> application1ScopeAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, ScopeLocatorImplTest._APPLICATION_NAME);
        Collection<String> application2ScopeAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, applicationName2);
        for (String scope : scopesSet1) {
            Assert.assertThat(application1ScopeAliases, CoreMatchers.hasItem(defaultPrefixHandler.addPrefix(scope)));
            Assert.assertThat(application2ScopeAliases, CoreMatchers.hasItem(defaultPrefixHandler.addPrefix(scope)));
        }
        PrefixHandler appPrefixHandler = ( target) -> "app/" + target;
        PrefixHandler companyPrefixHandler = ( target) -> "company/" + target;
        builder = new ScopeLocatorImplTest.Builder();
        scopeLocatorImpl = builder.withPrefixHandlerFactories(( propertyAccessor) -> defaultPrefixHandler, ( registrator) -> {
            registrator.register(null, ScopeLocatorImplTest._APPLICATION_NAME, ( propertyAccessor) -> appPrefixHandler);
            registrator.register(ScopeLocatorImplTest._COMPANY_ID, null, ( propertyAccessor) -> companyPrefixHandler);
        }).withScopeFinders(( registrator) -> {
            registrator.register(_COMPANY_ID, _APPLICATION_NAME, scopeFinder);
            registrator.register(_COMPANY_ID, applicationName2, scopeFinder);
        }).build();
        application1ScopeAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, ScopeLocatorImplTest._APPLICATION_NAME);
        application2ScopeAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, applicationName2);
        for (String scope : scopesSet1) {
            Assert.assertThat(application1ScopeAliases, CoreMatchers.hasItem(appPrefixHandler.addPrefix(scope)));
            Assert.assertThat(application2ScopeAliases, CoreMatchers.hasItem(companyPrefixHandler.addPrefix(scope)));
        }
    }

    @Test
    public void testScopeFinderByName() throws Exception {
        String applicationName2 = "com.liferay.test2";
        ScopeFinder application1ScopeFinder = () -> scopesSet1;
        ScopeFinder application2ScopeFinder = () -> scopedSet2;
        ScopeLocatorImplTest.Builder builder = new ScopeLocatorImplTest.Builder();
        ScopeLocatorImpl scopeLocatorImpl = builder.withScopeFinders(( registrator) -> {
            registrator.register(ScopeLocatorImplTest._COMPANY_ID, ScopeLocatorImplTest._APPLICATION_NAME, application1ScopeFinder);
            registrator.register(ScopeLocatorImplTest._COMPANY_ID, applicationName2, application2ScopeFinder);
        }).build();
        Collection<String> application1ScopeAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, ScopeLocatorImplTest._APPLICATION_NAME);
        Collection<String> application2ScopesAliasesDefault = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, applicationName2);
        for (String scope : scopesSet1) {
            Assert.assertThat(application1ScopeAliases, CoreMatchers.hasItem(scope));
        }
        for (String scope : scopedSet2) {
            Assert.assertThat(application2ScopesAliasesDefault, CoreMatchers.hasItem(scope));
        }
        Assert.assertNotEquals(application1ScopeAliases, application2ScopesAliasesDefault);
    }

    @Test
    public void testScopeMapperByNameAndCompany() throws Exception {
        String applicationName2 = "com.liferay.test2";
        ScopeMapper defaultScopeMapper = ScopeMapper.PASS_THROUGH_SCOPE_MAPPER;
        ScopeFinder scopeFinder = () -> scopesSet1;
        ScopeLocatorImplTest.Builder builder = new ScopeLocatorImplTest.Builder();
        ScopeLocatorImpl scopeLocatorImpl = builder.withScopeMappers(defaultScopeMapper, ( registrator) -> {
        }).withScopeFinders(( registrator) -> {
            registrator.register(_COMPANY_ID, _APPLICATION_NAME, scopeFinder);
            registrator.register(_COMPANY_ID, applicationName2, scopeFinder);
        }).build();
        Collection<String> application1ScopeAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, ScopeLocatorImplTest._APPLICATION_NAME);
        Collection<String> application2ScopeAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, applicationName2);
        for (String scope : scopesSet1) {
            Assert.assertThat(application1ScopeAliases, CoreMatchers.hasItem(scope));
            Assert.assertThat(application2ScopeAliases, CoreMatchers.hasItem(scope));
        }
        ScopeMapper appScopeMapper = ( scope) -> Collections.singleton(("app/" + scope));
        ScopeMapper companyScopeMapper = ( scope) -> Collections.singleton(("company/" + scope));
        builder = new ScopeLocatorImplTest.Builder();
        scopeLocatorImpl = builder.withScopeMappers(defaultScopeMapper, ( registrator) -> {
            registrator.register(null, _APPLICATION_NAME, appScopeMapper);
            registrator.register(_COMPANY_ID, null, companyScopeMapper);
        }).withScopeFinders(( registrator) -> {
            registrator.register(_COMPANY_ID, _APPLICATION_NAME, scopeFinder);
            registrator.register(_COMPANY_ID, applicationName2, scopeFinder);
        }).build();
        Collection<String> application1ScopesAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, ScopeLocatorImplTest._APPLICATION_NAME);
        Collection<String> application2ScopesAliases = scopeLocatorImpl.getScopeAliases(ScopeLocatorImplTest._COMPANY_ID, applicationName2);
        for (String scope : scopesSet1) {
            Assert.assertThat(application1ScopesAliases, CoreMatchers.hasItems(appScopeMapper.map(scope).toArray(new String[0])));
            Assert.assertThat(application2ScopesAliases, CoreMatchers.hasItems(companyScopeMapper.map(scope).toArray(new String[0])));
        }
    }

    @Test
    public void testScopeMatcherByCompany() throws Exception {
        String applicationName2 = "com.liferay.test2";
        ScopeFinder service = () -> scopesSet1;
        Set<String> matchScopes = Collections.singleton("everything.readonly");
        ScopeMatcherFactory explicitScopeMatcherFactory = ( scopeAlias) -> ( scope) -> (scope.equals(scopeAlias)) && (matchScopes.contains(scope));
        ScopeLocatorImplTest.Builder builder = new ScopeLocatorImplTest.Builder();
        ScopeLocatorImpl scopeLocatorImpl = builder.withScopeFinders(( registrator) -> {
            registrator.register(ScopeLocatorImplTest._COMPANY_ID, ScopeLocatorImplTest._APPLICATION_NAME, service);
            registrator.register(ScopeLocatorImplTest._COMPANY_ID, applicationName2, service);
        }).withScopeMatcherFactories(( scopeAlias) -> scopeAlias::equals, ( registrator) -> registrator.register(String.valueOf(ScopeLocatorImplTest._COMPANY_ID), explicitScopeMatcherFactory)).build();
        Collection<LiferayOAuth2Scope> matchedLiferayOAuth2Scopes = scopeLocatorImpl.getLiferayOAuth2Scopes(ScopeLocatorImplTest._COMPANY_ID, "everything", ScopeLocatorImplTest._APPLICATION_NAME);
        Set<String> matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);
        Assert.assertFalse(matchedScopes.contains("everything"));
        matchedLiferayOAuth2Scopes = scopeLocatorImpl.getLiferayOAuth2Scopes(ScopeLocatorImplTest._COMPANY_ID, "everything.readonly", ScopeLocatorImplTest._APPLICATION_NAME);
        matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);
        Assert.assertTrue(matchedScopes.contains("everything.readonly"));
    }

    @Test
    public void testScopeMatcherIsolatedFromPrefixHanderFactory() throws Exception {
        PrefixHandlerFactory testPrefixHandlerFactory = ( propertyAccessor) -> ( target) -> "test/" + target;
        final ScopeMatcherFactory scopeMatcherFactory = Mockito.spy(new StrictScopeMatcherFactory());
        ScopeLocatorImplTest.Builder builder = new ScopeLocatorImplTest.Builder();
        ScopeLocatorImpl scopeLocatorImpl = builder.withPrefixHandlerFactories(( propertyAccessor) -> PrefixHandler.PASS_THROUGH_PREFIX_HANDLER, ( registrator) -> {
            registrator.register(ScopeLocatorImplTest._COMPANY_ID, ScopeLocatorImplTest._APPLICATION_NAME, testPrefixHandlerFactory);
        }).withScopeMatcherFactories(( scopeAlias) -> scopeAlias::equals, ( registrator) -> {
            registrator.register(String.valueOf(_COMPANY_ID), scopeMatcherFactory);
        }).withScopeFinders(( registrator) -> {
            registrator.register(_COMPANY_ID, _APPLICATION_NAME, () -> scopesSet1);
        }).build();
        Collection<LiferayOAuth2Scope> matchedLiferayOAuth2Scopes = scopeLocatorImpl.getLiferayOAuth2Scopes(ScopeLocatorImplTest._COMPANY_ID, "test/everything", ScopeLocatorImplTest._APPLICATION_NAME);
        Mockito.verify(scopeMatcherFactory, Mockito.atLeast(1)).create("everything");
        Set<String> matchedScopes = _getScopes(matchedLiferayOAuth2Scopes);
        Assert.assertTrue(matchedScopes.contains("everything"));
    }

    protected final Set<String> scopedSet2 = new HashSet<>(Arrays.asList("GET", "POST"));

    protected final Set<String> scopesSet1 = new HashSet<>(Arrays.asList("everything", "everything.readonly"));

    private static final String _APPLICATION_NAME = "com.liferay.test1";

    private static final long _COMPANY_ID = 1;

    private class Builder {
        public ScopeLocatorImpl build() throws IllegalAccessException {
            if (!(_scopeMatcherFactoriesInitialized)) {
                withScopeMatcherFactories(( scopeAlias) -> scopeAlias::equals, ( registrator) -> {
                });
            }
            if (!(_prefixHandlerFactoriesInitialized)) {
                withPrefixHandlerFactories(( propertyAccessor) -> PrefixHandler.PASS_THROUGH_PREFIX_HANDLER, ( registrator) -> {
                });
            }
            if (!(_scopeMappersInitialized)) {
                withScopeMappers(PASS_THROUGH_SCOPE_MAPPER, ( registrator) -> {
                });
            }
            if (!(_scopeFindersInitialized)) {
                withScopeFinders(( registrator) -> {
                });
            }
            return _scopeLocatorImpl;
        }

        public ScopeLocatorImplTest.Builder withPrefixHandlerFactories(PrefixHandlerFactory defaultPrefixHandlerFactory, ScopeLocatorImplTest.CompanyAndKeyConfigurator<PrefixHandlerFactory> configurator) throws IllegalAccessException {
            ScopedServiceTrackerMap<PrefixHandlerFactory> scopedPrefixHandlerFactories = _prepareScopeServiceTrackerMapMock(defaultPrefixHandlerFactory, configurator);
            ScopeLocatorImplTest._set(_scopeLocatorImpl, "_defaultPrefixHandlerFactory", defaultPrefixHandlerFactory);
            _scopeLocatorImpl.setScopedPrefixHandlerFactories(scopedPrefixHandlerFactories);
            _prefixHandlerFactoriesInitialized = true;
            return this;
        }

        public ScopeLocatorImplTest.Builder withScopeFinders(ScopeLocatorImplTest.CompanyAndKeyConfigurator<ScopeFinder> configurator) throws IllegalAccessException, IllegalArgumentException {
            ServiceTrackerMap<String, ServiceReferenceServiceTuple<?, ScopeFinder>> scopeFinderByNameServiceTrackerMap = Mockito.mock(ServiceTrackerMap.class);
            _scopeLocatorImpl.setScopeFinderByNameServiceTrackerMap(scopeFinderByNameServiceTrackerMap);
            ScopedServiceTrackerMap<ScopeFinder> scopedScopeFinder = Mockito.mock(ScopedServiceTrackerMap.class);
            _scopeLocatorImpl.setScopedScopeFinders(scopedScopeFinder);
            configurator.configure(( companyId, applicationName, service) -> {
                ServiceReference<?> serviceReference = Mockito.mock(.class);
                when(scopeFinderByNameServiceTrackerMap.getService(applicationName)).thenReturn(new ServiceReferenceServiceTuple(serviceReference, service));
                when(scopedScopeFinder.getService(companyId, applicationName)).thenReturn(service);
            });
            _scopeFindersInitialized = true;
            return this;
        }

        public ScopeLocatorImplTest.Builder withScopeMappers(ScopeMapper defaultScopeMapper, ScopeLocatorImplTest.CompanyAndKeyConfigurator<ScopeMapper> configurator) throws IllegalAccessException {
            ScopedServiceTrackerMap<ScopeMapper> scopedScopeMapper = _prepareScopeServiceTrackerMapMock(defaultScopeMapper, configurator);
            ScopeLocatorImplTest._set(_scopeLocatorImpl, "_defaultScopeMapper", defaultScopeMapper);
            _scopeLocatorImpl.setScopedScopeMapper(scopedScopeMapper);
            _scopeMappersInitialized = true;
            return this;
        }

        public ScopeLocatorImplTest.Builder withScopeMatcherFactories(ScopeMatcherFactory defaultScopeMatcherFactory, ScopeLocatorImplTest.KeyConfigurator<ScopeMatcherFactory> configurator) throws IllegalAccessException, IllegalArgumentException {
            ServiceTrackerMap<String, ScopeMatcherFactory> scopeMatcherFactoriesServiceTrackerMap = Mockito.mock(ServiceTrackerMap.class);
            _scopeLocatorImpl.setDefaultScopeMatcherFactory(defaultScopeMatcherFactory);
            _scopeLocatorImpl.setScopedScopeMatcherFactories(scopeMatcherFactoriesServiceTrackerMap);
            configurator.configure(( companyId, service) -> {
                when(scopeMatcherFactoriesServiceTrackerMap.getService(companyId)).thenReturn(service);
            });
            _scopeMatcherFactoriesInitialized = true;
            return this;
        }

        private <T> ScopedServiceTrackerMap<T> _prepareScopeServiceTrackerMapMock(T defaultService, ScopeLocatorImplTest.CompanyAndKeyConfigurator<T> configurator) {
            ScopedServiceTrackerMap<T> scopedServiceTrackerMap = Mockito.mock(ScopedServiceTrackerMap.class);
            TestScopedServiceTrackerMap<T> testScopedServiceTrackerMap = new TestScopedServiceTrackerMap<>(defaultService);
            Answer<T> answer = ( invocation) -> {
                long companyId = getArgumentAt(0, Long.class);
                String key = invocation.getArgumentAt(1, String.class);
                return testScopedServiceTrackerMap.getService(companyId, key);
            };
            when(scopedServiceTrackerMap.getService(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenAnswer(answer);
            configurator.configure(testScopedServiceTrackerMap::setService);
            return scopedServiceTrackerMap;
        }

        private boolean _prefixHandlerFactoriesInitialized;

        private boolean _scopeFindersInitialized;

        private final ScopeLocatorImpl _scopeLocatorImpl = new ScopeLocatorImpl();

        private boolean _scopeMappersInitialized;

        private boolean _scopeMatcherFactoriesInitialized;
    }

    private interface CompanyAndKeyConfigurator<T> {
        public void configure(ScopeLocatorImplTest.CompanyAndKeyRegistrator<T> registrator);
    }

    private interface CompanyAndKeyRegistrator<T> {
        public void register(Long companyId, String key, T service);
    }

    private interface KeyConfigurator<T> {
        public void configure(ScopeLocatorImplTest.KeyRegistrator<T> registrator);
    }

    private interface KeyRegistrator<T> {
        public void register(String key, T service);
    }
}


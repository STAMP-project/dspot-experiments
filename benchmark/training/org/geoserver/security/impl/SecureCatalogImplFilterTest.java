/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import CatalogMode.CHALLENGE;
import CatalogMode.HIDE;
import java.util.ArrayList;
import java.util.List;
import org.easymock.Capture;
import org.geoserver.catalog.util.CloseableIteratorAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static CatalogMode.HIDE;


public class SecureCatalogImplFilterTest {
    Authentication anonymous = new TestingAuthenticationToken("anonymous", null);

    ResourceAccessManager manager;

    @Test
    public void testFeatureTypeList() throws Exception {
        Catalog catalog = createMock(Catalog.class);
        manager = createMock(ResourceAccessManager.class);
        Filter mockFilter = createMock(Filter.class);
        expect(manager.getSecurityFilter(eq(anonymous), eq(FeatureTypeInfo.class))).andStubReturn(mockFilter);// TODO

        final Capture<Filter> filterCapture = new Capture<Filter>();
        final List<FeatureTypeInfo> source = new ArrayList<FeatureTypeInfo>();
        WorkspaceInfo mockWSInfo = createMock(WorkspaceInfo.class);
        expect(manager.getAccessLimits(eq(anonymous), eq(mockWSInfo))).andStubReturn(new WorkspaceAccessLimits(HIDE, true, false, false));
        FeatureTypeInfo mockFTInfo = createMockFeatureType("foo", mockWSInfo, HIDE, mockFilter, true, false);
        source.add(mockFTInfo);
        replay(mockFTInfo);
        mockFTInfo = createMockFeatureType("bar", mockWSInfo, HIDE, mockFilter, false, false);
        source.add(mockFTInfo);
        replay(mockFTInfo);
        mockFTInfo = createMockFeatureType("baz", mockWSInfo, CHALLENGE, mockFilter, false, false);
        source.add(mockFTInfo);
        replay(mockFTInfo);
        expect(catalog.list(eq(FeatureTypeInfo.class), capture(filterCapture), ((Integer) (isNull())), ((Integer) (isNull())), ((SortBy) (isNull())))).andStubAnswer(new org.easymock.IAnswer<org.geoserver.catalog.util.CloseableIterator<FeatureTypeInfo>>() {
            @Override
            public org.geoserver.catalog.util.CloseableIterator<FeatureTypeInfo> answer() throws Throwable {
                Filter filter = filterCapture.getValue();
                return CloseableIteratorAdapter.filter(source.iterator(), filter);
            }
        });
        replay(catalog, manager, mockFilter);
        @SuppressWarnings("serial")
        SecureCatalogImpl sc = new SecureCatalogImpl(catalog, manager) {
            // Calls static method we can't mock
            @Override
            protected boolean isAdmin(Authentication authentication) {
                return false;
            }

            // Not relevant to the test ad complicates things due to static calls
            @Override
            protected <T extends CatalogInfo> T checkAccess(Authentication user, T info, MixedModeBehavior mixedModeBehavior) {
                return info;
            }
        };
        // use no user at all
        SecurityContextHolder.getContext().setAuthentication(anonymous);
        List<FeatureTypeInfo> ftResult = SecureCatalogImplFilterTest.collectAndClose(sc.list(FeatureTypeInfo.class, Predicates.acceptAll()));
        WorkspaceInfo foo = ftResult.get(0).getStore().getWorkspace();
        Assert.assertThat(ftResult, contains(SecureCatalogImplFilterTest.matchFT("foo", mockWSInfo), SecureCatalogImplFilterTest.matchFT("baz", mockWSInfo)));
        verify(catalog, manager);
    }
}


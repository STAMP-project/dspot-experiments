/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.extensionservice.marketplace.internal;


import java.net.URI;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class MarketplaceExtensionServiceTest {
    private static final String BASE_PATH = "http://marketplace.eclipse.org/marketplace-client-intro";

    private MarketplaceExtensionService marketplaceService;

    private MarketplaceProxy proxy;

    @Test
    public void shouldParseBindingExtensionIdFromValidURI() throws Exception {
        String url = (MarketplaceExtensionServiceTest.BASE_PATH) + "?mpc_install=3305842";
        String extensionId = marketplaceService.getExtensionId(new URI(url));
        Assert.assertThat(extensionId, Is.is("market:binding-3305842"));
    }

    @Test
    public void shouldParseRuleExtensionIdFromValidURI() throws Exception {
        String url = (MarketplaceExtensionServiceTest.BASE_PATH) + "?mpc_install=3459873";
        String extensionId = marketplaceService.getExtensionId(new URI(url));
        Assert.assertThat(extensionId, Is.is("market:ruletemplate-3459873"));
    }

    @Test
    public void shouldParseVoiceExtensionIdFromValidURI() throws Exception {
        String url = (MarketplaceExtensionServiceTest.BASE_PATH) + "?mpc_install=396962433";
        String extensionId = marketplaceService.getExtensionId(new URI(url));
        Assert.assertThat(extensionId, Is.is("market:voice-396962433"));
    }

    @Test
    public void shouldParseExtensionIdFromValidURIWithMultipleQueryParams() throws Exception {
        String url = (MarketplaceExtensionServiceTest.BASE_PATH) + "?p1&p2=foo&mpc_install=3305842&p3=bar";
        String extensionId = marketplaceService.getExtensionId(new URI(url));
        Assert.assertThat(extensionId, Is.is("market:binding-3305842"));
    }

    @Test
    public void shouldReturnNullFormInvalidQueryParam() throws Exception {
        String url = (MarketplaceExtensionServiceTest.BASE_PATH) + "?extensionId=3305842";
        String extensionId = marketplaceService.getExtensionId(new URI(url));
        Assert.assertThat(extensionId, IsNull.nullValue());
    }

    @Test
    public void shouldReturnNullFromInvalidHost() throws Exception {
        String url = "http://m.eclipse.org/marketplace-client-intro?extensionId=3305842";
        String extensionId = marketplaceService.getExtensionId(new URI(url));
        Assert.assertThat(extensionId, IsNull.nullValue());
    }

    @Test
    public void shouldReturnNullFromEmptyId() throws Exception {
        String url = (MarketplaceExtensionServiceTest.BASE_PATH) + "?p1&p2=foo&mpc_install=&p3=bar";
        String extensionId = marketplaceService.getExtensionId(new URI(url));
        Assert.assertThat(extensionId, IsNull.nullValue());
    }
}


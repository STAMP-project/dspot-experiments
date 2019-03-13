/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.websocket.impl;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link UrlResolver}
 *
 * @author Dmitry Kuleshov
 */
public class UrlResolverTest {
    private UrlResolver urlResolver = new UrlResolver();

    @Test
    public void shouldResolveUrl() {
        urlResolver.setMapping("id", "url");
        final String id = urlResolver.resolve("url");
        Assert.assertEquals("id", id);
    }

    @Test
    public void shouldResolveId() {
        urlResolver.setMapping("id", "url");
        final String url = urlResolver.getUrl("id");
        Assert.assertEquals("url", url);
    }
}


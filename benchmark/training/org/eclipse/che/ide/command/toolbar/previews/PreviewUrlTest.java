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
package org.eclipse.che.ide.command.toolbar.previews;


import org.eclipse.che.ide.api.workspace.WsAgentServerUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link PreviewUrl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PreviewUrlTest {
    private static final String PREVIEW_URL = "http://preview.com/param";

    private static final String MACHINE_NAME = "dev-machine";

    private static final String SERVER_PORT = "8080";

    @Mock
    private WsAgentServerUtil wsAgentServerUtil;

    private PreviewUrl previewUrl;

    @Test
    public void testGetUrl() throws Exception {
        Assert.assertEquals(PreviewUrlTest.PREVIEW_URL, previewUrl.getUrl());
    }

    @Test
    public void testGetDisplayName() throws Exception {
        Assert.assertEquals(((((PreviewUrlTest.MACHINE_NAME) + ':') + (PreviewUrlTest.SERVER_PORT)) + "/param"), previewUrl.getDisplayName());
    }
}


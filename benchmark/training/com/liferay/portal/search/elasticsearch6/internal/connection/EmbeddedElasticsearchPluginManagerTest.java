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
package com.liferay.portal.search.elasticsearch6.internal.connection;


import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.io.IOException;
import org.elasticsearch.Version;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class EmbeddedElasticsearchPluginManagerTest {
    @Test
    public void testInstallForTheFirstTime() throws Exception {
        install("analysis-kuromoji");
        Mockito.verify(_pluginManager).install(Mockito.eq("analysis-kuromoji"));
        Mockito.verify(_pluginZipFactory).createPluginZip((("/plugins/org.elasticsearch.plugin.analysis.kuromoji-" + (Version.CURRENT)) + ".zip"));
        Mockito.verify(_pluginZip).delete();
    }

    @Test
    public void testInstallForTheSecondTime() throws Exception {
        setUpAlreadyInstalled(true);
        install();
        Mockito.verify(_pluginManager, Mockito.never()).install(Mockito.anyString());
        Mockito.verify(_pluginZipFactory, Mockito.never()).createPluginZip(Mockito.anyString());
        Mockito.verifyZeroInteractions(_pluginZip);
    }

    @Test
    public void testInstallOverObsoleteVersion() throws Exception {
        setUpAlreadyInstalled(false);
        install();
        Mockito.verify(_pluginManager).remove(Mockito.eq(EmbeddedElasticsearchPluginManagerTest._PLUGIN_NAME));
    }

    @Test
    public void testPluginZipIsDeletedOnError() throws Exception {
        IOException ioException = new IOException();
        setUpBrokenDownloadAndExtract(ioException);
        try {
            install();
            Assert.fail();
        } catch (IOException ioe) {
            Assert.assertSame(ioException, ioe);
        }
        Mockito.verify(_pluginZip).delete();
    }

    @Test
    public void testSurviveConcurrentInstallInSameDir() throws Exception {
        setUpBrokenDownloadAndExtract(new IOException("already exists. To update the plugin, uninstall it first"));
        install();
        Mockito.verify(_pluginZip).delete();
    }

    private static final String _PLUGIN_NAME = RandomTestUtil.randomString();

    private static final String _PLUGINS_PATH_STRING = ".";

    @Mock
    private PluginManager _pluginManager;

    @Mock
    private PluginManagerFactory _pluginManagerFactory;

    @Mock
    private PluginZip _pluginZip;

    @Mock
    private PluginZipFactory _pluginZipFactory;
}


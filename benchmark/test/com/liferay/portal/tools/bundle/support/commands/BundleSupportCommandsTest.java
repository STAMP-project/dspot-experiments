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
package com.liferay.portal.tools.bundle.support.commands;


import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.littleshoot.proxy.HttpProxyServer;


/**
 *
 *
 * @author David Truong
 * @author Andrea Di Giorgi
 */
public class BundleSupportCommandsTest {
    @Test
    public void testClean() throws Exception {
        File liferayHomeDir = temporaryFolder.newFolder("bundles");
        File osgiModulesDir = BundleSupportCommandsTest._createDirectory(liferayHomeDir, "osgi/modules");
        File jarFile = BundleSupportCommandsTest._createFile(osgiModulesDir, "test.jar");
        File osgiWarDir = BundleSupportCommandsTest._createDirectory(liferayHomeDir, "osgi/war");
        File warFile = BundleSupportCommandsTest._createFile(osgiWarDir, "test.war");
        clean(warFile.getName(), liferayHomeDir);
        Assert.assertFalse(warFile.exists());
        Assert.assertTrue(jarFile.exists());
    }

    @Test
    public void testCreateToken() throws Exception {
        _testCreateToken(BundleSupportCommandsTest._CONTEXT_PATH_TOKEN);
    }

    @Test
    public void testCreateTokenForce() throws Exception {
        File tokenFile = temporaryFolder.newFile();
        _testCreateToken(BundleSupportCommandsTest._CONTEXT_PATH_TOKEN, true, null, tokenFile);
    }

    @Test
    public void testCreateTokenInNonexistentDirectory() throws Exception {
        File tokenFile = new File(temporaryFolder.getRoot(), "nonexistent/directory/token");
        _testCreateToken(BundleSupportCommandsTest._CONTEXT_PATH_TOKEN, false, null, tokenFile);
    }

    @Test
    public void testCreateTokenPasswordFile() throws Exception {
        File passwordFile = temporaryFolder.newFile();
        File tokenFile = temporaryFolder.newFile();
        Files.write(passwordFile.toPath(), BundleSupportCommandsTest._HTTP_SERVER_PASSWORD.getBytes(StandardCharsets.UTF_8));
        _testCreateToken(BundleSupportCommandsTest._CONTEXT_PATH_TOKEN, true, passwordFile, tokenFile);
    }

    @Test
    public void testCreateTokenUnformatted() throws Exception {
        _testCreateToken(BundleSupportCommandsTest._CONTEXT_PATH_TOKEN_UNFORMATTED);
    }

    @Test
    public void testDeployJar() throws Exception {
        File liferayHomeDir = temporaryFolder.newFolder("bundles");
        File file = temporaryFolder.newFile("test-1.0.0.jar");
        File deployedFile = new File(liferayHomeDir, "osgi/modules/test.jar");
        deploy(file, liferayHomeDir, deployedFile.getName());
        Assert.assertTrue(deployedFile.exists());
    }

    @Test
    public void testDeployWar() throws Exception {
        File liferayHomeDir = temporaryFolder.newFolder("bundles");
        File file = temporaryFolder.newFile("test-1.0.0.war");
        File deployedFile = new File(liferayHomeDir, "osgi/war/test.war");
        deploy(file, liferayHomeDir, deployedFile.getName());
        Assert.assertTrue(deployedFile.exists());
    }

    @Test
    public void testDistBundle7z() throws Exception {
        _testDistBundle("7z");
    }

    @Test
    public void testDistBundleTar() throws Exception {
        _testDistBundle("tar.gz");
    }

    @Test
    public void testDistBundleZip() throws Exception {
        _testDistBundle("zip");
    }

    @Test
    public void testInitBundle7z() throws Exception {
        _testInitBundle(BundleSupportCommandsTest._getHttpServerUrl(BundleSupportCommandsTest._CONTEXT_PATH_7Z), BundleSupportCommandsTest._HTTP_SERVER_PASSWORD, BundleSupportCommandsTest._HTTP_SERVER_USER_NAME);
    }

    @Test
    public void testInitBundleTar() throws Exception {
        _testInitBundleTar(null, null, null, null, null, null, null);
    }

    @Test
    public void testInitBundleTarDifferentLocale() throws Exception {
        Locale locale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ITALY);
            _testInitBundleTar(null, null, null, null, null, null, null);
        } finally {
            Locale.setDefault(locale);
        }
    }

    @Test
    public void testInitBundleTarProxy() throws Exception {
        _testInitBundleTar("localhost", BundleSupportCommandsTest._HTTP_PROXY_SERVER_PORT, null, null, null, BundleSupportCommandsTest._httpProxyHit, Boolean.TRUE);
    }

    @Test
    public void testInitBundleTarProxyAuthenticated() throws Exception {
        _testInitBundleTar("localhost", BundleSupportCommandsTest._AUTHENTICATED_HTTP_PROXY_SERVER_PORT, BundleSupportCommandsTest._HTTP_PROXY_SERVER_USER_NAME, BundleSupportCommandsTest._HTTP_PROXY_SERVER_PASSWORD, null, BundleSupportCommandsTest._authenticatedHttpProxyHit, Boolean.TRUE);
    }

    @Test
    public void testInitBundleTarProxyNonproxyHosts() throws Exception {
        _testInitBundleTar("localhost", BundleSupportCommandsTest._HTTP_PROXY_SERVER_PORT, null, null, "localhost2.localdomain", BundleSupportCommandsTest._httpProxyHit, Boolean.TRUE);
    }

    @Test
    public void testInitBundleTarProxySkip() throws Exception {
        _testInitBundleTar("localhost", BundleSupportCommandsTest._HTTP_PROXY_SERVER_PORT, null, null, "localhost.localdomain", BundleSupportCommandsTest._httpProxyHit, Boolean.FALSE);
    }

    @Test
    public void testInitBundleTarProxyUnauthorized() throws Exception {
        expectedException.expectMessage("Proxy Authentication Required");
        _testInitBundleTar("localhost", BundleSupportCommandsTest._AUTHENTICATED_HTTP_PROXY_SERVER_PORT, null, null, null, BundleSupportCommandsTest._authenticatedHttpProxyHit, Boolean.TRUE);
    }

    @Test
    public void testInitBundleZip() throws Exception {
        _testInitBundle(BundleSupportCommandsTest._getHttpServerUrl(BundleSupportCommandsTest._CONTEXT_PATH_ZIP), BundleSupportCommandsTest._HTTP_SERVER_PASSWORD, BundleSupportCommandsTest._HTTP_SERVER_USER_NAME);
    }

    @Test
    public void testInitBundleZipFile() throws Exception {
        URI uri = BundleSupportCommandsTest._bundleZipFile.toURI();
        _testInitBundle(uri.toURL(), null, null);
    }

    @Test
    public void testInitBundleZipUnauthorized() throws Exception {
        expectedException.expectMessage("Unauthorized");
        _testInitBundle(BundleSupportCommandsTest._getHttpServerUrl(BundleSupportCommandsTest._CONTEXT_PATH_ZIP), null, null);
    }

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final int _AUTHENTICATED_HTTP_PROXY_SERVER_PORT;

    private static final String _CONTEXT_PATH_7Z = "/test.7z";

    private static final String _CONTEXT_PATH_TAR = "/test.tar.gz";

    private static final String _CONTEXT_PATH_TOKEN = "/token.json";

    private static final String _CONTEXT_PATH_TOKEN_UNFORMATTED = "/token_unformatted";

    private static final String _CONTEXT_PATH_ZIP = "/test.zip";

    private static final String _HTTP_PROXY_SERVER_PASSWORD = "proxyTest";

    private static final int _HTTP_PROXY_SERVER_PORT;

    private static final String _HTTP_PROXY_SERVER_REALM = "proxyTest";

    private static final String _HTTP_PROXY_SERVER_USER_NAME = "proxyTest";

    private static final String _HTTP_SERVER_PASSWORD = "test";

    private static final int _HTTP_SERVER_PORT;

    private static final String _HTTP_SERVER_REALM = "test";

    private static final String _HTTP_SERVER_USER_NAME = "test";

    private static final String _INIT_BUNDLE_ENVIRONMENT = "local";

    private static final int _INIT_BUNDLE_STRIP_COMPONENTS = 0;

    private static final int _TEST_PORT_RETRIES = 20;

    private static final AtomicBoolean _authenticatedHttpProxyHit = new AtomicBoolean();

    private static HttpProxyServer _authenticatedHttpProxyServer;

    private static File _bundleZipFile;

    private static final Set<PosixFilePermission> _expectedPosixFilePermissions = PosixFilePermissions.fromString("rwxr-x---");

    private static final AtomicBoolean _httpProxyHit = new AtomicBoolean();

    private static HttpProxyServer _httpProxyServer;

    private static HttpServer _httpServer;

    static {
        try {
            _AUTHENTICATED_HTTP_PROXY_SERVER_PORT = BundleSupportCommandsTest._getTestPort();
            _HTTP_PROXY_SERVER_PORT = BundleSupportCommandsTest._getTestPort(BundleSupportCommandsTest._AUTHENTICATED_HTTP_PROXY_SERVER_PORT);
            _HTTP_SERVER_PORT = BundleSupportCommandsTest._getTestPort(BundleSupportCommandsTest._AUTHENTICATED_HTTP_PROXY_SERVER_PORT, BundleSupportCommandsTest._HTTP_PROXY_SERVER_PORT);
        } catch (IOException ioe) {
            throw new ExceptionInInitializerError(ioe);
        }
    }
}


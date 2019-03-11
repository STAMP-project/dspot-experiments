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
package com.liferay.osgi.bundle.builder.commands;


import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author David Truong
 * @author Andrea Di Giorgi
 */
public class OSGiBundleBuilderCommandTest {
    @Test
    public void testExplodedJarCommand() throws Exception {
        ExplodedJarCommand explodedJarCommand = new ExplodedJarCommand();
        File actualDir = new File(_projectDir, "build/com.liferay.blade.authenticator.shiro");
        explodedJarCommand.build(_getOSGiBundleBuilderArgs(actualDir));
        Assert.assertTrue(actualDir.isDirectory());
        OSGiBundleBuilderCommandTest._compareJarDirs(_expectedDir, actualDir);
    }

    @Test
    public void testJarCommand() throws Exception {
        JarCommand jarCommand = new JarCommand();
        File jarFile = new File(_projectDir, "build/com.liferay.blade.authenticator.shiro.jar");
        jarCommand.build(_getOSGiBundleBuilderArgs(jarFile));
        Assert.assertTrue(jarFile.exists());
        File actualDir = temporaryFolder.newFolder("actual");
        OSGiBundleBuilderCommandTest._unzip(jarFile, actualDir);
        OSGiBundleBuilderCommandTest._compareJarDirs(_expectedDir, actualDir);
    }

    @Test
    public void testManifestCommand() throws Exception {
        ManifestCommand manifestCommand = new ManifestCommand();
        File actualFile = new File(_projectDir, "build/MANIFEST.MF");
        manifestCommand.build(_getOSGiBundleBuilderArgs(actualFile));
        Assert.assertTrue(actualFile.exists());
        OSGiBundleBuilderCommandTest._compareManifestFiles(new File(_expectedDir, "META-INF/MANIFEST.MF"), actualFile);
    }

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final Set<String> _ignoredJarDirNames = new HashSet<>(Arrays.asList("OSGI-INF", "OSGI-OPT"));

    private static final Set<String> _ignoredManifestAttributeNames = new HashSet<>(Arrays.asList("Bnd-LastModified", "Created-By", "Javac-Debug", "Javac-Deprecation", "Javac-Encoding"));

    private File _expectedDir;

    private File _projectDir;
}


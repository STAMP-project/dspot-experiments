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
package com.liferay.css.builder;


import com.liferay.css.builder.util.FileTestUtil;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Eduardo Garc?a
 * @author David Truong
 * @author Andrea Di Giorgi
 */
public abstract class BaseCSSBuilderTestCase {
    @Test
    public void testCSSBuilderOutputPath() throws Exception {
        Path outputDirPath = _baseDirPath.resolve("absolute");
        Files.createDirectories(outputDirPath);
        executeCSSBuilder(_baseDirPath, "/css", false, BaseCSSBuilderTestCase._importDirPath, String.valueOf(outputDirPath.toAbsolutePath()), 6, new String[0], "jni");
        File outputDir = outputDirPath.toFile();
        File[] files = outputDir.listFiles();
        Assert.assertTrue(((files.length) > 0));
    }

    @Test
    public void testCSSBuilderWithFragmentChange() throws Exception {
        Path fragmentChangePath = _baseDirPath.resolve("css/_import_change.scss");
        FileTestUtil.changeContentInPath(fragmentChangePath, "brown", "khaki");
        executeCSSBuilder(_baseDirPath, "/css", false, BaseCSSBuilderTestCase._importDirPath, ".sass-cache/", 6, new String[0], "jni");
        Path cssPath = _baseDirPath.resolve("css/.sass-cache/test_import_change.css");
        String css = FileTestUtil.read(cssPath);
        FileTestUtil.changeContentInPath(fragmentChangePath, "khaki", "brown");
        executeCSSBuilder(_baseDirPath, "/css", false, BaseCSSBuilderTestCase._importDirPath, ".sass-cache/", 6, new String[0], "jni");
        css = FileTestUtil.read(cssPath);
        Assert.assertTrue(css, css.contains("brown"));
    }

    @Test
    public void testCSSBuilderWithJni() throws Exception {
        _testCSSBuilder(BaseCSSBuilderTestCase._importDirPath, "jni");
    }

    @Test
    public void testCSSBuilderWithJniAndPortalCommonJar() throws Exception {
        _testCSSBuilder(BaseCSSBuilderTestCase._importJarPath, "jni");
    }

    @Test
    public void testCSSBuilderWithRuby() throws Exception {
        _testCSSBuilder(BaseCSSBuilderTestCase._importDirPath, "ruby");
    }

    @Test
    public void testCSSBuilderWithRubyAndPortalCommonJar() throws Exception {
        _testCSSBuilder(BaseCSSBuilderTestCase._importJarPath, "ruby");
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final Pattern _cssImportPattern = Pattern.compile("@import\\s+url\\s*\\(\\s*[\'\"]?(.+\\.css\\?t=\\d+)[\'\"]?\\s*\\)\\s*;");

    private static Path _dependenciesDirPath;

    private static Path _importDirPath;

    private static Path _importJarPath;

    private Path _baseDirPath;
}


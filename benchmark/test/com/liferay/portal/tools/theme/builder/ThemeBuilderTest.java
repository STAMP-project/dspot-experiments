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
package com.liferay.portal.tools.theme.builder;


import ThemeBuilder.STYLED;
import ThemeBuilder.UNSTYLED;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author David Truong
 * @author Andrea Di Giorgi
 */
public class ThemeBuilderTest {
    @Test
    public void testThemeBuilderClassic() throws Exception {
        buildTheme(ThemeBuilderTest._diffsDir, ThemeBuilderTest._NAME, temporaryFolder.getRoot(), ThemeBuilderTest._classicWarFile, "classic", "ftl", ThemeBuilderTest._unstyledJarFile);
        _assertEquals("css/_custom.scss", ".text { color: black; }");
        _assertNotExists("css/main.css");
        _assertNotEquals("css/_portal.scss", "");
        _assertExists("images/thumbnail.png");
        _assertExists("templates/init.ftl");
        _assertNotExists("templates/init.vm");
        _assertExists("WEB-INF/liferay-look-and-feel.xml");
    }

    @Test
    public void testThemeBuilderStyled() throws Exception {
        buildTheme(ThemeBuilderTest._diffsDir, ThemeBuilderTest._NAME, temporaryFolder.getRoot(), ThemeBuilderTest._styledJarFile, STYLED, "ftl", ThemeBuilderTest._unstyledJarFile);
        _assertEquals("css/_custom.scss", ".text { color: black; }");
        _assertNotEquals("css/_portal.scss", "");
        _assertExists("images/thumbnail.png");
        _assertExists("templates/init.ftl");
        _assertNotExists("templates/init.vm");
        _assertExists("WEB-INF/liferay-look-and-feel.xml");
    }

    @Test
    public void testThemeBuilderUnstyled() throws Exception {
        buildTheme(ThemeBuilderTest._diffsDir, ThemeBuilderTest._NAME, temporaryFolder.getRoot(), null, UNSTYLED, "ftl", ThemeBuilderTest._unstyledJarFile);
        _assertEquals("css/_custom.scss", ".text { color: black; }");
        _assertEquals("css/_portal.scss", "");
        _assertNotExists("images/thumbnail.png");
        _assertExists("templates/init.ftl");
        _assertNotExists("templates/init.vm");
        _assertExists("WEB-INF/liferay-look-and-feel.xml");
    }

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String _NAME = "Test Theme";

    private static File _classicWarFile;

    private static File _diffsDir;

    private static File _styledJarFile;

    private static File _unstyledJarFile;
}


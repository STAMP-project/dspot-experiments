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
package com.liferay.portal.tools.soy.builder.commands;


import com.liferay.portal.tools.soy.builder.util.FileTestUtil;
import java.io.File;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Andrea Di Giorgi
 */
public abstract class BaseSoyCommandTestCase {
    @Test
    public void testSoy() throws Exception {
        File dir = temporaryFolder.getRoot();
        testSoy(dir);
        Path dirPath = dir.toPath();
        ClassLoader classLoader = BaseSoyCommandTestCase.class.getClassLoader();
        String dirName = getTestDirName();
        for (String fileName : getTestExpectedFileNames()) {
            String content = FileTestUtil.read(dirPath.resolve(fileName));
            String expectedContent = FileTestUtil.read(classLoader, ((dirName + "expected/") + fileName));
            Assert.assertEquals(fixTestContent(expectedContent), fixTestContent(content));
        }
    }

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();
}


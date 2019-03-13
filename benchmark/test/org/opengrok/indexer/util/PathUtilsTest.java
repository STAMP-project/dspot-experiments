/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2017, Chris Fraire <cfraire@me.com>.
 * Copyright (c) 2008, 2018, Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.UnixPresent;


/**
 * Represents a container for tests of {@link PathUtils}.
 */
public class PathUtilsTest {
    private final List<File> tempDirs = new ArrayList<>();

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    @Test
    public void shouldHandleSameInputs() throws IOException {
        final String USR_BIN = Paths.get("/usr/bin").toString();
        String rel = PathUtils.getRelativeToCanonical(USR_BIN, USR_BIN);
        Assert.assertEquals((USR_BIN + " rel to itself"), "", rel);
    }

    @Test
    public void shouldHandleEffectivelySameInputs() throws IOException {
        String USR_BIN = Paths.get(Paths.get("/usr/bin").toUri()).toString();
        String rel = PathUtils.getRelativeToCanonical((USR_BIN + (File.separator)), USR_BIN);
        Assert.assertEquals((USR_BIN + " rel to ~itself"), "", rel);
    }

    @Test
    @ConditionalRun(UnixPresent.class)
    public void shouldHandleLinksOfArbitraryDepthWithValidation() throws IOException, ForbiddenSymlinkException {
        // Create real directories
        File sourceRoot = createTemporaryDirectory("srcroot");
        Assert.assertTrue("sourceRoot.isDirectory()", sourceRoot.isDirectory());
        File realDir1 = createTemporaryDirectory("realdir1");
        Assert.assertTrue("realDir1.isDirectory()", realDir1.isDirectory());
        File realDir1b = new File(realDir1, "b");
        realDir1b.mkdir();
        Assert.assertTrue("realDir1b.isDirectory()", realDir1b.isDirectory());
        File realDir2 = createTemporaryDirectory("realdir2");
        Assert.assertTrue("realDir2.isDirectory()", realDir2.isDirectory());
        // Create symlink #1 underneath source root.
        final String SYMLINK1 = "symlink1";
        File symlink1 = new File(sourceRoot, SYMLINK1);
        Files.createSymbolicLink(Paths.get(symlink1.getPath()), Paths.get(realDir1.getPath()));
        Assert.assertTrue("symlink1.exists()", symlink1.exists());
        // Create symlink #2 underneath realdir1/b.
        final String SYMLINK2 = "symlink2";
        File symlink2 = new File(realDir1b, SYMLINK2);
        Files.createSymbolicLink(Paths.get(symlink2.getPath()), Paths.get(realDir2.getPath()));
        Assert.assertTrue("symlink2.exists()", symlink2.exists());
        // Assert symbolic path
        Path sympath = Paths.get(sourceRoot.getPath(), SYMLINK1, "b", SYMLINK2);
        Assert.assertTrue("2-link path exists", Files.exists(sympath));
        // Test v. realDir1 canonical
        String realDir1Canon = realDir1.getCanonicalPath();
        String rel = PathUtils.getRelativeToCanonical(sympath.toString(), realDir1Canon);
        Assert.assertEquals("because links aren't validated", ("b/" + SYMLINK2), rel);
        // Test v. realDir1 canonical with validation and no allowed links
        Set<String> allowedSymLinks = new HashSet<>();
        ForbiddenSymlinkException expex = null;
        try {
            PathUtils.getRelativeToCanonical(sympath.toString(), realDir1Canon, allowedSymLinks);
        } catch (ForbiddenSymlinkException e) {
            expex = e;
        }
        Assert.assertNotNull("because no links allowed, arg1 is returned fully", expex);
        // Test v. realDir1 canonical with validation and an allowed link
        allowedSymLinks.add(symlink2.getPath());
        rel = PathUtils.getRelativeToCanonical(sympath.toString(), realDir1Canon, allowedSymLinks);
        Assert.assertEquals("because link is OKed", ("b/" + SYMLINK2), rel);
    }
}


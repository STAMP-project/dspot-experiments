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
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.index;


import IndexVersion.IndexVersionException;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.util.FileUtilities;
import org.opengrok.indexer.util.TestRepository;


/**
 * Verify index version check.
 *
 * @author Vladimir Kotal
 */
public class IndexVersionTest {
    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    private TestRepository repository;

    private RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    private Path oldIndexDataDir;

    @Test
    public void testIndexVersionNoIndex() throws Exception {
        IndexVersion.check(new ArrayList());
    }

    @Test
    @ConditionalRun(CtagsInstalled.class)
    public void testIndexVersionProjects() throws Exception {
        testIndexVersion(true, new ArrayList<>());
    }

    @Test
    @ConditionalRun(CtagsInstalled.class)
    public void testIndexVersionSelectedProjects() throws Exception {
        testIndexVersion(true, Arrays.asList(new String[]{ "mercurial", "git" }));
    }

    @Test
    @ConditionalRun(CtagsInstalled.class)
    public void testIndexVersionNoProjects() throws Exception {
        testIndexVersion(false, new ArrayList<>());
    }

    @Test(expected = IndexVersionException.class)
    public void testIndexVersionOldIndex() throws Exception {
        oldIndexDataDir = Files.createTempDirectory("data");
        Path indexPath = oldIndexDataDir.resolve("index");
        Files.createDirectory(indexPath);
        File indexDir = new File(indexPath.toString());
        Assert.assertTrue("index directory check", indexDir.isDirectory());
        URL oldindex = getClass().getResource("/index/oldindex.zip");
        Assert.assertNotNull("resource needs to be non null", oldindex);
        File archive = new File(oldindex.getPath());
        Assert.assertTrue("archive exists", archive.isFile());
        FileUtilities.extractArchive(archive, indexDir);
        env.setDataRoot(oldIndexDataDir.toString());
        env.setProjectsEnabled(false);
        IndexVersion.check(new ArrayList());
    }
}


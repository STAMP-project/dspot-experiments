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
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.web.api.v1.controller;


import RepositoryInstalled.GitInstalled;
import RepositoryInstalled.MercurialInstalled;
import java.io.File;
import java.nio.file.Paths;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.history.MercurialRepositoryTest;
import org.opengrok.indexer.index.Indexer;
import org.opengrok.indexer.util.TestRepository;


@ConditionalRun(MercurialInstalled.class)
@ConditionalRun(GitInstalled.class)
@ConditionalRun(CtagsInstalled.class)
public class RepositoriesControllerTest extends JerseyTest {
    private RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    private TestRepository repository;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    @Test
    public void testGetRepositoryTypeOfNonExistenRepository() throws Exception {
        Assert.assertEquals(((Paths.get("/totally-nonexistent-repository").toString()) + ":N/A"), getRepoType(Paths.get("/totally-nonexistent-repository").toString()));
    }

    @Test
    public void testGetRepositoryType() throws Exception {
        // Create sub-repository.
        File mercurialRoot = new File((((repository.getSourceRoot()) + (File.separator)) + "mercurial"));
        MercurialRepositoryTest.runHgCommand(mercurialRoot, "clone", mercurialRoot.getAbsolutePath(), (((mercurialRoot.getAbsolutePath()) + (File.separator)) + "closed"));
        env.setHistoryEnabled(true);
        // search for repositories
        // scan and add projects
        // don't create dictionary
        // subFiles - needed when refreshing history partially
        Indexer.getInstance().prepareIndexer(env, true, true, false, null, null);// repositories - needed when refreshing history partially

        Assert.assertEquals(((Paths.get("/mercurial").toString()) + ":Mercurial"), getRepoType(Paths.get("/mercurial").toString()));
        Assert.assertEquals(((Paths.get("/mercurial/closed").toString()) + ":Mercurial"), getRepoType(Paths.get("/mercurial/closed").toString()));
        Assert.assertEquals(((Paths.get("/git").toString()) + ":git"), getRepoType(Paths.get("/git").toString()));
    }
}


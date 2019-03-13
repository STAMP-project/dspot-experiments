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
 * Copyright (c) 2008, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.analysis.executables;


import java.io.File;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.CtagsInstalled;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.search.SearchEngine;
import org.opengrok.indexer.util.TestRepository;


/**
 * Represents a container for tests of {@link JarAnalyzer} and
 * {@link SearchEngine}.
 * <p>
 * Derived from Trond Norbye's {@code SearchEngineTest}
 */
@ConditionalRun(CtagsInstalled.class)
public class JarAnalyzerTest {
    private static final String TESTPLUGINS_JAR = "testplugins.jar";

    private static RuntimeEnvironment env;

    private static TestRepository repository;

    private static File configFile;

    private static boolean originalProjectsEnabled;

    @ClassRule
    public static ConditionalRunRule rule = new ConditionalRunRule();

    @Test
    public void testSearchForJar() {
        SearchEngine instance = new SearchEngine();
        instance.setFile(JarAnalyzerTest.TESTPLUGINS_JAR);
        int noHits = instance.search();
        Assert.assertTrue((("noHits for " + (JarAnalyzerTest.TESTPLUGINS_JAR)) + " should be positive"), (noHits > 0));
        instance.destroy();
    }
}


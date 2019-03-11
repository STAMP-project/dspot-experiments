/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.deploy;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.deploy.graph.GraphOutputDot;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


/**
 * Just an overly picky test case to validate the potential paths.
 */
@ExtendWith(WorkDirExtension.class)
public class AppLifeCycleTest {
    public WorkDir testdir;

    @Test
    public void testFindPath_Deployed_Deployed() {
        assertNoPath("deployed", "deployed");
    }

    @Test
    public void testFindPath_Deployed_Started() {
        List<String> expected = new ArrayList<String>();
        expected.add("deployed");
        expected.add("starting");
        expected.add("started");
        assertPath("deployed", "started", expected);
    }

    @Test
    public void testFindPath_Deployed_Undeployed() {
        List<String> expected = new ArrayList<String>();
        expected.add("deployed");
        expected.add("undeploying");
        expected.add("undeployed");
        assertPath("deployed", "undeployed", expected);
    }

    @Test
    public void testFindPath_Started_Deployed() {
        List<String> expected = new ArrayList<String>();
        expected.add("started");
        expected.add("stopping");
        expected.add("deployed");
        assertPath("started", "deployed", expected);
    }

    @Test
    public void testFindPath_Started_Started() {
        assertNoPath("started", "started");
    }

    @Test
    public void testFindPath_Started_Undeployed() {
        List<String> expected = new ArrayList<String>();
        expected.add("started");
        expected.add("stopping");
        expected.add("deployed");
        expected.add("undeploying");
        expected.add("undeployed");
        assertPath("started", "undeployed", expected);
    }

    @Test
    public void testFindPath_Undeployed_Deployed() {
        List<String> expected = new ArrayList<String>();
        expected.add("undeployed");
        expected.add("deploying");
        expected.add("deployed");
        assertPath("undeployed", "deployed", expected);
    }

    @Test
    public void testFindPath_Undeployed_Started() {
        List<String> expected = new ArrayList<String>();
        expected.add("undeployed");
        expected.add("deploying");
        expected.add("deployed");
        expected.add("starting");
        expected.add("started");
        assertPath("undeployed", "started", expected);
    }

    @Test
    public void testFindPath_Undeployed_Uavailable() {
        assertNoPath("undeployed", "undeployed");
    }

    /**
     * Request multiple lifecycle paths with a single lifecycle instance. Just to ensure that there is no state
     * maintained between {@link AppLifeCycle#getPath(Node, Node)} requests.
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testFindPathMultiple() throws IOException {
        AppLifeCycle lifecycle = new AppLifeCycle();
        List<String> expected = new ArrayList<String>();
        File outputDir = testdir.getEmptyPathDir().toFile();
        // Modify graph to add new 'staging' -> 'staged' between 'deployed' and 'started'
        GraphOutputDot.write(lifecycle, new File(outputDir, "multiple-1.dot"));// before change

        lifecycle.insertNode(lifecycle.getPath("deployed", "started").getEdge(0), "staging");
        GraphOutputDot.write(lifecycle, new File(outputDir, "multiple-2.dot"));// after first change

        lifecycle.insertNode(lifecycle.getPath("staging", "started").getEdge(0), "staged");
        GraphOutputDot.write(lifecycle, new File(outputDir, "multiple-3.dot"));// after second change

        // Deployed -> Deployed
        expected.clear();
        assertPath(lifecycle, "deployed", "deployed", expected);
        // Deployed -> Staged
        expected.clear();
        expected.add("deployed");
        expected.add("staging");
        expected.add("staged");
        assertPath(lifecycle, "deployed", "staged", expected);
        // Staged -> Undeployed
        expected.clear();
        expected.add("staged");
        expected.add("starting");
        expected.add("started");
        expected.add("stopping");
        expected.add("deployed");
        expected.add("undeploying");
        expected.add("undeployed");
        assertPath(lifecycle, "staged", "undeployed", expected);
        // Undeployed -> Started
        expected.clear();
        expected.add("undeployed");
        expected.add("deploying");
        expected.add("deployed");
        expected.add("staging");
        expected.add("staged");
        expected.add("starting");
        expected.add("started");
        assertPath(lifecycle, "undeployed", "started", expected);
    }
}


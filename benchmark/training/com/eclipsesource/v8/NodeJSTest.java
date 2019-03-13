/**
 * *****************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 * ****************************************************************************
 */
package com.eclipsesource.v8;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class NodeJSTest {
    private NodeJS nodeJS;

    private static final String skipMessage = "Skipped test (Node.js features not included in native library)";

    @Test
    public void testCreateNodeJS() {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        Assert.assertNotNull(nodeJS);
    }

    @Test
    public void testSingleThreadAccess_Require() throws InterruptedException {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        final boolean[] result = new boolean[]{ false };
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nodeJS.require(File.createTempFile("temp", ".js"));
                } catch (Error e) {
                    result[0] = e.getMessage().contains("Invalid V8 thread access");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
        t.join();
        Assert.assertTrue(result[0]);
    }

    @Test
    public void testGetVersion() {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        String result = nodeJS.getNodeVersion();
        Assert.assertEquals("7.4.0", result);
    }

    @Test
    public void testSingleThreadAccess_HandleMessage() throws InterruptedException {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        final boolean[] result = new boolean[]{ false };
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nodeJS.handleMessage();
                } catch (Error e) {
                    result[0] = e.getMessage().contains("Invalid V8 thread access");
                }
            }
        });
        t.start();
        t.join();
        Assert.assertTrue(result[0]);
    }

    @Test
    public void testSingleThreadAccess_IsRunning() throws InterruptedException {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        final boolean[] result = new boolean[]{ false };
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nodeJS.isRunning();
                } catch (Error e) {
                    result[0] = e.getMessage().contains("Invalid V8 thread access");
                }
            }
        });
        t.start();
        t.join();
        Assert.assertTrue(result[0]);
    }

    @Test
    public void testExecuteNodeScript_Startup() throws IOException {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        nodeJS.release();
        File testScript = NodeJSTest.createTemporaryScriptFile("global.passed = true;", "testScript");
        nodeJS = NodeJS.createNodeJS(testScript);
        runMessageLoop();
        Assert.assertEquals(true, nodeJS.getRuntime().getBoolean("passed"));
        testScript.delete();
    }

    @Test
    public void testExecNodeScript() throws IOException {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        nodeJS.release();
        File testScript = NodeJSTest.createTemporaryScriptFile("global.passed = true;", "testScript");
        nodeJS = NodeJS.createNodeJS();
        nodeJS.exec(testScript);
        runMessageLoop();
        Assert.assertEquals(true, nodeJS.getRuntime().getBoolean("passed"));
        testScript.delete();
    }

    @Test
    public void testExecuteNodeScript_viaRequire() throws IOException {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        nodeJS.release();
        File testScript = NodeJSTest.createTemporaryScriptFile("global.passed = true;", "testScript");
        nodeJS = NodeJS.createNodeJS();
        nodeJS.require(testScript).close();
        runMessageLoop();
        Assert.assertEquals(true, nodeJS.getRuntime().getBoolean("passed"));
        testScript.delete();
    }

    @Test
    public void testExports() throws IOException {
        Assume.assumeFalse(NodeJSTest.skipMessage, NodeJSTest.skipTest());// conditional skip

        nodeJS.release();
        File testScript = NodeJSTest.createTemporaryScriptFile("exports.foo=7", "testScript");
        nodeJS = NodeJS.createNodeJS();
        V8Object exports = nodeJS.require(testScript);
        runMessageLoop();
        Assert.assertEquals(7, exports.getInteger("foo"));
        exports.close();
    }
}


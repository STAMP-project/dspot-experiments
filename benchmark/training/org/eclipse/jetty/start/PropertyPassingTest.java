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
package org.eclipse.jetty.start;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.toolchain.test.IO;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(WorkDirExtension.class)
public class PropertyPassingTest {
    private static class ConsoleCapture implements Runnable {
        private String mode;

        private BufferedReader reader;

        private StringWriter output;

        private CountDownLatch latch = new CountDownLatch(1);

        public ConsoleCapture(String mode, InputStream is) {
            this.mode = mode;
            this.reader = new BufferedReader(new InputStreamReader(is));
            this.output = new StringWriter();
        }

        @Override
        public void run() {
            String line;
            try (PrintWriter out = new PrintWriter(output)) {
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                    out.flush();
                } 
            } catch (IOException ignore) {
                /* ignore */
            } finally {
                IO.close(reader);
                latch.countDown();
            }
        }

        public String getConsoleOutput() throws InterruptedException {
            latch.await(30, TimeUnit.SECONDS);
            return output.toString();
        }

        public PropertyPassingTest.ConsoleCapture start() {
            Thread thread = new Thread(this, ("ConsoleCapture/" + (mode)));
            thread.start();
            return this;
        }
    }

    public WorkDir testingdir;

    @Test
    public void testAsJvmArg() throws IOException, InterruptedException {
        File bogusXml = MavenTestingUtils.getTestResourceFile("bogus.xml");
        // Setup command line
        List<String> commands = new ArrayList<>();
        commands.add(getJavaBin());
        commands.add(("-Dmain.class=" + (PropertyDump.class.getName())));
        commands.add("-cp");
        commands.add(getClassPath());
        // addDebug(commands);
        commands.add("-Dtest.foo=bar");// TESTING THIS

        commands.add(getStartJarBin());
        commands.add(bogusXml.getAbsolutePath());
        // Run command, collect output
        String output = collectRunOutput(commands);
        // Test for values
        MatcherAssert.assertThat("output", output, Matchers.containsString("foo=bar"));
    }

    @Test
    public void testAsCommandLineArg() throws IOException, InterruptedException {
        File bogusXml = MavenTestingUtils.getTestResourceFile("bogus.xml");
        // Setup command line
        List<String> commands = new ArrayList<>();
        commands.add(getJavaBin());
        commands.add(("-Dmain.class=" + (PropertyDump.class.getName())));
        commands.add("-cp");
        commands.add(getClassPath());
        // addDebug(commands);
        commands.add(getStartJarBin());
        commands.add("test.foo=bar");// TESTING THIS

        commands.add(bogusXml.getAbsolutePath());
        // Run command, collect output
        String output = collectRunOutput(commands);
        // Test for values
        MatcherAssert.assertThat("output", output, Matchers.containsString("foo=bar"));
    }

    @Test
    public void testAsDashDCommandLineArg() throws IOException, InterruptedException {
        File bogusXml = MavenTestingUtils.getTestResourceFile("bogus.xml");
        // Setup command line
        List<String> commands = new ArrayList<>();
        commands.add(getJavaBin());
        commands.add(("-Dmain.class=" + (PropertyDump.class.getName())));
        commands.add("-cp");
        commands.add(getClassPath());
        // addDebug(commands);
        commands.add(getStartJarBin());
        commands.add("-Dtest.foo=bar");// TESTING THIS

        commands.add(bogusXml.getAbsolutePath());
        // Run command, collect output
        String output = collectRunOutput(commands);
        // Test for values
        MatcherAssert.assertThat(output, Matchers.containsString("test.foo=bar"));
    }
}


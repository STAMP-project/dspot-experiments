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
package org.eclipse.jetty.server;


import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import org.eclipse.jetty.util.component.Dumpable;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ClassLoaderDumpTest {
    @Test
    public void testSimple() throws Exception {
        Server server = new Server();
        ClassLoader loader = new ClassLoader() {
            public String toString() {
                return "SimpleLoader";
            }
        };
        server.addBean(new ClassLoaderDump(loader));
        StringBuilder out = new StringBuilder();
        server.dump(out);
        String dump = out.toString();
        MatcherAssert.assertThat(dump, Matchers.containsString("+- SimpleLoader"));
        MatcherAssert.assertThat(dump, Matchers.containsString(("+> " + (Server.class.getClassLoader()))));
    }

    @Test
    public void testParent() throws Exception {
        Server server = new Server();
        ClassLoader loader = new ClassLoader(Server.class.getClassLoader()) {
            public String toString() {
                return "ParentedLoader";
            }
        };
        server.addBean(new ClassLoaderDump(loader));
        StringBuilder out = new StringBuilder();
        server.dump(out);
        String dump = out.toString();
        MatcherAssert.assertThat(dump, Matchers.containsString("+- ParentedLoader"));
        MatcherAssert.assertThat(dump, Matchers.containsString(("|  +> " + (Server.class.getClassLoader()))));
        MatcherAssert.assertThat(dump, Matchers.containsString(("+> " + (Server.class.getClassLoader()))));
    }

    @Test
    public void testNested() throws Exception {
        Server server = new Server();
        ClassLoader middleLoader = new ClassLoader(Server.class.getClassLoader()) {
            public String toString() {
                return "MiddleLoader";
            }
        };
        ClassLoader loader = new ClassLoader(middleLoader) {
            public String toString() {
                return "TopLoader";
            }
        };
        server.addBean(new ClassLoaderDump(loader));
        StringBuilder out = new StringBuilder();
        server.dump(out);
        String dump = out.toString();
        MatcherAssert.assertThat(dump, Matchers.containsString("+- TopLoader"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|  +> MiddleLoader"));
        MatcherAssert.assertThat(dump, Matchers.containsString(("|     +> " + (Server.class.getClassLoader()))));
        MatcherAssert.assertThat(dump, Matchers.containsString(("+> " + (Server.class.getClassLoader()))));
    }

    @Test
    public void testDumpable() throws Exception {
        Server server = new Server();
        ClassLoader middleLoader = new ClassLoaderDumpTest.DumpableClassLoader(Server.class.getClassLoader());
        ClassLoader loader = new ClassLoader(middleLoader) {
            public String toString() {
                return "TopLoader";
            }
        };
        server.addBean(new ClassLoaderDump(loader));
        StringBuilder out = new StringBuilder();
        server.dump(out);
        String dump = out.toString();
        MatcherAssert.assertThat(dump, Matchers.containsString("+- TopLoader"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|  +> DumpableClassLoader"));
        MatcherAssert.assertThat(dump, Matchers.not(Matchers.containsString(("|    +> " + (Server.class.getClassLoader())))));
        MatcherAssert.assertThat(dump, Matchers.containsString(("+> " + (Server.class.getClassLoader()))));
    }

    public static class DumpableClassLoader extends ClassLoader implements Dumpable {
        public DumpableClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public String dump() {
            return "DumpableClassLoader";
        }

        @Override
        public void dump(Appendable out, String indent) throws IOException {
            out.append(dump()).append('\n');
        }

        public String toString() {
            return "DumpableClassLoader";
        }
    }

    @Test
    public void testUrlClassLoaders() throws Exception {
        Server server = new Server();
        ClassLoader middleLoader = new URLClassLoader(new URL[]{ new URL("file:/one"), new URL("file:/two"), new URL("file:/three") }, Server.class.getClassLoader()) {
            public String toString() {
                return "MiddleLoader";
            }
        };
        ClassLoader loader = new URLClassLoader(new URL[]{ new URL("file:/ONE"), new URL("file:/TWO"), new URL("file:/THREE") }, middleLoader) {
            public String toString() {
                return "TopLoader";
            }
        };
        server.addBean(new ClassLoaderDump(loader));
        StringBuilder out = new StringBuilder();
        server.dump(out);
        String dump = out.toString();
        // System.err.println(dump);
        MatcherAssert.assertThat(dump, Matchers.containsString("+- TopLoader"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|  |  +> file:/ONE"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|  |  +> file:/TWO"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|  |  +> file:/THREE"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|  +> MiddleLoader"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|     |  +> file:/one"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|     |  +> file:/two"));
        MatcherAssert.assertThat(dump, Matchers.containsString("|     |  +> file:/three"));
        MatcherAssert.assertThat(dump, Matchers.containsString(("|     +> " + (Server.class.getClassLoader()))));
        MatcherAssert.assertThat(dump, Matchers.containsString(("+> " + (Server.class.getClassLoader()))));
    }
}


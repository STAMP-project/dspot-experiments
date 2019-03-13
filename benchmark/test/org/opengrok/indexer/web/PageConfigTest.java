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
 * Copyright (c) 2011, 2018, Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.web;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.opengrok.indexer.authorization.AuthControlFlag;
import org.opengrok.indexer.authorization.AuthorizationFramework;
import org.opengrok.indexer.authorization.TestPlugin;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.condition.RepositoryInstalled;
import org.opengrok.indexer.condition.UnixPresent;
import org.opengrok.indexer.configuration.Project;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.history.Annotation;
import org.opengrok.indexer.util.TestRepository;


/**
 * Unit tests for the {@code PageConfig}?class.
 */
public class PageConfigTest {
    private static TestRepository repository = new TestRepository();

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    @Test
    public void testRequestAttributes() {
        HttpServletRequest req = new DummyHttpServletRequest();
        PageConfig cfg = PageConfig.get(req);
        String[] attrs = new String[]{ "a", "b", "c", "d" };
        Object[] values = new Object[]{ "some object", new DummyHttpServletRequest(), 1, this };
        Assert.assertEquals(attrs.length, values.length);
        for (int i = 0; i < (attrs.length); i++) {
            cfg.setRequestAttribute(attrs[i], values[i]);
            Object attribute = req.getAttribute(attrs[i]);
            Assert.assertNotNull(attribute);
            Assert.assertEquals(values[i], attribute);
            attribute = cfg.getRequestAttribute(attrs[i]);
            Assert.assertNotNull(attribute);
            Assert.assertEquals(values[i], attribute);
        }
    }

    @Test
    @ConditionalRun(RepositoryInstalled.MercurialInstalled.class)
    public void canProcessHistory() {
        // Expect no redirection (that is, empty string is returned) for a
        // file that exists.
        assertCanProcess("", "/source", "/history", "/mercurial/main.c");
        // Expect directories without trailing slash to get a trailing slash
        // appended.
        assertCanProcess("/source/history/mercurial/", "/source", "/history", "/mercurial");
        // Expect no redirection (that is, empty string is returned) if the
        // directories already have a trailing slash.
        assertCanProcess("", "/source", "/history", "/mercurial/");
        // Expect null if the file or directory doesn't exist.
        assertCanProcess(null, "/source", "/history", "/mercurial/xyz");
        assertCanProcess(null, "/source", "/history", "/mercurial/xyz/");
    }

    @Test
    public void canProcessXref() {
        // Expect no redirection (that is, empty string is returned) for a
        // file that exists.
        assertCanProcess("", "/source", "/xref", "/mercurial/main.c");
        // Expect directories without trailing slash to get a trailing slash
        // appended.
        assertCanProcess("/source/xref/mercurial/", "/source", "/xref", "/mercurial");
        // Expect no redirection (that is, empty string is returned) if the
        // directories already have a trailing slash.
        assertCanProcess("", "/source", "/xref", "/mercurial/");
        // Expect null if the file or directory doesn't exist.
        assertCanProcess(null, "/source", "/xref", "/mercurial/xyz");
        assertCanProcess(null, "/source", "/xref", "/mercurial/xyz/");
    }

    /**
     * Testing the root of /xref for authorization filtering.
     */
    @Test
    public void testGetResourceFileList() {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        // backup original values
        String oldSourceRootPath = env.getSourceRootPath();
        AuthorizationFramework oldAuthorizationFramework = env.getAuthorizationFramework();
        Map<String, Project> oldProjects = env.getProjects();
        // Set up the source root directory containing some projects.
        env.setSourceRoot(PageConfigTest.repository.getSourceRoot());
        env.setProjectsEnabled(true);
        // Enable projects.
        for (String file : new File(PageConfigTest.repository.getSourceRoot()).list()) {
            Project proj = new Project(file);
            proj.setIndexed(true);
            env.getProjects().put(file, proj);
        }
        HttpServletRequest req = PageConfigTest.createRequest("/source", "/xref", "");
        PageConfig cfg = PageConfig.get(req);
        List<String> allFiles = new java.util.ArrayList(cfg.getResourceFileList());
        /**
         * Check if there are some files (the "5" here is just a sufficient
         * value for now which won't break any future repository tests) without
         * any authorization.
         */
        Assert.assertTrue(((allFiles.size()) > 5));
        Assert.assertTrue(allFiles.contains("git"));
        Assert.assertTrue(allFiles.contains("mercurial"));
        /**
         * Now set up the same projects with authorization plugin enabling only
         * some of them.
         * <pre>
         *  - disabling "git"
         *  - disabling "mercurial"
         * </pre>
         */
        env.setAuthorizationFramework(new AuthorizationFramework());
        env.getAuthorizationFramework().reload();
        env.getAuthorizationFramework().getStack().add(new org.opengrok.indexer.authorization.AuthorizationPlugin(AuthControlFlag.REQUIRED, new TestPlugin() {
            @Override
            public boolean isAllowed(HttpServletRequest request, Project project) {
                return (!(project.getName().startsWith("git"))) && (!(project.getName().startsWith("mercurial")));
            }
        }));
        req = PageConfigTest.createRequest("/source", "/xref", "");
        cfg = PageConfig.get(req);
        List<String> filteredFiles = new java.util.ArrayList(cfg.getResourceFileList());
        // list subtraction - retains only disabled files
        allFiles.removeAll(filteredFiles);
        Assert.assertEquals(2, allFiles.size());
        Assert.assertTrue(allFiles.contains("git"));
        Assert.assertTrue(allFiles.contains("mercurial"));
        // restore original values
        env.setAuthorizationFramework(oldAuthorizationFramework);
        env.setSourceRoot(oldSourceRootPath);
        env.setProjects(oldProjects);
    }

    @Test
    public void testGetIntParam() {
        String[] attrs = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h" };
        int[] values = new int[]{ 1, 100, -1, 2, 200, 3000, -200, 3000 };
        DummyHttpServletRequest req = new DummyHttpServletRequest() {
            @Override
            public String getParameter(String name) {
                switch (name) {
                    case "a" :
                        return "1";
                    case "b" :
                        return "100";
                    case "c" :
                        return null;
                    case "d" :
                        return "2";
                    case "e" :
                        return "200";
                    case "f" :
                        return "3000";
                    case "g" :
                        return null;
                    case "h" :
                        return "abcdef";
                }
                return null;
            }
        };
        PageConfig cfg = PageConfig.get(req);
        Assert.assertEquals(attrs.length, values.length);
        for (int i = 0; i < (attrs.length); i++) {
            Assert.assertEquals(values[i], cfg.getIntParam(attrs[i], values[i]));
        }
    }

    @Test
    @ConditionalRun(RepositoryInstalled.GitInstalled.class)
    public void testGetLatestRevisionValid() {
        DummyHttpServletRequest req1 = new DummyHttpServletRequest() {
            @Override
            public String getPathInfo() {
                return "/git/main.c";
            }
        };
        PageConfig cfg = PageConfig.get(req1);
        String rev = cfg.getLatestRevision();
        Assert.assertEquals("aa35c258", rev);
    }

    @Test
    @ConditionalRun(RepositoryInstalled.GitInstalled.class)
    public void testGetRevisionLocation() {
        DummyHttpServletRequest req1 = new DummyHttpServletRequest() {
            @Override
            public String getPathInfo() {
                return "/git/main.c";
            }

            @Override
            public String getContextPath() {
                return "source";
            }

            @Override
            public String getQueryString() {
                return "a=true";
            }
        };
        PageConfig cfg = PageConfig.get(req1);
        String location = cfg.getRevisionLocation(cfg.getLatestRevision());
        Assert.assertNotNull(location);
        Assert.assertEquals("source/xref/git/main.c?r=aa35c258&a=true", location);
    }

    @Test
    @ConditionalRun(RepositoryInstalled.GitInstalled.class)
    public void testGetRevisionLocationNullQuery() {
        DummyHttpServletRequest req1 = new DummyHttpServletRequest() {
            @Override
            public String getPathInfo() {
                return "/git/main.c";
            }

            @Override
            public String getContextPath() {
                return "source";
            }

            @Override
            public String getQueryString() {
                return null;
            }
        };
        PageConfig cfg = PageConfig.get(req1);
        String location = cfg.getRevisionLocation(cfg.getLatestRevision());
        Assert.assertNotNull(location);
        Assert.assertEquals("source/xref/git/main.c?r=aa35c258", location);
    }

    @Test
    @ConditionalRun(RepositoryInstalled.GitInstalled.class)
    public void testGetLatestRevisionNotValid() {
        DummyHttpServletRequest req2 = new DummyHttpServletRequest() {
            @Override
            public String getPathInfo() {
                return "/git/nonexistent_file";
            }
        };
        PageConfig cfg = PageConfig.get(req2);
        String rev = cfg.getLatestRevision();
        Assert.assertNull(rev);
        String location = cfg.getRevisionLocation(cfg.getLatestRevision());
        Assert.assertNull(location);
    }

    @Test
    public void testGetRequestedRevision() {
        final String[] params = new String[]{ "r", "h", "r", "r", "r" };
        final String[] revisions = new String[]{ "6c5588de", "", "6c5588de", "6c5588de", "6c5588de" };
        Assert.assertEquals(params.length, revisions.length);
        for (int i = 0; i < (revisions.length); i++) {
            final int index = i;
            DummyHttpServletRequest req = new DummyHttpServletRequest() {
                @Override
                public String getParameter(String name) {
                    if (name.equals("r")) {
                        return revisions[index];
                    }
                    return null;
                }
            };
            PageConfig cfg = PageConfig.get(req);
            String rev = cfg.getRequestedRevision();
            Assert.assertNotNull(rev);
            Assert.assertEquals(revisions[i], rev);
            Assert.assertFalse(rev.contains("r="));
            PageConfig.cleanup(req);
        }
    }

    @Test
    @ConditionalRun(RepositoryInstalled.GitInstalled.class)
    public void testGetAnnotation() {
        final String[] revisions = new String[]{ "aa35c258", "bb74b7e8" };
        for (int i = 0; i < (revisions.length); i++) {
            final int index = i;
            HttpServletRequest req = new DummyHttpServletRequest() {
                @Override
                public String getContextPath() {
                    return "/source";
                }

                @Override
                public String getServletPath() {
                    return "/history";
                }

                @Override
                public String getPathInfo() {
                    return "/git/main.c";
                }

                @Override
                public String getParameter(String name) {
                    switch (name) {
                        case "r" :
                            return revisions[index];
                        case "a" :
                            return "true";
                    }
                    return null;
                }
            };
            PageConfig cfg = PageConfig.get(req);
            Annotation annotation = cfg.getAnnotation();
            Assert.assertNotNull(annotation);
            Assert.assertEquals("main.c", annotation.getFilename());
            Assert.assertEquals(((revisions.length) - i), annotation.getFileVersionsCount());
            for (int j = 1; j <= (annotation.size()); j++) {
                String tmp = annotation.getRevision(j);
                Assert.assertTrue(Arrays.asList(revisions).contains(tmp));
            }
            Assert.assertEquals("The version should be reflected through the revision", ((revisions.length) - i), annotation.getFileVersion(revisions[i]));
            PageConfig.cleanup(req);
        }
    }

    /**
     * Test the case when the source root is null
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test(expected = FileNotFoundException.class)
    public void testCheckSourceRootExistence1() throws IOException {
        HttpServletRequest req = new DummyHttpServletRequest();
        PageConfig cfg = PageConfig.get(req);
        String path = RuntimeEnvironment.getInstance().getSourceRootPath();
        System.out.println(path);
        RuntimeEnvironment.getInstance().setSourceRoot(null);
        try {
            cfg.checkSourceRootExistence();
        } finally {
            RuntimeEnvironment.getInstance().setSourceRoot(path);
            PageConfig.cleanup(req);
        }
    }

    /**
     * Test the case when source root is empty
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test(expected = FileNotFoundException.class)
    public void testCheckSourceRootExistence2() throws IOException {
        HttpServletRequest req = new DummyHttpServletRequest();
        PageConfig cfg = PageConfig.get(req);
        String path = RuntimeEnvironment.getInstance().getSourceRootPath();
        RuntimeEnvironment.getInstance().setSourceRoot("");
        try {
            cfg.checkSourceRootExistence();
        } finally {
            RuntimeEnvironment.getInstance().setSourceRoot(path);
            PageConfig.cleanup(req);
        }
    }

    /**
     * Test the case when source root does not exist
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test
    public void testCheckSourceRootExistence3() throws IOException {
        HttpServletRequest req = new DummyHttpServletRequest();
        PageConfig cfg = PageConfig.get(req);
        String path = RuntimeEnvironment.getInstance().getSourceRootPath();
        File temp = File.createTempFile("opengrok", "-test-file.tmp");
        Files.delete(temp.toPath());
        RuntimeEnvironment.getInstance().setSourceRoot(temp.getAbsolutePath());
        try {
            cfg.checkSourceRootExistence();
            Assert.fail("This should throw an exception when the file does not exist");
        } catch (IOException ex) {
        }
        RuntimeEnvironment.getInstance().setSourceRoot(path);
        PageConfig.cleanup(req);
    }

    /**
     * Test the case when source root can not be read
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test
    @ConditionalRun(UnixPresent.class)
    public void testCheckSourceRootExistence4() throws IOException {
        HttpServletRequest req = new DummyHttpServletRequest();
        PageConfig cfg = PageConfig.get(req);
        String path = RuntimeEnvironment.getInstance().getSourceRootPath();
        File temp = File.createTempFile("opengrok", "-test-file.tmp");
        Files.delete(temp.toPath());
        Files.createDirectories(temp.toPath());
        // skip the test if the implementation does not permit setting permissions
        Assume.assumeTrue(temp.setReadable(false));
        RuntimeEnvironment.getInstance().setSourceRoot(temp.getAbsolutePath());
        try {
            cfg.checkSourceRootExistence();
            Assert.fail("This should throw an exception when the file is not readable");
        } catch (IOException ex) {
        }
        RuntimeEnvironment.getInstance().setSourceRoot(path);
        PageConfig.cleanup(req);
        temp.deleteOnExit();
    }

    /**
     * Test a successful check
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test
    public void testCheckSourceRootExistence5() throws IOException {
        HttpServletRequest req = new DummyHttpServletRequest();
        PageConfig cfg = PageConfig.get(req);
        String path = RuntimeEnvironment.getInstance().getSourceRootPath();
        File temp = File.createTempFile("opengrok", "-test-file.tmp");
        temp.delete();
        temp.mkdirs();
        RuntimeEnvironment.getInstance().setSourceRoot(temp.getAbsolutePath());
        cfg.checkSourceRootExistence();
        RuntimeEnvironment.getInstance().setSourceRoot(path);
        temp.deleteOnExit();
        PageConfig.cleanup(req);
    }
}


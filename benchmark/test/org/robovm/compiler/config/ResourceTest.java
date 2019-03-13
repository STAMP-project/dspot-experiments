/**
 * Copyright (C) 2013 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler.config;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.config.Resource.Walker;


/**
 * Tests {@link Resource}.
 */
public class ResourceTest {
    List<File> roots = new ArrayList<File>();

    ResourceTest.TestWalker walker = new ResourceTest.TestWalker();

    @Test
    public void testSimpleResourceWithFile() throws Exception {
        File f = createDirStructure("test.txt");
        Resource res = new Resource(new File(f, "test.txt"));
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/test.txt"), walker.paths);
    }

    @Test
    public void testSimpleResourceWithDir() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/test2.txt", "src/woo/test3.txt");
        Resource res = new Resource(new File(f, "src"));
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/src/test1.txt", "/dest/src/test2.txt", "/dest/src/woo/test3.txt"), walker.paths);
    }

    @Test
    public void testSimpleResourceWithDirAndDefaultExcludes() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/test2.txt", "src/test2.txt~", "src/.svn/foo");
        Resource res = new Resource(new File(f, "src"));
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/src/test1.txt", "/dest/src/test2.txt"), walker.paths);
    }

    @Test
    public void testResourceWithDirectoryAndNullTargetPath() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/test2.txt", "src/woo/test3.txt");
        Resource res = new Resource(new File(f, "src"), null);
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/test1.txt", "/dest/test2.txt", "/dest/woo/test3.txt"), walker.paths);
    }

    @Test
    public void testResourceWithNullDirectoryAndNullTargetPath() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/test2.txt", "src/woo/test3.txt");
        String savedUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", new File(f, "src").getAbsolutePath());
        try {
            Resource res = new Resource(null, null);
            res.walk(walker, new File("/dest"));
            Assert.assertEquals(Arrays.asList("/dest/test1.txt", "/dest/test2.txt", "/dest/woo/test3.txt"), walker.paths);
        } finally {
            System.setProperty("user.dir", savedUserDir);
        }
    }

    @Test
    public void testResourceWithDirectoryAndTargetPath() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/test2.txt", "src/woo/test3.txt");
        Resource res = new Resource(new File(f, "src"), "foo");
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/foo/test1.txt", "/dest/foo/test2.txt", "/dest/foo/woo/test3.txt"), walker.paths);
    }

    @Test
    public void testResourceWithDirectoryAndFlatten() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/test2.txt", "src/woo/test3.txt");
        Resource res = new Resource(new File(f, "src"), null).flatten(true);
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/test1.txt", "/dest/test2.txt", "/dest/test3.txt"), walker.paths);
    }

    @Test
    public void testResourceWithDirectoryAndDefaultExcludes() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/test2.txt", "src/woo/test3.txt", "src/test2.txt~", "src/.svn/foo");
        Resource res = new Resource(new File(f, "src"), null);
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/test1.txt", "/dest/test2.txt", "/dest/woo/test3.txt"), walker.paths);
    }

    @Test
    public void testResourceWithDirectoryAndIgnoreDefaultExcludes() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/test2.txt", "src/woo/test3.txt", "src/test2.txt~", "src/.svn/foo");
        Resource res = new Resource(new File(f, "src"), null).ignoreDefaultExcludes(true);
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/.svn/foo", "/dest/test1.txt", "/dest/test2.txt", "/dest/test2.txt~", "/dest/woo/test3.txt"), walker.paths);
    }

    @Test
    public void testResourceWithIncludesExcludes1() throws Exception {
        File f = createDirStructure("src/test1.txt", "src/woo/test2.txt", "src/woo/woo.txt", "src/zoo/test3.txt");
        Resource res = new Resource(new File(f, "src"), null).include("**/*.txt").exclude("**/woo.*", "zoo");
        res.walk(walker, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/test1.txt", "/dest/woo/test2.txt"), walker.paths);
    }

    @Test
    public void testCustomProcessDir() throws Exception {
        File f = createDirStructure("src/bar/test1.txt", "src/foo/test2.txt", "src/woo/test3.txt");
        Resource res = new Resource(new File(f, "src"), null);
        ResourceTest.TestWalker w = new ResourceTest.TestWalker() {
            @Override
            public boolean processDir(Resource resource, File dir, File destDir) throws IOException {
                return !(dir.getName().equals("woo"));
            }
        };
        res.walk(w, new File("/dest"));
        Assert.assertEquals(Arrays.asList("/dest/bar/test1.txt", "/dest/foo/test2.txt"), w.paths);
    }

    private static class TestWalker implements Walker {
        List<String> paths = new ArrayList<String>();

        @Override
        public boolean processDir(Resource resource, File dir, File destDir) throws IOException {
            return true;
        }

        @Override
        public void processFile(Resource resource, File file, File destDir) throws IOException {
            paths.add(new File(destDir, file.getName()).getAbsolutePath());
            Collections.sort(paths);
        }
    }
}


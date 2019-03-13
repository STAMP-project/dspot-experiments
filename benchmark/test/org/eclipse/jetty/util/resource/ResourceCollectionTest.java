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
package org.eclipse.jetty.util.resource;


import java.io.File;
import java.nio.file.Path;
import org.eclipse.jetty.toolchain.test.FS;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.util.IO;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


@ExtendWith(WorkDirExtension.class)
public class ResourceCollectionTest {
    public WorkDir workdir;

    @Test
    public void testUnsetCollection_ThrowsISE() {
        ResourceCollection coll = new ResourceCollection();
        assertThrowIllegalStateException(coll);
    }

    @Test
    public void testEmptyResourceArray_ThrowsISE() {
        ResourceCollection coll = new ResourceCollection(new Resource[0]);
        assertThrowIllegalStateException(coll);
    }

    @Test
    public void testResourceArrayWithNull_ThrowsISE() {
        ResourceCollection coll = new ResourceCollection(new Resource[]{ null });
        assertThrowIllegalStateException(coll);
    }

    @Test
    public void testEmptyStringArray_ThrowsISE() {
        ResourceCollection coll = new ResourceCollection(new String[0]);
        assertThrowIllegalStateException(coll);
    }

    @Test
    public void testStringArrayWithNull_ThrowsIAE() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ResourceCollection(new String[]{ null }));
    }

    @Test
    public void testNullCsv_ThrowsIAE() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String csv = null;
            new ResourceCollection(csv);// throws IAE

        });
    }

    @Test
    public void testEmptyCsv_ThrowsIAE() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String csv = "";
            new ResourceCollection(csv);// throws IAE

        });
    }

    @Test
    public void testBlankCsv_ThrowsIAE() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String csv = ",,,,";
            new ResourceCollection(csv);// throws IAE

        });
    }

    @Test
    public void testSetResourceNull_ThrowsISE() {
        // Create a ResourceCollection with one valid entry
        Path path = MavenTestingUtils.getTargetPath();
        PathResource resource = new PathResource(path);
        ResourceCollection coll = new ResourceCollection(resource);
        // Reset collection to invalid state
        coll.setResources(null);
        assertThrowIllegalStateException(coll);
    }

    @Test
    public void testSetResourceEmpty_ThrowsISE() {
        // Create a ResourceCollection with one valid entry
        Path path = MavenTestingUtils.getTargetPath();
        PathResource resource = new PathResource(path);
        ResourceCollection coll = new ResourceCollection(resource);
        // Reset collection to invalid state
        coll.setResources(new Resource[0]);
        assertThrowIllegalStateException(coll);
    }

    @Test
    public void testSetResourceAllNulls_ThrowsISE() {
        // Create a ResourceCollection with one valid entry
        Path path = MavenTestingUtils.getTargetPath();
        PathResource resource = new PathResource(path);
        ResourceCollection coll = new ResourceCollection(resource);
        // Reset collection to invalid state
        Assertions.assertThrows(IllegalStateException.class, () -> coll.setResources(new Resource[]{ null, null, null }));
        // Ensure not modified.
        MatcherAssert.assertThat(coll.getResources().length, CoreMatchers.is(1));
    }

    @Test
    public void testMutlipleSources1() throws Exception {
        ResourceCollection rc1 = new ResourceCollection(new String[]{ "src/test/resources/org/eclipse/jetty/util/resource/one/", "src/test/resources/org/eclipse/jetty/util/resource/two/", "src/test/resources/org/eclipse/jetty/util/resource/three/" });
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc1, "1.txt"), "1 - one");
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc1, "2.txt"), "2 - two");
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc1, "3.txt"), "3 - three");
        ResourceCollection rc2 = new ResourceCollection(("src/test/resources/org/eclipse/jetty/util/resource/one/," + ("src/test/resources/org/eclipse/jetty/util/resource/two/," + "src/test/resources/org/eclipse/jetty/util/resource/three/")));
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc2, "1.txt"), "1 - one");
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc2, "2.txt"), "2 - two");
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc2, "3.txt"), "3 - three");
    }

    @Test
    public void testMergedDir() throws Exception {
        ResourceCollection rc = new ResourceCollection(new String[]{ "src/test/resources/org/eclipse/jetty/util/resource/one/", "src/test/resources/org/eclipse/jetty/util/resource/two/", "src/test/resources/org/eclipse/jetty/util/resource/three/" });
        Resource r = rc.addPath("dir");
        Assertions.assertTrue((r instanceof ResourceCollection));
        rc = ((ResourceCollection) (r));
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc, "1.txt"), "1 - one");
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc, "2.txt"), "2 - two");
        Assertions.assertEquals(ResourceCollectionTest.getContent(rc, "3.txt"), "3 - three");
    }

    @Test
    public void testCopyTo() throws Exception {
        ResourceCollection rc = new ResourceCollection(new String[]{ "src/test/resources/org/eclipse/jetty/util/resource/one/", "src/test/resources/org/eclipse/jetty/util/resource/two/", "src/test/resources/org/eclipse/jetty/util/resource/three/" });
        File dest = MavenTestingUtils.getTargetTestingDir("copyto");
        FS.ensureDirExists(dest);
        rc.copyTo(dest);
        Resource r = Resource.newResource(dest.toURI());
        Assertions.assertEquals(ResourceCollectionTest.getContent(r, "1.txt"), "1 - one");
        Assertions.assertEquals(ResourceCollectionTest.getContent(r, "2.txt"), "2 - two");
        Assertions.assertEquals(ResourceCollectionTest.getContent(r, "3.txt"), "3 - three");
        r = r.addPath("dir");
        Assertions.assertEquals(ResourceCollectionTest.getContent(r, "1.txt"), "1 - one");
        Assertions.assertEquals(ResourceCollectionTest.getContent(r, "2.txt"), "2 - two");
        Assertions.assertEquals(ResourceCollectionTest.getContent(r, "3.txt"), "3 - three");
        IO.delete(dest);
    }
}


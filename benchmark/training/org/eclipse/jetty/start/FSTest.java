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


import java.io.File;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FSTest {
    @Test
    public void testCanReadDirectory() {
        File targetDir = MavenTestingUtils.getTargetDir();
        Assertions.assertTrue(FS.canReadDirectory(targetDir.toPath()), ("Can read dir: " + targetDir));
    }

    @Test
    public void testCanReadDirectory_NotDir() {
        File bogusFile = MavenTestingUtils.getTestResourceFile("bogus.xml");
        Assertions.assertFalse(FS.canReadDirectory(bogusFile.toPath()), ("Can read dir: " + bogusFile));
    }

    @Test
    public void testCanReadFile() {
        File pom = MavenTestingUtils.getProjectFile("pom.xml");
        Assertions.assertTrue(FS.canReadFile(pom.toPath()), ("Can read file: " + pom));
    }
}


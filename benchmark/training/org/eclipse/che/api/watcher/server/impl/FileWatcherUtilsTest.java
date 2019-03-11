/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.watcher.server.impl;


import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link FileWatcherUtils}
 */
public class FileWatcherUtilsTest {
    Path root;

    @Test
    public void shouldGetInternalPath() throws Exception {
        Path path = Paths.get("/", "projects", "che");
        String expected = Paths.get("/", "che").toString();
        String actual = FileWatcherUtils.toInternalPath(root, path);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNormalPath() throws Exception {
        String path = Paths.get("/", "che").toString();
        Path expected = Paths.get("/", "projects", "che");
        Path actual = FileWatcherUtils.toNormalPath(root, path);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldBeExcluded() throws Exception {
        PathMatcher matcher = Mockito.mock(PathMatcher.class);
        Path path = Mockito.mock(Path.class);
        Mockito.when(matcher.matches(path)).thenReturn(true);
        boolean condition = FileWatcherUtils.isExcluded(Collections.singleton(matcher), path);
        Assert.assertTrue(condition);
    }

    @Test
    public void shouldNotBeExcluded() throws Exception {
        PathMatcher matcher = Mockito.mock(PathMatcher.class);
        Path path = Mockito.mock(Path.class);
        Mockito.when(matcher.matches(path)).thenReturn(false);
        boolean condition = FileWatcherUtils.isExcluded(Collections.singleton(matcher), path);
        Assert.assertFalse(condition);
    }
}


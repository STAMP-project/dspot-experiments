/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.stetho.inspector.database;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DatabasePeerManagerTest {
    @Test
    public void testTidyDatabaseList() {
        File[] databases = new File[]{ new File("foo.db"), new File("foo.db-journal"), new File("bar.db"), new File("bar.db-journal"), new File("bar.db-uid"), new File("baz.db"), new File("baz.db-somethingelse"), new File("dangling.db-journal") };
        File[] expected = new File[]{ new File("foo.db"), new File("bar.db"), new File("baz.db"), new File("baz.db-somethingelse"), new File("dangling.db-journal") };
        List<File> tidied = SqliteDatabaseDriver.tidyDatabaseList(Arrays.asList(databases));
        Assert.assertArrayEquals(expected, tidied.toArray());
    }
}


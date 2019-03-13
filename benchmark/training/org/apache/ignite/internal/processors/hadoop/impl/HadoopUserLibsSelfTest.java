/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl;


import java.io.File;
import java.util.Collection;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for user libs parsing.
 */
public class HadoopUserLibsSelfTest extends GridCommonAbstractTest {
    /**
     * Directory 1.
     */
    private static final File DIR_1 = HadoopTestUtils.testDir("dir1");

    /**
     * File 1 in directory 1.
     */
    private static final File FILE_1_1 = new File(HadoopUserLibsSelfTest.DIR_1, "file1.jar");

    /**
     * File 2 in directory 1.
     */
    private static final File FILE_1_2 = new File(HadoopUserLibsSelfTest.DIR_1, "file2.jar");

    /**
     * Directory 2.
     */
    private static final File DIR_2 = HadoopTestUtils.testDir("dir2");

    /**
     * File 1 in directory 2.
     */
    private static final File FILE_2_1 = new File(HadoopUserLibsSelfTest.DIR_2, "file1.jar");

    /**
     * File 2 in directory 2.
     */
    private static final File FILE_2_2 = new File(HadoopUserLibsSelfTest.DIR_2, "file2.jar");

    /**
     * Missing directory.
     */
    private static final File MISSING_DIR = HadoopTestUtils.testDir("missing_dir");

    /**
     * Missing file.
     */
    private static final File MISSING_FILE = new File(HadoopUserLibsSelfTest.MISSING_DIR, "file.jar");

    /**
     * Test null or empty user libs.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNullOrEmptyUserLibs() throws Exception {
        assert parse(null).isEmpty();
        assert parse("").isEmpty();
    }

    /**
     * Test single file.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingle() throws Exception {
        Collection<File> res = parse(HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.FILE_1_1));
        assert (res.size()) == 1;
        assert res.contains(HadoopUserLibsSelfTest.FILE_1_1);
        res = parse(HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.MISSING_FILE));
        assert (res.size()) == 0;
    }

    /**
     * Test multiple files.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultiple() throws Exception {
        Collection<File> res = parse(HadoopUserLibsSelfTest.merge(HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.FILE_1_1), HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.FILE_1_2), HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.FILE_2_1), HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.FILE_2_2), HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.MISSING_FILE)));
        assert (res.size()) == 4;
        assert res.contains(HadoopUserLibsSelfTest.FILE_1_1);
        assert res.contains(HadoopUserLibsSelfTest.FILE_1_2);
        assert res.contains(HadoopUserLibsSelfTest.FILE_2_1);
        assert res.contains(HadoopUserLibsSelfTest.FILE_2_2);
    }

    /**
     * Test single wildcard.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleWildcard() throws Exception {
        Collection<File> res = parse(HadoopUserLibsSelfTest.wildcard(HadoopUserLibsSelfTest.DIR_1));
        assert (res.size()) == 2;
        assert res.contains(HadoopUserLibsSelfTest.FILE_1_1);
        assert res.contains(HadoopUserLibsSelfTest.FILE_1_2);
        res = parse(HadoopUserLibsSelfTest.wildcard(HadoopUserLibsSelfTest.MISSING_DIR));
        assert (res.size()) == 0;
    }

    /**
     * Test multiple wildcards.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleWildcards() throws Exception {
        Collection<File> res = parse(HadoopUserLibsSelfTest.merge(HadoopUserLibsSelfTest.wildcard(HadoopUserLibsSelfTest.DIR_1), HadoopUserLibsSelfTest.wildcard(HadoopUserLibsSelfTest.DIR_2), HadoopUserLibsSelfTest.wildcard(HadoopUserLibsSelfTest.MISSING_DIR)));
        assert (res.size()) == 4;
        assert res.contains(HadoopUserLibsSelfTest.FILE_1_1);
        assert res.contains(HadoopUserLibsSelfTest.FILE_1_2);
        assert res.contains(HadoopUserLibsSelfTest.FILE_2_1);
        assert res.contains(HadoopUserLibsSelfTest.FILE_2_2);
    }

    /**
     * Test mixed tokens.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMixed() throws Exception {
        String str = HadoopUserLibsSelfTest.merge(HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.FILE_1_1), HadoopUserLibsSelfTest.wildcard(HadoopUserLibsSelfTest.DIR_2), HadoopUserLibsSelfTest.single(HadoopUserLibsSelfTest.MISSING_FILE), HadoopUserLibsSelfTest.wildcard(HadoopUserLibsSelfTest.MISSING_DIR));
        Collection<File> res = parse(str);
        assert (res.size()) == 3;
        assert res.contains(HadoopUserLibsSelfTest.FILE_1_1);
        assert res.contains(HadoopUserLibsSelfTest.FILE_2_1);
        assert res.contains(HadoopUserLibsSelfTest.FILE_2_2);
    }
}


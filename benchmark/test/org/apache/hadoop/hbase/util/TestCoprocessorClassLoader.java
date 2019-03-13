/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.util;


import CoprocessorClassLoader.parentDirLockSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.io.IOUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test TestCoprocessorClassLoader. More tests are in TestClassLoading
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestCoprocessorClassLoader {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorClassLoader.class);

    private static final HBaseCommonTestingUtility TEST_UTIL = new HBaseCommonTestingUtility();

    private static final Configuration conf = TestCoprocessorClassLoader.TEST_UTIL.getConfiguration();

    static {
        TestCoprocessorClassLoader.TEST_UTIL.getDataTestDir();// prepare data test dir and hbase local dir

    }

    @Test
    public void testCleanupOldJars() throws Exception {
        String className = "TestCleanupOldJars";
        String folder = TestCoprocessorClassLoader.TEST_UTIL.getDataTestDir().toString();
        File jarFile = ClassLoaderTestHelper.buildJar(folder, className, null, ClassLoaderTestHelper.localDirPath(TestCoprocessorClassLoader.conf));
        File tmpJarFile = new File(jarFile.getParent(), (("/tmp/" + className) + ".test.jar"));
        if (tmpJarFile.exists())
            tmpJarFile.delete();

        Assert.assertFalse("tmp jar file should not exist", tmpJarFile.exists());
        IOUtils.copyBytes(new FileInputStream(jarFile), new FileOutputStream(tmpJarFile), TestCoprocessorClassLoader.conf, true);
        Assert.assertTrue("tmp jar file should be created", tmpJarFile.exists());
        Path path = new Path(jarFile.getAbsolutePath());
        ClassLoader parent = TestCoprocessorClassLoader.class.getClassLoader();
        parentDirLockSet.clear();// So that clean up can be triggered

        ClassLoader classLoader = CoprocessorClassLoader.getClassLoader(path, parent, "111", TestCoprocessorClassLoader.conf);
        Assert.assertNotNull("Classloader should be created", classLoader);
        Assert.assertFalse("tmp jar file should be removed", tmpJarFile.exists());
    }

    @Test
    public void testLibJarName() throws Exception {
        checkingLibJarName("TestLibJarName.jar", "/lib/");
    }

    @Test
    public void testRelativeLibJarName() throws Exception {
        checkingLibJarName("TestRelativeLibJarName.jar", "lib/");
    }

    // HBASE-14548
    @Test
    public void testDirectoryAndWildcard() throws Exception {
        String testClassName = "TestClass";
        String dataTestDir = TestCoprocessorClassLoader.TEST_UTIL.getDataTestDir().toString();
        System.out.println(dataTestDir);
        String localDirContainingJar = ClassLoaderTestHelper.localDirPath(TestCoprocessorClassLoader.conf);
        ClassLoaderTestHelper.buildJar(dataTestDir, testClassName, null, localDirContainingJar);
        ClassLoader parent = TestCoprocessorClassLoader.class.getClassLoader();
        parentDirLockSet.clear();// So that clean up can be triggered

        CoprocessorClassLoader coprocessorClassLoader = null;
        Path testPath = null;
        // Directory
        testPath = new Path(localDirContainingJar);
        coprocessorClassLoader = CoprocessorClassLoader.getClassLoader(testPath, parent, "113_1", TestCoprocessorClassLoader.conf);
        verifyCoprocessorClassLoader(coprocessorClassLoader, testClassName);
        // Wildcard - *.jar
        testPath = new Path(localDirContainingJar, "*.jar");
        coprocessorClassLoader = CoprocessorClassLoader.getClassLoader(testPath, parent, "113_2", TestCoprocessorClassLoader.conf);
        verifyCoprocessorClassLoader(coprocessorClassLoader, testClassName);
        // Wildcard - *.j*
        testPath = new Path(localDirContainingJar, "*.j*");
        coprocessorClassLoader = CoprocessorClassLoader.getClassLoader(testPath, parent, "113_3", TestCoprocessorClassLoader.conf);
        verifyCoprocessorClassLoader(coprocessorClassLoader, testClassName);
    }
}


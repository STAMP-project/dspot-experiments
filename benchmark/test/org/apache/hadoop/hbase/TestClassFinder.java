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
package org.apache.hadoop.hbase;


import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, SmallTests.class })
public class TestClassFinder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClassFinder.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestClassFinder.class);

    @Rule
    public TestName name = new TestName();

    private static final HBaseCommonTestingUtility testUtil = new HBaseCommonTestingUtility();

    private static final String BASEPKG = "tfcpkg";

    private static final String PREFIX = "Prefix";

    // Use unique jar/class/package names in each test case with the help
    // of these global counters; we are mucking with ClassLoader in this test
    // and we don't want individual test cases to conflict via it.
    private static AtomicLong testCounter = new AtomicLong(0);

    private static AtomicLong jarCounter = new AtomicLong(0);

    private static String basePath = null;

    @Test
    public void testClassFinderCanFindClassesInJars() throws Exception {
        long counter = TestClassFinder.testCounter.incrementAndGet();
        TestClassFinder.FileAndPath c1 = TestClassFinder.compileTestClass(counter, "", "c1");
        TestClassFinder.FileAndPath c2 = TestClassFinder.compileTestClass(counter, ".nested", "c2");
        TestClassFinder.FileAndPath c3 = TestClassFinder.compileTestClass(counter, "", "c3");
        TestClassFinder.packageAndLoadJar(c1, c3);
        TestClassFinder.packageAndLoadJar(c2);
        ClassFinder allClassesFinder = new ClassFinder();
        Set<Class<?>> allClasses = allClassesFinder.findClasses(TestClassFinder.makePackageName("", counter), false);
        Assert.assertEquals(3, allClasses.size());
    }

    @Test
    public void testClassFinderHandlesConflicts() throws Exception {
        long counter = TestClassFinder.testCounter.incrementAndGet();
        TestClassFinder.FileAndPath c1 = TestClassFinder.compileTestClass(counter, "", "c1");
        TestClassFinder.FileAndPath c2 = TestClassFinder.compileTestClass(counter, "", "c2");
        TestClassFinder.packageAndLoadJar(c1, c2);
        TestClassFinder.packageAndLoadJar(c1);
        ClassFinder allClassesFinder = new ClassFinder();
        Set<Class<?>> allClasses = allClassesFinder.findClasses(TestClassFinder.makePackageName("", counter), false);
        Assert.assertEquals(2, allClasses.size());
    }

    @Test
    public void testClassFinderHandlesNestedPackages() throws Exception {
        final String NESTED = ".nested";
        final String CLASSNAME1 = (name.getMethodName()) + "1";
        final String CLASSNAME2 = (name.getMethodName()) + "2";
        long counter = TestClassFinder.testCounter.incrementAndGet();
        TestClassFinder.FileAndPath c1 = TestClassFinder.compileTestClass(counter, "", "c1");
        TestClassFinder.FileAndPath c2 = TestClassFinder.compileTestClass(counter, NESTED, CLASSNAME1);
        TestClassFinder.FileAndPath c3 = TestClassFinder.compileTestClass(counter, NESTED, CLASSNAME2);
        TestClassFinder.packageAndLoadJar(c1, c2);
        TestClassFinder.packageAndLoadJar(c3);
        ClassFinder allClassesFinder = new ClassFinder();
        Set<Class<?>> nestedClasses = allClassesFinder.findClasses(TestClassFinder.makePackageName(NESTED, counter), false);
        Assert.assertEquals(2, nestedClasses.size());
        Class<?> nestedClass1 = TestClassFinder.makeClass(NESTED, CLASSNAME1, counter);
        Assert.assertTrue(nestedClasses.contains(nestedClass1));
        Class<?> nestedClass2 = TestClassFinder.makeClass(NESTED, CLASSNAME2, counter);
        Assert.assertTrue(nestedClasses.contains(nestedClass2));
    }

    @Test
    public void testClassFinderFiltersByNameInJar() throws Exception {
        final long counter = TestClassFinder.testCounter.incrementAndGet();
        final String classNamePrefix = name.getMethodName();
        TestClassFinder.LOG.info(("Created jar " + (TestClassFinder.createAndLoadJar("", classNamePrefix, counter))));
        ClassFinder.FileNameFilter notExcNameFilter = new ClassFinder.FileNameFilter() {
            @Override
            public boolean isCandidateFile(String fileName, String absFilePath) {
                return !(fileName.startsWith(TestClassFinder.PREFIX));
            }
        };
        ClassFinder incClassesFinder = new ClassFinder(null, notExcNameFilter, null);
        Set<Class<?>> incClasses = incClassesFinder.findClasses(TestClassFinder.makePackageName("", counter), false);
        Assert.assertEquals(1, incClasses.size());
        Class<?> incClass = TestClassFinder.makeClass("", classNamePrefix, counter);
        Assert.assertTrue(incClasses.contains(incClass));
    }

    @Test
    public void testClassFinderFiltersByClassInJar() throws Exception {
        final long counter = TestClassFinder.testCounter.incrementAndGet();
        final String classNamePrefix = name.getMethodName();
        TestClassFinder.LOG.info(("Created jar " + (TestClassFinder.createAndLoadJar("", classNamePrefix, counter))));
        final ClassFinder.ClassFilter notExcClassFilter = new ClassFinder.ClassFilter() {
            @Override
            public boolean isCandidateClass(Class<?> c) {
                return !(c.getSimpleName().startsWith(TestClassFinder.PREFIX));
            }
        };
        ClassFinder incClassesFinder = new ClassFinder(null, null, notExcClassFilter);
        Set<Class<?>> incClasses = incClassesFinder.findClasses(TestClassFinder.makePackageName("", counter), false);
        Assert.assertEquals(1, incClasses.size());
        Class<?> incClass = TestClassFinder.makeClass("", classNamePrefix, counter);
        Assert.assertTrue(incClasses.contains(incClass));
    }

    @Test
    public void testClassFinderFiltersByPathInJar() throws Exception {
        final String CLASSNAME = name.getMethodName();
        long counter = TestClassFinder.testCounter.incrementAndGet();
        TestClassFinder.FileAndPath c1 = TestClassFinder.compileTestClass(counter, "", CLASSNAME);
        TestClassFinder.FileAndPath c2 = TestClassFinder.compileTestClass(counter, "", "c2");
        TestClassFinder.packageAndLoadJar(c1);
        final String excludedJar = TestClassFinder.packageAndLoadJar(c2);
        /* ResourcePathFilter will pass us the resourcePath as a path of a
        URL from the classloader. For Windows, the ablosute path and the
        one from the URL have different file separators.
         */
        final String excludedJarResource = new File(excludedJar).toURI().getRawSchemeSpecificPart();
        final ClassFinder.ResourcePathFilter notExcJarFilter = new ClassFinder.ResourcePathFilter() {
            @Override
            public boolean isCandidatePath(String resourcePath, boolean isJar) {
                return (!isJar) || (!(resourcePath.equals(excludedJarResource)));
            }
        };
        ClassFinder incClassesFinder = new ClassFinder(notExcJarFilter, null, null);
        Set<Class<?>> incClasses = incClassesFinder.findClasses(TestClassFinder.makePackageName("", counter), false);
        Assert.assertEquals(1, incClasses.size());
        Class<?> incClass = TestClassFinder.makeClass("", CLASSNAME, counter);
        Assert.assertTrue(incClasses.contains(incClass));
    }

    @Test
    public void testClassFinderCanFindClassesInDirs() throws Exception {
        // Make some classes for us to find.  Class naming and packaging is kinda cryptic.
        // TODO: Fix.
        final long counter = TestClassFinder.testCounter.incrementAndGet();
        final String classNamePrefix = name.getMethodName();
        String pkgNameSuffix = name.getMethodName();
        TestClassFinder.LOG.info(("Created jar " + (TestClassFinder.createAndLoadJar(pkgNameSuffix, classNamePrefix, counter))));
        ClassFinder allClassesFinder = new ClassFinder();
        String pkgName = TestClassFinder.makePackageName(pkgNameSuffix, counter);
        Set<Class<?>> allClasses = allClassesFinder.findClasses(pkgName, false);
        Assert.assertTrue(("Classes in " + pkgName), ((allClasses.size()) > 0));
        String classNameToFind = classNamePrefix + counter;
        Assert.assertTrue(TestClassFinder.contains(allClasses, classNameToFind));
    }

    @Test
    public void testClassFinderFiltersByNameInDirs() throws Exception {
        // Make some classes for us to find.  Class naming and packaging is kinda cryptic.
        // TODO: Fix.
        final long counter = TestClassFinder.testCounter.incrementAndGet();
        final String classNamePrefix = name.getMethodName();
        String pkgNameSuffix = name.getMethodName();
        TestClassFinder.LOG.info(("Created jar " + (TestClassFinder.createAndLoadJar(pkgNameSuffix, classNamePrefix, counter))));
        final String classNameToFilterOut = classNamePrefix + counter;
        final ClassFinder.FileNameFilter notThisFilter = new ClassFinder.FileNameFilter() {
            @Override
            public boolean isCandidateFile(String fileName, String absFilePath) {
                return !(fileName.equals((classNameToFilterOut + ".class")));
            }
        };
        String pkgName = TestClassFinder.makePackageName(pkgNameSuffix, counter);
        ClassFinder allClassesFinder = new ClassFinder();
        Set<Class<?>> allClasses = allClassesFinder.findClasses(pkgName, false);
        Assert.assertTrue(("Classes in " + pkgName), ((allClasses.size()) > 0));
        ClassFinder notThisClassFinder = new ClassFinder(null, notThisFilter, null);
        Set<Class<?>> notAllClasses = notThisClassFinder.findClasses(pkgName, false);
        Assert.assertFalse(TestClassFinder.contains(notAllClasses, classNameToFilterOut));
        Assert.assertEquals(((allClasses.size()) - 1), notAllClasses.size());
    }

    @Test
    public void testClassFinderFiltersByClassInDirs() throws Exception {
        // Make some classes for us to find.  Class naming and packaging is kinda cryptic.
        // TODO: Fix.
        final long counter = TestClassFinder.testCounter.incrementAndGet();
        final String classNamePrefix = name.getMethodName();
        String pkgNameSuffix = name.getMethodName();
        TestClassFinder.LOG.info(("Created jar " + (TestClassFinder.createAndLoadJar(pkgNameSuffix, classNamePrefix, counter))));
        final Class<?> clazz = TestClassFinder.makeClass(pkgNameSuffix, classNamePrefix, counter);
        final ClassFinder.ClassFilter notThisFilter = new ClassFinder.ClassFilter() {
            @Override
            public boolean isCandidateClass(Class<?> c) {
                return c != clazz;
            }
        };
        String pkgName = TestClassFinder.makePackageName(pkgNameSuffix, counter);
        ClassFinder allClassesFinder = new ClassFinder();
        Set<Class<?>> allClasses = allClassesFinder.findClasses(pkgName, false);
        Assert.assertTrue(("Classes in " + pkgName), ((allClasses.size()) > 0));
        ClassFinder notThisClassFinder = new ClassFinder(null, null, notThisFilter);
        Set<Class<?>> notAllClasses = notThisClassFinder.findClasses(pkgName, false);
        Assert.assertFalse(TestClassFinder.contains(notAllClasses, clazz.getSimpleName()));
        Assert.assertEquals(((allClasses.size()) - 1), notAllClasses.size());
    }

    @Test
    public void testClassFinderFiltersByPathInDirs() throws Exception {
        final String hardcodedThisSubdir = "hbase-common";
        final ClassFinder.ResourcePathFilter notExcJarFilter = new ClassFinder.ResourcePathFilter() {
            @Override
            public boolean isCandidatePath(String resourcePath, boolean isJar) {
                return isJar || (!(resourcePath.contains(hardcodedThisSubdir)));
            }
        };
        String thisPackage = this.getClass().getPackage().getName();
        ClassFinder notThisClassFinder = new ClassFinder(notExcJarFilter, null, null);
        Set<Class<?>> notAllClasses = notThisClassFinder.findClasses(thisPackage, false);
        Assert.assertFalse(notAllClasses.contains(this.getClass()));
    }

    @Test
    public void testClassFinderDefaultsToOwnPackage() throws Exception {
        // Correct handling of nested packages is tested elsewhere, so here we just assume
        // pkgClasses is the correct answer that we don't have to check.
        ClassFinder allClassesFinder = new ClassFinder();
        Set<Class<?>> pkgClasses = allClassesFinder.findClasses(ClassFinder.class.getPackage().getName(), false);
        Set<Class<?>> defaultClasses = allClassesFinder.findClasses(false);
        Assert.assertArrayEquals(pkgClasses.toArray(), defaultClasses.toArray());
    }

    private static class FileAndPath {
        String path;

        File file;

        public FileAndPath(String path, File file) {
            this.file = file;
            this.path = path;
        }
    }
}


/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dalvik.system;


import java.io.File;
import junit.framework.TestCase;


/**
 * Tests for the class {@link DexClassLoader}.
 */
public class DexClassLoaderTest extends TestCase {
    private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"), "loading-test");

    private static final String PACKAGE_PATH = "dalvik/system/";

    private static final String JAR_NAME = "loading-test.jar";

    private static final String DEX_NAME = "loading-test.dex";

    private static final String JAR2_NAME = "loading-test2.jar";

    private static final String DEX2_NAME = "loading-test2.dex";

    private static final File JAR_FILE = new File(DexClassLoaderTest.TMP_DIR, DexClassLoaderTest.JAR_NAME);

    private static final File DEX_FILE = new File(DexClassLoaderTest.TMP_DIR, DexClassLoaderTest.DEX_NAME);

    private static final File JAR2_FILE = new File(DexClassLoaderTest.TMP_DIR, DexClassLoaderTest.JAR2_NAME);

    private static final File DEX2_FILE = new File(DexClassLoaderTest.TMP_DIR, DexClassLoaderTest.DEX2_NAME);

    private static final File OPTIMIZED_DIR = new File(DexClassLoaderTest.TMP_DIR, "optimized");

    private static enum Configuration {

        /**
         * just one classpath element, a raw dex file
         */
        ONE_DEX(1),
        /**
         * just one classpath element, a jar file
         */
        ONE_JAR(1),
        /**
         * two classpath elements, both raw dex files
         */
        TWO_DEX(2),
        /**
         * two classpath elements, both jar files
         */
        TWO_JAR(2);
        public final int expectedFiles;

        Configuration(int expectedFiles) {
            this.expectedFiles = expectedFiles;
        }
    }

    /* These methods are all essentially just calls to the
    parametrically-defined tests above.
     */
    // ONE_JAR
    public void test_oneJar_init() throws Exception {
        DexClassLoaderTest.test_init(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    public void test_oneJar_simpleUse() throws Exception {
        DexClassLoaderTest.test_simpleUse(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    public void test_oneJar_constructor() throws Exception {
        DexClassLoaderTest.test_constructor(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    public void test_oneJar_callStaticMethod() throws Exception {
        DexClassLoaderTest.test_callStaticMethod(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    public void test_oneJar_getStaticVariable() throws Exception {
        DexClassLoaderTest.test_getStaticVariable(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    public void test_oneJar_callInstanceMethod() throws Exception {
        DexClassLoaderTest.test_callInstanceMethod(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    public void test_oneJar_getInstanceVariable() throws Exception {
        DexClassLoaderTest.test_getInstanceVariable(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    // ONE_DEX
    public void test_oneDex_init() throws Exception {
        DexClassLoaderTest.test_init(DexClassLoaderTest.Configuration.ONE_DEX);
    }

    public void test_oneDex_simpleUse() throws Exception {
        DexClassLoaderTest.test_simpleUse(DexClassLoaderTest.Configuration.ONE_DEX);
    }

    public void test_oneDex_constructor() throws Exception {
        DexClassLoaderTest.test_constructor(DexClassLoaderTest.Configuration.ONE_DEX);
    }

    public void test_oneDex_callStaticMethod() throws Exception {
        DexClassLoaderTest.test_callStaticMethod(DexClassLoaderTest.Configuration.ONE_DEX);
    }

    public void test_oneDex_getStaticVariable() throws Exception {
        DexClassLoaderTest.test_getStaticVariable(DexClassLoaderTest.Configuration.ONE_DEX);
    }

    public void test_oneDex_callInstanceMethod() throws Exception {
        DexClassLoaderTest.test_callInstanceMethod(DexClassLoaderTest.Configuration.ONE_DEX);
    }

    public void test_oneDex_getInstanceVariable() throws Exception {
        DexClassLoaderTest.test_getInstanceVariable(DexClassLoaderTest.Configuration.ONE_DEX);
    }

    // TWO_JAR
    public void test_twoJar_init() throws Exception {
        DexClassLoaderTest.test_init(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public void test_twoJar_simpleUse() throws Exception {
        DexClassLoaderTest.test_simpleUse(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public void test_twoJar_constructor() throws Exception {
        DexClassLoaderTest.test_constructor(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public void test_twoJar_callStaticMethod() throws Exception {
        DexClassLoaderTest.test_callStaticMethod(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public void test_twoJar_getStaticVariable() throws Exception {
        DexClassLoaderTest.test_getStaticVariable(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public void test_twoJar_callInstanceMethod() throws Exception {
        DexClassLoaderTest.test_callInstanceMethod(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public void test_twoJar_getInstanceVariable() throws Exception {
        DexClassLoaderTest.test_getInstanceVariable(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public static void test_twoJar_diff_constructor() throws Exception {
        DexClassLoaderTest.test_diff_constructor(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public static void test_twoJar_diff_callStaticMethod() throws Exception {
        DexClassLoaderTest.test_diff_callStaticMethod(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public static void test_twoJar_diff_getStaticVariable() throws Exception {
        DexClassLoaderTest.test_diff_getStaticVariable(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public static void test_twoJar_diff_callInstanceMethod() throws Exception {
        DexClassLoaderTest.test_diff_callInstanceMethod(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public static void test_twoJar_diff_getInstanceVariable() throws Exception {
        DexClassLoaderTest.test_diff_getInstanceVariable(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    // TWO_DEX
    public void test_twoDex_init() throws Exception {
        DexClassLoaderTest.test_init(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public void test_twoDex_simpleUse() throws Exception {
        DexClassLoaderTest.test_simpleUse(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public void test_twoDex_constructor() throws Exception {
        DexClassLoaderTest.test_constructor(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public void test_twoDex_callStaticMethod() throws Exception {
        DexClassLoaderTest.test_callStaticMethod(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public void test_twoDex_getStaticVariable() throws Exception {
        DexClassLoaderTest.test_getStaticVariable(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public void test_twoDex_callInstanceMethod() throws Exception {
        DexClassLoaderTest.test_callInstanceMethod(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public void test_twoDex_getInstanceVariable() throws Exception {
        DexClassLoaderTest.test_getInstanceVariable(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public static void test_twoDex_diff_constructor() throws Exception {
        DexClassLoaderTest.test_diff_constructor(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public static void test_twoDex_diff_callStaticMethod() throws Exception {
        DexClassLoaderTest.test_diff_callStaticMethod(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public static void test_twoDex_diff_getStaticVariable() throws Exception {
        DexClassLoaderTest.test_diff_getStaticVariable(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public static void test_twoDex_diff_callInstanceMethod() throws Exception {
        DexClassLoaderTest.test_diff_callInstanceMethod(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public static void test_twoDex_diff_getInstanceVariable() throws Exception {
        DexClassLoaderTest.test_diff_getInstanceVariable(DexClassLoaderTest.Configuration.TWO_DEX);
    }

    public void test_oneJar_directGetResourceAsStream() throws Exception {
        DexClassLoaderTest.test_directGetResourceAsStream(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    public void test_oneJar_getResourceAsStream() throws Exception {
        DexClassLoaderTest.test_getResourceAsStream(DexClassLoaderTest.Configuration.ONE_JAR);
    }

    public void test_twoJar_directGetResourceAsStream() throws Exception {
        DexClassLoaderTest.test_directGetResourceAsStream(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    public void test_twoJar_getResourceAsStream() throws Exception {
        DexClassLoaderTest.test_getResourceAsStream(DexClassLoaderTest.Configuration.TWO_JAR);
    }

    /**
     * Check that a resource in the second jar file is retrievable and
     * contains the expected contents.
     */
    public void test_twoJar_diff_directGetResourceAsStream() throws Exception {
        DexClassLoaderTest.test_directGetResourceAsStream(DexClassLoaderTest.Configuration.TWO_JAR, "test2/Resource2.txt", "Who doesn\'t like a good biscuit?\n");
    }

    /**
     * Check that a resource in a jar file can be retrieved from
     * a class within the other jar file.
     */
    public void test_twoJar_diff_getResourceAsStream() throws Exception {
        DexClassLoaderTest.createInstanceAndCallStaticMethod(DexClassLoaderTest.Configuration.TWO_JAR, "test.TestMethods", "test_diff_getResourceAsStream");
    }
}


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
package org.apache.hadoop.hbase.tool.coprocessor;


import Severity.ERROR;
import Severity.WARNING;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hbase.thirdparty.com.google.common.base.Throwables;
import org.apache.hbase.thirdparty.com.google.common.io.ByteStreams;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SmallTests.class })
@SuppressWarnings("deprecation")
public class CoprocessorValidatorTest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(CoprocessorValidatorTest.class);

    private CoprocessorValidator validator;

    public CoprocessorValidatorTest() {
        validator = new CoprocessorValidator();
        validator.setConf(HBaseConfiguration.create());
    }

    /* In this test case, we are try to load a not-existent class. */
    @Test
    public void testNoSuchClass() throws IOException {
        List<CoprocessorViolation> violations = validateClass("NoSuchClass");
        Assert.assertEquals(1, violations.size());
        CoprocessorViolation violation = violations.get(0);
        Assert.assertEquals(CoprocessorValidatorTest.getFullClassName("NoSuchClass"), violation.getClassName());
        Assert.assertEquals(ERROR, violation.getSeverity());
        String stackTrace = Throwables.getStackTraceAsString(violation.getThrowable());
        Assert.assertTrue(stackTrace.contains(("java.lang.ClassNotFoundException: " + "org.apache.hadoop.hbase.tool.coprocessor.CoprocessorValidatorTest$NoSuchClass")));
    }

    /* In this test case, we are validating MissingClass coprocessor, which
    references a missing class. With a special classloader, we prevent that
    class to be loaded at runtime. It simulates similar cases where a class
    is no more on our classpath.
    E.g. org.apache.hadoop.hbase.regionserver.wal.WALEdit was moved to
    org.apache.hadoop.hbase.wal, so class loading will fail on 2.0.
     */
    private static class MissingClass {}

    @SuppressWarnings("unused")
    private static class MissingClassObserver {
        public void method(CoprocessorValidatorTest.MissingClass missingClass) {
        }
    }

    private static class MissingClassClassLoader extends ClassLoader {
        public MissingClassClassLoader() {
            super(CoprocessorValidatorTest.getClassLoader());
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.equals(CoprocessorValidatorTest.getFullClassName("MissingClass"))) {
                throw new ClassNotFoundException(name);
            }
            return super.findClass(name);
        }
    }

    @Test
    public void testMissingClass() throws IOException {
        CoprocessorValidatorTest.MissingClassClassLoader missingClassClassLoader = new CoprocessorValidatorTest.MissingClassClassLoader();
        List<CoprocessorViolation> violations = validateClass(missingClassClassLoader, "MissingClassObserver");
        Assert.assertEquals(1, violations.size());
        CoprocessorViolation violation = violations.get(0);
        Assert.assertEquals(CoprocessorValidatorTest.getFullClassName("MissingClassObserver"), violation.getClassName());
        Assert.assertEquals(ERROR, violation.getSeverity());
        String stackTrace = Throwables.getStackTraceAsString(violation.getThrowable());
        Assert.assertTrue(stackTrace.contains(("java.lang.ClassNotFoundException: " + "org.apache.hadoop.hbase.tool.coprocessor.CoprocessorValidatorTest$MissingClass")));
    }

    /* ObsoleteMethod coprocessor implements preCreateTable method which has
    HRegionInfo parameters. In our current implementation, we pass only
    RegionInfo parameters, so this method won't be called by HBase at all.
     */
    /* implements MasterObserver */
    @SuppressWarnings("unused")
    private static class ObsoleteMethodObserver {
        public void preCreateTable(ObserverContext<MasterCoprocessorEnvironment> ctx, HTableDescriptor desc, HRegionInfo[] regions) throws IOException {
        }
    }

    @Test
    public void testObsoleteMethod() throws IOException {
        List<CoprocessorViolation> violations = validateClass("ObsoleteMethodObserver");
        Assert.assertEquals(1, violations.size());
        CoprocessorViolation violation = violations.get(0);
        Assert.assertEquals(WARNING, violation.getSeverity());
        Assert.assertEquals(CoprocessorValidatorTest.getFullClassName("ObsoleteMethodObserver"), violation.getClassName());
        Assert.assertTrue(violation.getMessage().contains("was removed from new coprocessor API"));
    }

    @Test
    public void testTableNoSuchClass() throws IOException {
        List<CoprocessorViolation> violations = validateTable(null, "NoSuchClass");
        Assert.assertEquals(1, violations.size());
        CoprocessorViolation violation = violations.get(0);
        Assert.assertEquals(CoprocessorValidatorTest.getFullClassName("NoSuchClass"), violation.getClassName());
        Assert.assertEquals(ERROR, violation.getSeverity());
        String stackTrace = Throwables.getStackTraceAsString(violation.getThrowable());
        Assert.assertTrue(stackTrace.contains(("java.lang.ClassNotFoundException: " + "org.apache.hadoop.hbase.tool.coprocessor.CoprocessorValidatorTest$NoSuchClass")));
    }

    @Test
    public void testTableMissingJar() throws IOException {
        List<CoprocessorViolation> violations = validateTable("no such file", "NoSuchClass");
        Assert.assertEquals(1, violations.size());
        CoprocessorViolation violation = violations.get(0);
        Assert.assertEquals(CoprocessorValidatorTest.getFullClassName("NoSuchClass"), violation.getClassName());
        Assert.assertEquals(ERROR, violation.getSeverity());
        Assert.assertTrue(violation.getMessage().contains("could not validate jar file 'no such file'"));
    }

    @Test
    public void testTableValidJar() throws IOException {
        Path outputDirectory = Paths.get("target", "test-classes");
        String className = CoprocessorValidatorTest.getFullClassName("ObsoleteMethodObserver");
        Path classFile = Paths.get(((className.replace('.', '/')) + ".class"));
        Path fullClassFile = outputDirectory.resolve(classFile);
        Path tempJarFile = Files.createTempFile("coprocessor-validator-test-", ".jar");
        try {
            try (OutputStream fileStream = Files.newOutputStream(tempJarFile);JarOutputStream jarStream = new JarOutputStream(fileStream);InputStream classStream = Files.newInputStream(fullClassFile)) {
                ZipEntry entry = new ZipEntry(classFile.toString());
                jarStream.putNextEntry(entry);
                ByteStreams.copy(classStream, jarStream);
            }
            String tempJarFileUri = tempJarFile.toUri().toString();
            List<CoprocessorViolation> violations = validateTable(tempJarFileUri, "ObsoleteMethodObserver");
            Assert.assertEquals(1, violations.size());
            CoprocessorViolation violation = violations.get(0);
            Assert.assertEquals(CoprocessorValidatorTest.getFullClassName("ObsoleteMethodObserver"), violation.getClassName());
            Assert.assertEquals(WARNING, violation.getSeverity());
            Assert.assertTrue(violation.getMessage().contains("was removed from new coprocessor API"));
        } finally {
            Files.delete(tempJarFile);
        }
    }
}


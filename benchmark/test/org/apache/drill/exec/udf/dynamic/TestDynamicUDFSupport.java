/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.udf.dynamic;


import ConfigConstants.DRILL_JAR_MARKER_FILE_RESOURCE_PATHNAME;
import LocalFunctionRegistry.BUILT_IN;
import RemoteFunctionRegistry.Action;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.categories.SqlFunctionTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.exec.exception.VersionMismatchException;
import org.apache.drill.exec.expr.fn.FunctionImplementationRegistry;
import org.apache.drill.exec.expr.fn.registry.LocalFunctionRegistry;
import org.apache.drill.exec.expr.fn.registry.RemoteFunctionRegistry;
import org.apache.drill.exec.proto.UserBitShared.Jar;
import org.apache.drill.exec.proto.UserBitShared.Registry;
import org.apache.drill.exec.store.sys.store.DataChangeVersion;
import org.apache.drill.exec.util.JarUtil;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.HadoopUtils;
import org.apache.drill.test.TestBuilder;
import org.apache.hadoop.fs.FileSystem;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@Category({ SlowTest.class, SqlFunctionTest.class })
public class TestDynamicUDFSupport extends BaseTestQuery {
    private static final String DEFAULT_JAR_NAME = "drill-custom-lower";

    private static URI fsUri;

    private static File udfDir;

    private static File jarsDir;

    private static File buildDirectory;

    private static JarBuilder jarBuilder;

    private static String defaultBinaryJar;

    private static String defaultSourceJar;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSyntax() throws Exception {
        BaseTestQuery.test("create function using jar 'jar_name.jar'");
        BaseTestQuery.test("drop function using jar 'jar_name.jar'");
    }

    @Test
    public void testEnableDynamicSupport() throws Exception {
        try {
            BaseTestQuery.test("alter system set `exec.udf.enable_dynamic_support` = true");
            BaseTestQuery.test("create function using jar 'jar_name.jar'");
            BaseTestQuery.test("drop function using jar 'jar_name.jar'");
        } finally {
            BaseTestQuery.test("alter system reset `exec.udf.enable_dynamic_support`");
        }
    }

    @Test
    public void testDisableDynamicSupportCreate() throws Exception {
        try {
            BaseTestQuery.test("alter system set `exec.udf.enable_dynamic_support` = false");
            String query = "create function using jar 'jar_name.jar'";
            thrown.expect(UserRemoteException.class);
            thrown.expectMessage(CoreMatchers.containsString("Dynamic UDFs support is disabled."));
            BaseTestQuery.test(query);
        } finally {
            BaseTestQuery.test("alter system reset `exec.udf.enable_dynamic_support`");
        }
    }

    @Test
    public void testDisableDynamicSupportDrop() throws Exception {
        try {
            BaseTestQuery.test("alter system set `exec.udf.enable_dynamic_support` = false");
            String query = "drop function using jar 'jar_name.jar'";
            thrown.expect(UserRemoteException.class);
            thrown.expectMessage(CoreMatchers.containsString("Dynamic UDFs support is disabled."));
            BaseTestQuery.test(query);
        } finally {
            BaseTestQuery.test("alter system reset `exec.udf.enable_dynamic_support`");
        }
    }

    @Test
    public void testAbsentBinaryInStaging() throws Exception {
        Path staging = HadoopUtils.hadoopToJavaPath(BaseTestQuery.getDrillbitContext().getRemoteFunctionRegistry().getStagingArea());
        String summary = String.format("File %s does not exist on file system %s", staging.resolve(TestDynamicUDFSupport.defaultBinaryJar).toUri().getPath(), TestDynamicUDFSupport.fsUri);
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, summary).go();
    }

    @Test
    public void testAbsentSourceInStaging() throws Exception {
        Path staging = HadoopUtils.hadoopToJavaPath(BaseTestQuery.getDrillbitContext().getRemoteFunctionRegistry().getStagingArea());
        copyJar(TestDynamicUDFSupport.jarsDir.toPath(), staging, TestDynamicUDFSupport.defaultBinaryJar);
        String summary = String.format("File %s does not exist on file system %s", staging.resolve(TestDynamicUDFSupport.defaultSourceJar).toUri().getPath(), TestDynamicUDFSupport.fsUri);
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, summary).go();
    }

    @Test
    public void testJarWithoutMarkerFile() throws Exception {
        String jarName = "drill-no-marker";
        String jar = buildAndCopyJarsToStagingArea(jarName, null, "**/dummy.conf");
        String summary = "Marker file %s is missing in %s";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", jar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, DRILL_JAR_MARKER_FILE_RESOURCE_PATHNAME, jar)).go();
    }

    @Test
    public void testJarWithoutFunctions() throws Exception {
        String jarName = "drill-no-functions";
        String jar = buildAndCopyJarsToStagingArea(jarName, "**/CustomLowerDummyFunction.java", null);
        String summary = "Jar %s does not contain functions";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", jar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, jar)).go();
    }

    @Test
    public void testSuccessfulRegistration() throws Exception {
        copyDefaultJarsToStagingArea();
        String summary = "The following UDFs in jar %s have been registered:\n" + "[custom_lower(VARCHAR-REQUIRED)]";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
        RemoteFunctionRegistry remoteFunctionRegistry = BaseTestQuery.getDrillbitContext().getRemoteFunctionRegistry();
        FileSystem fs = remoteFunctionRegistry.getFs();
        Assert.assertFalse("Staging area should be empty", fs.listFiles(remoteFunctionRegistry.getStagingArea(), false).hasNext());
        Assert.assertFalse("Temporary area should be empty", fs.listFiles(remoteFunctionRegistry.getTmpArea(), false).hasNext());
        Path path = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getRegistryArea());
        Assert.assertTrue("Binary should be present in registry area", path.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertTrue("Source should be present in registry area", path.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Registry registry = remoteFunctionRegistry.getRegistry(new DataChangeVersion());
        Assert.assertEquals("Registry should contain one jar", registry.getJarList().size(), 1);
        Assert.assertEquals(registry.getJar(0).getName(), TestDynamicUDFSupport.defaultBinaryJar);
    }

    @Test
    public void testDuplicatedJarInRemoteRegistry() throws Exception {
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        copyDefaultJarsToStagingArea();
        String summary = "Jar with %s name has been already registered";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
    }

    @Test
    public void testDuplicatedJarInLocalRegistry() throws Exception {
        String jarName = "drill-custom-upper";
        String jar = buildAndCopyJarsToStagingArea(jarName, "**/CustomUpperFunction.java", null);
        BaseTestQuery.test("create function using jar '%s'", jar);
        BaseTestQuery.test("select custom_upper('A') from (values(1))");
        copyJarsToStagingArea(TestDynamicUDFSupport.buildDirectory.toPath(), jar, JarUtil.getSourceName(jar));
        String summary = "Jar with %s name has been already registered";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", jar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, jar)).go();
    }

    @Test
    public void testDuplicatedFunctionsInRemoteRegistry() throws Exception {
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        String jarName = "drill-custom-lower-copy";
        String jar = buildAndCopyJarsToStagingArea(jarName, "**/CustomLowerFunction.java", null);
        String summary = "Found duplicated function in %s: custom_lower(VARCHAR-REQUIRED)";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", jar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
    }

    @Test
    public void testDuplicatedFunctionsInLocalRegistry() throws Exception {
        String jarName = "drill-lower";
        String jar = buildAndCopyJarsToStagingArea(jarName, "**/LowerFunction.java", null);
        String summary = "Found duplicated function in %s: lower(VARCHAR-REQUIRED)";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", jar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, BUILT_IN)).go();
    }

    @Test
    public void testSuccessfulRegistrationAfterSeveralRetryAttempts() throws Exception {
        RemoteFunctionRegistry remoteFunctionRegistry = spyRemoteFunctionRegistry();
        Path registryPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getRegistryArea());
        Path stagingPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getStagingArea());
        Path tmpPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getTmpArea());
        copyDefaultJarsToStagingArea();
        Mockito.doThrow(new VersionMismatchException("Version mismatch detected", 1)).doThrow(new VersionMismatchException("Version mismatch detected", 1)).doCallRealMethod().when(remoteFunctionRegistry).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        String summary = "The following UDFs in jar %s have been registered:\n" + "[custom_lower(VARCHAR-REQUIRED)]";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
        Mockito.verify(remoteFunctionRegistry, Mockito.times(3)).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        Assert.assertTrue("Staging area should be empty", ArrayUtils.isEmpty(stagingPath.toFile().listFiles()));
        Assert.assertTrue("Temporary area should be empty", ArrayUtils.isEmpty(tmpPath.toFile().listFiles()));
        Assert.assertTrue("Binary should be present in registry area", registryPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertTrue("Source should be present in registry area", registryPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
        Registry registry = remoteFunctionRegistry.getRegistry(new DataChangeVersion());
        Assert.assertEquals("Registry should contain one jar", registry.getJarList().size(), 1);
        Assert.assertEquals(registry.getJar(0).getName(), TestDynamicUDFSupport.defaultBinaryJar);
    }

    @Test
    public void testSuccessfulUnregistrationAfterSeveralRetryAttempts() throws Exception {
        RemoteFunctionRegistry remoteFunctionRegistry = spyRemoteFunctionRegistry();
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        Mockito.reset(remoteFunctionRegistry);
        Mockito.doThrow(new VersionMismatchException("Version mismatch detected", 1)).doThrow(new VersionMismatchException("Version mismatch detected", 1)).doCallRealMethod().when(remoteFunctionRegistry).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        String summary = "The following UDFs in jar %s have been unregistered:\n" + "[custom_lower(VARCHAR-REQUIRED)]";
        BaseTestQuery.testBuilder().sqlQuery("drop function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
        Mockito.verify(remoteFunctionRegistry, Mockito.times(3)).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        FileSystem fs = remoteFunctionRegistry.getFs();
        Assert.assertFalse("Registry area should be empty", fs.listFiles(remoteFunctionRegistry.getRegistryArea(), false).hasNext());
        Assert.assertEquals("Registry should be empty", remoteFunctionRegistry.getRegistry(new DataChangeVersion()).getJarList().size(), 0);
    }

    @Test
    public void testExceedRetryAttemptsDuringRegistration() throws Exception {
        RemoteFunctionRegistry remoteFunctionRegistry = spyRemoteFunctionRegistry();
        Path registryPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getRegistryArea());
        Path stagingPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getStagingArea());
        Path tmpPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getTmpArea());
        copyDefaultJarsToStagingArea();
        Mockito.doThrow(new VersionMismatchException("Version mismatch detected", 1)).when(remoteFunctionRegistry).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        String summary = "Failed to update remote function registry. Exceeded retry attempts limit.";
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, summary).go();
        Mockito.verify(remoteFunctionRegistry, Mockito.times(((remoteFunctionRegistry.getRetryAttempts()) + 1))).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        Assert.assertTrue("Binary should be present in staging area", stagingPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertTrue("Source should be present in staging area", stagingPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
        Assert.assertTrue("Registry area should be empty", ArrayUtils.isEmpty(registryPath.toFile().listFiles()));
        Assert.assertTrue("Temporary area should be empty", ArrayUtils.isEmpty(tmpPath.toFile().listFiles()));
        Assert.assertEquals("Registry should be empty", remoteFunctionRegistry.getRegistry(new DataChangeVersion()).getJarList().size(), 0);
    }

    @Test
    public void testExceedRetryAttemptsDuringUnregistration() throws Exception {
        RemoteFunctionRegistry remoteFunctionRegistry = spyRemoteFunctionRegistry();
        Path registryPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getRegistryArea());
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        Mockito.reset(remoteFunctionRegistry);
        Mockito.doThrow(new VersionMismatchException("Version mismatch detected", 1)).when(remoteFunctionRegistry).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        String summary = "Failed to update remote function registry. Exceeded retry attempts limit.";
        BaseTestQuery.testBuilder().sqlQuery("drop function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, summary).go();
        Mockito.verify(remoteFunctionRegistry, Mockito.times(((remoteFunctionRegistry.getRetryAttempts()) + 1))).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        Assert.assertTrue("Binary should be present in registry area", registryPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertTrue("Source should be present in registry area", registryPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
        Registry registry = remoteFunctionRegistry.getRegistry(new DataChangeVersion());
        Assert.assertEquals("Registry should contain one jar", registry.getJarList().size(), 1);
        Assert.assertEquals(registry.getJar(0).getName(), TestDynamicUDFSupport.defaultBinaryJar);
    }

    @Test
    public void testLazyInit() throws Exception {
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.containsString("No match found for function signature custom_lower(<CHARACTER>)"));
        BaseTestQuery.test("select custom_lower('A') from (values(1))");
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        BaseTestQuery.testBuilder().sqlQuery("select custom_lower('A') as res from (values(1))").unOrdered().baselineColumns("res").baselineValues("a").go();
        Path localUdfDirPath = HadoopUtils.hadoopToJavaPath(((org.apache.hadoop.fs.Path) (FieldUtils.readField(BaseTestQuery.getDrillbitContext().getFunctionImplementationRegistry(), "localUdfDir", true))));
        Assert.assertTrue("Binary should exist in local udf directory", localUdfDirPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertTrue("Source should exist in local udf directory", localUdfDirPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
    }

    @Test
    public void testLazyInitWhenDynamicUdfSupportIsDisabled() throws Exception {
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.containsString("No match found for function signature custom_lower(<CHARACTER>)"));
        BaseTestQuery.test("select custom_lower('A') from (values(1))");
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        try {
            BaseTestQuery.testBuilder().sqlQuery("select custom_lower('A') as res from (values(1))").optionSettingQueriesForTestQuery("alter system set `exec.udf.enable_dynamic_support` = false").unOrdered().baselineColumns("res").baselineValues("a").go();
        } finally {
            BaseTestQuery.test("alter system reset `exec.udf.enable_dynamic_support`");
        }
    }

    @Test
    public void testOverloadedFunctionPlanningStage() throws Exception {
        String jarName = "drill-custom-abs";
        String jar = buildAndCopyJarsToStagingArea(jarName, "**/CustomAbsFunction.java", null);
        BaseTestQuery.test("create function using jar '%s'", jar);
        BaseTestQuery.testBuilder().sqlQuery("select abs('A', 'A') as res from (values(1))").unOrdered().baselineColumns("res").baselineValues("ABS was overloaded. Input: A, A").go();
    }

    @Test
    public void testOverloadedFunctionExecutionStage() throws Exception {
        String jarName = "drill-custom-log";
        String jar = buildAndCopyJarsToStagingArea(jarName, "**/CustomLogFunction.java", null);
        BaseTestQuery.test("create function using jar '%s'", jar);
        BaseTestQuery.testBuilder().sqlQuery("select log('A') as res from (values(1))").unOrdered().baselineColumns("res").baselineValues("LOG was overloaded. Input: A").go();
    }

    @Test
    public void testDropFunction() throws Exception {
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        BaseTestQuery.test("select custom_lower('A') from (values(1))");
        Path localUdfDirPath = HadoopUtils.hadoopToJavaPath(((org.apache.hadoop.fs.Path) (FieldUtils.readField(BaseTestQuery.getDrillbitContext().getFunctionImplementationRegistry(), "localUdfDir", true))));
        Assert.assertTrue("Binary should exist in local udf directory", localUdfDirPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertTrue("Source should exist in local udf directory", localUdfDirPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
        String summary = "The following UDFs in jar %s have been unregistered:\n" + "[custom_lower(VARCHAR-REQUIRED)]";
        BaseTestQuery.testBuilder().sqlQuery("drop function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.containsString("No match found for function signature custom_lower(<CHARACTER>)"));
        BaseTestQuery.test("select custom_lower('A') from (values(1))");
        RemoteFunctionRegistry remoteFunctionRegistry = BaseTestQuery.getDrillbitContext().getRemoteFunctionRegistry();
        Path registryPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getRegistryArea());
        Assert.assertEquals("Remote registry should be empty", remoteFunctionRegistry.getRegistry(new DataChangeVersion()).getJarList().size(), 0);
        Assert.assertFalse("Binary should not be present in registry area", registryPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertFalse("Source should not be present in registry area", registryPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
        Assert.assertFalse("Binary should not be present in local udf directory", localUdfDirPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertFalse("Source should not be present in local udf directory", localUdfDirPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
    }

    @Test
    public void testReRegisterTheSameJarWithDifferentContent() throws Exception {
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        BaseTestQuery.testBuilder().sqlQuery("select custom_lower('A') as res from (values(1))").unOrdered().baselineColumns("res").baselineValues("a").go();
        BaseTestQuery.test("drop function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        Thread.sleep(1000);
        buildAndCopyJarsToStagingArea(TestDynamicUDFSupport.DEFAULT_JAR_NAME, "**/CustomLowerFunctionV2.java", null);
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        BaseTestQuery.testBuilder().sqlQuery("select custom_lower('A') as res from (values(1))").unOrdered().baselineColumns("res").baselineValues("a_v2").go();
    }

    @Test
    public void testDropAbsentJar() throws Exception {
        String summary = "Jar %s is not registered in remote registry";
        BaseTestQuery.testBuilder().sqlQuery("drop function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
    }

    @Test
    public void testRegistrationFailDuringRegistryUpdate() throws Exception {
        RemoteFunctionRegistry remoteFunctionRegistry = spyRemoteFunctionRegistry();
        Path registryPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getRegistryArea());
        Path stagingPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getStagingArea());
        Path tmpPath = HadoopUtils.hadoopToJavaPath(remoteFunctionRegistry.getTmpArea());
        final String errorMessage = "Failure during remote registry update.";
        Mockito.doAnswer(( invocation) -> {
            Assert.assertTrue("Binary should be present in registry area", registryPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
            Assert.assertTrue("Source should be present in registry area", registryPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
            throw new RuntimeException(errorMessage);
        }).when(remoteFunctionRegistry).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        copyDefaultJarsToStagingArea();
        BaseTestQuery.testBuilder().sqlQuery("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, errorMessage).go();
        Assert.assertTrue("Registry area should be empty", ArrayUtils.isEmpty(registryPath.toFile().listFiles()));
        Assert.assertTrue("Temporary area should be empty", ArrayUtils.isEmpty(tmpPath.toFile().listFiles()));
        Assert.assertTrue("Binary should be present in staging area", stagingPath.resolve(TestDynamicUDFSupport.defaultBinaryJar).toFile().exists());
        Assert.assertTrue("Source should be present in staging area", stagingPath.resolve(TestDynamicUDFSupport.defaultSourceJar).toFile().exists());
    }

    @Test
    public void testConcurrentRegistrationOfTheSameJar() throws Exception {
        RemoteFunctionRegistry remoteFunctionRegistry = spyRemoteFunctionRegistry();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        Mockito.doAnswer(( invocation) -> {
            String result = ((String) (invocation.callRealMethod()));
            latch2.countDown();
            latch1.await();
            return result;
        }).doCallRealMethod().doCallRealMethod().when(remoteFunctionRegistry).addToJars(ArgumentMatchers.anyString(), ArgumentMatchers.any(Action.class));
        final String query = String.format("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        Thread thread = new Thread(new TestDynamicUDFSupport.SimpleQueryRunner(query));
        thread.start();
        latch2.await();
        try {
            String summary = "Jar with %s name is used. Action: REGISTRATION";
            BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
            BaseTestQuery.testBuilder().sqlQuery("drop function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format(summary, TestDynamicUDFSupport.defaultBinaryJar)).go();
        } finally {
            latch1.countDown();
            thread.join();
        }
    }

    @Test
    public void testConcurrentRemoteRegistryUpdateWithDuplicates() throws Exception {
        RemoteFunctionRegistry remoteFunctionRegistry = spyRemoteFunctionRegistry();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(1);
        Mockito.doAnswer(( invocation) -> {
            latch3.countDown();
            latch1.await();
            invocation.callRealMethod();
            latch2.countDown();
            return null;
        }).doAnswer(( invocation) -> {
            latch1.countDown();
            latch2.await();
            invocation.callRealMethod();
            return null;
        }).when(remoteFunctionRegistry).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        final String jar1 = TestDynamicUDFSupport.defaultBinaryJar;
        copyDefaultJarsToStagingArea();
        final String copyJarName = "drill-custom-lower-copy";
        final String jar2 = buildAndCopyJarsToStagingArea(copyJarName, "**/CustomLowerFunction.java", null);
        final String query = "create function using jar '%s'";
        Thread thread1 = new Thread(new TestDynamicUDFSupport.TestBuilderRunner(BaseTestQuery.testBuilder().sqlQuery(query, jar1).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format(("The following UDFs in jar %s have been registered:\n" + "[custom_lower(VARCHAR-REQUIRED)]"), jar1))));
        Thread thread2 = new Thread(new TestDynamicUDFSupport.TestBuilderRunner(BaseTestQuery.testBuilder().sqlQuery(query, jar2).unOrdered().baselineColumns("ok", "summary").baselineValues(false, String.format("Found duplicated function in %s: custom_lower(VARCHAR-REQUIRED)", jar1))));
        thread1.start();
        latch3.await();
        thread2.start();
        thread1.join();
        thread2.join();
        DataChangeVersion version = new DataChangeVersion();
        Registry registry = remoteFunctionRegistry.getRegistry(version);
        Assert.assertEquals("Remote registry version should match", 2, version.getVersion());
        List<Jar> jarList = registry.getJarList();
        Assert.assertEquals("Only one jar should be registered", 1, jarList.size());
        Assert.assertEquals("Jar name should match", jar1, jarList.get(0).getName());
        Mockito.verify(remoteFunctionRegistry, Mockito.times(2)).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
    }

    @Test
    public void testConcurrentRemoteRegistryUpdateForDifferentJars() throws Exception {
        RemoteFunctionRegistry remoteFunctionRegistry = spyRemoteFunctionRegistry();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            latch2.countDown();
            latch1.await();
            invocation.callRealMethod();
            return null;
        }).when(remoteFunctionRegistry).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
        final String jar1 = TestDynamicUDFSupport.defaultBinaryJar;
        copyDefaultJarsToStagingArea();
        final String upperJarName = "drill-custom-upper";
        final String jar2 = buildAndCopyJarsToStagingArea(upperJarName, "**/CustomUpperFunction.java", null);
        final String query = "create function using jar '%s'";
        Thread thread1 = new Thread(new TestDynamicUDFSupport.TestBuilderRunner(BaseTestQuery.testBuilder().sqlQuery(query, jar1).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format(("The following UDFs in jar %s have been registered:\n" + "[custom_lower(VARCHAR-REQUIRED)]"), jar1))));
        Thread thread2 = new Thread(new TestDynamicUDFSupport.TestBuilderRunner(BaseTestQuery.testBuilder().sqlQuery(query, jar2).unOrdered().baselineColumns("ok", "summary").baselineValues(true, String.format(("The following UDFs in jar %s have been registered:\n" + "[custom_upper(VARCHAR-REQUIRED)]"), jar2))));
        thread1.start();
        thread2.start();
        latch2.await();
        latch1.countDown();
        thread1.join();
        thread2.join();
        DataChangeVersion version = new DataChangeVersion();
        Registry registry = remoteFunctionRegistry.getRegistry(version);
        Assert.assertEquals("Remote registry version should match", 3, version.getVersion());
        List<Jar> actualJars = registry.getJarList();
        List<String> expectedJars = Lists.newArrayList(jar1, jar2);
        Assert.assertEquals("Only one jar should be registered", 2, actualJars.size());
        for (Jar jar : actualJars) {
            Assert.assertTrue("Jar should be present in remote function registry", expectedJars.contains(jar.getName()));
        }
        Mockito.verify(remoteFunctionRegistry, Mockito.times(3)).updateRegistry(ArgumentMatchers.any(Registry.class), ArgumentMatchers.any(DataChangeVersion.class));
    }

    @Test
    public void testLazyInitConcurrent() throws Exception {
        FunctionImplementationRegistry functionImplementationRegistry = spyFunctionImplementationRegistry();
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final String query = "select custom_lower('A') from (values(1))";
        Mockito.doAnswer(( invocation) -> {
            latch1.await();
            boolean result = ((boolean) (invocation.callRealMethod()));
            Assert.assertTrue("syncWithRemoteRegistry() should return true", result);
            latch2.countDown();
            return true;
        }).doAnswer(( invocation) -> {
            latch1.countDown();
            latch2.await();
            boolean result = ((boolean) (invocation.callRealMethod()));
            Assert.assertTrue("syncWithRemoteRegistry() should return true", result);
            return true;
        }).when(functionImplementationRegistry).syncWithRemoteRegistry(ArgumentMatchers.anyInt());
        TestDynamicUDFSupport.SimpleQueryRunner simpleQueryRunner = new TestDynamicUDFSupport.SimpleQueryRunner(query);
        Thread thread1 = new Thread(simpleQueryRunner);
        Thread thread2 = new Thread(simpleQueryRunner);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        Mockito.verify(functionImplementationRegistry, Mockito.times(2)).syncWithRemoteRegistry(ArgumentMatchers.anyInt());
        LocalFunctionRegistry localFunctionRegistry = ((LocalFunctionRegistry) (FieldUtils.readField(functionImplementationRegistry, "localFunctionRegistry", true)));
        Assert.assertEquals("Sync function registry version should match", 2, localFunctionRegistry.getVersion());
    }

    @Test
    public void testLazyInitNoReload() throws Exception {
        FunctionImplementationRegistry functionImplementationRegistry = spyFunctionImplementationRegistry();
        copyDefaultJarsToStagingArea();
        BaseTestQuery.test("create function using jar '%s'", TestDynamicUDFSupport.defaultBinaryJar);
        Mockito.doAnswer(( invocation) -> {
            boolean result = ((boolean) (invocation.callRealMethod()));
            Assert.assertTrue("syncWithRemoteRegistry() should return true", result);
            return true;
        }).doAnswer(( invocation) -> {
            boolean result = ((boolean) (invocation.callRealMethod()));
            Assert.assertFalse("syncWithRemoteRegistry() should return false", result);
            return false;
        }).when(functionImplementationRegistry).syncWithRemoteRegistry(ArgumentMatchers.anyInt());
        BaseTestQuery.test("select custom_lower('A') from (values(1))");
        try {
            BaseTestQuery.test("select unknown_lower('A') from (values(1))");
            Assert.fail();
        } catch (UserRemoteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("No match found for function signature unknown_lower(<CHARACTER>)"));
        }
        Mockito.verify(functionImplementationRegistry, Mockito.times(2)).syncWithRemoteRegistry(ArgumentMatchers.anyInt());
        LocalFunctionRegistry localFunctionRegistry = ((LocalFunctionRegistry) (FieldUtils.readField(functionImplementationRegistry, "localFunctionRegistry", true)));
        Assert.assertEquals("Sync function registry version should match", 2, localFunctionRegistry.getVersion());
    }

    private class SimpleQueryRunner implements Runnable {
        private final String query;

        SimpleQueryRunner(String query) {
            this.query = query;
        }

        @Override
        public void run() {
            try {
                BaseTestQuery.test(query);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class TestBuilderRunner implements Runnable {
        private final TestBuilder testBuilder;

        TestBuilderRunner(TestBuilder testBuilder) {
            this.testBuilder = testBuilder;
        }

        @Override
        public void run() {
            try {
                testBuilder.go();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}


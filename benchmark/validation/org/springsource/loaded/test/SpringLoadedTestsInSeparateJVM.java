/**
 * Copyright 2010-2012 VMware and contributors
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
package org.springsource.loaded.test;


import org.junit.Assert;
import org.junit.Test;


/**
 * These tests use a harness that forks a JVM with the agent attached, closely simulating a real environment. The forked
 * process is running a special class that can be sent commands.
 *
 * @author Andy Clement
 */
public class SpringLoadedTestsInSeparateJVM extends SpringLoadedTests {
    private static ReloadingJVM jvm;

    @Test
    public void testEcho() throws Exception {
        ReloadingJVM.JVMOutput result = SpringLoadedTestsInSeparateJVM.jvm.echo("hello");
        assertStdout("hello", result);
    }

    @Test
    public void testRunClass() throws Exception {
        ReloadingJVM.JVMOutput output = SpringLoadedTestsInSeparateJVM.jvm.run("jvmtwo.Runner");
        assertStdout("jvmtwo.Runner.run() running", output);
    }

    @Test
    public void githubIssue34() throws Exception {
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("issue34.Interface1");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("issue34.Interface2");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("issue34.Implementation1");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("issue34.Implementation2");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("issue34.Implementation3");
        ReloadingJVM.JVMOutput output = SpringLoadedTestsInSeparateJVM.jvm.run("issue34.Implementation3");
        assertStdout("Hello World!\n", output);
    }

    @Test
    public void testReferenceInstanceMethodOfObject() throws Exception {
        ReloadingJVM.JVMOutput jo = null;
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("basic.LambdaL");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("basic.LambdaL$Foo");
        // jvm.newInstance("l", "basic.LambdaL", true);
        jo = SpringLoadedTestsInSeparateJVM.jvm.run("basic.LambdaL");
        // Total output:
        // original static initializer
        // original instance
        // in first foo
        // fooa
        assertStdoutContains("in first foo", jo);
        assertStdoutContains("fooa", jo);
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("basic.LambdaL", loadBytesForClass("basic.LambdaL"));
        SpringLoadedTests.pause(2);
        // Run the same thing as before:
        jo = SpringLoadedTestsInSeparateJVM.jvm.run("basic.LambdaL");
        assertStdoutContains("in first foo", jo);
        assertStdoutContains("fooa", jo);
        // New version: Foo interface has one method argument
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("basic.LambdaL$Foo", retrieveRename("basic.LambdaL$Foo", "basic.LambdaL2$Foo"));
        waitForReloadToOccur();
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("basic.LambdaL", retrieveRename("basic.LambdaL", "basic.LambdaL2", "basic.LambdaL2$Foo:basic.LambdaL$Foo"));
        waitForReloadToOccur();
        // Run the new version
        jo = SpringLoadedTestsInSeparateJVM.jvm.run("basic.LambdaL");
        assertStdoutContains("in second foo", jo);
        assertStdoutContains("fooab", jo);
    }

    @Test
    public void testStaticMethodReference() throws Exception {
        ReloadingJVM.JVMOutput jo = null;
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("basic.StaticMethodReference", "basic.StaticMethodReference$Foo", "basic.StaticMethodReference$Bar");
        jo = SpringLoadedTestsInSeparateJVM.jvm.run("basic.StaticMethodReference");
        assertStdoutContains("in 1st static Method", jo);
        assertStdoutContains("staticsa", jo);
        // Reload itself
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("basic.StaticMethodReference", loadBytesForClass("basic.StaticMethodReference"));
        waitForReloadToOccur();
        jo = SpringLoadedTestsInSeparateJVM.jvm.run("basic.StaticMethodReference");
        assertStdoutContains("in 1st static Method", jo);
        assertStdoutContains("staticsa", jo);
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("basic.StaticMethodReference$Foo", retrieveRename("basic.StaticMethodReference$Foo", "basic.StaticMethodReference2$Foo"));
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("basic.StaticMethodReference$Bar", retrieveRename("basic.StaticMethodReference$Bar", "basic.StaticMethodReference2$Bar"));
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("basic.StaticMethodReference", retrieveRename("basic.StaticMethodReference", "basic.StaticMethodReference2", "basic.StaticMethodReference2$Foo:basic.StaticMethodReference$Foo", "basic.StaticMethodReference2$Bar:basic.StaticMethodReference$Bar"));
        waitForReloadToOccur();
        jo = SpringLoadedTestsInSeparateJVM.jvm.run("basic.StaticMethodReference");
        assertStdoutContains("in 2nd static Method", jo);
        assertStdoutContains("staticsasb", jo);
        // // New version: Foo interface has one method argument
        // jvm.updateClass("basic.LambdaL$Foo",
        // retrieveRename("basic.LambdaL$Foo", "basic.LambdaL2$Foo"));
        // waitForReloadToOccur();
        // 
        // jvm.updateClass("basic.LambdaL",
        // retrieveRename("basic.LambdaL", "basic.LambdaL2", "basic.LambdaL2$Foo:basic.LambdaL$Foo"));
        // waitForReloadToOccur();
        // 
        // // Run the new version
        // jo = jvm.run("basic.LambdaL");
        // assertStdoutContains("in second foo", jo);
        // assertStdoutContains("fooab", jo);
        // compile("/original/", "/original/basic/StaticMethodReference.java.file");
        // +		JVMOutput output = jvm.sendAndReceive("run basic.StaticMethodReference");
        // +
        // +		assertStdoutContains("in 1st static Method", output);
        // +		assertStdoutContains("staticsa", output);
        // +
        // +		compile("/modified/", "/modified/basic/StaticMethodReference.java.file");
        // +		jvm.reload("basic.StaticMethodReference");
        // +		waitForReloadToOccur();
        // +
        // +		output = jvm.sendAndReceive("run basic.StaticMethodReference");
        // +		assertStdoutContains("in 2nd static Method", output);
        // +		assertStdoutContains("staticsasb", output);
        // +	}
        // 
    }

    @Test
    public void serialization() throws Exception {
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("remote.Serialize");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("remote.Person");
        ReloadingJVM.JVMOutput output = null;
        // When the Serialize class is run directly, we see: byteinfo:len=98:crc=c1047cf6
        // When run via the non separate JVM test, we see: byteinfo:len=98:crc=7e07276a
        // When run here, we see: byteinfo:len=98:crc=c1047cf6
        output = SpringLoadedTestsInSeparateJVM.jvm.run("remote.Serialize");
        assertStdoutContains("check ok\n", output);
        // Load new Person
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("remote.Person", retrieveRename("remote.Person", "remote.Person2"));
        SpringLoadedTests.pause(2);
        output = SpringLoadedTestsInSeparateJVM.jvm.run("remote.Serialize");
        assertStdoutContains("check ok\n", output);
        // Load original Person
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("remote.Person", loadBytesForClass("remote.Person"));
        SpringLoadedTests.pause(2);
        output = SpringLoadedTestsInSeparateJVM.jvm.run("remote.Serialize");
        assertStdoutContains("check ok\n", output);
    }

    // Deserializing something serialized earlier
    @Test
    public void serialization2() throws Exception {
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("remote.Serialize");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("remote.Person");
        ReloadingJVM.JVMOutput output = null;
        output = SpringLoadedTestsInSeparateJVM.jvm.run("remote.Serialize");
        assertStdoutContains("check ok\n", output);
        SpringLoadedTestsInSeparateJVM.jvm.newInstance("a", "remote.Serialize");
        ReloadingJVM.JVMOutput jo = SpringLoadedTestsInSeparateJVM.jvm.call("a", "checkPredeserializedData");
        assertStdoutContains("Pre-serialized form checked ok\n", jo);
    }

    // Deserialize a groovy closure
    @Test
    public void serializationGroovy() throws Exception {
        // debug();
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("remote.SerializeG");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("remote.FakeClosure");
        // Notes on serialization
        // When SerializeG run standalone, reports byteinfo:len=283:crc=245529d9
        // When run in agented JVM, reports byteinfo:len=283:crc=245529d9
        SpringLoadedTestsInSeparateJVM.jvm.newInstance("a", "remote.SerializeG");
        ReloadingJVM.JVMOutput jo = SpringLoadedTestsInSeparateJVM.jvm.call("a", "checkPredeserializedData");
        assertStdoutContains("Pre-serialized groovy form checked ok\n", jo);
    }

    @Test
    public void githubIssue34_2() throws Exception {
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("issue34.InnerEnum$sorters");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("issue34.InnerEnum$MyComparator");
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory("issue34.InnerEnum$sorters$1");
        ReloadingJVM.JVMOutput output = SpringLoadedTestsInSeparateJVM.jvm.run("issue34.InnerEnum");
        assertStdout("Hello World!\n", output);
    }

    @Test
    public void testCreatingAndInvokingMethodsOnInstance() throws Exception {
        assertStderrContains("creating new instance 'a' of type 'jvmtwo.Runner'", SpringLoadedTestsInSeparateJVM.jvm.newInstance("a", "jvmtwo.Runner"));
        assertStdout("jvmtwo.Runner.run1() running", SpringLoadedTestsInSeparateJVM.jvm.call("a", "run1"));
    }

    @Test
    public void testReloadingInOtherVM() throws Exception {
        SpringLoadedTestsInSeparateJVM.jvm.newInstance("b", "remote.One");
        assertStdout("first", SpringLoadedTestsInSeparateJVM.jvm.call("b", "run"));
        SpringLoadedTests.pause(1);
        SpringLoadedTestsInSeparateJVM.jvm.updateClass("remote.One", retrieveRename("remote.One", "remote.One2"));
        SpringLoadedTests.pause(2);
        assertStdoutContains("second", SpringLoadedTestsInSeparateJVM.jvm.call("b", "run"));
    }

    @Test
    public void testReloadingJarsInOtherVM() throws Exception {
        SpringLoadedTestsInSeparateJVM.jvm.shutdown();
        SpringLoadedTestsInSeparateJVM.jvm = ReloadingJVM.launch("watchJars=foo.jar", false);// ;verbose=true;logging=true

        String path = SpringLoadedTestsInSeparateJVM.jvm.copyJarToTestdataDirectory("one/foo.jar", "foo.jar");
        ReloadingJVM.JVMOutput output = SpringLoadedTestsInSeparateJVM.jvm.extendCp(path);
        SpringLoadedTestsInSeparateJVM.jvm.newInstance("a", "Foo", false);
        ReloadingJVM.JVMOutput jo = SpringLoadedTestsInSeparateJVM.jvm.call("a", "run");
        Assert.assertEquals("m() running", jo.stdout.trim());
        String s = SpringLoadedTestsInSeparateJVM.jvm.copyJarToTestdataDirectory("two/foo.jar", "foo.jar", "Foo.class");
        SpringLoadedTests.pause(2);
        jo = SpringLoadedTestsInSeparateJVM.jvm.call("a", "run");
        String stdout = jo.stdout.trim();
        if (stdout.startsWith("Reloading:")) {
            stdout = stdout.substring(((stdout.indexOf("\n")) + 1));
        }
        Assert.assertEquals("n() running", stdout);
    }

    @Test
    public void testReloadingJarsInOtherVM_packages() throws Exception {
        SpringLoadedTestsInSeparateJVM.jvm.shutdown();
        SpringLoadedTestsInSeparateJVM.jvm = ReloadingJVM.launch("watchJars=foo.jar:bar.jar", false);// ;verbose=true;logging=true

        String path = SpringLoadedTestsInSeparateJVM.jvm.copyJarToTestdataDirectory("one/bar.jar", "bar.jar");
        SpringLoadedTestsInSeparateJVM.jvm.extendCp(path);
        SpringLoadedTestsInSeparateJVM.jvm.newInstance("a", "test.Bar", false);
        ReloadingJVM.JVMOutput jo = SpringLoadedTestsInSeparateJVM.jvm.call("a", "run");
        Assert.assertEquals("Wibble.foo() running, version 1", jo.stdout.trim());
        SpringLoadedTestsInSeparateJVM.jvm.copyJarToTestdataDirectory("two/bar.jar", "bar.jar", "test/Wibble.class");
        SpringLoadedTests.pause(2);
        jo = SpringLoadedTestsInSeparateJVM.jvm.call("a", "run");
        String stdout = jo.stdout.trim();
        if (stdout.startsWith("Reloading:")) {
            System.out.println(("retpos = " + (stdout.indexOf("\n"))));
            stdout = stdout.substring(((stdout.indexOf("\n")) + 1));
        }
        Assert.assertEquals("Wibble.foo() running, version 2", stdout);
    }

    // TODO tidyup test data area after each test?
    // TODO flush/replace classloader in forked VM to clear it out after each test?
    // GRAILS-10411
    /**
     * GRAILS-10411. The supertype is not reloadable, the subtype is reloadable and makes super calls to overridden
     * methods.
     */
    @Test
    public void testClassMakingSuperCalls() throws Exception {
        String supertype = "grails.Top";
        String subtype = "foo.Controller";
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory(supertype);
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory(subtype);
        ReloadingJVM.JVMOutput jo = SpringLoadedTestsInSeparateJVM.jvm.newInstance("bb", subtype);
        System.out.println(jo);
        SpringLoadedTests.pause(1);
        assertStdout("Top.foo() running\nController.foo() running\n", SpringLoadedTestsInSeparateJVM.jvm.call("bb", "foo"));
        SpringLoadedTests.pause(1);
        SpringLoadedTestsInSeparateJVM.jvm.updateClass(subtype, retrieveRename(subtype, (subtype + "2")));
        waitForReloadToOccur();
        jo = SpringLoadedTestsInSeparateJVM.jvm.call("bb", "foo");
        assertStdoutContains("Top.foo() running\nController.foo() running again!\n", jo);
    }

    /**
     * GRAILS-10411. The supertype is not reloadable, the subtype is reloadable and makes super calls to overridden
     * methods. This time the supertype method is protected.
     */
    @Test
    public void testClassMakingSuperCalls2() throws Exception {
        String supertype = "grails.TopB";
        String subtype = "foo.ControllerB";
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory(supertype);
        SpringLoadedTestsInSeparateJVM.jvm.copyToTestdataDirectory(subtype);
        SpringLoadedTestsInSeparateJVM.jvm.newInstance("a", subtype);
        assertStdout("TopB.foo() running\nControllerB.foo() running\n", SpringLoadedTestsInSeparateJVM.jvm.call("a", "foo"));
        SpringLoadedTestsInSeparateJVM.jvm.updateClass(subtype, retrieveRename(subtype, (subtype + "2")));
        waitForReloadToOccur();
        assertStdoutContains("TopB.foo() running\nControllerB.foo() running again!\n", SpringLoadedTestsInSeparateJVM.jvm.call("a", "foo"));
    }
}


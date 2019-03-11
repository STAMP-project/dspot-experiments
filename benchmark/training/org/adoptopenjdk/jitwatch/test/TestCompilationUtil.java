/**
 * Copyright (c) 2013-2016 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.adoptopenjdk.jitwatch.logger.NullLogListener;
import org.adoptopenjdk.jitwatch.process.compiler.CompilerJava;
import org.adoptopenjdk.jitwatch.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestCompilationUtil {
    private Path tempDirPath;

    private File testSourceFile;

    private File testClassFile;

    @Test
    public void testCompileSimple() {
        StringBuilder builder = new StringBuilder();
        builder.append("package org.adoptopenjdk.jitwatch.compiletest;\n");
        builder.append("public class CompileTest\n");
        builder.append("{\n");
        builder.append("private int foo = 0;\n");
        builder.append("public void setFoo(int foo) {this.foo=foo;}\n");
        builder.append("public int getFoo() {return foo;}\n");
        builder.append("}");
        try {
            File f = FileUtil.writeSource(tempDirPath.toFile(), "org.adoptopenjdk.jitwatch.compiletest.CompileTest", builder.toString());
            Assert.assertTrue(testSourceFile.exists());
            List<File> sources = new ArrayList<>();
            sources.add(f);
            CompilerJava compiler = new CompilerJava(System.getProperty("java.home"));
            List<String> compileClasspath = new ArrayList<>();
            boolean success = compiler.compile(sources, compileClasspath, tempDirPath.toFile(), new NullLogListener());
            if (!success) {
                System.err.println(compiler.getErrorStream());
                Assert.fail();
            }
            Assert.assertTrue(testClassFile.exists());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}


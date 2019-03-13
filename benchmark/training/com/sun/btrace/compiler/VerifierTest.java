package com.sun.btrace.compiler;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.junit.Test;


public class VerifierTest {
    private String sourcePath;

    private String classPath;

    @Test
    public void testVerifier() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8));
        Compiler c = new Compiler();
        File f = new File((((((((sourcePath) + (File.separator)) + "traces") + (File.separator)) + "verifier") + (File.separator)) + "VerifierScript.java"));
        compile(f, bw, sourcePath, classPath);
        String log = bos.toString();
        checkForString("btrace class can not implement interfaces", log);
        checkForString("instance variables are not allowed", log);
        checkForString("object creation is not allowed", log);
        checkForString("array creation is not allowed", log);
        checkForString("instance methods are not allowed", log);
        checkForString("try .. catch .. finally blocks are not allowed", log);
        checkForString("method calls are not allowed - only calls to BTraceUtils are allowed", log);
        checkForString("catching exception is not allowed", log);
        checkForString("throwing exception is not allowed", log);
        checkForString("for loops are not allowed", log);
        checkForString("enhanced for statements are not allowed", log);
        checkForString("while loops are not allowed", log);
        checkForString("btrace probe methods must return void", log);
        checkForString("synchronized blocks are not allowed", log);
        checkForString("probe action methods should not be synchronized", log);
        checkForString("class literals are not allowed", log);
        System.out.println(bos.toString());
    }
}


package com.sun.btrace.compiler;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class CompilerTest {
    private Compiler instance;

    public CompilerTest() {
    }

    @Test
    @SuppressWarnings("DefaultCharset")
    public void testCompile() throws Exception {
        System.out.println("compile");
        URL script = CompilerTest.class.getResource("/traces/OnMethodTest.java");
        InputStream is = script.openStream();
        File f = new File(script.toURI());
        File base = f.getParentFile().getParentFile().getParentFile();
        String classpath = ((((base.getAbsolutePath()) + "/main") + ":") + (base.getAbsolutePath())) + "/test";
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        } 
        System.out.println(sb.toString());
        Map<String, byte[]> data = compile("traces/OnMethodTest.java", sb.toString(), new PrintWriter(System.err), ".", classpath);
        Assert.assertNotNull(data);
    }
}


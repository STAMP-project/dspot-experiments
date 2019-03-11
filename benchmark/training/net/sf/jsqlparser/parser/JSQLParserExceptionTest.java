/**
 * -
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.parser;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author schwitters
 */
public class JSQLParserExceptionTest {
    public JSQLParserExceptionTest() {
    }

    /**
     * Test of parseExpression method, of class CCJSqlParserUtil.
     */
    @Test
    public void testExceptionWithCause() throws Exception {
        IllegalArgumentException arg1 = new IllegalArgumentException();
        JSQLParserException ex1 = new JSQLParserException("", arg1);
        Assert.assertSame(arg1, ex1.getCause());
    }

    @Test
    public void testExceptionPrintStacktrace() throws Exception {
        IllegalArgumentException arg1 = new IllegalArgumentException("BRATKARTOFFEL");
        JSQLParserException ex1 = new JSQLParserException("", arg1);
        StringWriter sw = new StringWriter();
        ex1.printStackTrace(new PrintWriter(sw, true));
        Assert.assertTrue(sw.toString().contains("BRATKARTOFFEL"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ex1.printStackTrace(new PrintStream(bos, true));
        Assert.assertTrue(new String(bos.toByteArray(), StandardCharsets.UTF_8).contains("BRATKARTOFFEL"));
    }

    @Test
    public void testExceptionPrintStacktraceNoCause() throws Exception {
        JSQLParserException ex1 = new JSQLParserException("", null);
        StringWriter sw = new StringWriter();
        ex1.printStackTrace(new PrintWriter(sw, true));
        Assert.assertFalse(sw.toString().contains("BRATKARTOFFEL"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ex1.printStackTrace(new PrintStream(bos, true));
        Assert.assertFalse(new String(bos.toByteArray(), StandardCharsets.UTF_8).contains("BRATKARTOFFEL"));
    }

    @Test
    public void testExceptionDefaultContructorCauseInit() throws Exception {
        JSQLParserException ex1 = new JSQLParserException();
        Assert.assertNull(ex1.getCause());
        ex1 = new JSQLParserException(((Throwable) (null)));
        Assert.assertNull(ex1.getCause());
    }
}


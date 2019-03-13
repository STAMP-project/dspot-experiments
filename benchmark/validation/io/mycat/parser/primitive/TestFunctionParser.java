package io.mycat.parser.primitive;


import java.sql.SQLNonTransientException;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Hash Zhang
 * @version 1.0.0
 * @unknown 2016/7/26
 */
public class TestFunctionParser {
    @Test
    public void testMultiFunctions() throws SQLNonTransientException {
        Assert.assertEquals("[arg1, a.t]", testFunctionParse("function1(arg1,a.t)"));
        Assert.assertEquals("[arg1, a.t]", testFunctionParse("function1(arg1,a.t,\"ast(,)\")"));
        Assert.assertEquals("[arg1, a.t, c.t, x]", testFunctionParse("function1(arg1,a.t,\"ast(,)\",\",\",function2(c.t,function3(x)))"));
        Assert.assertEquals("[arg1, a.t, c.t, x]", testFunctionParse("function1(arg1,a.t,\"ast(,)\",\",\",function2(c.t,\"(,)\",function3(function4(x))))"));
    }
}


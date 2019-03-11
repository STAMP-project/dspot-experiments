package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;


public class ImportTest {
    @Test
    public void testImport() throws Exception {
        String express = "import java.math.*;" + (("import com.ql.util.express.test.BeanExample;" + "abc = new BeanExample(\"\u5f20\u4e09\").unionName(\"\u674e\u56db\") ;") + "return new BigInteger(\"1000\");");
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertTrue("import ????", r.toString().equals("1000"));
        System.out.println(r);
        System.out.println(context);
    }
}


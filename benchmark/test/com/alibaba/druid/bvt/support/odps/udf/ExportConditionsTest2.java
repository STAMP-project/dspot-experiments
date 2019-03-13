package com.alibaba.druid.bvt.support.odps.udf;


import com.alibaba.druid.support.opds.udf.ExportConditions;
import junit.framework.TestCase;
import org.junit.Assert;


public class ExportConditionsTest2 extends TestCase {
    ExportConditions udf = new ExportConditions();

    public void test_export_conditions() throws Exception {
        String result = udf.evaluate("select * from t where proof_account is not null");
        Assert.assertEquals("[[\"t\",\"proof_account\",\"IS NOT\",null]]", result);
    }
}


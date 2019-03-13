package com.alibaba.json.bvt;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldTest2_private extends TestCase {
    public void test_0() throws Exception {
        JSONReader read = new JSONReader(new StringReader("[1,2]"));
        read.config(SupportArrayToBean, true);
        EnumFieldTest2_private.Model model = read.readObject(EnumFieldTest2_private.Model.class);
        Assert.assertEquals(EnumFieldTest2_private.Type.B, model.value);
        Assert.assertEquals(EnumFieldTest2_private.Type.C, model.value1);
        read.close();
    }

    public void test_1() throws Exception {
        JSONReader read = new JSONReader(new StringReader("[\"A\",\"B\"]"));
        read.config(SupportArrayToBean, true);
        EnumFieldTest2_private.Model model = read.readObject(EnumFieldTest2_private.Model.class);
        Assert.assertEquals(EnumFieldTest2_private.Type.A, model.value);
        Assert.assertEquals(EnumFieldTest2_private.Type.B, model.value1);
        read.close();
    }

    public void test_2() throws Exception {
        JSONReader read = new JSONReader(new StringReader("[null,null]"));
        read.config(SupportArrayToBean, true);
        EnumFieldTest2_private.Model model = read.readObject(EnumFieldTest2_private.Model.class);
        Assert.assertEquals(null, model.value);
        Assert.assertEquals(null, model.value1);
        read.close();
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            JSONReader read = new JSONReader(new StringReader("[null:null]"));
            read.config(SupportArrayToBean, true);
            EnumFieldTest2_private.Model model = read.readObject(EnumFieldTest2_private.Model.class);
            read.readObject(EnumFieldTest2_private.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_n() throws Exception {
        Exception error = null;
        try {
            JSONReader read = new JSONReader(new StringReader("[n"));
            read.config(SupportArrayToBean, true);
            EnumFieldTest2_private.Model model = read.readObject(EnumFieldTest2_private.Model.class);
            read.readObject(EnumFieldTest2_private.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_nu() throws Exception {
        Exception error = null;
        try {
            JSONReader read = new JSONReader(new StringReader("[nu"));
            read.config(SupportArrayToBean, true);
            EnumFieldTest2_private.Model model = read.readObject(EnumFieldTest2_private.Model.class);
            read.readObject(EnumFieldTest2_private.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_nul() throws Exception {
        Exception error = null;
        try {
            JSONReader read = new JSONReader(new StringReader("[nul"));
            read.config(SupportArrayToBean, true);
            EnumFieldTest2_private.Model model = read.readObject(EnumFieldTest2_private.Model.class);
            read.readObject(EnumFieldTest2_private.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class Model {
        public EnumFieldTest2_private.Type value;

        public EnumFieldTest2_private.Type value1;
    }

    public static enum Type {

        A,
        B,
        C;}
}


package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanFieldTest_array extends TestCase {
    public void test_model_error_t() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[t", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_model_error_tr() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[tr", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_model_error_tru() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[tru", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_model_error_true_notclose() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[true", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_model_error_false_notclose() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[false", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_model_error_f() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[f", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_model_error_fa() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[fa", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_model_error_fal() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[fal", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_model_error_fals() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[fals", BooleanFieldTest_array.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    @JSONType(serialzeFeatures = SerializerFeature.BeanToArray, parseFeatures = Feature.SupportArrayToBean)
    public static class Model {
        public boolean value;

        public boolean value1;
    }
}


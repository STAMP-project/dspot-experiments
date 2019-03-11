package com.alibaba.json.bvt.parser.array;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeanToArrayTest_private extends TestCase {
    public void test_beanToArray_parse() throws Exception {
        String text = "{\"go\":[[\"0\",[true,false],9999999999999,99,\"012345678901234567890123\",\"ftp://gfw.yma.co/x160\",\"xxxx\",\"9876543210123456\",[[\"m\",\"\u4e0d\u8981\u5f00\u5fc3\",\"http://gfw.meiya.co\",\"123456@gg.com\",\"\u9ebb\u9ebb\",\"add\",null,\"9876543210123456\"]],null,[\"add\",\"ww\"],999,1234567890123]],\"success\":true}";
        BeanToArrayTest_private.GR result = JSON.parseObject(text, BeanToArrayTest_private.GR.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.go.size());
        Assert.assertEquals("0", result.go.get(0).bi);
        Assert.assertEquals(true, result.go.get(0).co.qu);
        Assert.assertEquals(false, result.go.get(0).co.sa);
        Assert.assertEquals(9999999999999L, result.go.get(0).gm.getTime());
        Assert.assertEquals(99, result.go.get(0).grCo);
    }

    public static class GR {
        public List<BeanToArrayTest_private.GO> go;

        public boolean success;
    }

    @JSONType(parseFeatures = Feature.SupportArrayToBean)
    private static class GO {
        public String bi;

        public BeanToArrayTest_private.CO co;

        public Date gm;

        public int grCo;

        public String grId;

        public String grNa;

        public String grIm;

        public String ma;

        public List<BeanToArrayTest_private.MO> me = new ArrayList<BeanToArrayTest_private.MO>();

        public int th = 500;

        public List<String> pe = new ArrayList<String>();

        public String no;

        public long ve;
    }

    @JSONType(parseFeatures = Feature.SupportArrayToBean)
    private static class MO {
        public String ope;

        public String use;

        public String log;

        public String rea;

        public String gro;

        public String gen;

        public String hea;

        public String nic;
    }

    @JSONType(parseFeatures = Feature.SupportArrayToBean)
    private static class CO {
        public boolean sa;

        public boolean qu;
    }
}


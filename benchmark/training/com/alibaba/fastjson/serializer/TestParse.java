package com.alibaba.fastjson.serializer;


import com.alibaba.fastjson.JSON;
import java.util.logging.Logger;
import org.junit.Test;


/**
 * test parse json contains jsonobject in javabean
 * Created by yixian on 2016-02-25.
 */
public class TestParse {
    private final Logger logger = Logger.getLogger(TestParse.class.getSimpleName());

    private String jsonString;

    @Test
    public void testParse() {
        logger.info(("parsing json string:" + (jsonString)));
        TestBean testBean = ((TestBean) (JSON.parse(jsonString)));
        assert (testBean.getData()) != null;
        assert "tester".equals(testBean.getName());
        assert "value".equals(testBean.getData().getString("key"));
    }
}


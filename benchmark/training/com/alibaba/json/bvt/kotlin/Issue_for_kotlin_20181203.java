package com.alibaba.json.bvt.kotlin;


import JSON.VERSION;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


public class Issue_for_kotlin_20181203 extends TestCase {
    public void test_user() throws Exception {
        Issue_for_kotlin_20181203.ExtClassLoader classLoader = new Issue_for_kotlin_20181203.ExtClassLoader();
        Class clazz = classLoader.loadClass("com.autonavi.falcon.data.service.vulpeData.ProjectItemCheckItemRelation1");
        String str = "    [{\n" + ((((((((((((("        \"project_item\": \"1105067\",\n" + "        \"project_name\": \"\u660e\u660e\u60f3\",\n") + "        \"product_id_3\": \"0210202\",\n") + "        \"task_type_name\": \"\u9ece\u660eX\",\n") + "        \"product_id_2\": \"02102\",\n") + "        \"product_id_1\": \"021\",\n") + "        \"job_item_type\": \"\u9ad8\u4e2d\",\n") + "        \"product_name_1\": \"\u7280\u5229\",\n") + "        \"product_name_2\": \"\u57fa\u7840\u8def\u7f51\",\n") + "        \"unit\": \"\u6761\",\n") + "        \"product_name_3\": \"\u5230\u5e95\",\n") + "        \"unitremark\": \"\u4efb\u52a1\u6761\u6570\",\n") + "        \"task_type\": \"57205\"\n") + "    }]");
        System.out.println(VERSION);
        Object obj = JSONArray.parseArray(str, clazz);
        String result = JSON.toJSONString(obj);
        System.out.println(result);
        TestCase.assertEquals("[{\"job_item_type\":\"\u9ad8\u4e2d\",\"product_id_1\":\"021\",\"product_id_2\":\"02102\",\"product_id_3\":\"0210202\",\"product_name_1\":\"\u7280\u5229\",\"product_name_2\":\"\u57fa\u7840\u8def\u7f51\",\"product_name_3\":\"\u5230\u5e95\",\"project_item\":\"1105067\",\"project_name\":\"\u660e\u660e\u60f3\",\"task_type\":\"57205\",\"task_type_name\":\"\u9ece\u660eX\",\"unit\":\"\u6761\",\"unitremark\":\"\u4efb\u52a1\u6761\u6570\"}]", result);
    }

    private static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/ProjectItemCheckItemRelation1.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("com.autonavi.falcon.data.service.vulpeData.ProjectItemCheckItemRelation1", bytes, 0, bytes.length);
            }
        }
    }
}


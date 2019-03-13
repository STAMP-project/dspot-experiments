package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


/**
 * Created by wenshao on 2016/10/19.
 */
public class Issue859 extends TestCase {
    public void test_for_issue() throws Exception {
        InputStream is = Issue72.class.getClassLoader().getResourceAsStream("issue859.zip");
        GZIPInputStream gzipInputStream = new GZIPInputStream(is);
        String text = IOUtils.toString(gzipInputStream);
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1; ++i) {
            JSON.parseObject(text);
        }
        // new Gson().fromJson(text, java.util.HashMap.class);
        // new ObjectMapper().readValue(text, java.util.HashMap.class);
        long costMillis = (System.currentTimeMillis()) - startMillis;
        System.out.println(("cost : " + costMillis));
    }
}


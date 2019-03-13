package com.alibaba.json.bvt.issue_1000;


import com.alibaba.fastjson.JSON;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 25/03/2017.
 */
public class Issue1066 extends TestCase {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    public void test_for_issue() throws Exception {
        Map<Issue1066.EnumType, Issue1066.EnumType> map = new HashMap<Issue1066.EnumType, Issue1066.EnumType>();
        map.put(Issue1066.EnumType.ONE, Issue1066.EnumType.TWO);
        System.out.println(("????????:" + map));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Issue1066.serialize(map, bos);
            Object desRes = Issue1066.deserialize(bos.toByteArray());
            System.out.println(("?????????:" + (JSON.toJSONString(desRes))));
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }
        }
    }

    public static enum EnumType {

        ONE(1, "1"),
        TWO(2, "2");
        private int code;

        private String desc;

        EnumType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @Override
        public String toString() {
            return ((((("EnumType{" + "code=") + (code)) + ", desc='") + (desc)) + '\'') + '}';
        }
    }
}


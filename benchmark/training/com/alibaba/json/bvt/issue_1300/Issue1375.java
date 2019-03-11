package com.alibaba.json.bvt.issue_1300;


import CollectionCodec.instance;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.LinkedList;
import junit.framework.TestCase;


/**
 * Created by wenshao on 06/08/2017.
 */
public class Issue1375 extends TestCase {
    public void test_issue() throws Exception {
        TestCase.assertSame(instance, SerializeConfig.getGlobalInstance().getObjectWriter(LinkedList.class));
    }
}


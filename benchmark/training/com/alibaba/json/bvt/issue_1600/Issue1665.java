package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import java.util.Collection;
import junit.framework.TestCase;


public class Issue1665 extends TestCase {
    public void test_for_issue() throws Exception {
        TypeReference<Collection<Issue1665.Model>> typeReference = new TypeReference<Collection<Issue1665.Model>>() {};
        Collection<Issue1665.Model> collection = TypeUtils.cast(JSON.parse("[{\"id\":101}]"), typeReference.getType(), ParserConfig.getGlobalInstance());
        TestCase.assertEquals(1, collection.size());
        Issue1665.Model model = collection.iterator().next();
        TestCase.assertEquals(101, model.id);
    }

    public static class Model {
        public int id;
    }
}


package com.ql.util.express.rule;


import com.ql.util.express.ExpressRunner;
import org.junit.Test;


/**
 * Created by tianqiao on 16/12/21.
 */
public class RuleParseTest {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        parseRule(("rule \'RULE_FEATURESET_BOOK_TOP_CAT_FEATURE\' name \'\u4e66\u7c4d\u7c7b\u76ee\u589e\u52a0\u7279\u5f81topCategoryId\'\n" + ((("when inCategory.mainCategory.categoryId == 33\n" + "then add(inItem.features,\'topCategoryId\',inCategory.mainCategory.categoryId.cast2String());\n") + "when inCategory.mainCategory.categoryId != 33\n") + "then del(inItem.features,'topCategoryId');")), runner);
    }
}


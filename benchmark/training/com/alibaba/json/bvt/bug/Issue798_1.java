/**
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * ?Test.java??????TODO ?????
 *
 * @author jieyu.ljy 2016?7?22? ??12:39:17
 */
public class Issue798_1 extends TestCase {
    public void test_for_issue() throws Exception {
        String str = "<p>?????????? ????????????? ???????????? ?????????</p>";
        String json = JSON.toJSONString(str);
        TestCase.assertEquals("\"<p>\u4e3b\u8981\u5b66\u6821\uff1a\u5bc6\u6b47\u6839\u5927\u5b66 \u5b89\u5a1c\u5821\u5206\u6821\u3001\u4e1c\u5bc6\u897f\u6839\u5927\u5b66\u3001 \u514b\u83b1\u5229\u5b66\u9662\\u007F\u3001\u5eb7\u8003\u8fea\u4e9a\u5b66\u9662 \\u007F\u3001\u74e6\u4ec0\u7279\u6d1b\u793e\u533a\u5b66\u9662\\u007F</p>\"", json);
        String parsedStr = ((String) (JSON.parse(json)));
        TestCase.assertEquals(str, parsedStr);
    }
}


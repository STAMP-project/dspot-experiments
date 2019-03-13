package com.test.script;


import com.jarvis.cache.script.AbstractScriptParser;
import com.jarvis.cache.script.OgnlParser;
import com.test.Simple;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


/**
 *
 *
 * @unknown jiayu.qiu
 */
public class OgnlTest extends TestCase {
    AbstractScriptParser scriptParser = new OgnlParser();

    public void testJavaScript() throws Exception {
        String keySpEL = "'test_'+#args[0]+'_'+#args[1]";
        Simple simple = new Simple();
        simple.setAge(18);
        simple.setName("???");
        simple.setSex(0);
        Object[] arguments = new Object[]{ "1111", "2222", simple };
        String res = scriptParser.getDefinedCacheKey(keySpEL, null, arguments, null, false);
        System.out.println(res);
        TestCase.assertEquals("test_1111_2222", res);
        // ???????
        Boolean rv = scriptParser.getElValue("@@empty(#args[0])", null, arguments, Boolean.class);
        TestCase.assertFalse(rv);
        String val = null;
        val = scriptParser.getElValue("@@hash(#args[0])", null, arguments, String.class);
        System.out.println(val);
        TestCase.assertEquals("1111", val);
        val = scriptParser.getElValue("@@hash(#args[1])", null, arguments, String.class);
        System.out.println(val);
        TestCase.assertEquals("2222", val);
        val = scriptParser.getElValue("@@hash(#args[2])", null, arguments, String.class);
        System.out.println(val);
        TestCase.assertEquals("-290203482_-550943035_-57743508_-1052004462", val);
        val = scriptParser.getElValue("@@hash(#args)", null, arguments, String.class);
        System.out.println(val);
        TestCase.assertEquals("322960956_-1607969343_673194431_1921252123", val);
    }

    public void testReturnIsMapWithHfield() throws Exception {
        String keySpEL = "#retVal.rid";
        Object[] arguments = new Object[]{ "1111", "2222" };
        Map returnObj = new HashMap(1);
        returnObj.put("rid", "iamrid");
        String res = scriptParser.getDefinedCacheKey(keySpEL, null, arguments, returnObj, true);
        System.out.println(res);
        TestCase.assertEquals("iamrid", res);
        Simple simple = new Simple();
        simple.setAge(18);
        simple.setName("???");
        simple.setSex(0);
        keySpEL = "#retVal.name";
        res = scriptParser.getDefinedCacheKey(keySpEL, null, arguments, simple, true);
        System.out.println(res);
        TestCase.assertEquals("???", res);
        // ???????
        Boolean rv = scriptParser.getElValue("@@empty(#args[0])", null, arguments, Boolean.class);
        TestCase.assertFalse(rv);
    }
}


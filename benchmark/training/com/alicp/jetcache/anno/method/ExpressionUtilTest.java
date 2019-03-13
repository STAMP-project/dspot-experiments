/**
 * Created on  13-10-02 22:14
 */
package com.alicp.jetcache.anno.method;


import CacheConsts.UNDEFINED_STRING;
import com.alicp.jetcache.anno.support.CacheUpdateAnnoConfig;
import com.alicp.jetcache.anno.support.CachedAnnoConfig;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class ExpressionUtilTest {
    private CacheInvokeContext context;

    private CachedAnnoConfig cachedAnnoConfig;

    private CacheInvokeConfig cic;

    private Method method;

    @Test
    public void testCondition1() {
        cachedAnnoConfig.setCondition("mvel{args[0]==null}");
        Assertions.assertFalse(ExpressionUtil.evalCondition(context, cachedAnnoConfig));
        context.setArgs(new Object[2]);
        Assertions.assertTrue(ExpressionUtil.evalCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testCondition2() {
        cachedAnnoConfig.setCondition("mvel{args[0].length()==4}");
        context.setArgs(new Object[]{ "1234", 5678 });
        Assertions.assertTrue(ExpressionUtil.evalCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testCondition3() {
        cachedAnnoConfig.setCondition(UNDEFINED_STRING);
        Assertions.assertTrue(ExpressionUtil.evalCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testPostCondition1() {
        cachedAnnoConfig.setPostCondition("result==null");
        context.setArgs(new Object[]{ "1234", 5678 });
        Assertions.assertTrue(ExpressionUtil.evalPostCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testPostCondition2() {
        cachedAnnoConfig.setPostCondition("#p2==1000");
        context.setArgs(new Object[]{ "1234", 5678 });
        Assertions.assertFalse(ExpressionUtil.evalPostCondition(context, cachedAnnoConfig));
        context.setArgs(new Object[]{ "1234", 1000 });
        Assertions.assertTrue(ExpressionUtil.evalPostCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testPostCondition3() {
        cachedAnnoConfig.setPostCondition(UNDEFINED_STRING);
        context.setArgs(new Object[]{ "1234", 5678 });
        Assertions.assertTrue(ExpressionUtil.evalPostCondition(context, cachedAnnoConfig));
    }

    @Test
    public void testKey1() {
        cachedAnnoConfig.setKey("#p2");
        context.setArgs(new Object[]{ "1234", 5678 });
        Assertions.assertEquals(5678, ExpressionUtil.evalKey(context, cachedAnnoConfig));
    }

    @Test
    public void testKey2() {
        cachedAnnoConfig.setKey("#p3");
        context.setArgs(new Object[]{ "1234", 5678 });
        Assertions.assertNull(ExpressionUtil.evalKey(context, cachedAnnoConfig));
    }

    @Test
    public void testValue1() {
        cic.setCachedAnnoConfig(null);
        CacheUpdateAnnoConfig updateAnnoConfig = new CacheUpdateAnnoConfig();
        updateAnnoConfig.setDefineMethod(method);
        updateAnnoConfig.setValue("#p2");
        context.setArgs(new Object[]{ "1234", 5678 });
        Assertions.assertEquals(5678, ExpressionUtil.evalValue(context, updateAnnoConfig));
    }

    @Test
    public void testValue2() {
        cic.setCachedAnnoConfig(null);
        CacheUpdateAnnoConfig updateAnnoConfig = new CacheUpdateAnnoConfig();
        updateAnnoConfig.setDefineMethod(method);
        updateAnnoConfig.setValue("#p3");
        context.setArgs(new Object[]{ "1234", 5678 });
        Assertions.assertNull(ExpressionUtil.evalValue(context, updateAnnoConfig));
    }
}


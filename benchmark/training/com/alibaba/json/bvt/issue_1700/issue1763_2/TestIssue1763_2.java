package com.alibaba.json.bvt.issue_1700.issue1763_2;


import com.alibaba.fastjson.JSON;
import com.alibaba.json.bvt.issue_1700.issue1763_2.bean.BaseResult;
import com.alibaba.json.bvt.issue_1700.issue1763_2.bean.CouponResult;
import com.alibaba.json.bvt.issue_1700.issue1763_2.bean.PageResult;
import java.lang.reflect.Type;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Issue 1763_2
 * ????????????????????????????
 *
 * @author cnlyml
 */
public class TestIssue1763_2<T> {
    private String jsonStr;

    private Class<T> clazz;

    // ??test
    @Test
    public void testFixBug1763_2() {
        BaseResult<PageResult<CouponResult>> data = JSON.parseObject(jsonStr, getType());
        Assert.assertTrue(data.isSuccess());
        Assert.assertTrue(((data.getContent().getList().size()) == 2));
        Assert.assertTrue(data.getContent().getList().get(0).getId().equals(10000001L));
        Assert.assertEquals(CouponResult.class, data.getContent().getList().get(0).getClass());
    }

    // ??BUG
    @Test
    public void testBug1763_2() {
        BaseResult<PageResult<CouponResult>> data = JSON.parseObject(jsonStr, new TypeReferenceBug1763_2<BaseResult<PageResult<T>>>(clazz) {}.getType());
        Assert.assertTrue(data.isSuccess());
        Assert.assertTrue(((data.getContent().getList().size()) == 2));
        try {
            data.getContent().getList().get(0).getId();
        } catch (Throwable ex) {
            Assert.assertEquals(((ex.getCause()) instanceof ClassCastException), false);
        }
    }
}


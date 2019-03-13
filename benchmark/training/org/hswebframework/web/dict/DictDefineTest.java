package org.hswebframework.web.dict;


import com.alibaba.fastjson.JSON;
import java.util.List;
import org.hswebframework.web.dict.defaults.DefaultDictDefineRepository;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zhouhao
 * @since 3.0
 */
public class DictDefineTest {
    private DefaultDictDefineRepository repository = new DefaultDictDefineRepository();

    private DictSupportApi api = new org.hswebframework.web.dict.defaults.DefaultDictSupportApi(repository);

    @Test
    public void testJson() {
        UserCode code = UserCode.CODE0;
        String json = JSON.toJSONString(code);
        System.out.println(json);
        Assert.assertNotNull(json);
        Assert.assertNotNull(JSON.parseObject(json, UserCode.class));
    }

    @Test
    public void testEnumDict() {
        Assert.assertEquals(UserCode.SIMPLE, findByValue(UserCode.class, getValue()).orElse(null));
        Assert.assertEquals(UserCode.SIMPLE, findByText(UserCode.class, getText()).orElse(null));
        Assert.assertEquals(UserCode.SIMPLE, find(UserCode.class, getText()).orElse(null));
        long bit = toMask(UserCode.values());
        System.out.println(maskIn(bit, UserCode.SIMPLE, UserCode.TEST, UserCode.SIMPLE));
        long bit2 = toMask(UserCode.SIMPLE, UserCode.CODE0, UserCode.SIMPLE);
        Assert.assertTrue(maskInAny(bit2, UserCode.SIMPLE, UserCode.CODE4, UserCode.CODE0));
        Assert.assertFalse(maskInAny(bit2, UserCode.CODE1, UserCode.CODE4, UserCode.CODE5));
        for (UserCode userCode : UserCode.values()) {
            Assert.assertTrue(userCode.in(bit));
        }
        Assert.assertTrue(in(UserCode.SIMPLE, UserCode.CODE1, UserCode.CODE2));
        Assert.assertFalse(in(UserCode.SIMPLE, UserCode.CODE1, UserCode.CODE2));
        Assert.assertTrue(EnumDict.EnumDict.in(UserCode.SIMPLE, UserCode.SIMPLE, UserCode.CODE1, UserCode.CODE2));
        Assert.assertFalse(EnumDict.EnumDict.in(UserCode.CODE7, UserCode.SIMPLE, UserCode.CODE1, UserCode.CODE2));
        List<UserCode> codes = getByMask(UserCode.class, bit);
    }
}


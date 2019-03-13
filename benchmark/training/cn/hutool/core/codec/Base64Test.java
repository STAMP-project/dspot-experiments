package cn.hutool.core.codec;


import cn.hutool.core.util.StrUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base64????
 *
 * @author looly
 */
public class Base64Test {
    @Test
    public void encodeAndDecodeTest() {
        String a = "????????????66";
        String encode = Base64.encode(a);
        Assert.assertEquals("5Lym5a625piv5LiA5Liq6Z2e5bi46ZW/55qE5a2X56ym5LiyNjY=", encode);
        String decodeStr = Base64.decodeStr(encode);
        Assert.assertEquals(a, decodeStr);
    }

    @Test
    public void urlSafeEncodeAndDecodeTest() {
        String a = "?????????55";
        String encode = StrUtil.utf8Str(Base64.encodeUrlSafe(StrUtil.utf8Bytes(a), false));
        Assert.assertEquals("5bm_5bee5Lym5a626ZyA6KaB5a6J5YWo5oSfNTU", encode);
        String decodeStr = Base64.decodeStr(encode);
        Assert.assertEquals(a, decodeStr);
    }
}


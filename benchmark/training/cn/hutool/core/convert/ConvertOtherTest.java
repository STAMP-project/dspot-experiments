package cn.hutool.core.convert;


import CharsetUtil.CHARSET_UTF_8;
import CharsetUtil.ISO_8859_1;
import CharsetUtil.UTF_8;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * ????
 *
 * @author Looly
 */
public class ConvertOtherTest {
    @Test
    public void hexTest() {
        String a = "?????????????";
        String hex = Convert.toHex(a, CHARSET_UTF_8);
        Assert.assertEquals("e68891e698afe4b880e4b8aae5b08fe5b08fe79a84e58fafe788b1e79a84e5ad97e7aca6e4b8b2", hex);
        String raw = Convert.hexToStr(hex, CHARSET_UTF_8);
        Assert.assertEquals(a, raw);
    }

    @Test
    public void unicodeTest() {
        String a = "?????????????";
        String unicode = Convert.strToUnicode(a);
        Assert.assertEquals("\\u6211\\u662f\\u4e00\\u4e2a\\u5c0f\\u5c0f\\u7684\\u53ef\\u7231\\u7684\\u5b57\\u7b26\\u4e32", unicode);
        String raw = Convert.unicodeToStr(unicode);
        Assert.assertEquals(raw, a);
        // ?????????Unicode
        String str = "???";
        String unicode2 = Convert.strToUnicode(str);
        Assert.assertEquals("\\u4f60\\u00a0\\u597d", unicode2);
        String str2 = Convert.unicodeToStr(unicode2);
        Assert.assertEquals(str, str2);
    }

    @Test
    public void convertCharsetTest() {
        String a = "?????";
        // ???result???
        String result = Convert.convertCharset(a, UTF_8, ISO_8859_1);
        String raw = Convert.convertCharset(result, ISO_8859_1, "UTF-8");
        Assert.assertEquals(raw, a);
    }

    @Test
    public void convertTimeTest() {
        long a = 4535345;
        long minutes = Convert.convertTime(a, TimeUnit.MILLISECONDS, TimeUnit.MINUTES);
        Assert.assertEquals(75, minutes);
    }

    @Test
    public void digitToChineseTest() {
        double a = 67556.32;
        String digitUppercase = Convert.digitToChinese(a);
        Assert.assertEquals("??????????????", digitUppercase);
        a = 1024.0;
        digitUppercase = Convert.digitToChinese(a);
        Assert.assertEquals("????????", digitUppercase);
        a = 1024;
        digitUppercase = Convert.digitToChinese(a);
        Assert.assertEquals("????????", digitUppercase);
    }

    @Test
    public void wrapUnwrapTest() {
        // ???
        Class<?> wrapClass = Integer.class;
        Class<?> unWraped = Convert.unWrap(wrapClass);
        Assert.assertEquals(int.class, unWraped);
        // ??
        Class<?> primitiveClass = long.class;
        Class<?> wraped = Convert.wrap(primitiveClass);
        Assert.assertEquals(Long.class, wraped);
    }
}


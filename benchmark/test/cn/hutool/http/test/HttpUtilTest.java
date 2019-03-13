package cn.hutool.http.test;


import CharsetUtil.CHARSET_UTF_8;
import CharsetUtil.UTF_8;
import HttpUtil.CHARSET_PATTERN;
import HttpUtil.META_CHARSET_PATTERN;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class HttpUtilTest {
    @Test
    public void decodeParamsTest() {
        String paramsStr = "uuuu=0&a=b&c=%3F%23%40!%24%25%5E%26%3Ddsssss555555";
        Map<String, List<String>> map = HttpUtil.decodeParams(paramsStr, UTF_8);
        Assert.assertEquals("0", map.get("uuuu").get(0));
        Assert.assertEquals("b", map.get("a").get(0));
        Assert.assertEquals("?#@!$%^&=dsssss555555", map.get("c").get(0));
    }

    @Test
    public void toParamsTest() {
        String paramsStr = "uuuu=0&a=b&c=3Ddsssss555555";
        Map<String, List<String>> map = HttpUtil.decodeParams(paramsStr, UTF_8);
        String encodedParams = HttpUtil.toParams(((Map<String, List<String>>) (map)));
        Assert.assertEquals(paramsStr, encodedParams);
    }

    @Test
    public void encodeParamTest() {
        // ?????????&?????????
        String paramsStr = "?a=b&c=d&";
        String encode = HttpUtil.encodeParams(paramsStr, CHARSET_UTF_8);
        Assert.assertEquals("a=b&c=d", encode);
        // url?????
        paramsStr = "http://www.abc.dd?a=b&c=d&";
        encode = HttpUtil.encodeParams(paramsStr, CHARSET_UTF_8);
        Assert.assertEquals("http://www.abc.dd?a=b&c=d", encode);
        // b=b??=???????????encode
        paramsStr = "a=b=b&c=d&";
        encode = HttpUtil.encodeParams(paramsStr, CHARSET_UTF_8);
        Assert.assertEquals("a=b=b&c=d", encode);
        // =d???????key??
        paramsStr = "a=bbb&c=d&=d";
        encode = HttpUtil.encodeParams(paramsStr, CHARSET_UTF_8);
        Assert.assertEquals("a=bbb&c=d&=d", encode);
        // d=???????value??
        paramsStr = "a=bbb&c=d&d=";
        encode = HttpUtil.encodeParams(paramsStr, CHARSET_UTF_8);
        Assert.assertEquals("a=bbb&c=d&d=", encode);
        // ??&&?????????????
        paramsStr = "a=bbb&c=d&&&d=";
        encode = HttpUtil.encodeParams(paramsStr, CHARSET_UTF_8);
        Assert.assertEquals("a=bbb&c=d&d=", encode);
        // &d&????????????
        paramsStr = "a=bbb&c=d&d&";
        encode = HttpUtil.encodeParams(paramsStr, CHARSET_UTF_8);
        Assert.assertEquals("a=bbb&c=d&d=", encode);
        // ?????????
        paramsStr = "a=bbb&c=??&??&";
        encode = HttpUtil.encodeParams(paramsStr, CHARSET_UTF_8);
        Assert.assertEquals("a=bbb&c=%E4%BD%A0%E5%A5%BD&%E5%93%88%E5%96%BD=", encode);
    }

    @Test
    public void decodeParamTest() {
        // ???????
        String a = "?a=b&c=d&";
        Map<String, List<String>> map = HttpUtil.decodeParams(a, UTF_8);
        Assert.assertEquals("b", map.get("a").get(0));
        Assert.assertEquals("d", map.get("c").get(0));
        // =e?????key?e?value
        a = "?a=b&c=d&=e";
        map = HttpUtil.decodeParams(a, UTF_8);
        Assert.assertEquals("b", map.get("a").get(0));
        Assert.assertEquals("d", map.get("c").get(0));
        Assert.assertEquals("e", map.get("").get(0));
        // ???&??
        a = "?a=b&c=d&=e&&&&";
        map = HttpUtil.decodeParams(a, UTF_8);
        Assert.assertEquals("b", map.get("a").get(0));
        Assert.assertEquals("d", map.get("c").get(0));
        Assert.assertEquals("e", map.get("").get(0));
        // ???
        a = "?a=b&c=d&e=";
        map = HttpUtil.decodeParams(a, UTF_8);
        Assert.assertEquals("b", map.get("a").get(0));
        Assert.assertEquals("d", map.get("c").get(0));
        Assert.assertEquals("", map.get("e").get(0));
        // &=?????????
        a = "a=b&c=d&=";
        map = HttpUtil.decodeParams(a, UTF_8);
        Assert.assertEquals("b", map.get("a").get(0));
        Assert.assertEquals("d", map.get("c").get(0));
        Assert.assertEquals("", map.get("").get(0));
        // &e&???????????key
        a = "a=b&c=d&e&";
        map = HttpUtil.decodeParams(a, UTF_8);
        Assert.assertEquals("b", map.get("a").get(0));
        Assert.assertEquals("d", map.get("c").get(0));
        Assert.assertEquals("", map.get("e").get(0));
        // ??????????
        a = "a=bbb&c=%E4%BD%A0%E5%A5%BD&%E5%93%88%E5%96%BD=";
        map = HttpUtil.decodeParams(a, UTF_8);
        Assert.assertEquals("bbb", map.get("a").get(0));
        Assert.assertEquals("??", map.get("c").get(0));
        Assert.assertEquals("", map.get("??").get(0));
    }

    @Test
    public void urlWithFormTest() {
        Map<String, Object> param = new HashMap<>();
        param.put("AccessKeyId", "123");
        param.put("Action", "DescribeDomainRecords");
        param.put("Format", "date");
        param.put("DomainName", "lesper.cn");// ????

        param.put("SignatureMethod", "POST");
        param.put("SignatureNonce", "123");
        param.put("SignatureVersion", "4.3.1");
        param.put("Timestamp", 123432453);
        param.put("Version", "1.0");
        String urlWithForm = HttpUtil.urlWithForm("http://api.hutool.cn/login?type=aaa", param, CHARSET_UTF_8, false);
        Assert.assertEquals("http://api.hutool.cn/login?type=aaa&Format=date&Action=DescribeDomainRecords&AccessKeyId=123&SignatureMethod=POST&DomainName=lesper.cn&SignatureNonce=123&Version=1.0&SignatureVersion=4.3.1&Timestamp=123432453", urlWithForm);
        urlWithForm = HttpUtil.urlWithForm("http://api.hutool.cn/login?type=aaa", param, CHARSET_UTF_8, false);
        Assert.assertEquals("http://api.hutool.cn/login?type=aaa&Format=date&Action=DescribeDomainRecords&AccessKeyId=123&SignatureMethod=POST&DomainName=lesper.cn&SignatureNonce=123&Version=1.0&SignatureVersion=4.3.1&Timestamp=123432453", urlWithForm);
    }

    @Test
    public void getCharsetTest() {
        String charsetName = ReUtil.get(CHARSET_PATTERN, "Charset=UTF-8;fq=0.9", 1);
        Assert.assertEquals("UTF-8", charsetName);
        charsetName = ReUtil.get(META_CHARSET_PATTERN, "<meta charset=utf-8", 1);
        Assert.assertEquals("utf-8", charsetName);
        charsetName = ReUtil.get(META_CHARSET_PATTERN, "<meta charset='utf-8'", 1);
        Assert.assertEquals("utf-8", charsetName);
        charsetName = ReUtil.get(META_CHARSET_PATTERN, "<meta charset=\"utf-8\"", 1);
        Assert.assertEquals("utf-8", charsetName);
        charsetName = ReUtil.get(META_CHARSET_PATTERN, "<meta charset = \"utf-8\"", 1);
        Assert.assertEquals("utf-8", charsetName);
    }
}


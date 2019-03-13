package cn.hutool.core.util;


import cn.hutool.core.collection.CollectionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


public class ReUtilTest {
    final String content = "ZZZaaabbbccc??1234";

    @Test
    public void getTest() {
        String resultGet = ReUtil.get("\\w{2}", content, 0);
        Assert.assertEquals("ZZ", resultGet);
    }

    @Test
    public void extractMultiTest() {
        // ???????????????
        String resultExtractMulti = ReUtil.extractMulti("(\\w)aa(\\w)", content, "$1-$2");
        Assert.assertEquals("Z-a", resultExtractMulti);
    }

    @Test
    public void extractMultiTest2() {
        // ???????????????
        String resultExtractMulti = ReUtil.extractMulti("(\\w)(\\w)(\\w)(\\w)(\\w)(\\w)(\\w)(\\w)(\\w)(\\w)", content, "$1-$2-$3-$4-$5-$6-$7-$8-$9-$10");
        Assert.assertEquals("Z-Z-Z-a-a-a-b-b-b-c", resultExtractMulti);
    }

    @Test
    public void delFirstTest() {
        // ???????????
        String resultDelFirst = ReUtil.delFirst("(\\w)aa(\\w)", content);
        Assert.assertEquals("ZZbbbccc??1234", resultDelFirst);
    }

    @Test
    public void delAllTest() {
        // ??????????
        String content = "?????eee![images]http://abc.com/2.gpg]???eee![images]http://abc.com/2.gpg]???";
        String resultDelAll = ReUtil.delAll("!\\[images\\][^\\u4e00-\\u9fa5\\\\s]*", content);
        Assert.assertEquals("?????eee???eee???", resultDelAll);
    }

    @Test
    public void findAllTest() {
        // ????????
        List<String> resultFindAll = ReUtil.findAll("\\w{2}", content, 0, new ArrayList<String>());
        ArrayList<String> expected = CollectionUtil.newArrayList("ZZ", "Za", "aa", "bb", "bc", "cc", "12", "34");
        Assert.assertEquals(expected, resultFindAll);
    }

    @Test
    public void getFirstNumberTest() {
        // ??????????
        Integer resultGetFirstNumber = ReUtil.getFirstNumber(content);
        Assert.assertEquals(Integer.valueOf(1234), resultGetFirstNumber);
    }

    @Test
    public void isMatchTest() {
        // ?????????????
        boolean isMatch = ReUtil.isMatch("\\w+[\u4e00-\u9fff]+\\d+", content);
        Assert.assertTrue(isMatch);
    }

    @Test
    public void replaceAllTest() {
        // ????????????????????????replacementTemplate??$1????1????
        // ???1234??? ->1234<-
        String replaceAll = ReUtil.replaceAll(content, "(\\d+)", "->$1<-");
        Assert.assertEquals("ZZZaaabbbccc??->1234<-", replaceAll);
    }

    @Test
    public void replaceAllTest2() {
        // ???1234??? ->1234<-
        String replaceAll = ReUtil.replaceAll(this.content, "(\\d+)", new cn.hutool.core.lang.Func1<Matcher, String>() {
            @Override
            public String call(Matcher parameters) {
                return ("->" + (parameters.group(1))) + "<-";
            }
        });
        Assert.assertEquals("ZZZaaabbbccc??->1234<-", replaceAll);
    }

    @Test
    public void replaceTest() {
        String str = "AAABBCCCBBDDDBB";
        String replace = StrUtil.replace(str, 0, "BB", "22", false);
        Assert.assertEquals("AAA22CCC22DDD22", replace);
        replace = StrUtil.replace(str, 3, "BB", "22", false);
        Assert.assertEquals("AAA22CCC22DDD22", replace);
        replace = StrUtil.replace(str, 4, "BB", "22", false);
        Assert.assertEquals("AAABBCCC22DDD22", replace);
        replace = StrUtil.replace(str, 4, "bb", "22", true);
        Assert.assertEquals("AAABBCCC22DDD22", replace);
        replace = StrUtil.replace(str, 4, "bb", "", true);
        Assert.assertEquals("AAABBCCCDDD", replace);
        replace = StrUtil.replace(str, 4, "bb", null, true);
        Assert.assertEquals("AAABBCCCDDD", replace);
    }

    @Test
    public void escapeTest() {
        // ????????????????????
        String escape = ReUtil.escape("???$??{}");
        Assert.assertEquals("\u6211\u6709\u4e2a\\$\u7b26\u53f7\\{\\}", escape);
    }

    @Test
    public void getAllGroupsTest() {
        // ????????????????????
        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)-(\\d+)");
        List<String> allGroups = ReUtil.getAllGroups(pattern, "192-168-1-1");
        Assert.assertEquals("192-168-1", allGroups.get(0));
        Assert.assertEquals("192", allGroups.get(1));
        Assert.assertEquals("168", allGroups.get(2));
        Assert.assertEquals("1", allGroups.get(3));
        allGroups = ReUtil.getAllGroups(pattern, "192-168-1-1", false);
        Assert.assertEquals("192", allGroups.get(0));
        Assert.assertEquals("168", allGroups.get(1));
        Assert.assertEquals("1", allGroups.get(2));
    }
}


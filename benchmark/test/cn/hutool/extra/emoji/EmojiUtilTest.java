package cn.hutool.extra.emoji;


import org.junit.Assert;
import org.junit.Test;


public class EmojiUtilTest {
    @Test
    public void toUnicodeTest() {
        String emoji = EmojiUtil.toUnicode(":smile:");
        Assert.assertEquals("?", emoji);
    }

    @Test
    public void toAliasTest() {
        String alias = EmojiUtil.toAlias("?");
        Assert.assertEquals(":smile:", alias);
    }
}


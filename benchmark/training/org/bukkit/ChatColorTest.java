package org.bukkit;


import ChatColor.BLACK;
import ChatColor.BLUE;
import ChatColor.COLOR_CHAR;
import ChatColor.ITALIC;
import ChatColor.RED;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static ChatColor.AQUA;
import static ChatColor.BLUE;
import static ChatColor.DARK_AQUA;
import static ChatColor.DARK_BLUE;
import static ChatColor.DARK_GRAY;
import static ChatColor.DARK_GREEN;
import static ChatColor.DARK_PURPLE;
import static ChatColor.DARK_RED;
import static ChatColor.GOLD;
import static ChatColor.GRAY;
import static ChatColor.GREEN;
import static ChatColor.ITALIC;
import static ChatColor.LIGHT_PURPLE;
import static ChatColor.MAGIC;
import static ChatColor.RED;
import static ChatColor.WHITE;
import static ChatColor.YELLOW;


public class ChatColorTest {
    @Test
    public void getByChar() {
        for (ChatColor color : ChatColor.values()) {
            Assert.assertThat(ChatColor.getByChar(color.getChar()), CoreMatchers.is(color));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByStringWithNull() {
        ChatColor.getByChar(((String) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByStringWithEmpty() {
        ChatColor.getByChar("");
    }

    @Test
    public void getByNull() {
        Assert.assertThat(ChatColor.stripColor(null), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void getByString() {
        for (ChatColor color : ChatColor.values()) {
            Assert.assertThat(ChatColor.getByChar(String.valueOf(color.getChar())), CoreMatchers.is(color));
        }
    }

    @Test
    public void stripColorOnNullString() {
        Assert.assertThat(ChatColor.stripColor(null), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void stripColor() {
        StringBuilder subject = new StringBuilder();
        StringBuilder expected = new StringBuilder();
        final String filler = "test";
        for (ChatColor color : ChatColor.values()) {
            subject.append(color).append(filler);
            expected.append(filler);
        }
        Assert.assertThat(ChatColor.stripColor(subject.toString()), CoreMatchers.is(expected.toString()));
    }

    @Test
    public void toStringWorks() {
        for (ChatColor color : ChatColor.values()) {
            Assert.assertThat(String.format("%c%c", COLOR_CHAR, color.getChar()), CoreMatchers.is(color.toString()));
        }
    }

    @Test
    public void translateAlternateColorCodes() {
        String s = "&0&1&2&3&4&5&6&7&8&9&A&a&B&b&C&c&D&d&E&e&F&f&K&k & more";
        String t = ChatColor.translateAlternateColorCodes('&', s);
        String u = ((((((((((((((((((((((((BLACK.toString()) + (DARK_BLUE)) + (DARK_GREEN)) + (DARK_AQUA)) + (DARK_RED)) + (DARK_PURPLE)) + (GOLD)) + (GRAY)) + (DARK_GRAY)) + (BLUE)) + (GREEN)) + (GREEN)) + (AQUA)) + (AQUA)) + (RED)) + (RED)) + (LIGHT_PURPLE)) + (LIGHT_PURPLE)) + (YELLOW)) + (YELLOW)) + (WHITE)) + (WHITE)) + (MAGIC)) + (MAGIC)) + " & more";
        Assert.assertThat(t, CoreMatchers.is(u));
    }

    @Test
    public void getChatColors() {
        String s = String.format("%c%ctest%c%ctest%c", COLOR_CHAR, RED.getChar(), COLOR_CHAR, ITALIC.getChar(), COLOR_CHAR);
        String expected = (RED.toString()) + (ITALIC);
        Assert.assertThat(ChatColor.getLastColors(s), CoreMatchers.is(expected));
        s = String.format("%c%ctest%c%ctest", COLOR_CHAR, RED.getChar(), COLOR_CHAR, BLUE.getChar());
        Assert.assertThat(ChatColor.getLastColors(s), CoreMatchers.is(BLUE.toString()));
    }
}


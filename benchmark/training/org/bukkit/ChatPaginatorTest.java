package org.bukkit;


import ChatColor.RED;
import ChatColor.WHITE;
import ChatPaginator.ChatPage;
import org.bukkit.util.ChatPaginator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static ChatColor.BLUE;
import static ChatColor.RED;


/**
 *
 */
public class ChatPaginatorTest {
    @Test
    public void testWordWrap1() {
        String rawString = (RED) + "123456789 123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);
        Assert.assertThat(lines.length, CoreMatchers.is(2));
        Assert.assertThat(lines[0], CoreMatchers.is(((RED) + "123456789 123456789")));
        Assert.assertThat(lines[1], CoreMatchers.is(((RED.toString()) + "123456789")));
    }

    @Test
    public void testWordWrap2() {
        String rawString = "123456789 123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 22);
        Assert.assertThat(lines.length, CoreMatchers.is(2));
        Assert.assertThat(lines[0], CoreMatchers.is(((WHITE.toString()) + "123456789 123456789")));
        Assert.assertThat(lines[1], CoreMatchers.is(((WHITE.toString()) + "123456789")));
    }

    @Test
    public void testWordWrap3() {
        String rawString = ((((((RED) + "123456789 ") + (RED)) + (RED)) + "123456789 ") + (RED)) + "123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 16);
        Assert.assertThat(lines.length, CoreMatchers.is(3));
        Assert.assertThat(lines[0], CoreMatchers.is(((RED) + "123456789")));
        Assert.assertThat(lines[1], CoreMatchers.is((((RED.toString()) + (RED)) + "123456789")));
        Assert.assertThat(lines[2], CoreMatchers.is(((RED) + "123456789")));
    }

    @Test
    public void testWordWrap4() {
        String rawString = "123456789 123456789 123456789 12345";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);
        Assert.assertThat(lines.length, CoreMatchers.is(2));
        Assert.assertThat(lines[0], CoreMatchers.is(((WHITE.toString()) + "123456789 123456789")));
        Assert.assertThat(lines[1], CoreMatchers.is(((WHITE.toString()) + "123456789 12345")));
    }

    @Test
    public void testWordWrap5() {
        String rawString = "123456789\n123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);
        Assert.assertThat(lines.length, CoreMatchers.is(2));
        Assert.assertThat(lines[0], CoreMatchers.is(((WHITE.toString()) + "123456789")));
        Assert.assertThat(lines[1], CoreMatchers.is(((WHITE.toString()) + "123456789 123456789")));
    }

    @Test
    public void testWordWrap6() {
        String rawString = "12345678   23456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);
        Assert.assertThat(lines.length, CoreMatchers.is(2));
        Assert.assertThat(lines[0], CoreMatchers.is(((WHITE.toString()) + "12345678   23456789")));
        Assert.assertThat(lines[1], CoreMatchers.is(((WHITE.toString()) + "123456789")));
    }

    @Test
    public void testWordWrap7() {
        String rawString = "12345678   23456789   123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);
        Assert.assertThat(lines.length, CoreMatchers.is(2));
        Assert.assertThat(lines[0], CoreMatchers.is(((WHITE.toString()) + "12345678   23456789")));
        Assert.assertThat(lines[1], CoreMatchers.is(((WHITE.toString()) + "123456789")));
    }

    @Test
    public void testWordWrap8() {
        String rawString = "123456789 123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 6);
        Assert.assertThat(lines.length, CoreMatchers.is(6));
        Assert.assertThat(lines[0], CoreMatchers.is(((WHITE.toString()) + "123456")));
        Assert.assertThat(lines[1], CoreMatchers.is(((WHITE.toString()) + "789")));
        Assert.assertThat(lines[2], CoreMatchers.is(((WHITE.toString()) + "123456")));
        Assert.assertThat(lines[3], CoreMatchers.is(((WHITE.toString()) + "789")));
        Assert.assertThat(lines[4], CoreMatchers.is(((WHITE.toString()) + "123456")));
        Assert.assertThat(lines[5], CoreMatchers.is(((WHITE.toString()) + "789")));
    }

    @Test
    public void testWordWrap9() {
        String rawString = "1234 123456789 123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 6);
        Assert.assertThat(lines.length, CoreMatchers.is(7));
        Assert.assertThat(lines[0], CoreMatchers.is(((WHITE.toString()) + "1234")));
        Assert.assertThat(lines[1], CoreMatchers.is(((WHITE.toString()) + "123456")));
        Assert.assertThat(lines[2], CoreMatchers.is(((WHITE.toString()) + "789")));
        Assert.assertThat(lines[3], CoreMatchers.is(((WHITE.toString()) + "123456")));
        Assert.assertThat(lines[4], CoreMatchers.is(((WHITE.toString()) + "789")));
        Assert.assertThat(lines[5], CoreMatchers.is(((WHITE.toString()) + "123456")));
        Assert.assertThat(lines[6], CoreMatchers.is(((WHITE.toString()) + "789")));
    }

    @Test
    public void testWordWrap10() {
        String rawString = "123456789\n123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);
        Assert.assertThat(lines.length, CoreMatchers.is(2));
        Assert.assertThat(lines[0], CoreMatchers.is(((WHITE.toString()) + "123456789")));
        Assert.assertThat(lines[1], CoreMatchers.is(((WHITE.toString()) + "123456789")));
    }

    @Test
    public void testWordWrap11() {
        String rawString = (((RED) + "a a a ") + (BLUE)) + "a a";
        String[] lines = ChatPaginator.wordWrap(rawString, 9);
        Assert.assertThat(lines.length, CoreMatchers.is(1));
        Assert.assertThat(lines[0], CoreMatchers.is(((((RED) + "a a a ") + (BLUE)) + "a a")));
    }

    @Test
    public void testPaginate1() {
        String rawString = "1234 123456789 123456789 123456789";
        ChatPaginator.ChatPage page = ChatPaginator.paginate(rawString, 1, 6, 2);
        Assert.assertThat(page.getPageNumber(), CoreMatchers.is(1));
        Assert.assertThat(page.getTotalPages(), CoreMatchers.is(4));
        Assert.assertThat(page.getLines().length, CoreMatchers.is(2));
        Assert.assertThat(page.getLines()[0], CoreMatchers.is(((WHITE.toString()) + "1234")));
        Assert.assertThat(page.getLines()[1], CoreMatchers.is(((WHITE.toString()) + "123456")));
    }

    @Test
    public void testPaginate2() {
        String rawString = "1234 123456789 123456789 123456789";
        ChatPaginator.ChatPage page = ChatPaginator.paginate(rawString, 2, 6, 2);
        Assert.assertThat(page.getPageNumber(), CoreMatchers.is(2));
        Assert.assertThat(page.getTotalPages(), CoreMatchers.is(4));
        Assert.assertThat(page.getLines().length, CoreMatchers.is(2));
        Assert.assertThat(page.getLines()[0], CoreMatchers.is(((WHITE.toString()) + "789")));
        Assert.assertThat(page.getLines()[1], CoreMatchers.is(((WHITE.toString()) + "123456")));
    }

    @Test
    public void testPaginate3() {
        String rawString = "1234 123456789 123456789 123456789";
        ChatPaginator.ChatPage page = ChatPaginator.paginate(rawString, 4, 6, 2);
        Assert.assertThat(page.getPageNumber(), CoreMatchers.is(4));
        Assert.assertThat(page.getTotalPages(), CoreMatchers.is(4));
        Assert.assertThat(page.getLines().length, CoreMatchers.is(1));
        Assert.assertThat(page.getLines()[0], CoreMatchers.is(((WHITE.toString()) + "789")));
    }
}


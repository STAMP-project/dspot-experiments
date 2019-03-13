package org.bukkit.event;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class PlayerChatTabCompleteEventTest {
    @Test
    public void testGetLastToken() {
        Assert.assertThat(getToken("Hello everyone!"), CoreMatchers.is("everyone!"));
        Assert.assertThat(getToken(" welcome to the show..."), CoreMatchers.is("show..."));
        Assert.assertThat(getToken("The whitespace is here "), CoreMatchers.is(""));
        Assert.assertThat(getToken("Too much whitespace is here  "), CoreMatchers.is(""));
        Assert.assertThat(getToken("The_whitespace_is_missing"), CoreMatchers.is("The_whitespace_is_missing"));
        Assert.assertThat(getToken(""), CoreMatchers.is(""));
        Assert.assertThat(getToken(" "), CoreMatchers.is(""));
    }
}


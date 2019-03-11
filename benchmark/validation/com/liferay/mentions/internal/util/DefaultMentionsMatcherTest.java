/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.mentions.internal.util;


import com.liferay.mentions.matcher.MentionsMatcher;
import java.util.Arrays;
import org.junit.Test;


/**
 *
 *
 * @author Adolfo P?rez
 */
public class DefaultMentionsMatcherTest {
    @Test
    public void testMatchBBCodeAtMention() {
        assertEquals("user1", _mentionsMatcher.match("[span]@user1[span]"));
    }

    @Test
    public void testMatchBBCodeSpecialCharacters() {
        assertEquals(DefaultMentionsMatcherTest._SCREEN_NAME_WITH_SPECIAL_CHARS, _mentionsMatcher.match((("[span]@" + (DefaultMentionsMatcherTest._SCREEN_NAME_WITH_SPECIAL_CHARS)) + "[span]")));
    }

    @Test
    public void testMatchBBCodeXMLEntityMention() {
        assertEquals("user1", _mentionsMatcher.match("[span]&#64;user1[span]"));
    }

    @Test
    public void testMatchHTMLAtMention() {
        assertEquals("user1", _mentionsMatcher.match("<span>@user1</span>"));
    }

    @Test
    public void testMatchHTMLSpecialCharacters() {
        assertEquals(DefaultMentionsMatcherTest._SCREEN_NAME_WITH_SPECIAL_CHARS, _mentionsMatcher.match((("<span>@" + (DefaultMentionsMatcherTest._SCREEN_NAME_WITH_SPECIAL_CHARS)) + "</span>")));
    }

    @Test
    public void testMatchHTMLXMLEntityMention() {
        assertEquals("user1", _mentionsMatcher.match("<span>&#64;user1</span>"));
    }

    @Test
    public void testMatchMixedContent() {
        String content = "Lorem ipsum @user1 dolor sit amet, consectetur adipiscing elit " + ("Sed non venenatis &#64;user2 justo. Morbi augue mauris, " + "suscipit ]@user3 tempus@notthis @@neitherthis et,>@user4");
        assertEquals(Arrays.asList("user1", "user2", "user3", "user4"), _mentionsMatcher.match(content));
    }

    @Test
    public void testMatchSimpleAtMention() {
        assertEquals("user1", _mentionsMatcher.match("@user1"));
    }

    @Test
    public void testMatchSimpleAtMentions() {
        assertEquals(Arrays.asList("user1", "user2"), _mentionsMatcher.match("@user1 @user2"));
    }

    @Test
    public void testMatchSimpleXMLEntityMention() {
        assertEquals("user1", _mentionsMatcher.match("&#64;user1"));
    }

    @Test
    public void testMatchSimpleXMLEntityMentions() {
        assertEquals(Arrays.asList("user1", "user2"), _mentionsMatcher.match("&#64;user1 &#64;user2"));
    }

    private static final String _SCREEN_NAME_SPECIAL_CHARS = "-._";

    private static final String _SCREEN_NAME_WITH_SPECIAL_CHARS = "user" + (DefaultMentionsMatcherTest._SCREEN_NAME_SPECIAL_CHARS);

    private MentionsMatcher _mentionsMatcher;
}


package com.vdurmont.emoji;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class EmojiManagerTest {
    @Test
    public void getForTag_with_unknown_tag_returns_null() throws IOException {
        // GIVEN
        // WHEN
        Set<Emoji> emojis = EmojiManager.getForTag("jkahsgdfjksghfjkshf");
        // THEN
        Assert.assertNull(emojis);
    }

    @Test
    public void getForTag_returns_the_emojis_for_the_tag() throws IOException {
        // GIVEN
        // WHEN
        Set<Emoji> emojis = EmojiManager.getForTag("happy");
        // THEN
        Assert.assertEquals(4, emojis.size());
        Assert.assertTrue(TestTools.containsEmojis(emojis, "smile", "smiley", "grinning", "satisfied"));
    }

    @Test
    public void getForAlias_with_unknown_alias_returns_null() throws IOException {
        // GIVEN
        // WHEN
        Emoji emoji = EmojiManager.getForAlias("jkahsgdfjksghfjkshf");
        // THEN
        Assert.assertNull(emoji);
    }

    @Test
    public void getForAlias_returns_the_emoji_for_the_alias() throws IOException {
        // GIVEN
        // WHEN
        Emoji emoji = EmojiManager.getForAlias("smile");
        // THEN
        Assert.assertEquals("smiling face with open mouth and smiling eyes", emoji.getDescription());
    }

    @Test
    public void getForAlias_with_colons_returns_the_emoji_for_the_alias() throws IOException {
        // GIVEN
        // WHEN
        Emoji emoji = EmojiManager.getForAlias(":smile:");
        // THEN
        Assert.assertEquals("smiling face with open mouth and smiling eyes", emoji.getDescription());
    }

    @Test
    public void isEmoji_for_an_emoji_returns_true() {
        // GIVEN
        String emoji = "?";
        // WHEN
        boolean isEmoji = EmojiManager.isEmoji(emoji);
        // THEN
        Assert.assertTrue(isEmoji);
    }

    @Test
    public void isEmoji_with_fitzpatric_modifier_returns_true() {
        // GIVEN
        String emoji = "\ud83e\udd30\ud83c\udffb";
        // WHEN
        boolean isEmoji = EmojiManager.isEmoji(emoji);
        // THEN
        Assert.assertTrue(isEmoji);
    }

    @Test
    public void isEmoji_for_a_non_emoji_returns_false() {
        // GIVEN
        String str = "test";
        // WHEN
        boolean isEmoji = EmojiManager.isEmoji(str);
        // THEN
        Assert.assertFalse(isEmoji);
    }

    @Test
    public void isEmoji_for_an_emoji_and_other_chars_returns_false() {
        // GIVEN
        String str = "? test";
        // WHEN
        boolean isEmoji = EmojiManager.isEmoji(str);
        // THEN
        Assert.assertFalse(isEmoji);
    }

    @Test
    public void isOnlyEmojis_for_an_emoji_returns_true() {
        // GIVEN
        String str = "?";
        // WHEN
        boolean isEmoji = EmojiManager.isOnlyEmojis(str);
        // THEN
        Assert.assertTrue(isEmoji);
    }

    @Test
    public void isOnlyEmojis_for_emojis_returns_true() {
        // GIVEN
        String str = "???";
        // WHEN
        boolean isEmoji = EmojiManager.isOnlyEmojis(str);
        // THEN
        Assert.assertTrue(isEmoji);
    }

    @Test
    public void isOnlyEmojis_for_random_string_returns_false() {
        // GIVEN
        String str = "?a";
        // WHEN
        boolean isEmoji = EmojiManager.isOnlyEmojis(str);
        // THEN
        Assert.assertFalse(isEmoji);
    }

    @Test
    public void getAllTags_returns_the_tags() {
        // GIVEN
        // WHEN
        Collection<String> tags = EmojiManager.getAllTags();
        // THEN
        // We know the number of distinct tags int the...!
        Assert.assertEquals(594, tags.size());
    }

    @Test
    public void getAll_doesnt_return_duplicates() {
        // GIVEN
        // WHEN
        Collection<Emoji> emojis = EmojiManager.getAll();
        // THEN
        Set<String> unicodes = new HashSet<String>();
        for (Emoji emoji : emojis) {
            Assert.assertFalse(("Duplicate: " + (emoji.getDescription())), unicodes.contains(emoji.getUnicode()));
            unicodes.add(emoji.getUnicode());
        }
        Assert.assertEquals(unicodes.size(), emojis.size());
    }

    @Test
    public void no_duplicate_alias() {
        // GIVEN
        // WHEN
        Collection<Emoji> emojis = EmojiManager.getAll();
        // THEN
        Set<String> aliases = new HashSet<String>();
        Set<String> duplicates = new HashSet<String>();
        for (Emoji emoji : emojis) {
            for (String alias : emoji.getAliases()) {
                if (aliases.contains(alias)) {
                    duplicates.add(alias);
                }
                aliases.add(alias);
            }
        }
        Assert.assertEquals(("Duplicates: " + duplicates), duplicates.size(), 0);
    }
}


package com.vdurmont.emoji;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class EmojiLoaderTest {
    @Test
    public void load_empty_database_returns_empty_list() throws IOException {
        // GIVEN
        byte[] bytes = new JSONArray().toString().getBytes("UTF-8");
        InputStream stream = new ByteArrayInputStream(bytes);
        // WHEN
        List<Emoji> emojis = EmojiLoader.loadEmojis(stream);
        // THEN
        Assert.assertEquals(0, emojis.size());
    }

    @Test
    public void buildEmojiFromJSON() throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject(("{" + (((("\"emoji\": \"\ud83d\ude04\"," + "\"description\": \"smiling face with open mouth and smiling eyes\",") + "\"aliases\": [\"smile\"],") + "\"tags\": [\"happy\", \"joy\", \"pleased\"]") + "}")));
        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);
        // THEN
        Assert.assertNotNull(emoji);
        Assert.assertEquals("?", emoji.getUnicode());
        Assert.assertEquals("smiling face with open mouth and smiling eyes", emoji.getDescription());
        Assert.assertEquals(1, emoji.getAliases().size());
        Assert.assertEquals("smile", emoji.getAliases().get(0));
        Assert.assertEquals(3, emoji.getTags().size());
        Assert.assertEquals("happy", emoji.getTags().get(0));
        Assert.assertEquals("joy", emoji.getTags().get(1));
        Assert.assertEquals("pleased", emoji.getTags().get(2));
    }

    @Test
    public void buildEmojiFromJSON_without_description_sets_a_null_description() throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject(("{" + ((("\"emoji\": \"\ud83d\ude04\"," + "\"aliases\": [\"smile\"],") + "\"tags\": [\"happy\", \"joy\", \"pleased\"]") + "}")));
        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);
        // THEN
        Assert.assertNotNull(emoji);
        Assert.assertNull(emoji.getDescription());
    }

    @Test
    public void buildEmojiFromJSON_without_unicode_returns_null() throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject(("{" + (("\"aliases\": [\"smile\"]," + "\"tags\": [\"happy\", \"joy\", \"pleased\"]") + "}")));
        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);
        // THEN
        Assert.assertNull(emoji);
    }

    @Test
    public void buildEmojiFromJSON_computes_the_html_codes() throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject(("{" + (((("\"emoji\": \"\ud83d\ude04\"," + "\"description\": \"smiling face with open mouth and smiling eyes\",") + "\"aliases\": [\"smile\"],") + "\"tags\": [\"happy\", \"joy\", \"pleased\"]") + "}")));
        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);
        // THEN
        Assert.assertNotNull(emoji);
        Assert.assertEquals("?", emoji.getUnicode());
        Assert.assertEquals("&#128516;", emoji.getHtmlDecimal());
        Assert.assertEquals("&#x1f604;", emoji.getHtmlHexadecimal());
    }

    @Test
    public void buildEmojiFromJSON_with_support_for_fitzpatrick_true() throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject(("{" + ((((("\"emoji\": \"\ud83d\udc66\"," + "\"description\": \"boy\",") + "\"supports_fitzpatrick\": true,") + "\"aliases\": [\"boy\"],") + "\"tags\": [\"child\"]") + "}")));
        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);
        // THEN
        Assert.assertNotNull(emoji);
        Assert.assertTrue(emoji.supportsFitzpatrick());
    }

    @Test
    public void buildEmojiFromJSON_with_support_for_fitzpatrick_false() throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject(("{" + ((((("\"emoji\": \"\ud83d\ude15\"," + "\"description\": \"confused face\",") + "\"supports_fitzpatrick\": false,") + "\"aliases\": [\"confused\"],") + "\"tags\": []") + "}")));
        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);
        // THEN
        Assert.assertNotNull(emoji);
        Assert.assertFalse(emoji.supportsFitzpatrick());
    }

    @Test
    public void buildEmojiFromJSON_without_support_for_fitzpatrick() throws UnsupportedEncodingException {
        // GIVEN
        JSONObject json = new JSONObject(("{" + (((("\"emoji\": \"\ud83d\ude15\"," + "\"description\": \"confused face\",") + "\"aliases\": [\"confused\"],") + "\"tags\": []") + "}")));
        // WHEN
        Emoji emoji = EmojiLoader.buildEmojiFromJSON(json);
        // THEN
        Assert.assertNotNull(emoji);
        Assert.assertFalse(emoji.supportsFitzpatrick());
    }
}


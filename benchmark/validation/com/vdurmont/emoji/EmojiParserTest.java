package com.vdurmont.emoji;


import Fitzpatrick.TYPE_3;
import FitzpatrickAction.IGNORE;
import FitzpatrickAction.PARSE;
import FitzpatrickAction.REMOVE;
import com.vdurmont.emoji.EmojiParser.AliasCandidate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class EmojiParserTest {
    @Test
    public void parseToAliases_replaces_the_emojis_by_one_of_their_aliases() {
        // GIVEN
        String str = "An ?awesome ?string with a few ?emojis!";
        // WHEN
        String result = EmojiParser.parseToAliases(str);
        // THEN
        Assert.assertEquals("An :grinning:awesome :smiley:string with a few :wink:emojis!", result);
    }

    @Test
    public void replaceAllEmojis_replace_the_emojis_by_string() throws Exception {
        // GIVEN
        String str = "An ?awesome ?string with a few ?emojis!";
        // WHEN
        String result = EmojiParser.replaceAllEmojis(str, ":)");
        // THEN
        Assert.assertEquals("An :)awesome :)string with a few :)emojis!", result);
    }

    @Test
    public void parseToAliases_REPLACE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToAliases(str);
        // THEN
        Assert.assertEquals(":boy|type_6:", result);
    }

    @Test
    public void parseToAliases_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToAliases(str, REMOVE);
        // THEN
        Assert.assertEquals(":boy:", result);
    }

    @Test
    public void parseToAliases_REMOVE_without_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66";
        // WHEN
        String result = EmojiParser.parseToAliases(str, REMOVE);
        // THEN
        Assert.assertEquals(":boy:", result);
    }

    @Test
    public void parseToAliases_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToAliases(str, IGNORE);
        // THEN
        Assert.assertEquals(":boy:\ud83c\udfff", result);
    }

    @Test
    public void parseToAliases_IGNORE_without_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66";
        // WHEN
        String result = EmojiParser.parseToAliases(str, IGNORE);
        // THEN
        Assert.assertEquals(":boy:", result);
    }

    @Test
    public void parseToAliases_with_long_overlapping_emoji() {
        // GIVEN
        String str = "\ud83d\udc68\u200d\ud83d\udc69\u200d\ud83d\udc66";
        // WHEN
        String result = EmojiParser.parseToAliases(str);
        // With greedy parsing, this will give :man::woman::boy:
        // THEN
        Assert.assertEquals(":family_man_woman_boy:", result);
    }

    @Test
    public void parseToAliases_continuous_non_overlapping_emojis() {
        // GIVEN
        String str = "\ud83d\udc69\ud83d\udc68\ud83d\udc66";
        // WHEN
        String result = EmojiParser.parseToAliases(str);
        // THEN
        Assert.assertEquals(":woman::man::boy:", result);
    }

    @Test
    public void parseToHtmlDecimal_replaces_the_emojis_by_their_html_decimal_representation() {
        // GIVEN
        String str = "An ?awesome ?string with a few ?emojis!";
        // WHEN
        String result = EmojiParser.parseToHtmlDecimal(str);
        // THEN
        Assert.assertEquals("An &#128512;awesome &#128515;string with a few &#128521;emojis!", result);
    }

    @Test
    public void parseToHtmlDecimal_PARSE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToHtmlDecimal(str, PARSE);
        // THEN
        Assert.assertEquals("&#128102;", result);
    }

    @Test
    public void parseToHtmlDecimal_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToHtmlDecimal(str, REMOVE);
        // THEN
        Assert.assertEquals("&#128102;", result);
    }

    @Test
    public void parseToHtmlDecimal_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToHtmlDecimal(str, IGNORE);
        // THEN
        Assert.assertEquals("&#128102;\ud83c\udfff", result);
    }

    @Test
    public void parseToHtmlHexadecimal_replaces_the_emojis_by_their_htm_hex_representation() {
        // GIVEN
        String str = "An ?awesome ?string with a few ?emojis!";
        // WHEN
        String result = EmojiParser.parseToHtmlHexadecimal(str);
        // THEN
        Assert.assertEquals("An &#x1f600;awesome &#x1f603;string with a few &#x1f609;emojis!", result);
    }

    @Test
    public void parseToHtmlHexadecimal_PARSE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToHtmlHexadecimal(str, PARSE);
        // THEN
        Assert.assertEquals("&#x1f466;", result);
    }

    @Test
    public void parseToHtmlHexadecimal_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToHtmlHexadecimal(str, REMOVE);
        // THEN
        Assert.assertEquals("&#x1f466;", result);
    }

    @Test
    public void parseToHtmlHexadecimal_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "\ud83d\udc66\ud83c\udfff";
        // WHEN
        String result = EmojiParser.parseToHtmlHexadecimal(str, IGNORE);
        // THEN
        Assert.assertEquals("&#x1f466;\ud83c\udfff", result);
    }

    @Test
    public void parseToUnicode_replaces_the_aliases_and_the_html_by_their_emoji() {
        // GIVEN
        String str = "An :grinning:awesome :smiley:string " + "&#128516;with a few &#x1f609;emojis!";
        // WHEN
        String result = EmojiParser.parseToUnicode(str);
        // THEN
        Assert.assertEquals("An ?awesome ?string ?with a few ?emojis!", result);
    }

    @Test
    public void parseToUnicode_with_the_thumbsup_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        String str = "An :+1:awesome :smiley:string " + "&#128516;with a few :wink:emojis!";
        // WHEN
        String result = EmojiParser.parseToUnicode(str);
        // THEN
        Assert.assertEquals("An \ud83d\udc4dawesome \ud83d\ude03string \ud83d\ude04with a few \ud83d\ude09emojis!", result);
    }

    @Test
    public void parseToUnicode_with_the_thumbsdown_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        String str = "An :-1:awesome :smiley:string &#128516;" + "with a few :wink:emojis!";
        // WHEN
        String result = EmojiParser.parseToUnicode(str);
        // THEN
        Assert.assertEquals("An \ud83d\udc4eawesome \ud83d\ude03string \ud83d\ude04with a few \ud83d\ude09emojis!", result);
    }

    @Test
    public void parseToUnicode_with_the_thumbsup_emoji_in_hex_replaces_the_alias_by_the_emoji() {
        // GIVEN
        String str = "An :+1:awesome :smiley:string &#x1f604;" + "with a few :wink:emojis!";
        // WHEN
        String result = EmojiParser.parseToUnicode(str);
        // THEN
        Assert.assertEquals("An \ud83d\udc4dawesome \ud83d\ude03string \ud83d\ude04with a few \ud83d\ude09emojis!", result);
    }

    @Test
    public void parseToUnicode_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = ":boy|type_6:";
        // WHEN
        String result = EmojiParser.parseToUnicode(str);
        // THEN
        Assert.assertEquals("\ud83d\udc66\ud83c\udfff", result);
    }

    @Test
    public void parseToUnicode_with_an_unsupported_fitzpatrick_modifier_doesnt_replace() {
        // GIVEN
        String str = ":grinning|type_6:";
        // WHEN
        String result = EmojiParser.parseToUnicode(str);
        // THEN
        Assert.assertEquals(str, result);
    }

    @Test
    public void getAliasCanditates_with_one_alias() {
        // GIVEN
        String str = "test :candidate: test";
        // WHEN
        List<AliasCandidate> candidates = EmojiParser.getAliasCandidates(str);
        // THEN
        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals("candidate", candidates.get(0).alias);
        Assert.assertNull(candidates.get(0).fitzpatrick);
    }

    @Test
    public void getAliasCanditates_with_one_alias_an_another_colon_after() {
        // GIVEN
        String str = "test :candidate: test:";
        // WHEN
        List<AliasCandidate> candidates = EmojiParser.getAliasCandidates(str);
        // THEN
        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals("candidate", candidates.get(0).alias);
        Assert.assertNull(candidates.get(0).fitzpatrick);
    }

    @Test
    public void getAliasCanditates_with_one_alias_an_another_colon_right_after() {
        // GIVEN
        String str = "test :candidate::test";
        // WHEN
        List<AliasCandidate> candidates = EmojiParser.getAliasCandidates(str);
        // THEN
        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals("candidate", candidates.get(0).alias);
        Assert.assertNull(candidates.get(0).fitzpatrick);
    }

    @Test
    public void getAliasCanditates_with_one_alias_an_another_colon_before_after() {
        // GIVEN
        String str = "test ::candidate: test";
        // WHEN
        List<AliasCandidate> candidates = EmojiParser.getAliasCandidates(str);
        // THEN
        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals("candidate", candidates.get(0).alias);
        Assert.assertNull(candidates.get(0).fitzpatrick);
    }

    @Test
    public void getAliasCanditates_with_two_aliases() {
        // GIVEN
        String str = "test :candi: :candidate: test";
        // WHEN
        List<AliasCandidate> candidates = EmojiParser.getAliasCandidates(str);
        // THEN
        Assert.assertEquals(2, candidates.size());
        Assert.assertEquals("candi", candidates.get(0).alias);
        Assert.assertNull(candidates.get(0).fitzpatrick);
        Assert.assertEquals("candidate", candidates.get(1).alias);
        Assert.assertNull(candidates.get(1).fitzpatrick);
    }

    @Test
    public void getAliasCanditates_with_two_aliases_sharing_a_colon() {
        // GIVEN
        String str = "test :candi:candidate: test";
        // WHEN
        List<AliasCandidate> candidates = EmojiParser.getAliasCandidates(str);
        // THEN
        Assert.assertEquals(2, candidates.size());
        Assert.assertEquals("candi", candidates.get(0).alias);
        Assert.assertNull(candidates.get(0).fitzpatrick);
        Assert.assertEquals("candidate", candidates.get(1).alias);
        Assert.assertNull(candidates.get(1).fitzpatrick);
    }

    @Test
    public void getAliasCanditates_with_a_fitzpatrick_modifier() {
        // GIVEN
        String str = "test :candidate|type_3: test";
        // WHEN
        List<AliasCandidate> candidates = EmojiParser.getAliasCandidates(str);
        // THEN
        Assert.assertEquals(1, candidates.size());
        Assert.assertEquals("candidate", candidates.get(0).alias);
        Assert.assertEquals(TYPE_3, candidates.get(0).fitzpatrick);
    }

    @Test
    public void test_with_a_new_flag() {
        String input = "Cuba has a new flag! :cu:";
        String expected = "Cuba has a new flag! \ud83c\udde8\ud83c\uddfa";
        Assert.assertEquals(expected, EmojiParser.parseToUnicode(input));
        Assert.assertEquals(input, EmojiParser.parseToAliases(expected));
    }

    @Test
    public void removeAllEmojis_removes_all_the_emojis_from_the_string() {
        // GIVEN
        String input = "An ?awesome ?string ?with " + "a \ud83d\udc66\ud83c\udffffew \ud83d\ude09emojis!";
        // WHEN
        String result = EmojiParser.removeAllEmojis(input);
        // THEN
        String expected = "An awesome string with a few emojis!";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void removeEmojis_only_removes_the_emojis_in_the_iterable_from_the_string() {
        // GIVEN
        String input = "An\ud83d\ude03 awesome\ud83d\ude04 string" + "\ud83d\udc4d\ud83c\udfff with\ud83d\udcaa\ud83c\udffd a few emojis!";
        List<Emoji> emojis = new ArrayList<Emoji>();
        emojis.add(EmojiManager.getForAlias("smile"));
        emojis.add(EmojiManager.getForAlias("+1"));
        // WHEN
        String result = EmojiParser.removeEmojis(input, emojis);
        // THEN
        String expected = "An\ud83d\ude03 awesome string with" + "\ud83d\udcaa\ud83c\udffd a few emojis!";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void removeAllEmojisExcept_removes_all_the_emojis_from_the_string_except_those_in_the_iterable() {
        // GIVEN
        String input = "An\ud83d\ude03 awesome\ud83d\ude04 string" + "\ud83d\udc4d\ud83c\udfff with\ud83d\udcaa\ud83c\udffd a few emojis!";
        List<Emoji> emojis = new ArrayList<Emoji>();
        emojis.add(EmojiManager.getForAlias("smile"));
        emojis.add(EmojiManager.getForAlias("+1"));
        // WHEN
        String result = EmojiParser.removeAllEmojisExcept(input, emojis);
        // THEN
        String expected = "An awesome\ud83d\ude04 string\ud83d\udc4d\ud83c\udfff " + "with a few emojis!";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void parseToUnicode_with_the_keycap_asterisk_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        String str = "Let's test the :keycap_asterisk: emoji and " + "its other alias :star_keycap:";
        // WHEN
        String result = EmojiParser.parseToUnicode(str);
        // THEN
        Assert.assertEquals("Let's test the *? emoji and its other alias *?", result);
    }

    @Test
    public void parseToAliases_NG_and_nigeria() {
        // GIVEN
        String str = "Nigeria is ??, NG is ?";
        // WHEN
        String result = EmojiParser.parseToAliases(str);
        // THEN
        Assert.assertEquals("Nigeria is :ng:, NG is :squared_ng:", result);
    }

    @Test
    public void parseToAliases_couplekiss_woman_woman() {
        // GIVEN
        String str = "????????";
        // WHEN
        String result = EmojiParser.parseToAliases(str);
        // THEN
        Assert.assertEquals(":couplekiss_woman_woman:", result);
    }

    @Test
    public void extractEmojis() {
        // GIVEN
        String str = "An ?awesome ?string with a few ?emojis!";
        // WHEN
        List<String> result = EmojiParser.extractEmojis(str);
        // THEN
        Assert.assertEquals("?", result.get(0));
        Assert.assertEquals("?", result.get(1));
        Assert.assertEquals("?", result.get(2));
    }
}


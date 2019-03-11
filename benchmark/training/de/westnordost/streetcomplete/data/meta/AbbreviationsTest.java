package de.westnordost.streetcomplete.data.meta;


import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class AbbreviationsTest {
    @Test
    public void capitalizesFirstLetter() {
        Assert.assertEquals("Stra?e", abbr("str: stra?e", Locale.GERMANY).getExpansion("str", true, true));
    }

    @Test
    public void removesAbbreviationDot() {
        Assert.assertEquals("Stra?e", abbr("str: stra?e", Locale.GERMANY).getExpansion("str.", true, true));
    }

    @Test
    public void ignoresCase() {
        Assert.assertEquals("Stra?e", abbr("sTr: Stra?e", Locale.GERMANY).getExpansion("StR", true, true));
    }

    @Test
    public void expectsOwnWordByDefault() {
        Assert.assertNull(abbr("st: street").getExpansion("Hanswurst", true, true));
    }

    @Test
    public void concatenable() {
        Assert.assertEquals("Konigstraat", abbr("...str: Straat").getExpansion("Konigstr", true, true));
    }

    @Test
    public void concatenableEnd() {
        Assert.assertEquals("Konigstraat", abbr("...str$: Straat").getExpansion("Konigstr", true, true));
    }

    @Test
    public void concatenableWorksNormallyForNonConcatenation() {
        Assert.assertEquals("Straat", abbr("...str: Straat").getExpansion("str", true, true));
    }

    @Test
    public void onlyFirstWord() {
        Abbreviations abbr = abbr("^st: Saint");
        Assert.assertNull(abbr.getExpansion("st.", false, false));
        Assert.assertEquals("Saint", abbr.getExpansion("st.", true, false));
    }

    @Test
    public void onlyLastWord() {
        Abbreviations abbr = abbr("str$: Stra?e");
        Assert.assertNull(abbr.getExpansion("str", true, false));
        Assert.assertNull(abbr.getExpansion("str", true, true));
        Assert.assertEquals("Stra?e", abbr.getExpansion("str", false, true));
    }

    @Test
    public void unicode() {
        Assert.assertEquals("????", abbr("??: ????", new Locale("ru", "RU")).getExpansion("??", true, true));
    }

    @Test
    public void localeCase() {
        Assert.assertEquals("????", abbr("??: ????", new Locale("ru", "RU")).getExpansion("??", true, true));
    }

    @Test
    public void findsAbbreviation() {
        Assert.assertFalse(abbr("str: Stra?e", Locale.GERMANY).containsAbbreviations("stri stra stra?e"));
        Assert.assertTrue(abbr("str: Stra?e", Locale.GERMANY).containsAbbreviations("stri str stra?e"));
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionOnInvalidInput() {
        abbr("d:\n  - a\n  b: c\n");
    }
}


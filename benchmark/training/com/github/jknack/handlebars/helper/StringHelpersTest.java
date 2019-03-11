package com.github.jknack.handlebars.helper;


import StringHelpers.defaultIfEmpty;
import StringHelpers.join;
import StringHelpers.yesno;
import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link StringHelpers}.
 *
 * @author edgar.espina
 * @since 0.2.2
 */
public class StringHelpersTest extends AbstractTest {
    @Test
    public void capFirst() throws IOException {
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("capitalizeFirst", StringHelpers.capitalizeFirst.name());
        Assert.assertEquals("Handlebars.java", StringHelpers.capitalizeFirst.apply("handlebars.java", options));
        verify(options);
    }

    @Test
    public void center() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("size")).andReturn(19);
        expect(options.isFalsy(anyObject())).andReturn(false);
        expect(options.hash("pad", " ")).andReturn(null);
        replay(options);
        Assert.assertEquals("center", StringHelpers.center.name());
        Assert.assertEquals("  Handlebars.java  ", StringHelpers.center.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void centerWithPad() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("size")).andReturn(19);
        expect(options.hash("pad", " ")).andReturn("*");
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("center", StringHelpers.center.name());
        Assert.assertEquals("**Handlebars.java**", StringHelpers.center.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void cut() throws IOException {
        Options options = createMock(Options.class);
        expect(options.param(0, " ")).andReturn(" ");
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("cut", StringHelpers.cut.name());
        Assert.assertEquals("handlebars.java", StringHelpers.cut.apply("handle bars .  java", options));
        verify(options);
    }

    @Test
    public void cutNoWhitespace() throws IOException {
        Options options = createMock(Options.class);
        expect(options.param(0, " ")).andReturn("*");
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("cut", StringHelpers.cut.name());
        Assert.assertEquals("handlebars.java", StringHelpers.cut.apply("handle*bars*.**java", options));
        verify(options);
    }

    @Test
    public void defaultStr() throws IOException {
        Options options = createMock(Options.class);
        expect(options.param(0, "")).andReturn("handlebars.java").anyTimes();
        replay(options);
        Assert.assertEquals("defaultIfEmpty", StringHelpers.defaultIfEmpty.name());
        Assert.assertEquals("handlebars.java", StringHelpers.defaultIfEmpty.apply(null, options));
        Assert.assertEquals("handlebars.java", StringHelpers.defaultIfEmpty.apply(false, options));
        Assert.assertEquals("handlebars.java", StringHelpers.defaultIfEmpty.apply(Collections.emptyList(), options));
        Assert.assertEquals("something", StringHelpers.defaultIfEmpty.apply("something", options));
        verify(options);
    }

    @Test
    public void joinIterable() throws IOException {
        shouldCompileTo("{{{join this \", \"}}}", Arrays.asList("6", "7", "8"), AbstractTest.$("join", join), "6, 7, 8");
    }

    @Test
    public void joinEmptyList() throws IOException {
        shouldCompileTo("{{{join this \", \"}}}", Collections.emptyList(), AbstractTest.$("join", join), "");
    }

    @Test
    public void joinIterator() throws IOException {
        shouldCompileTo("{{{join this \", \"}}}", Arrays.asList("6", "7", "8").iterator(), AbstractTest.$("join", join), "6, 7, 8");
    }

    @Test
    public void joinArray() throws IOException {
        shouldCompileTo("{{{join this \", \"}}}", new String[]{ "6", "7", "8" }, AbstractTest.$("join", join), "6, 7, 8");
    }

    @Test
    public void joinValues() throws IOException {
        shouldCompileTo("{{{join \"6\" 7 n8 \"-\"}}}", AbstractTest.$("n8", 8), AbstractTest.$("join", join), "6-7-8");
    }

    @Test
    public void joinWithPrefixAndSuffix() throws IOException {
        shouldCompileTo("{{{join this \", \" prefix=\'<\' suffix=\'>\'}}}", Arrays.asList("6", "7", "8"), AbstractTest.$("join", join), "<6, 7, 8>");
    }

    @Test
    public void ljust() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("size")).andReturn(20);
        expect(options.hash("pad", " ")).andReturn(null);
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("ljust", StringHelpers.ljust.name());
        Assert.assertEquals("Handlebars.java     ", StringHelpers.ljust.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void ljustWithPad() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("size")).andReturn(17);
        expect(options.hash("pad", " ")).andReturn("+");
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("ljust", StringHelpers.ljust.name());
        Assert.assertEquals("Handlebars.java++", StringHelpers.ljust.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void rjust() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("size")).andReturn(20);
        expect(options.hash("pad", " ")).andReturn(null);
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("rjust", StringHelpers.rjust.name());
        Assert.assertEquals("     Handlebars.java", StringHelpers.rjust.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void rjustWithPad() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("size")).andReturn(17);
        expect(options.hash("pad", " ")).andReturn("+");
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("rjust", StringHelpers.rjust.name());
        Assert.assertEquals("++Handlebars.java", StringHelpers.rjust.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void substringWithStart() throws IOException {
        Handlebars hbs = createMock(Handlebars.class);
        Context ctx = createMock(Context.class);
        Template fn = createMock(Template.class);
        Options options = setParams(new Object[]{ 11 }).build();
        Assert.assertEquals("substring", StringHelpers.substring.name());
        Assert.assertEquals("java", StringHelpers.substring.apply("Handlebars.java", options));
    }

    @Test
    public void substringWithStartAndEnd() throws IOException {
        Handlebars hbs = createMock(Handlebars.class);
        Context ctx = createMock(Context.class);
        Template fn = createMock(Template.class);
        Options options = setParams(new Object[]{ 0, 10 }).build();
        Assert.assertEquals("substring", StringHelpers.substring.name());
        Assert.assertEquals("Handlebars", StringHelpers.substring.apply("Handlebars.java", options));
    }

    @Test
    public void lower() throws IOException {
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("lower", StringHelpers.lower.name());
        Assert.assertEquals("handlebars.java", StringHelpers.lower.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void upper() throws IOException {
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("upper", StringHelpers.upper.name());
        Assert.assertEquals("HANDLEBARS.JAVA", StringHelpers.upper.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void slugify() throws IOException {
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("slugify", StringHelpers.slugify.name());
        Assert.assertEquals("joel-is-a-slug", StringHelpers.slugify.apply("  Joel is a slug  ", options));
        verify(options);
    }

    @Test
    public void replace() throws IOException {
        Handlebars hbs = createMock(Handlebars.class);
        Context ctx = createMock(Context.class);
        Template fn = createMock(Template.class);
        Options options = setParams(new Object[]{ "...", "rocks" }).build();
        Assert.assertEquals("replace", StringHelpers.replace.name());
        Assert.assertEquals("Handlebars rocks", StringHelpers.replace.apply("Handlebars ...", options));
    }

    @Test
    public void stringFormat() throws IOException {
        Handlebars hbs = createMock(Handlebars.class);
        Context ctx = createMock(Context.class);
        Template fn = createMock(Template.class);
        Options options = setParams(new Object[]{ "handlebars.java" }).build();
        Assert.assertEquals("stringFormat", StringHelpers.stringFormat.name());
        Assert.assertEquals("Hello handlebars.java!", StringHelpers.stringFormat.apply("Hello %s!", options));
    }

    @Test
    public void stringDecimalFormat() throws IOException {
        Handlebars hbs = createMock(Handlebars.class);
        Context ctx = createMock(Context.class);
        Template fn = createMock(Template.class);
        Options options = setParams(new Object[]{ 10.0 / 3.0 }).build();
        Assert.assertEquals("stringFormat", StringHelpers.stringFormat.name());
        Assert.assertEquals(String.format("10 / 3 = %.2f", (10.0 / 3.0)), StringHelpers.stringFormat.apply("10 / 3 = %.2f", options));
    }

    @Test
    public void stripTags() throws IOException {
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("stripTags", StringHelpers.stripTags.name());
        Assert.assertEquals("Joel is a slug", StringHelpers.stripTags.apply("<b>Joel</b> <button>is</button> a <span>slug</span>", options));
        verify(options);
    }

    @Test
    public void stripTagsMultiLine() throws IOException {
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(false);
        replay(options);
        Assert.assertEquals("stripTags", StringHelpers.stripTags.name());
        Assert.assertEquals("Joel\nis a slug", StringHelpers.stripTags.apply("<b>Joel</b>\n<button>is<\n/button> a <span>slug</span>", options));
        verify(options);
    }

    @Test
    public void capitalize() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("fully", false)).andReturn(false);
        expect(options.hash("fully", false)).andReturn(true);
        expect(options.isFalsy(anyObject())).andReturn(false).times(2);
        replay(options);
        Assert.assertEquals("capitalize", StringHelpers.capitalize.name());
        Assert.assertEquals("Handlebars Java", StringHelpers.capitalize.apply("handlebars java", options));
        Assert.assertEquals("Handlebars Java", StringHelpers.capitalize.apply("HAndleBars JAVA", options));
        verify(options);
    }

    @Test
    public void abbreviate() throws IOException {
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(false);
        expect(options.param(0, null)).andReturn(13);
        replay(options);
        Assert.assertEquals("abbreviate", StringHelpers.abbreviate.name());
        Assert.assertEquals("Handlebars...", StringHelpers.abbreviate.apply("Handlebars.java", options));
        verify(options);
    }

    @Test
    public void wordWrap() throws IOException {
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(false);
        expect(options.param(0, null)).andReturn(5);
        replay(options);
        Assert.assertEquals("wordWrap", StringHelpers.wordWrap.name());
        Assert.assertEquals((((("Joel" + (SystemUtils.LINE_SEPARATOR)) + "is a") + (SystemUtils.LINE_SEPARATOR)) + "slug"), StringHelpers.wordWrap.apply("Joel is a slug", options));
        verify(options);
    }

    @Test
    public void yesno() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("yes", "yes")).andReturn("yes");
        expect(options.hash("no", "no")).andReturn("no");
        expect(options.hash("maybe", "maybe")).andReturn("maybe");
        replay(options);
        Assert.assertEquals("yesno", StringHelpers.yesno.name());
        Assert.assertEquals("yes", StringHelpers.yesno.apply(true, options));
        Assert.assertEquals("no", StringHelpers.yesno.apply(false, options));
        Assert.assertEquals("maybe", StringHelpers.yesno.apply(null, options));
        verify(options);
    }

    @Test
    public void yesnoCustom() throws IOException {
        Options options = createMock(Options.class);
        expect(options.hash("yes", "yes")).andReturn("yea");
        expect(options.hash("no", "no")).andReturn("nop");
        expect(options.hash("maybe", "maybe")).andReturn("whatever");
        replay(options);
        Assert.assertEquals("yesno", StringHelpers.yesno.name());
        Assert.assertEquals("yea", StringHelpers.yesno.apply(true, options));
        Assert.assertEquals("nop", StringHelpers.yesno.apply(false, options));
        Assert.assertEquals("whatever", StringHelpers.yesno.apply(null, options));
        verify(options);
    }

    @Test
    public void nullContext() throws IOException {
        Set<Helper<Object>> helpers = new LinkedHashSet<Helper<Object>>(Arrays.asList(StringHelpers.values()));
        helpers.remove(join);
        helpers.remove(yesno);
        helpers.remove(defaultIfEmpty);
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(true).times(((helpers.size()) - 1));
        expect(options.param(0, null)).andReturn(null).times(helpers.size());
        replay(options);
        for (Helper<Object> helper : helpers) {
            Assert.assertEquals(null, helper.apply(AbstractTest.$, options));
        }
        verify(options);
    }

    @Test
    public void nullContextWithDefault() throws IOException {
        Set<Helper<Object>> helpers = new LinkedHashSet<Helper<Object>>(Arrays.asList(StringHelpers.values()));
        helpers.remove(join);
        helpers.remove(yesno);
        helpers.remove(defaultIfEmpty);
        String nothing = "nothing";
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(true).times(((helpers.size()) - 1));
        expect(options.param(0, null)).andReturn(nothing).times(helpers.size());
        replay(options);
        for (Helper<Object> helper : helpers) {
            Assert.assertEquals(nothing, helper.apply(AbstractTest.$, options));
        }
        verify(options);
    }

    @Test
    public void nullContextWithNumber() throws IOException {
        Set<Helper<Object>> helpers = new LinkedHashSet<Helper<Object>>(Arrays.asList(StringHelpers.values()));
        helpers.remove(join);
        helpers.remove(yesno);
        helpers.remove(defaultIfEmpty);
        Object number = 32;
        Options options = createMock(Options.class);
        expect(options.isFalsy(anyObject())).andReturn(true).times(((helpers.size()) - 1));
        expect(options.param(0, null)).andReturn(number).times(helpers.size());
        replay(options);
        for (Helper<Object> helper : helpers) {
            Assert.assertEquals(number.toString(), helper.apply(AbstractTest.$, options));
        }
        verify(options);
    }
}


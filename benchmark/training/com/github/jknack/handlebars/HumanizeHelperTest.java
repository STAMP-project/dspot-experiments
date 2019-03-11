package com.github.jknack.handlebars;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class HumanizeHelperTest {
    private static final Handlebars handlebars = new Handlebars();

    static {
        HumanizeHelper.register(HumanizeHelperTest.handlebars);
    }

    @Test
    public void binaryPrefix() throws IOException {
        Assert.assertEquals("2 bytes", HumanizeHelperTest.handlebars.compileInline("{{binaryPrefix this}}").apply(2));
        Assert.assertEquals("1.5 KB", HumanizeHelperTest.handlebars.compileInline("{{binaryPrefix this}}").apply(1536));
        Assert.assertEquals("5 MB", HumanizeHelperTest.handlebars.compileInline("{{binaryPrefix this}}").apply(5242880));
    }

    @Test
    public void camelize() throws IOException {
        Assert.assertEquals("ThisIsCamelCase", HumanizeHelperTest.handlebars.compileInline("{{camelize this}}").apply("This is camel case"));
        Assert.assertEquals("thisIsCamelCase", HumanizeHelperTest.handlebars.compileInline("{{camelize this capFirst=false}}").apply("This is camel case"));
    }

    @Test
    public void decamelize() throws IOException {
        Assert.assertEquals("this Is Camel Case", HumanizeHelperTest.handlebars.compileInline("{{decamelize this}}").apply("thisIsCamelCase"));
        Assert.assertEquals("This Is Camel Case", HumanizeHelperTest.handlebars.compileInline("{{decamelize this}}").apply("ThisIsCamelCase"));
        Assert.assertEquals("ThisxIsxCamelxCase", HumanizeHelperTest.handlebars.compileInline("{{decamelize this replacement=\"x\"}}").apply("ThisIsCamelCase"));
    }

    /**
     * Note: beside locale is optional it must be set in unit testing, otherwise
     * the test might fail in a different machine.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void formatCurrency_es_AR() throws IOException {
        Assert.assertEquals("$34", HumanizeHelperTest.handlebars.compileInline("{{formatCurrency this locale=\"es_AR\"}}").apply(34));
        Assert.assertEquals("$1.000", HumanizeHelperTest.handlebars.compileInline("{{formatCurrency this locale=\"es_AR\"}}").apply(1000));
        Assert.assertEquals("$12,50", HumanizeHelperTest.handlebars.compileInline("{{formatCurrency this locale=\"es_AR\"}}").apply(12.5));
    }

    /**
     * Note: beside locale is optional it must be set in unit testing, otherwise
     * the test might fail in a different machine.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void formatCurrency_en_GB() throws IOException {
        Assert.assertEquals("?34", HumanizeHelperTest.handlebars.compileInline("{{formatCurrency this locale=\"en_GB\"}}").apply(34));
        Assert.assertEquals("?1,000", HumanizeHelperTest.handlebars.compileInline("{{formatCurrency this locale=\"en_GB\"}}").apply(1000));
        Assert.assertEquals("?12.50", HumanizeHelperTest.handlebars.compileInline("{{formatCurrency this locale=\"en_GB\"}}").apply(12.5));
    }

    @Test
    public void formatPercent() throws IOException {
        Assert.assertEquals("50%", HumanizeHelperTest.handlebars.compileInline("{{formatPercent this}}").apply(0.5));
        Assert.assertEquals("100%", HumanizeHelperTest.handlebars.compileInline("{{formatPercent this}}").apply(1));
        Assert.assertEquals("56%", HumanizeHelperTest.handlebars.compileInline("{{formatPercent this}}").apply(0.564));
    }

    @Test
    public void metricPrefix() throws IOException {
        Assert.assertEquals("200", HumanizeHelperTest.handlebars.compileInline("{{metricPrefix this}}").apply(200));
        Assert.assertEquals("1k", HumanizeHelperTest.handlebars.compileInline("{{metricPrefix this}}").apply(1000));
        Assert.assertEquals("3.5M", HumanizeHelperTest.handlebars.compileInline("{{metricPrefix this}}").apply(3500000));
    }

    @Test
    public void naturalDay() throws IOException {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.HOUR, (-24));
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.HOUR, (24 * 2));
        Date tomorrow = calendar.getTime();
        Assert.assertEquals("yesterday", HumanizeHelperTest.handlebars.compileInline("{{naturalDay this locale=\"en_US\"}}").apply(yesterday));
        Assert.assertEquals("today", HumanizeHelperTest.handlebars.compileInline("{{naturalDay this locale=\"en_US\"}}").apply(now));
        Assert.assertEquals("tomorrow", HumanizeHelperTest.handlebars.compileInline("{{naturalDay this locale=\"en_US\"}}").apply(tomorrow));
    }

    @Test
    public void naturalTime() throws IOException, InterruptedException {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        Thread.sleep(1000);
        Assert.assertEquals("moments ago", HumanizeHelperTest.handlebars.compileInline("{{naturalTime this locale=\"en_US\"}}").apply(now));
    }

    @Test
    public void ordinal() throws IOException {
        Assert.assertEquals("1st", HumanizeHelperTest.handlebars.compileInline("{{ordinal this locale=\"en_US\"}}").apply(1));
        Assert.assertEquals("2nd", HumanizeHelperTest.handlebars.compileInline("{{ordinal this locale=\"en_US\"}}").apply(2));
        Assert.assertEquals("3rd", HumanizeHelperTest.handlebars.compileInline("{{ordinal this locale=\"en_US\"}}").apply(3));
        Assert.assertEquals("10th", HumanizeHelperTest.handlebars.compileInline("{{ordinal this locale=\"en_US\"}}").apply(10));
    }

    @Test
    public void pluralize() throws IOException {
        Assert.assertEquals("There are no files on disk.", HumanizeHelperTest.handlebars.compileInline("{{pluralize this 0 \"disk\" locale=\"en_US\"}}").apply("There {0} on {1}.::are no files::is one file::are {0} files"));
        Assert.assertEquals("There is one file on disk.", HumanizeHelperTest.handlebars.compileInline("{{pluralize this 1 \"disk\" locale=\"en_US\"}}").apply("There {0} on {1}.::are no files::is one file::are {0} files"));
        Assert.assertEquals("There are 1,000 files on disk.", HumanizeHelperTest.handlebars.compileInline("{{pluralize this 1000 \"disk\" locale=\"en_US\"}}").apply("There {0} on {1}.::are no files::is one file::are {0} files"));
    }

    @Test
    public void slugify() throws IOException {
        Assert.assertEquals("hablo-espanol", HumanizeHelperTest.handlebars.compileInline("{{slugify this}}").apply("Hablo espa?ol"));
    }

    @Test
    public void titleize() throws IOException {
        Assert.assertEquals("Handlebars.java Rocks!", HumanizeHelperTest.handlebars.compileInline("{{titleize this}}").apply("Handlebars.java rocks!"));
    }

    @Test
    public void underscore() throws IOException {
        Assert.assertEquals("Handlebars_Java_rock", HumanizeHelperTest.handlebars.compileInline("{{underscore this}}").apply("Handlebars Java rock"));
    }

    @Test
    public void wordWrap() throws IOException {
        Assert.assertEquals("Handlebars.java", HumanizeHelperTest.handlebars.compileInline("{{wordWrap this 14}}").apply("Handlebars.java rock"));
    }
}


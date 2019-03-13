package com.vaadin.tests.design;


import FontAwesome.AMBULANCE;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.GenericFontIcon;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.declarative.DesignFormatter;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


/**
 * Various tests related to formatter.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignFormatterTest {
    private DesignFormatter formatter;

    @Test
    public void testSupportedClasses() {
        for (Class<?> type : new Class<?>[]{ boolean.class, char.class, byte.class, short.class, int.class, long.class, float.class, double.class, Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, BigDecimal.class, String.class, ShortcutAction.class, Date.class, FileResource.class, ExternalResource.class, ThemeResource.class, Resource.class, TimeZone.class }) {
            Assert.assertTrue(("not supported " + (type.getSimpleName())), formatter.canConvert(type));
        }
    }

    @Test
    public void testBoolean() {
        Assert.assertEquals("", formatter.format(true));
        Assert.assertEquals("false", formatter.format(false));
        Assert.assertTrue(formatter.parse("true", boolean.class));
        Assert.assertTrue(formatter.parse("foobar", boolean.class));
        Assert.assertTrue(formatter.parse("", boolean.class));
        Assert.assertFalse(formatter.parse("false", boolean.class));
        Assert.assertTrue(formatter.parse("true", Boolean.class));
        Assert.assertTrue(formatter.parse("foobar", Boolean.class));
        Assert.assertTrue(formatter.parse("", Boolean.class));
        Assert.assertFalse(formatter.parse("false", Boolean.class));
    }

    @Test
    public void testIntegral() {
        byte b = 123;
        Assert.assertEquals("123", formatter.format(b));
        Assert.assertEquals(b, ((byte) (formatter.parse("123", byte.class))));
        Assert.assertEquals(((Byte) (b)), formatter.parse("123", Byte.class));
        b = -123;
        Assert.assertEquals("-123", formatter.format(b));
        Assert.assertEquals(b, ((byte) (formatter.parse("-123", byte.class))));
        Assert.assertEquals(((Byte) (b)), formatter.parse("-123", Byte.class));
        short s = 12345;
        Assert.assertEquals("12345", formatter.format(s));
        Assert.assertEquals(s, ((short) (formatter.parse("12345", short.class))));
        Assert.assertEquals(((Short) (s)), formatter.parse("12345", Short.class));
        s = -12345;
        Assert.assertEquals("-12345", formatter.format(s));
        Assert.assertEquals(s, ((short) (formatter.parse("-12345", short.class))));
        Assert.assertEquals(((Short) (s)), formatter.parse("-12345", Short.class));
        int i = 123456789;
        Assert.assertEquals("123456789", formatter.format(i));
        Assert.assertEquals(i, ((int) (formatter.parse("123456789", int.class))));
        Assert.assertEquals(((Integer) (i)), formatter.parse("123456789", Integer.class));
        i = -123456789;
        Assert.assertEquals("-123456789", formatter.format(i));
        Assert.assertEquals(i, ((int) (formatter.parse("-123456789", int.class))));
        Assert.assertEquals(((Integer) (i)), formatter.parse("-123456789", Integer.class));
        long l = 123456789123456789L;
        Assert.assertEquals("123456789123456789", formatter.format(l));
        Assert.assertEquals(l, ((long) (formatter.parse("123456789123456789", long.class))));
        Assert.assertEquals(((Long) (l)), formatter.parse("123456789123456789", Long.class));
        l = -123456789123456789L;
        Assert.assertEquals("-123456789123456789", formatter.format(l));
        Assert.assertEquals(l, ((long) (formatter.parse("-123456789123456789", long.class))));
        Assert.assertEquals(((Long) (l)), formatter.parse("-123456789123456789", Long.class));
    }

    @Test
    public void testFloatingPoint() {
        float f = 123.4567F;
        Assert.assertEquals("123.457", formatter.format(f));
        float f1 = formatter.parse("123.4567", float.class);
        Assert.assertEquals(f, f1, 1.0E-4);
        Float f2 = formatter.parse("123.4567", Float.class);
        Assert.assertEquals(f, f2, 1.0E-4);
        double d = 1.2345678912345679E8;
        Assert.assertEquals("123456789.123", formatter.format(d));
        Assert.assertEquals(d, formatter.parse("123456789.123456789", double.class), 1.0E-9);
        Assert.assertEquals(d, formatter.parse("123456789.123456789", Double.class), 1.0E-9);
    }

    @Test
    public void testBigDecimal() {
        BigDecimal bd = new BigDecimal("123456789123456789.123456789123456789");
        Assert.assertEquals("123456789123456789.123", formatter.format(bd));
        Assert.assertEquals(bd, formatter.parse("123456789123456789.123456789123456789", BigDecimal.class));
    }

    @Test
    public void testChar() {
        char c = '\uabcd';
        Assert.assertEquals("\uabcd", formatter.format(c));
        Assert.assertEquals(c, ((char) (formatter.parse("\uabcd", char.class))));
        Assert.assertEquals(((Character) (c)), formatter.parse("\uabcd", Character.class));
        c = 'y';
        Assert.assertEquals(c, ((char) (formatter.parse("yes", char.class))));
    }

    @Test
    public void testString() {
        for (String s : new String[]{ "", "foobar", "\uabcd", "??" }) {
            Assert.assertEquals(s, formatter.format(s));
            Assert.assertEquals(s, formatter.parse(s, String.class));
        }
    }

    @Test
    public void testDate() throws Exception {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2012-02-17");
        String formatted = formatter.format(date);
        Date result = formatter.parse(formatted, Date.class);
        // writing will always give full date string
        String timeZone = new SimpleDateFormat("Z").format(date);
        Assert.assertEquals(("2012-02-17 00:00:00" + timeZone), formatted);
        Assert.assertEquals(date, result);
        // try short date as well
        result = formatter.parse("2012-02-17", Date.class);
        Assert.assertEquals(date, result);
    }

    @Test
    public void testShortcutActions() {
        ShortcutAction action = new ShortcutAction("&^d");
        String formatted = formatter.format(action);
        // note the space here - it separates key combination from caption
        Assert.assertEquals("ctrl-alt-d d", formatted);
        ShortcutAction result = formatter.parse(formatted, ShortcutAction.class);
        Assert.assertTrue(DesignFormatterTest.equals(action, result));
    }

    @Test
    public void testShortcutActionNoCaption() {
        ShortcutAction action = new ShortcutAction(null, KeyCode.D, new int[]{ ModifierKey.ALT, ModifierKey.CTRL });
        String formatted = formatter.format(action);
        Assert.assertEquals("ctrl-alt-d", formatted);
        ShortcutAction result = formatter.parse(formatted, ShortcutAction.class);
        Assert.assertTrue(DesignFormatterTest.equals(action, result));
    }

    @Test
    public void testInvalidShortcutAction() {
        assertInvalidShortcut("-");
        assertInvalidShortcut("foo");
        assertInvalidShortcut("atl-ctrl");
        assertInvalidShortcut("-a");
    }

    @Test
    public void testTimeZone() {
        TimeZone zone = TimeZone.getTimeZone("GMT+2");
        String formatted = formatter.format(zone);
        Assert.assertEquals("GMT+02:00", formatted);
        TimeZone result = formatter.parse(formatted, TimeZone.class);
        Assert.assertEquals(zone, result);
        // try shorthand notation as well
        result = formatter.parse("GMT+2", TimeZone.class);
        Assert.assertEquals(zone, result);
    }

    @Test
    public void testExternalResource() {
        String url = "://example.com/my%20icon.png?a=b";
        for (String scheme : new String[]{ "http", "https", "ftp", "ftps" }) {
            Resource resource = formatter.parse((scheme + url), Resource.class);
            Assert.assertTrue((scheme + " url should be parsed as ExternalResource"), (resource instanceof ExternalResource));
            Assert.assertEquals("parsed ExternalResource", (scheme + url), getURL());
            String formatted = formatter.format(new ExternalResource((scheme + url)));
            Assert.assertEquals("formatted ExternalResource", (scheme + url), formatted);
        }
    }

    @Test
    public void testResourceFormat() {
        String httpUrl = "http://example.com/icon.png";
        String httpsUrl = "https://example.com/icon.png";
        String themePath = "icons/icon.png";
        String fontAwesomeUrl = "fonticon://FontAwesome/f0f9";
        String someOtherFontUrl = "fonticon://SomeOther/F0F9";
        String fileSystemPath = "c:/app/resources/icon.png";
        Assert.assertEquals(httpUrl, formatter.format(new ExternalResource(httpUrl)));
        Assert.assertEquals(httpsUrl, formatter.format(new ExternalResource(httpsUrl)));
        Assert.assertEquals(((ApplicationConstants.THEME_PROTOCOL_PREFIX) + themePath), formatter.format(new ThemeResource(themePath)));
        Assert.assertEquals(fontAwesomeUrl, formatter.format(AMBULANCE));
        Assert.assertEquals(someOtherFontUrl.toLowerCase(Locale.ROOT), formatter.format(new GenericFontIcon("SomeOther", 61689)).toLowerCase(Locale.ROOT));
        Assert.assertEquals(fileSystemPath, formatter.format(new FileResource(new File(fileSystemPath))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResourceParseException() {
        String someRandomResourceUrl = "random://url";
        formatter.parse(someRandomResourceUrl, Resource.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResourceFormatException() {
        formatter.format(new Resource() {
            // must use unknown resource type
            @Override
            public String getMIMEType() {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }

    @Test
    public void testResourceParse() {
        String httpUrl = "http://example.com/icon.png";
        String httpsUrl = "https://example.com/icon.png";
        String themePath = "icons/icon.png";
        String fontAwesomeUrl = "fonticon://FontAwesome/f0f9";
        String someOtherFont = "fonticon://SomeOther/F0F9";
        String fontAwesomeUrlOld = "font://AMBULANCE";
        String fileSystemPath = "c:\\app\\resources\\icon.png";
        Assert.assertEquals(new ExternalResource(httpUrl).getURL(), getURL());
        Assert.assertEquals(new ExternalResource(httpsUrl).getURL(), getURL());
        Assert.assertEquals(new ThemeResource(themePath), formatter.parse(((ApplicationConstants.THEME_PROTOCOL_PREFIX) + themePath), ThemeResource.class));
        Assert.assertEquals(AMBULANCE, formatter.parse(fontAwesomeUrlOld, FontAwesome.class));
        Assert.assertEquals(AMBULANCE, formatter.parse(fontAwesomeUrl, FontAwesome.class));
        Assert.assertEquals(new GenericFontIcon("SomeOther", 61689), formatter.parse(someOtherFont, FontIcon.class));
        Assert.assertEquals(new FileResource(new File(fileSystemPath)).getSourceFile(), formatter.parse(fileSystemPath, FileResource.class).getSourceFile());
    }
}


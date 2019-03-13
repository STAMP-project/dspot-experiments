/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.context.support;


import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.NoSuchMessageException;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 03.02.2004
 */
public class ResourceBundleMessageSourceTests {
    @Test
    public void testMessageAccessWithDefaultMessageSource() {
        doTestMessageAccess(false, true, false, false, false);
    }

    @Test
    public void testMessageAccessWithDefaultMessageSourceAndMessageFormat() {
        doTestMessageAccess(false, true, false, false, true);
    }

    @Test
    public void testMessageAccessWithDefaultMessageSourceAndFallbackToGerman() {
        doTestMessageAccess(false, true, true, true, false);
    }

    @Test
    public void testMessageAccessWithDefaultMessageSourceAndFallbackTurnedOff() {
        doTestMessageAccess(false, false, false, false, false);
    }

    @Test
    public void testMessageAccessWithDefaultMessageSourceAndFallbackTurnedOffAndFallbackToGerman() {
        doTestMessageAccess(false, false, true, true, false);
    }

    @Test
    public void testMessageAccessWithReloadableMessageSource() {
        doTestMessageAccess(true, true, false, false, false);
    }

    @Test
    public void testMessageAccessWithReloadableMessageSourceAndMessageFormat() {
        doTestMessageAccess(true, true, false, false, true);
    }

    @Test
    public void testMessageAccessWithReloadableMessageSourceAndFallbackToGerman() {
        doTestMessageAccess(true, true, true, true, false);
    }

    @Test
    public void testMessageAccessWithReloadableMessageSourceAndFallbackTurnedOff() {
        doTestMessageAccess(true, false, false, false, false);
    }

    @Test
    public void testMessageAccessWithReloadableMessageSourceAndFallbackTurnedOffAndFallbackToGerman() {
        doTestMessageAccess(true, false, true, true, false);
    }

    @Test
    public void testDefaultApplicationContextMessageSource() {
        GenericApplicationContext ac = new GenericApplicationContext();
        ac.refresh();
        Assert.assertEquals("default", ac.getMessage("code1", null, "default", Locale.ENGLISH));
        Assert.assertEquals("default value", ac.getMessage("code1", new Object[]{ "value" }, "default {0}", Locale.ENGLISH));
    }

    @Test
    public void testDefaultApplicationContextMessageSourceWithParent() {
        GenericApplicationContext ac = new GenericApplicationContext();
        GenericApplicationContext parent = new GenericApplicationContext();
        parent.refresh();
        ac.setParent(parent);
        ac.refresh();
        Assert.assertEquals("default", ac.getMessage("code1", null, "default", Locale.ENGLISH));
        Assert.assertEquals("default value", ac.getMessage("code1", new Object[]{ "value" }, "default {0}", Locale.ENGLISH));
    }

    @Test
    public void testStaticApplicationContextMessageSourceWithStaticParent() {
        StaticApplicationContext ac = new StaticApplicationContext();
        StaticApplicationContext parent = new StaticApplicationContext();
        parent.refresh();
        ac.setParent(parent);
        ac.refresh();
        Assert.assertEquals("default", ac.getMessage("code1", null, "default", Locale.ENGLISH));
        Assert.assertEquals("default value", ac.getMessage("code1", new Object[]{ "value" }, "default {0}", Locale.ENGLISH));
    }

    @Test
    public void testStaticApplicationContextMessageSourceWithDefaultParent() {
        StaticApplicationContext ac = new StaticApplicationContext();
        GenericApplicationContext parent = new GenericApplicationContext();
        parent.refresh();
        ac.setParent(parent);
        ac.refresh();
        Assert.assertEquals("default", ac.getMessage("code1", null, "default", Locale.ENGLISH));
        Assert.assertEquals("default value", ac.getMessage("code1", new Object[]{ "value" }, "default {0}", Locale.ENGLISH));
    }

    @Test
    public void testResourceBundleMessageSourceStandalone() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testResourceBundleMessageSourceWithWhitespaceInBasename() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasename("  org/springframework/context/support/messages  ");
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testResourceBundleMessageSourceWithDefaultCharset() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        ms.setDefaultEncoding("ISO-8859-1");
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testResourceBundleMessageSourceWithInappropriateDefaultCharset() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        ms.setDefaultEncoding("argh");
        ms.setFallbackToSystemLocale(false);
        try {
            ms.getMessage("code1", null, Locale.ENGLISH);
            Assert.fail("Should have thrown NoSuchMessageException");
        } catch (NoSuchMessageException ex) {
            // expected
        }
    }

    @Test
    public void testReloadableResourceBundleMessageSourceStandalone() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testReloadableResourceBundleMessageSourceWithCacheSeconds() throws InterruptedException {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        ms.setCacheSeconds(1);
        // Initial cache attempt
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
        Thread.sleep(1100);
        // Late enough for a re-cache attempt
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testReloadableResourceBundleMessageSourceWithNonConcurrentRefresh() throws InterruptedException {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        ms.setCacheSeconds(1);
        ms.setConcurrentRefresh(false);
        // Initial cache attempt
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
        Thread.sleep(1100);
        // Late enough for a re-cache attempt
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testReloadableResourceBundleMessageSourceWithCommonMessages() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        Properties commonMessages = new Properties();
        commonMessages.setProperty("warning", "Do not do {0}");
        ms.setCommonMessages(commonMessages);
        ms.setBasename("org/springframework/context/support/messages");
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
        Assert.assertEquals("Do not do this", ms.getMessage("warning", new Object[]{ "this" }, Locale.ENGLISH));
        Assert.assertEquals("Do not do that", ms.getMessage("warning", new Object[]{ "that" }, Locale.GERMAN));
    }

    @Test
    public void testReloadableResourceBundleMessageSourceWithWhitespaceInBasename() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("  org/springframework/context/support/messages  ");
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testReloadableResourceBundleMessageSourceWithDefaultCharset() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        ms.setDefaultEncoding("ISO-8859-1");
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("nachricht2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testReloadableResourceBundleMessageSourceWithInappropriateDefaultCharset() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        ms.setDefaultEncoding("unicode");
        Properties fileCharsets = new Properties();
        fileCharsets.setProperty("org/springframework/context/support/messages_de", "unicode");
        ms.setFileEncodings(fileCharsets);
        ms.setFallbackToSystemLocale(false);
        try {
            ms.getMessage("code1", null, Locale.ENGLISH);
            Assert.fail("Should have thrown NoSuchMessageException");
        } catch (NoSuchMessageException ex) {
            // expected
        }
    }

    @Test
    public void testReloadableResourceBundleMessageSourceWithInappropriateEnglishCharset() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        ms.setFallbackToSystemLocale(false);
        Properties fileCharsets = new Properties();
        fileCharsets.setProperty("org/springframework/context/support/messages", "unicode");
        ms.setFileEncodings(fileCharsets);
        try {
            ms.getMessage("code1", null, Locale.ENGLISH);
            Assert.fail("Should have thrown NoSuchMessageException");
        } catch (NoSuchMessageException ex) {
            // expected
        }
    }

    @Test
    public void testReloadableResourceBundleMessageSourceWithInappropriateGermanCharset() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        ms.setFallbackToSystemLocale(false);
        Properties fileCharsets = new Properties();
        fileCharsets.setProperty("org/springframework/context/support/messages_de", "unicode");
        ms.setFileEncodings(fileCharsets);
        Assert.assertEquals("message1", ms.getMessage("code1", null, Locale.ENGLISH));
        Assert.assertEquals("message2", ms.getMessage("code2", null, Locale.GERMAN));
    }

    @Test
    public void testReloadableResourceBundleMessageSourceFileNameCalculation() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        List<String> filenames = ms.calculateFilenamesForLocale("messages", Locale.ENGLISH);
        Assert.assertEquals(1, filenames.size());
        Assert.assertEquals("messages_en", filenames.get(0));
        filenames = ms.calculateFilenamesForLocale("messages", Locale.UK);
        Assert.assertEquals(2, filenames.size());
        Assert.assertEquals("messages_en", filenames.get(1));
        Assert.assertEquals("messages_en_GB", filenames.get(0));
        filenames = ms.calculateFilenamesForLocale("messages", new Locale("en", "GB", "POSIX"));
        Assert.assertEquals(3, filenames.size());
        Assert.assertEquals("messages_en", filenames.get(2));
        Assert.assertEquals("messages_en_GB", filenames.get(1));
        Assert.assertEquals("messages_en_GB_POSIX", filenames.get(0));
        filenames = ms.calculateFilenamesForLocale("messages", new Locale("en", "", "POSIX"));
        Assert.assertEquals(2, filenames.size());
        Assert.assertEquals("messages_en", filenames.get(1));
        Assert.assertEquals("messages_en__POSIX", filenames.get(0));
        filenames = ms.calculateFilenamesForLocale("messages", new Locale("", "UK", "POSIX"));
        Assert.assertEquals(2, filenames.size());
        Assert.assertEquals("messages__UK", filenames.get(1));
        Assert.assertEquals("messages__UK_POSIX", filenames.get(0));
        filenames = ms.calculateFilenamesForLocale("messages", new Locale("", "", "POSIX"));
        Assert.assertEquals(0, filenames.size());
    }

    @Test
    public void testMessageSourceResourceBundle() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasename("org/springframework/context/support/messages");
        MessageSourceResourceBundle rbe = new MessageSourceResourceBundle(ms, Locale.ENGLISH);
        Assert.assertEquals("message1", rbe.getString("code1"));
        Assert.assertTrue(rbe.containsKey("code1"));
        MessageSourceResourceBundle rbg = new MessageSourceResourceBundle(ms, Locale.GERMAN);
        Assert.assertEquals("nachricht2", rbg.getString("code2"));
        Assert.assertTrue(rbg.containsKey("code2"));
    }
}


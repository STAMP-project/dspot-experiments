/**
 * This file is part of the GhostDriver by Ivan De Marino <http://ivandemarino.me>.
 *
 * Copyright (c) 2012-2014, Ivan De Marino <http://ivandemarino.me>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ghostdriver;


import ghostdriver.server.EmptyPageHttpRequestCallback;
import ghostdriver.server.HttpRequestCallback;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.InvalidCookieDomainException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;


public class CookieTest extends BaseTestWithServer {
    private WebDriver driver;

    private static final HttpRequestCallback COOKIE_SETTING_CALLBACK = new EmptyPageHttpRequestCallback() {
        @Override
        public void call(HttpServletRequest req, HttpServletResponse res) throws IOException {
            super.call(req, res);
            Cookie cookie = new Cookie("test", "test");
            cookie.setDomain(".localhost");
            cookie.setMaxAge(360);
            res.addCookie(cookie);
            cookie = new Cookie("test2", "test2");
            cookie.setDomain(".localhost");
            res.addCookie(cookie);
        }
    };

    private static final HttpRequestCallback EMPTY_CALLBACK = new EmptyPageHttpRequestCallback();

    @Test
    public void gettingAllCookies() {
        server.setHttpHandler("GET", CookieTest.COOKIE_SETTING_CALLBACK);
        goToPage();
        org.openqa.selenium.Cookie[] cookies = getCookies();
        Assert.assertEquals(2, cookies.length);
        org.openqa.selenium.Cookie cookie = driver.manage().getCookieNamed("test");
        Assert.assertEquals("test", cookie.getName());
        Assert.assertEquals("test", cookie.getValue());
        Assert.assertEquals(".localhost", cookie.getDomain());
        Assert.assertEquals("/", cookie.getPath());
        Assert.assertTrue(((cookie.getExpiry()) != null));
        Assert.assertEquals(false, cookie.isSecure());
        org.openqa.selenium.Cookie cookie2 = driver.manage().getCookieNamed("test2");
        Assert.assertEquals("test2", cookie2.getName());
        Assert.assertEquals("test2", cookie2.getValue());
        Assert.assertEquals(".localhost", cookie2.getDomain());
        Assert.assertEquals("/", cookie2.getPath());
        Assert.assertEquals(false, cookie2.isSecure());
        Assert.assertTrue(((cookie2.getExpiry()) == null));
    }

    @Test
    public void gettingAllCookiesOnANonCookieSettingPage() {
        server.setHttpHandler("GET", CookieTest.EMPTY_CALLBACK);
        goToPage();
        Assert.assertEquals(0, getCookies().length);
    }

    @Test
    public void deletingAllCookies() {
        server.setHttpHandler("GET", CookieTest.COOKIE_SETTING_CALLBACK);
        goToPage();
        driver.manage().deleteAllCookies();
        Assert.assertEquals(0, getCookies().length);
    }

    @Test
    public void deletingOneCookie() {
        server.setHttpHandler("GET", CookieTest.COOKIE_SETTING_CALLBACK);
        goToPage();
        driver.manage().deleteCookieNamed("test");
        org.openqa.selenium.Cookie[] cookies = getCookies();
        Assert.assertEquals(1, cookies.length);
        Assert.assertEquals("test2", cookies[0].getName());
    }

    @Test
    public void addingACookie() {
        server.setHttpHandler("GET", CookieTest.EMPTY_CALLBACK);
        goToPage();
        driver.manage().addCookie(new org.openqa.selenium.Cookie("newCookie", "newValue", ".localhost", "/", null, false, false));
        org.openqa.selenium.Cookie[] cookies = getCookies();
        Assert.assertEquals(1, cookies.length);
        Assert.assertEquals("newCookie", cookies[0].getName());
        Assert.assertEquals("newValue", cookies[0].getValue());
        Assert.assertEquals(".localhost", cookies[0].getDomain());
        Assert.assertEquals("/", cookies[0].getPath());
        Assert.assertEquals(false, cookies[0].isSecure());
        Assert.assertEquals(false, cookies[0].isHttpOnly());
    }

    @Test
    public void modifyingACookie() {
        server.setHttpHandler("GET", CookieTest.COOKIE_SETTING_CALLBACK);
        goToPage();
        driver.manage().addCookie(new org.openqa.selenium.Cookie("test", "newValue", "localhost", "/", null, false));
        org.openqa.selenium.Cookie[] cookies = getCookies();
        Assert.assertEquals(2, cookies.length);
        Assert.assertEquals("test", cookies[1].getName());
        Assert.assertEquals("newValue", cookies[1].getValue());
        Assert.assertEquals(".localhost", cookies[1].getDomain());
        Assert.assertEquals("/", cookies[1].getPath());
        Assert.assertEquals(false, cookies[1].isSecure());
        Assert.assertEquals("test2", cookies[0].getName());
        Assert.assertEquals("test2", cookies[0].getValue());
        Assert.assertEquals(".localhost", cookies[0].getDomain());
        Assert.assertEquals("/", cookies[0].getPath());
        Assert.assertEquals(false, cookies[0].isSecure());
    }

    @Test
    public void shouldRetainCookieInfo() {
        server.setHttpHandler("GET", CookieTest.EMPTY_CALLBACK);
        goToPage();
        // Added cookie (in a sub-path - allowed)
        org.openqa.selenium.Cookie addedCookie = // < now + 100sec
        new org.openqa.selenium.Cookie.Builder("fish", "cod").expiresOn(new Date(((System.currentTimeMillis()) + (100 * 1000)))).path("/404").domain("localhost").build();
        driver.manage().addCookie(addedCookie);
        // Search cookie on the root-path and fail to find it
        org.openqa.selenium.Cookie retrieved = driver.manage().getCookieNamed("fish");
        Assert.assertNull(retrieved);
        // Go to the "/404" sub-path (to find the cookie)
        goToPage("404");
        retrieved = driver.manage().getCookieNamed("fish");
        Assert.assertNotNull(retrieved);
        // Check that it all matches
        Assert.assertEquals(addedCookie.getName(), retrieved.getName());
        Assert.assertEquals(addedCookie.getValue(), retrieved.getValue());
        Assert.assertEquals(addedCookie.getExpiry(), retrieved.getExpiry());
        Assert.assertEquals(addedCookie.isSecure(), retrieved.isSecure());
        Assert.assertEquals(addedCookie.getPath(), retrieved.getPath());
        Assert.assertTrue(retrieved.getDomain().contains(addedCookie.getDomain()));
    }

    @Test(expected = InvalidCookieDomainException.class)
    public void shouldNotAllowToCreateCookieOnDifferentDomain() {
        goToPage();
        // Added cookie (in a sub-path)
        org.openqa.selenium.Cookie addedCookie = // < now + 100sec
        new org.openqa.selenium.Cookie.Builder("fish", "cod").expiresOn(new Date(((System.currentTimeMillis()) + (100 * 1000)))).path("/404").domain("github.com").build();
        driver.manage().addCookie(addedCookie);
    }

    @Test
    public void shouldAllowToDeleteCookiesEvenIfNotSet() {
        WebDriver d = getDriver();
        d.get("https://github.com/");
        // Clear all cookies
        Assert.assertTrue(((d.manage().getCookies().size()) > 0));
        d.manage().deleteAllCookies();
        Assert.assertEquals(d.manage().getCookies().size(), 0);
        // All cookies deleted, call deleteAllCookies again. Should be a no-op.
        d.manage().deleteAllCookies();
        d.manage().deleteCookieNamed("non_existing_cookie");
        Assert.assertEquals(d.manage().getCookies().size(), 0);
    }

    @Test
    public void shouldAllowToSetCookieThatIsAlreadyExpired() {
        WebDriver d = getDriver();
        d.get("https://github.com/");
        // Clear all cookies
        Assert.assertTrue(((d.manage().getCookies().size()) > 0));
        d.manage().deleteAllCookies();
        Assert.assertEquals(d.manage().getCookies().size(), 0);
        // Added cookie that expires in the past
        org.openqa.selenium.Cookie addedCookie = // < now - 1 second
        new org.openqa.selenium.Cookie.Builder("expired", "yes").expiresOn(new Date(((System.currentTimeMillis()) - 1000))).build();
        d.manage().addCookie(addedCookie);
        org.openqa.selenium.Cookie cookie = d.manage().getCookieNamed("expired");
        Assert.assertNull(cookie);
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionIfAddingCookieBeforeLoadingAnyUrl() {
        // NOTE: At the time of writing, this test doesn't pass with FirefoxDriver.
        // ChromeDriver is fine instead.
        String xval = "123456789101112";// < detro: I buy you a beer if you guess what am I quoting here

        WebDriver d = getDriver();
        // Set cookie, without opening any page: should throw an exception
        d.manage().addCookie(new org.openqa.selenium.Cookie("x", xval));
    }

    @Test
    public void shouldBeAbleToCreateCookieViaJavascriptOnGoogle() {
        String ckey = "cookiekey";
        String cval = "cookieval";
        WebDriver d = getDriver();
        d.get("http://www.google.com");
        JavascriptExecutor js = ((JavascriptExecutor) (d));
        // Of course, no cookie yet(!)
        org.openqa.selenium.Cookie c = d.manage().getCookieNamed(ckey);
        Assert.assertNull(c);
        // Attempt to create cookie on multiple Google domains
        js.executeScript((((((((((((((((((((((("javascript:(" + (("function() {" + "   cook = document.cookie;") + "   begin = cook.indexOf('")) + ckey) + "=');") + "   var val;") + "   if (begin !== -1) {") + "       var end = cook.indexOf(\";\",begin);") + "       if (end === -1)") + "           end=cook.length;") + "       val=cook.substring(begin+11,end);") + "   }") + "   val = ['") + cval) + "'];") + "   if (val) {") + "       var d=Array('com','co.jp','ca','fr','de','co.uk','it','es','com.br');") + "       for (var i = 0; i < d.length; i++) {") + "           document.cookie = '") + ckey) + "='+val+';path=/;domain=.google.'+d[i]+'; ';") + "       }") + "   }") + "})();"));
        c = d.manage().getCookieNamed(ckey);
        Assert.assertNotNull(c);
        Assert.assertEquals(cval, c.getValue());
        // Set cookie as empty
        js.executeScript(((((("javascript:(" + ((("function() {" + "   var d = Array('com','co.jp','ca','fr','de','co.uk','it','cn','es','com.br');") + "   for(var i = 0; i < d.length; i++) {") + "       document.cookie='")) + ckey) + "=;path=/;domain=.google.'+d[i]+'; ';") + "   }") + "})();"));
        c = d.manage().getCookieNamed(ckey);
        Assert.assertNotNull(c);
        Assert.assertEquals("", c.getValue());
    }

    @Test
    public void addingACookieWithDefaults() {
        server.setHttpHandler("GET", CookieTest.EMPTY_CALLBACK);
        goToPage();
        long startTime = new Date().getTime();
        driver.manage().addCookie(new org.openqa.selenium.Cookie("newCookie", "newValue"));
        org.openqa.selenium.Cookie[] cookies = getCookies();
        Assert.assertEquals(1, cookies.length);
        Assert.assertEquals("newCookie", cookies[0].getName());
        Assert.assertEquals("newValue", cookies[0].getValue());
        Assert.assertEquals(".localhost", cookies[0].getDomain());
        Assert.assertEquals("/", cookies[0].getPath());
        Assert.assertEquals(false, cookies[0].isSecure());
        Assert.assertEquals(false, cookies[0].isHttpOnly());
        // expiry > 19 years in the future
        Assert.assertTrue(((startTime + 599184000000L) <= (cookies[0].getExpiry().getTime())));
    }
}


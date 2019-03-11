/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.session;


import NinjaConstant.applicationCookiePrefix;
import ninja.Context;
import ninja.Cookie;
import ninja.utils.NinjaProperties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FlashScopeTest {
    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    @Mock
    private NinjaProperties ninjaProperties;

    @Test
    public void testFlashScopeDoesNothingWhenFlashCookieEmpty() {
        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);
        flashCookie.init(context);
        // put nothing => intentionally to check if no flash cookie will be
        // saved
        flashCookie.save(context);
        // no cookie should be set as the flash scope is empty...:
        Mockito.verify(context, Mockito.never()).addCookie(Matchers.any(Cookie.class));
    }

    @Test
    public void testFlashCookieSettingWorks() {
        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);
        flashCookie.init(context);
        flashCookie.put("hello", "flashScope");
        // put nothing => intentionally to check if no flash cookie will be
        // saved
        flashCookie.save(context);
        // a cookie will be set => hello:flashScope
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        // verify some stuff on the set cookie
        Assert.assertEquals("NINJA_FLASH", cookieCaptor.getValue().getName());
        Assert.assertEquals("hello=flashScope", cookieCaptor.getValue().getValue());
        Assert.assertEquals((-1), cookieCaptor.getValue().getMaxAge());
        Assert.assertEquals(1, getCurrentFlashCookieData().size());
        Assert.assertEquals(1, getOutgoingFlashCookieData().size());
    }

    @Test
    public void testThatFlashCookieWorksAndIsActiveOnlyOneTime() {
        // setup this testmethod
        Cookie cookie = Cookie.builder("NINJA_FLASH", "hello=flashScope").build();
        Mockito.when(context.getCookie("NINJA_FLASH")).thenReturn(cookie);
        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);
        flashCookie.init(context);
        // make sure the old cookue gets parsed:
        Assert.assertEquals("flashScope", flashCookie.get("hello"));
        flashCookie.put("another message", "is there...");
        flashCookie.put("yet another message", "is there...");
        flashCookie.save(context);
        // a cookie will be set => hello:flashScope
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        // verify some stuff on the set cookie
        Assert.assertEquals("NINJA_FLASH", cookieCaptor.getValue().getName());
        // the new flash messages must be there..
        // but the old has disappeared (flashScope):
        Assert.assertEquals("another+message=is+there...&yet+another+message=is+there...", cookieCaptor.getValue().getValue());
        Assert.assertEquals(3, getCurrentFlashCookieData().size());
        Assert.assertEquals(2, getOutgoingFlashCookieData().size());
    }

    @Test
    public void testThatFlashCookieClearWorks() {
        // setup this testmethod
        Cookie cookie = Cookie.builder("NINJA_FLASH", "hello=flashScope").build();
        Mockito.when(context.getCookie("NINJA_FLASH")).thenReturn(cookie);
        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);
        flashCookie.init(context);
        // make sure the old cookue gets parsed:
        Assert.assertEquals("flashScope", flashCookie.get("hello"));
        flashCookie.put("funny new flash message", "is there...");
        // now test clearCurrentFlashCookieData
        flashCookie.clearCurrentFlashCookieData();
        Assert.assertEquals(0, getCurrentFlashCookieData().size());
        Assert.assertEquals(1, getOutgoingFlashCookieData().size());
    }

    @Test
    public void testThatFlashCookieClearOfOutgoingWorks() {
        // setup this testmethod
        Cookie cookie = Cookie.builder("NINJA_FLASH", "hello=flashScope").build();
        Mockito.when(context.getCookie("NINJA_FLASH")).thenReturn(cookie);
        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);
        flashCookie.init(context);
        // make sure the old cookue gets parsed:
        Assert.assertEquals("flashScope", flashCookie.get("hello"));
        flashCookie.put("funny new flash message", "is there...");
        // now test clearCurrentFlashCookieData
        flashCookie.discard();
        Assert.assertEquals(2, getCurrentFlashCookieData().size());
        Assert.assertEquals(0, getOutgoingFlashCookieData().size());
    }

    @Test
    public void testThatFlashCookieKeepWorks() {
        // setup this testmethod
        Cookie cookie = Cookie.builder("NINJA_FLASH", "hello=flashScope").build();
        Mockito.when(context.getCookie("NINJA_FLASH")).thenReturn(cookie);
        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);
        flashCookie.init(context);
        // make sure the old cookue gets parsed:
        Assert.assertEquals("flashScope", flashCookie.get("hello"));
        // make sure outgoing is 0
        Assert.assertEquals(1, getCurrentFlashCookieData().size());
        Assert.assertEquals(0, getOutgoingFlashCookieData().size());
        // now call keep.
        flashCookie.keep();
        // => now both queues must be 1
        Assert.assertEquals(1, getCurrentFlashCookieData().size());
        Assert.assertEquals(1, getOutgoingFlashCookieData().size());
    }

    @Test
    public void testThatCorrectMethodOfNinjaPropertiesIsUsedSoThatStuffBreaksWhenPropertyIsAbsent() {
        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);
        // Make sure that getOrDie has been called. This makes sure we have set
        // a cookie prefix:
        Mockito.verify(ninjaProperties).getOrDie(applicationCookiePrefix);
    }

    @Test
    public void testThatCookieUsesContextPath() {
        Mockito.when(context.getContextPath()).thenReturn("/my_context");
        FlashScope flashScope = new FlashScopeImpl(ninjaProperties);
        flashScope.put("anykey", "anyvalue");
        flashScope.init(context);
        flashScope.save(context);
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        Assert.assertThat(cookie.getPath(), CoreMatchers.equalTo("/my_context/"));
    }

    @Test
    public void removeClearsBothCurrentAndOutgoing() {
        FlashScope flashScope = new FlashScopeImpl(ninjaProperties);
        flashScope.init(context);
        flashScope.put("anykey", "anyvalue");
        flashScope.remove("anykey");
        flashScope.save(context);
        // cookie will not be created
        Mockito.verify(context, Mockito.times(0)).addCookie(cookieCaptor.capture());
    }
}


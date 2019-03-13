package org.mockserver.model;


import java.util.Arrays;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class CookiesTest {
    @Test
    public void shouldBuildCookie() throws Exception {
        // given
        Cookies cookies = new Cookies();
        // when
        Cookie cookie = cookies.build(NottableString.string("name"), NottableString.string("value"));
        // then
        Assert.assertThat(cookie, Is.is(new Cookie(NottableString.string("name"), NottableString.string("value"))));
    }

    @Test
    public void shouldAddEntriesAsCookieVarargs() throws Exception {
        // given
        Cookies cookies = new Cookies();
        // when
        cookies.withEntries(Cookie.cookie("name_one", "value_one"), Cookie.cookie("name_two", "value_two"), Cookie.cookie("name_three", "value_three"));
        // then
        Assert.assertThat(cookies.getEntries().size(), Is.is(3));
        Assert.assertThat(cookies.getEntries(), IsCollectionContaining.hasItems(Cookie.cookie("name_one", "value_one"), Cookie.cookie("name_two", "value_two"), Cookie.cookie("name_three", "value_three")));
    }

    @Test
    public void shouldAddEntriesAsCookieList() throws Exception {
        // given
        Cookies cookies = new Cookies();
        // when
        cookies.withEntries(Arrays.asList(Cookie.cookie("name_one", "value_one"), Cookie.cookie("name_two", "value_two"), Cookie.cookie("name_three", "value_three")));
        // then
        Assert.assertThat(cookies.getEntries().size(), Is.is(3));
        Assert.assertThat(cookies.getEntries(), IsCollectionContaining.hasItems(Cookie.cookie("name_one", "value_one"), Cookie.cookie("name_two", "value_two"), Cookie.cookie("name_three", "value_three")));
    }

    @Test
    public void shouldAddEntryAsCookie() throws Exception {
        // given
        Cookies cookies = new Cookies();
        // when
        cookies.withEntry(Cookie.cookie("name_one", "value_one"));
        cookies.withEntry(Cookie.cookie("name_two", "value_two"));
        cookies.withEntry(Cookie.cookie("name_three", "value_three"));
        // then
        Assert.assertThat(cookies.getEntries().size(), Is.is(3));
        Assert.assertThat(cookies.getEntries(), IsCollectionContaining.hasItems(Cookie.cookie("name_one", "value_one"), Cookie.cookie("name_two", "value_two"), Cookie.cookie("name_three", "value_three")));
    }

    @Test
    public void shouldAddEntryAsNameAndValueString() throws Exception {
        // given
        Cookies cookies = new Cookies();
        // when
        cookies.withEntry("name_one", "value_one");
        cookies.withEntry("name_two", "value_two");
        cookies.withEntry("name_three", "value_three");
        // then
        Assert.assertThat(cookies.getEntries().size(), Is.is(3));
        Assert.assertThat(cookies.getEntries(), IsCollectionContaining.hasItems(Cookie.cookie("name_one", "value_one"), Cookie.cookie("name_two", "value_two"), Cookie.cookie("name_three", "value_three")));
    }

    @Test
    public void shouldAddEntryAsNameAndValueNottableString() throws Exception {
        // given
        Cookies cookies = new Cookies();
        // when
        cookies.withEntry(NottableString.string("name_one"), NottableString.not("value_one"));
        cookies.withEntry(NottableString.not("name_two"), NottableString.string("value_two"));
        cookies.withEntry(NottableString.string("name_three"), NottableString.string("value_three"));
        // then
        Assert.assertThat(cookies.getEntries().size(), Is.is(3));
        Assert.assertThat(cookies.getEntries(), IsCollectionContaining.hasItems(Cookie.cookie(NottableString.string("name_one"), NottableString.not("value_one")), Cookie.cookie(NottableString.not("name_two"), NottableString.string("value_two")), Cookie.cookie(NottableString.string("name_three"), NottableString.string("value_three"))));
    }
}


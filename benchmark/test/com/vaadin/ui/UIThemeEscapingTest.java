package com.vaadin.ui;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class UIThemeEscapingTest {
    private UI ui;

    @Test
    public void dangerousCharactersAreRemoved() {
        ui.setTheme("a<\u00e5(_\"$");
        MatcherAssert.assertThat(ui.getTheme(), CoreMatchers.is("a?_$"));
    }

    @Test
    public void nullThemeIsSet() {
        ui.setTheme("foobar");
        ui.setTheme(null);
        MatcherAssert.assertThat(ui.getTheme(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void themeIsSetOnInit() {
        ui.setTheme("foobar");
        initUiWithTheme("bar");
        MatcherAssert.assertThat(ui.getTheme(), CoreMatchers.is("bar"));
    }

    @Test
    public void nullThemeIsSetOnInit() {
        ui.setTheme("foobar");
        initUiWithTheme(null);
        MatcherAssert.assertThat(ui.getTheme(), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}


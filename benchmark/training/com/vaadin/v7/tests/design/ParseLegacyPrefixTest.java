package com.vaadin.v7.tests.design;


import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import java.io.FileNotFoundException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Test reading a design with all components using the legacy prefix.
 */
public class ParseLegacyPrefixTest {
    @Test
    public void allComponentsAreParsed() throws FileNotFoundException {
        DesignContext ctx = Design.read(getClass().getResourceAsStream("all-components-legacy.html"), null);
        MatcherAssert.assertThat(ctx, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(ctx.getRootComponent(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }
}


package com.vaadin.v7.tests.design;


import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import java.io.FileNotFoundException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Just top level test case that contains all synchronizable components
 *
 * @author Vaadin Ltd
 */
public class ParseAllSupportedComponentsTest {
    @Test
    public void allComponentsAreParsed() throws FileNotFoundException {
        DesignContext ctx = Design.read(getClass().getResourceAsStream("all-components.html"), null);
        MatcherAssert.assertThat(ctx, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        MatcherAssert.assertThat(ctx.getRootComponent(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }
}


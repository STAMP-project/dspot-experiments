package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Test for context menu position and size.
 *
 * @author Vaadin Ltd
 */
public class ContextMenuSizeTest extends MultiBrowserTest {
    @Test
    public void contextMenuIsResizedToFitWindow() {
        int initialHeight = getContextMenuHeight();
        resizeViewPortHeightTo((initialHeight - 10));
        MatcherAssert.assertThat(getContextMenuHeight(), Matchers.lessThan(initialHeight));
        resizeViewPortHeightTo((initialHeight + 100));
        MatcherAssert.assertThat(getContextMenuHeight(), CoreMatchers.is(initialHeight));
    }

    @Test
    public void contextMenuPositionIsChangedAfterResize() {
        int height = getContextMenuHeight();
        int y = getContextMenuY();
        resizeViewPortHeightTo(((y + height) - 10));
        MatcherAssert.assertThat(getContextMenuY(), Matchers.lessThan(y));
        MatcherAssert.assertThat(getContextMenuHeight(), CoreMatchers.is(height));
    }
}


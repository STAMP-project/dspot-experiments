package com.vaadin.tests.themes.valo;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Test for $v-textfield-bevel value when $v-bevel is unset.
 *
 * @author Vaadin Ltd
 */
public class TextFieldBevelTest extends MultiBrowserTest {
    @Test
    public void bevelChangesBoxShadow() {
        openTestURL();
        String boxShadowWithBevel = getBoxShadow();
        openTestUrlWithoutBevel();
        String boxShadowWithoutBevel = getBoxShadow();
        MatcherAssert.assertThat(boxShadowWithBevel, CoreMatchers.is(CoreMatchers.not(boxShadowWithoutBevel)));
    }
}


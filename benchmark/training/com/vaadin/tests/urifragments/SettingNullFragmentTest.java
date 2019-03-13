package com.vaadin.tests.urifragments;


import SettingNullFragment.FRAG_1_URI;
import SettingNullFragment.NULL_FRAGMENT_URI;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * UI test: setting null as URI fragment clear (remove) the fragment in the
 * browser
 *
 * @author Vaadin Ltd
 */
public class SettingNullFragmentTest extends MultiBrowserTest {
    @Test
    public void testSettingNullURIFragment() throws Exception {
        openTestURL();
        navigateToFrag1();
        assertFragment(FRAG_1_URI);
        navigateToNull();
        assertFragment(NULL_FRAGMENT_URI);
    }
}


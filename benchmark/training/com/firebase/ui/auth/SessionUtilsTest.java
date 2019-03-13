package com.firebase.ui.auth;


import com.firebase.ui.auth.util.data.SessionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link SessionUtils}.
 */
@RunWith(RobolectricTestRunner.class)
public class SessionUtilsTest {
    @Test
    public void testGenerateRandomAlphaNumericString() {
        for (int i = 0; i < 10; i++) {
            assertThat(SessionUtils.generateRandomAlphaNumericString(i).length()).isEqualTo(i);
        }
    }
}


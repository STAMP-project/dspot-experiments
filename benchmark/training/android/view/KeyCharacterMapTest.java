package android.view;


import KeyCharacterMap.BUILT_IN_KEYBOARD;
import KeyCharacterMap.FULL;
import KeyEvent.ACTION_DOWN;
import KeyEvent.ACTION_UP;
import KeyEvent.KEYCODE_0;
import KeyEvent.KEYCODE_BACK;
import KeyEvent.KEYCODE_E;
import KeyEvent.KEYCODE_S;
import KeyEvent.KEYCODE_T;
import KeyEvent.KEYCODE_UNKNOWN;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Test {@link android.view.KeyCharacterMap}.
 *
 * <p>Inspired from Android cts/tests/tests/view/src/android/view/cts/KeyCharacterMap.java
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public final class KeyCharacterMapTest {
    private KeyCharacterMap keyCharacterMap;

    @Test
    public void testLoad() {
        keyCharacterMap = null;
        keyCharacterMap = KeyCharacterMap.load(BUILT_IN_KEYBOARD);
        Assert.assertNotNull(keyCharacterMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMatchNull() {
        keyCharacterMap.getMatch(KEYCODE_0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMatchMetaStateNull() {
        keyCharacterMap.getMatch(KEYCODE_0, null, 1);
    }

    @Test
    public void testGetKeyboardType() {
        assertThat(keyCharacterMap.getKeyboardType()).isEqualTo(FULL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEventsNull() {
        keyCharacterMap.getEvents(null);
    }

    @Test
    public void testGetEventsLowerCase() {
        KeyEvent[] events = keyCharacterMap.getEvents("test".toCharArray());
        assertThat(events[0].getAction()).isEqualTo(ACTION_DOWN);
        assertThat(events[0].getKeyCode()).isEqualTo(KEYCODE_T);
        assertThat(events[1].getAction()).isEqualTo(ACTION_UP);
        assertThat(events[1].getKeyCode()).isEqualTo(KEYCODE_T);
        assertThat(events[2].getAction()).isEqualTo(ACTION_DOWN);
        assertThat(events[2].getKeyCode()).isEqualTo(KEYCODE_E);
        assertThat(events[3].getAction()).isEqualTo(ACTION_UP);
        assertThat(events[3].getKeyCode()).isEqualTo(KEYCODE_E);
        assertThat(events[4].getAction()).isEqualTo(ACTION_DOWN);
        assertThat(events[4].getKeyCode()).isEqualTo(KEYCODE_S);
        assertThat(events[5].getAction()).isEqualTo(ACTION_UP);
        assertThat(events[5].getKeyCode()).isEqualTo(KEYCODE_S);
        assertThat(events[6].getAction()).isEqualTo(ACTION_DOWN);
        assertThat(events[6].getKeyCode()).isEqualTo(KEYCODE_T);
        assertThat(events[7].getAction()).isEqualTo(ACTION_UP);
        assertThat(events[7].getKeyCode()).isEqualTo(KEYCODE_T);
    }

    @Test
    public void testGetEventsCapital() {
        // Just assert that we got something back, there are many ways to return correct KeyEvents for
        // this sequence.
        assertThat(keyCharacterMap.getEvents("Test".toCharArray())).isNotEmpty();
    }

    @Test
    public void testUnknownCharacters() {
        assertThat(keyCharacterMap.get(KEYCODE_UNKNOWN, 0)).isEqualTo(0);
        assertThat(keyCharacterMap.get(KEYCODE_BACK, 0)).isEqualTo(0);
    }
}


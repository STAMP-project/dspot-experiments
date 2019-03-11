/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test.keyevents;


import android.view.KeyEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class KeyEventsActivityTest {
    private KeyEventsActivity activity;

    @Test
    public void subclassTakesPrecedenceInKeyEventHandling() {
        KeyEvent keyEvent = Mockito.mock(KeyEvent.class);
        int keyCode = KeyEvent.KEYCODE_E;
        activity.onKeyLongPress(keyCode, keyEvent);
        assertThat(activity.isELongPressed).isTrue();
        assertThat(activity.isELongPressedInAnnotatedClass).isFalse();
    }

    @Test
    public void keyDownKeyCodeNameFromMethod() {
        KeyEvent keyEvent = Mockito.mock(KeyEvent.class);
        int keyCode = KeyEvent.KEYCODE_ENTER;
        activity.onKeyDown(keyCode, keyEvent);
        assertThat(activity.isEnterDown).isTrue();
    }

    @Test
    public void keyUpKeyCodeNameFromMethod() {
        KeyEvent keyEvent = Mockito.mock(KeyEvent.class);
        int keyCode = KeyEvent.KEYCODE_U;
        activity.onKeyUp(keyCode, keyEvent);
        assertThat(activity.isUKeyUp).isTrue();
    }

    @Test
    public void multipleArguments() {
        KeyEvent keyEvent = Mockito.mock(KeyEvent.class);
        int keyCode = KeyEvent.KEYCODE_W;
        int count = 1;
        Mockito.when(keyEvent.getKeyCode()).thenReturn(keyCode);
        activity.onKeyMultiple(keyCode, count, keyEvent);
        assertThat(activity.isWMultiple).isTrue();
        assertThat(activity.isNineMultiple).isFalse();
    }

    @Test
    public void keyMultipleWithCount() {
        KeyEvent keyEvent = Mockito.mock(KeyEvent.class);
        int keyCode = KeyEvent.KEYCODE_9;
        int count = 9;
        Mockito.when(keyEvent.getKeyCode()).thenReturn(keyCode);
        activity.onKeyMultiple(keyCode, count, keyEvent);
        assertThat(activity.isNineMultiple).isTrue();
        assertThat(activity.nineMultipleCount).isEqualTo(count);
    }

    @Test
    public void goodMethodReturnIfKeyLongPress() {
        KeyEvent keyEvent = Mockito.mock(KeyEvent.class);
        int keyCode = KeyEvent.KEYCODE_E;
        boolean eKeyLongPressReturn = activity.onKeyLongPress(keyCode, keyEvent);
        assertThat(activity.isELongPressed).isTrue();
        assertThat(eKeyLongPressReturn).isFalse();
    }
}


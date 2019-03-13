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
package org.androidannotations.test;


import R.id.editText1;
import R.id.editText2;
import R.id.editText3;
import R.id.editText4;
import R.id.textView2;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class EditorActionsActivityTest {
    private EditorActionsHandledActivity activity;

    @Test
    public void testActionHandled() {
        assertThat(activity.actionHandled).isFalse();
        EditText editText = ((EditText) (activity.findViewById(editText1)));
        OnEditorActionListener listener = EditorActionsActivityTest.getOnEditorActionListener(editText);
        listener.onEditorAction(editText, 0, null);
        assertThat(activity.actionHandled).isTrue();
    }

    @Test
    public void testEditTextPassed() {
        assertThat(activity.passedEditText).isNull();
        EditText editText = ((EditText) (activity.findViewById(editText4)));
        OnEditorActionListener listener = EditorActionsActivityTest.getOnEditorActionListener(editText);
        listener.onEditorAction(editText, 0, null);
        assertThat(activity.passedEditText).isSameAs(editText);
    }

    @Test
    public void testActionIdPassed() {
        assertThat(activity.actionId).isZero();
        EditText editText = ((EditText) (activity.findViewById(editText2)));
        OnEditorActionListener listener = EditorActionsActivityTest.getOnEditorActionListener(editText);
        int actionId = 2;
        listener.onEditorAction(editText, actionId, null);
        assertThat(activity.actionId).isEqualTo(actionId);
    }

    @Test
    public void testKeyEventPassed() {
        assertThat(activity.keyEvent).isNull();
        EditText editText = ((EditText) (activity.findViewById(editText3)));
        OnEditorActionListener listener = EditorActionsActivityTest.getOnEditorActionListener(editText);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_F);
        listener.onEditorAction(editText, 0, event);
        assertThat(activity.keyEvent).isEqualTo(event);
    }

    @Test
    public void testTrueReturnedFromVoidEditorActionMethod() {
        EditText editText = ((EditText) (activity.findViewById(editText3)));
        OnEditorActionListener listener = EditorActionsActivityTest.getOnEditorActionListener(editText);
        assertThat(listener.onEditorAction(editText, 0, null)).isTrue();
    }

    @Test
    public void testValueReturnedFromNonVoidEditorActionMethod() {
        TextView editText = ((TextView) (activity.findViewById(textView2)));
        OnEditorActionListener listener = EditorActionsActivityTest.getOnEditorActionListener(editText);
        assertThat(listener.onEditorAction(editText, 0, null)).isFalse();
    }
}


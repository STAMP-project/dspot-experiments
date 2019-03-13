/**
 * Copyright (C) 2017-2018 Samuel Wall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.samuelwall.materialtaptargetprompt;


import KeyEvent.DispatcherState;
import KeyEvent.KEYCODE_BACK;
import MaterialTapTargetPrompt.PromptView;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

import static org.junit.Assert.fail;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class PromptViewUnitTest {
    @Test
    public void testPromptView_TouchEvent_OutsideClipBounds() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mClipBounds = new Rect(0, 0, 1080, 1920);
        promptView.mClipToBounds = true;
        final MotionEvent event = createMotionEvent(1081, 500);
        TestCase.assertFalse(promptView.onTouchEvent(event));
    }

    @Test
    public void testPromptView_TouchEvent_OutsideBackground() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mClipBounds = new Rect(0, 0, 1080, 1920);
        promptView.mClipToBounds = true;
        promptView.mPromptOptions.setPromptBackground(Mockito.spy(new RectanglePromptBackground()));
        final MotionEvent event = createMotionEvent(10, 10);
        Mockito.when(promptView.mPromptOptions.getPromptBackground().contains(10, 10)).thenReturn(false);
        TestCase.assertFalse(promptView.onTouchEvent(event));
    }

    @Test
    public void testPromptView_TouchEvent_OutsideBackground_Capture() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mClipBounds = new Rect(0, 0, 1080, 1920);
        promptView.mClipToBounds = true;
        promptView.mPromptOptions.setCaptureTouchEventOutsidePrompt(true);
        promptView.mPromptOptions.setPromptBackground(Mockito.spy(new RectanglePromptBackground()));
        final MotionEvent event = createMotionEvent(10, 10);
        Mockito.when(promptView.mPromptOptions.getPromptBackground().contains(10, 10)).thenReturn(false);
        Assert.assertTrue(promptView.onTouchEvent(event));
    }

    @Test
    public void testPromptView_TouchEvent_NoCapture() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mClipToBounds = false;
        promptView.mPromptOptions.setCaptureTouchEventOnFocal(false);
        promptView.mPromptOptions.setPromptBackground(Mockito.spy(new RectanglePromptBackground()));
        promptView.mPromptOptions.setPromptFocal(Mockito.spy(new RectanglePromptFocal()));
        final MotionEvent event = createMotionEvent(10, 10);
        Mockito.when(promptView.mPromptOptions.getPromptBackground().contains(10, 10)).thenReturn(true);
        Mockito.when(promptView.mPromptOptions.getPromptFocal().contains(10, 10)).thenReturn(true);
        TestCase.assertFalse(promptView.onTouchEvent(event));
    }

    @Test
    public void testPromptView_TouchEvent_NullListener() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mClipToBounds = false;
        promptView.mPromptOptions.setCaptureTouchEventOnFocal(true);
        promptView.mPromptOptions.setPromptBackground(Mockito.spy(new RectanglePromptBackground()));
        promptView.mPromptOptions.setPromptFocal(Mockito.spy(new RectanglePromptFocal()));
        final MotionEvent event = createMotionEvent(10, 10);
        Mockito.when(promptView.mPromptOptions.getPromptBackground().contains(10, 10)).thenReturn(true);
        Mockito.when(promptView.mPromptOptions.getPromptFocal().contains(10, 10)).thenReturn(true);
        Assert.assertTrue(promptView.onTouchEvent(event));
    }

    @Test
    public void testPromptView_BackButton_NoAutoDismiss_Handled() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mPromptOptions.setAutoDismiss(false);
        KeyEvent.DispatcherState state = new KeyEvent.DispatcherState();
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(state);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        Assert.assertTrue(promptView.dispatchKeyEventPreIme(event));
        event = new KeyEvent(1, System.currentTimeMillis(), KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, 0);
        Mockito.when(promptView.onKeyPreIme(KEYCODE_BACK, event)).thenReturn(true);
        Assert.assertTrue(promptView.dispatchKeyEventPreIme(event));
    }

    @Test
    public void testPromptView_BackButton_NoAutoDismiss() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mPromptOptions.setAutoDismiss(false);
        KeyEvent.DispatcherState state = new KeyEvent.DispatcherState();
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(state);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        Assert.assertTrue(promptView.dispatchKeyEventPreIme(event));
        event = new KeyEvent(1, System.currentTimeMillis(), KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, 0);
        TestCase.assertFalse(promptView.dispatchKeyEventPreIme(event));
    }

    @Test
    public void testPromptView_BackButton_NullListener() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        KeyEvent.DispatcherState state = new KeyEvent.DispatcherState();
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(state);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        Assert.assertTrue(promptView.dispatchKeyEventPreIme(event));
        event = new KeyEvent(1, System.currentTimeMillis(), KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, 0);
        Assert.assertTrue(promptView.dispatchKeyEventPreIme(event));
    }

    @Test
    public void testPromptView_BackButton() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        final PromptViewUnitTest.BackButtonTestPromptTouchListener listener = new PromptViewUnitTest.BackButtonTestPromptTouchListener();
        promptView.mPromptTouchedListener = listener;
        KeyEvent.DispatcherState state = new KeyEvent.DispatcherState();
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(state);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        Assert.assertTrue(promptView.dispatchKeyEventPreIme(event));
        event = new KeyEvent(1, System.currentTimeMillis(), KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, 0);
        Assert.assertTrue(promptView.dispatchKeyEventPreIme(event));
        Assert.assertTrue(listener.success);
    }

    private static class BackButtonTestPromptTouchListener implements MaterialTapTargetPrompt.PromptView.PromptTouchedListener {
        public boolean success;

        @Override
        public void onFocalPressed() {
            fail();
        }

        @Override
        public void onNonFocalPressed() {
            fail();
        }

        @Override
        public void onBackButtonPressed() {
            success = true;
        }
    }

    @Test
    public void testPromptView_BackButton_UpCancelled() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mPromptTouchedListener = new MaterialTapTargetPrompt.PromptView.PromptTouchedListener() {
            @Override
            public void onFocalPressed() {
                fail();
            }

            @Override
            public void onNonFocalPressed() {
                fail();
            }

            @Override
            public void onBackButtonPressed() {
                fail();
            }
        };
        KeyEvent.DispatcherState state = new KeyEvent.DispatcherState();
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(state);
        KeyEvent event = new KeyEvent(1, System.currentTimeMillis(), KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK, 1);
        TestCase.assertFalse(promptView.dispatchKeyEventPreIme(event));
        event = new KeyEvent(1, System.currentTimeMillis(), KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, 0, 0, 0, 0, KeyEvent.FLAG_CANCELED);
        TestCase.assertFalse(promptView.dispatchKeyEventPreIme(event));
    }

    @Test
    public void testPromptView_BackButton_RepeatEvent() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mPromptTouchedListener = new MaterialTapTargetPrompt.PromptView.PromptTouchedListener() {
            @Override
            public void onFocalPressed() {
                fail();
            }

            @Override
            public void onNonFocalPressed() {
                fail();
            }

            @Override
            public void onBackButtonPressed() {
                fail();
            }
        };
        KeyEvent.DispatcherState state = new KeyEvent.DispatcherState();
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(state);
        KeyEvent event = new KeyEvent(1, System.currentTimeMillis(), KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK, 1);
        TestCase.assertFalse(promptView.dispatchKeyEventPreIme(event));
        event = new KeyEvent(1, System.currentTimeMillis(), KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, 0);
        TestCase.assertFalse(promptView.dispatchKeyEventPreIme(event));
    }

    @Test
    public void testPromptView_BackButton_NullDispatcherState() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mPromptTouchedListener = new MaterialTapTargetPrompt.PromptView.PromptTouchedListener() {
            @Override
            public void onFocalPressed() {
                fail();
            }

            @Override
            public void onNonFocalPressed() {
                fail();
            }

            @Override
            public void onBackButtonPressed() {
                fail();
            }
        };
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(null);
        final KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        TestCase.assertFalse(promptView.dispatchKeyEventPreIme(event));
    }

    @Test
    public void testPromptView_BackButton_NotBackKey() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mPromptTouchedListener = new MaterialTapTargetPrompt.PromptView.PromptTouchedListener() {
            @Override
            public void onFocalPressed() {
                fail();
            }

            @Override
            public void onNonFocalPressed() {
                fail();
            }

            @Override
            public void onBackButtonPressed() {
                fail();
            }
        };
        KeyEvent.DispatcherState state = new KeyEvent.DispatcherState();
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(state);
        final KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_1);
        TestCase.assertFalse(promptView.dispatchKeyEventPreIme(event));
    }

    @Test
    public void testPromptView_BackButton_Disabled() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mPromptOptions.setBackButtonDismissEnabled(false);
        promptView.mPromptTouchedListener = new MaterialTapTargetPrompt.PromptView.PromptTouchedListener() {
            @Override
            public void onFocalPressed() {
                fail();
            }

            @Override
            public void onNonFocalPressed() {
                fail();
            }

            @Override
            public void onBackButtonPressed() {
                fail();
            }
        };
        KeyEvent.DispatcherState state = new KeyEvent.DispatcherState();
        Mockito.when(promptView.getKeyDispatcherState()).thenReturn(state);
        final KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        TestCase.assertFalse(promptView.dispatchKeyEventPreIme(event));
    }

    @SuppressLint("WrongCall")
    @Test
    public void testPromptView_Draw_IconDrawable() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mClipToBounds = false;
        promptView.mIconDrawable = Mockito.mock(Drawable.class);
        promptView.onDraw(Mockito.mock(Canvas.class));
    }

    @SuppressLint("WrongCall")
    @Test
    public void testPromptView_Draw_RenderView() {
        final MaterialTapTargetPrompt.PromptView promptView = createPromptView();
        promptView.mClipToBounds = false;
        promptView.mTargetRenderView = Mockito.mock(View.class);
        promptView.onDraw(Mockito.mock(Canvas.class));
    }
}


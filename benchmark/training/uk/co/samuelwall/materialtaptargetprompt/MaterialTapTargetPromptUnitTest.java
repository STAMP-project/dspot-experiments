/**
 * Copyright (C) 2016-2018 Samuel Wall
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


import AccessibilityEvent.TYPE_VIEW_FOCUSED;
import Build.VERSION;
import Build.VERSION_CODES.JELLY_BEAN_MR2;
import KeyEvent.DispatcherState;
import MaterialTapTargetPrompt.AnimatorListener;
import MaterialTapTargetPrompt.Builder;
import MaterialTapTargetPrompt.STATE_BACK_BUTTON_PRESSED;
import MaterialTapTargetPrompt.STATE_DISMISSED;
import MaterialTapTargetPrompt.STATE_DISMISSING;
import MaterialTapTargetPrompt.STATE_FINISHED;
import MaterialTapTargetPrompt.STATE_FINISHING;
import MaterialTapTargetPrompt.STATE_FOCAL_PRESSED;
import MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED;
import MaterialTapTargetPrompt.STATE_REVEALED;
import MaterialTapTargetPrompt.STATE_REVEALING;
import MaterialTapTargetPrompt.STATE_SHOW_FOR_TIMEOUT;
import MotionEvent.ACTION_DOWN;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.AccessibilityUtil;
import org.robolectric.annotation.AccessibilityChecks;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import uk.co.samuelwall.materialtaptargetprompt.extras.PromptFocal;
import uk.co.samuelwall.materialtaptargetprompt.extras.PromptOptions;

import static MaterialTapTargetPrompt.STATE_DISMISSED;
import static MaterialTapTargetPrompt.STATE_DISMISSING;
import static MaterialTapTargetPrompt.STATE_FINISHED;
import static MaterialTapTargetPrompt.STATE_FINISHING;
import static MaterialTapTargetPrompt.STATE_FOCAL_PRESSED;
import static MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED;
import static MaterialTapTargetPrompt.STATE_NOT_SHOWN;
import static MaterialTapTargetPrompt.STATE_REVEALED;
import static MaterialTapTargetPrompt.STATE_REVEALING;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class MaterialTapTargetPromptUnitTest extends BaseTestStateProgress {
    private static int SCREEN_WIDTH = 1080;

    private static int SCREEN_HEIGHT = 1920;

    @Test
    public void targetView() {
        final MaterialTapTargetPrompt.Builder builder = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setPrimaryText("test");
        final Button button = Mockito.mock(Button.class);
        builder.getResourceFinder().getPromptParentView().addView(button);
        builder.setTarget(button);
        final MaterialTapTargetPrompt prompt = builder.create();
        Assert.assertNotNull(prompt);
        prompt.show();
    }

    @Test
    public void targetViewBelowKitKat() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", JELLY_BEAN_MR2);
        final MaterialTapTargetPrompt.Builder builder = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setPrimaryText("test");
        final Button button = Mockito.mock(Button.class);
        Mockito.when(button.getWindowToken()).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                return Mockito.mock(IBinder.class);
            }
        });
        builder.getResourceFinder().getPromptParentView().addView(button);
        builder.setTarget(button);
        final MaterialTapTargetPrompt prompt = builder.create();
        Assert.assertNotNull(prompt);
        prompt.show();
    }

    @Test
    public void targetViewNotAttached() {
        final MaterialTapTargetPrompt.Builder builder = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setPrimaryText("test");
        final Button button = Mockito.mock(Button.class);
        builder.setTarget(button);
        final MaterialTapTargetPrompt prompt = builder.show();
        Assert.assertNotNull(prompt);
    }

    @Test
    public void targetViewBelowKitKatNotAttached() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", JELLY_BEAN_MR2);
        final MaterialTapTargetPrompt.Builder builder = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setPrimaryText("test");
        final Button button = Mockito.mock(Button.class);
        builder.setTarget(button);
        final MaterialTapTargetPrompt prompt = builder.show();
        Assert.assertNotNull(prompt);
    }

    @Test
    public void promptTouchEventFocal() {
        expectedStateProgress = 5;
        final MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_FOCAL_PRESSED, state);
                            Assert.assertEquals(STATE_FOCAL_PRESSED, prompt.getState());
                        } else
                            if ((actualStateProgress) == 3) {
                                Assert.assertEquals(STATE_FINISHING, state);
                                Assert.assertEquals(STATE_FINISHING, prompt.getState());
                                UnitTestUtils.endCurrentAnimation(prompt);
                            } else
                                if ((actualStateProgress) == 4) {
                                    Assert.assertEquals(STATE_FINISHED, state);
                                    Assert.assertEquals(STATE_FINISHED, prompt.getState());
                                } else {
                                    Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                                }




                (actualStateProgress)++;
            }
        }).show();
        Assert.assertNotNull(prompt);
        Assert.assertFalse(prompt.mView.onTouchEvent(MotionEvent.obtain(0, 0, ACTION_DOWN, 10, 10, 0)));
    }

    @Test
    public void promptTouchEventFocalDismissing() {
        expectedStateProgress = 4;
        createBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_DISMISSING, state);
                            Assert.assertEquals(STATE_DISMISSING, prompt.getState());
                        } else
                            if ((actualStateProgress) == 3) {
                                Assert.assertEquals(STATE_DISMISSED, state);
                                Assert.assertEquals(STATE_DISMISSED, prompt.getState());
                                UnitTestUtils.endCurrentAnimation(prompt);
                            } else {
                                Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                            }



                (actualStateProgress)++;
                if ((actualStateProgress) == 2) {
                    prompt.dismiss();
                } else
                    if ((actualStateProgress) == 3) {
                        Assert.assertFalse(prompt.mView.onTouchEvent(MotionEvent.obtain(0, 0, ACTION_DOWN, 10, 10, 0)));
                    }

            }
        }).show();
    }

    @Test
    public void promptTouchEventFocalNoFinish() {
        expectedStateProgress = 3;
        final MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setAutoFinish(false).setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_FOCAL_PRESSED, state);
                            Assert.assertEquals(STATE_FOCAL_PRESSED, prompt.getState());
                        } else {
                            Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                        }


                (actualStateProgress)++;
            }
        }).show();
        Assert.assertNotNull(prompt);
        Assert.assertFalse(prompt.mView.onTouchEvent(MotionEvent.obtain(0, 0, ACTION_DOWN, 10, 10, 0)));
    }

    @Test
    public void promptTouchEventFocalCaptureEvent() {
        expectedStateProgress = 5;
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setCaptureTouchEventOnFocal(true).setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_FOCAL_PRESSED, state);
                            Assert.assertEquals(STATE_FOCAL_PRESSED, prompt.getState());
                        } else
                            if ((actualStateProgress) == 3) {
                                Assert.assertEquals(STATE_FINISHING, state);
                                Assert.assertEquals(STATE_FINISHING, prompt.getState());
                                UnitTestUtils.endCurrentAnimation(prompt);
                            } else
                                if ((actualStateProgress) == 4) {
                                    Assert.assertEquals(STATE_FINISHED, state);
                                    Assert.assertEquals(STATE_FINISHED, prompt.getState());
                                } else {
                                    Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                                }




                (actualStateProgress)++;
            }
        }).show();
        Assert.assertNotNull(prompt);
        Assert.assertTrue(prompt.mView.onTouchEvent(MotionEvent.obtain(0, 0, ACTION_DOWN, 10, 10, 0)));
    }

    @Test
    public void promptTouchEventFocalNoListener() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setCaptureTouchEventOnFocal(true).show();
        Assert.assertNotNull(prompt);
        Assert.assertTrue(prompt.mView.onTouchEvent(MotionEvent.obtain(0, 0, ACTION_DOWN, 10, 10, 0)));
    }

    @Test
    public void promptTouchEventBackground() {
        expectedStateProgress = 5;
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_NON_FOCAL_PRESSED, state);
                            Assert.assertEquals(STATE_NON_FOCAL_PRESSED, prompt.getState());
                        } else
                            if ((actualStateProgress) == 3) {
                                Assert.assertEquals(STATE_DISMISSING, state);
                                Assert.assertEquals(STATE_DISMISSING, prompt.getState());
                                UnitTestUtils.endCurrentAnimation(prompt);
                            } else
                                if ((actualStateProgress) == 4) {
                                    Assert.assertEquals(STATE_DISMISSED, state);
                                    Assert.assertEquals(STATE_DISMISSED, prompt.getState());
                                } else {
                                    Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                                }




                (actualStateProgress)++;
            }
        }).show();
        Assert.assertNotNull(prompt);
        Assert.assertTrue(prompt.mView.onTouchEvent(MotionEvent.obtain(0, 0, ACTION_DOWN, 60, 60, 0)));
    }

    @Test
    public void promptTouchEventBackgroundDismissing() {
        expectedStateProgress = 4;
        final MaterialTapTargetPrompt prompt = createBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_FINISHING, state);
                            Assert.assertEquals(STATE_FINISHING, prompt.getState());
                        } else
                            if ((actualStateProgress) == 3) {
                                Assert.assertEquals(STATE_FINISHED, state);
                                Assert.assertEquals(STATE_FINISHED, prompt.getState());
                                UnitTestUtils.endCurrentAnimation(prompt);
                            } else {
                                Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                            }



                (actualStateProgress)++;
                if ((actualStateProgress) == 2) {
                    prompt.finish();
                } else
                    if ((actualStateProgress) == 3) {
                        Assert.assertTrue(prompt.mView.onTouchEvent(MotionEvent.obtain(0, 0, ACTION_DOWN, 60, 60, 0)));
                    }

            }
        }).show();
        Assert.assertNotNull(prompt);
    }

    @Test
    public void promptTouchEventBackgroundNoDismiss() {
        expectedStateProgress = 3;
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setAutoDismiss(false).setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_NON_FOCAL_PRESSED, state);
                            Assert.assertEquals(STATE_NON_FOCAL_PRESSED, prompt.getState());
                        } else {
                            Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                        }


                (actualStateProgress)++;
            }
        }).show();
        Assert.assertNotNull(prompt);
        Assert.assertTrue(prompt.mView.onTouchEvent(MotionEvent.obtain(0, 0, ACTION_DOWN, 60, 60, 0)));
    }

    @Test
    public void testPromptBackButtonDismiss() {
        expectedStateProgress = 6;
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setBackButtonDismissEnabled(true).setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_BACK_BUTTON_PRESSED, state);
                            Assert.assertEquals(STATE_BACK_BUTTON_PRESSED, prompt.getState());
                        } else
                            if ((actualStateProgress) == 3) {
                                Assert.assertEquals(STATE_NON_FOCAL_PRESSED, state);
                                Assert.assertEquals(STATE_NON_FOCAL_PRESSED, prompt.getState());
                            } else
                                if ((actualStateProgress) == 4) {
                                    Assert.assertEquals(STATE_DISMISSING, state);
                                    Assert.assertEquals(STATE_DISMISSING, prompt.getState());
                                    UnitTestUtils.endCurrentAnimation(prompt);
                                } else
                                    if ((actualStateProgress) == 5) {
                                        Assert.assertEquals(STATE_DISMISSED, state);
                                        Assert.assertEquals(STATE_DISMISSED, prompt.getState());
                                    } else {
                                        Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                                    }





                (actualStateProgress)++;
            }
        }).show();
        Assert.assertNotNull(prompt);
        final KeyEvent.DispatcherState dispatchState = new KeyEvent.DispatcherState();
        Mockito.doAnswer(new Answer<KeyEvent.DispatcherState>() {
            @Override
            public DispatcherState answer(final InvocationOnMock invocation) {
                return dispatchState;
            }
        }).when(prompt.mView).getKeyDispatcherState();
        Assert.assertTrue(prompt.mView.dispatchKeyEventPreIme(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK)));
        Assert.assertTrue(prompt.mView.dispatchKeyEventPreIme(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK)));
    }

    @Test
    public void testDismissBeforeShow() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.dismiss();
        prompt.show();
    }

    @Test
    public void testShowWhileDismissing() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.show();
        prompt.dismiss();
        prompt.show();
        Assert.assertTrue(prompt.isStarting());
    }

    @Test
    public void testShowWhileShowing() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_REVEALED;
        prompt.show();
        Assert.assertEquals(STATE_REVEALED, prompt.mState);
    }

    @Test
    public void testShowWhileShowingWithPress() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.show();
        prompt.mState = STATE_NON_FOCAL_PRESSED;
        prompt.show();
        Assert.assertEquals(STATE_REVEALING, prompt.mState);
    }

    @Test
    public void testShowForWhileShowing() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_REVEALED;
        prompt.showFor(2000);
        Assert.assertEquals(STATE_REVEALED, prompt.mState);
    }

    @Test
    public void testFinishWhileFinished() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.show();
        prompt.mState = STATE_FINISHED;
        prompt.finish();
        Assert.assertEquals(STATE_FINISHED, prompt.mState);
    }

    @Test
    public void testShowFor() {
        expectedStateProgress = 5;
        createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            final MaterialTapTargetPrompt prompt, final int state) {
                if ((actualStateProgress) == 0) {
                    Assert.assertEquals(STATE_REVEALING, state);
                    Assert.assertEquals(STATE_REVEALING, prompt.getState());
                } else
                    if ((actualStateProgress) == 1) {
                        Assert.assertEquals(STATE_REVEALED, state);
                        Assert.assertEquals(STATE_REVEALED, prompt.getState());
                        UnitTestUtils.runPromptTimeOut(prompt);
                    } else
                        if ((actualStateProgress) == 2) {
                            Assert.assertEquals(STATE_SHOW_FOR_TIMEOUT, state);
                            Assert.assertEquals(STATE_SHOW_FOR_TIMEOUT, prompt.getState());
                        } else
                            if ((actualStateProgress) == 3) {
                                Assert.assertEquals(STATE_DISMISSING, state);
                                Assert.assertEquals(STATE_DISMISSING, prompt.getState());
                                UnitTestUtils.endCurrentAnimation(prompt);
                            } else
                                if ((actualStateProgress) == 4) {
                                    Assert.assertEquals(STATE_DISMISSED, state);
                                    Assert.assertEquals(STATE_DISMISSED, prompt.getState());
                                } else {
                                    Assert.fail(String.format("Incorrect state progress %s for state %s", actualStateProgress, state));
                                }




                (actualStateProgress)++;
            }
        }).showFor(1000);
    }

    @Test
    public void testCancelShowFor() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").showFor(2000);
        Assert.assertNotNull(prompt);
        prompt.cancelShowForTimer();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(STATE_REVEALED, prompt.mState);
    }

    @Test
    public void testStateGetters_NOT_SHOWN() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_NOT_SHOWN;
        Assert.assertFalse(prompt.isStarting());
        Assert.assertTrue(prompt.isComplete());
        Assert.assertFalse(prompt.isDismissed());
        Assert.assertFalse(prompt.isDismissing());
    }

    @Test
    public void testStateGetters_REVEALING() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_REVEALING;
        Assert.assertTrue(prompt.isStarting());
        Assert.assertFalse(prompt.isComplete());
        Assert.assertFalse(prompt.isDismissed());
        Assert.assertFalse(prompt.isDismissing());
    }

    @Test
    public void testStateGetters_REVEALED() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_REVEALED;
        Assert.assertTrue(prompt.isStarting());
        Assert.assertFalse(prompt.isComplete());
        Assert.assertFalse(prompt.isDismissed());
        Assert.assertFalse(prompt.isDismissing());
    }

    @Test
    public void testStateGetters_PRESSED() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_FOCAL_PRESSED;
        Assert.assertFalse(prompt.isStarting());
        Assert.assertFalse(prompt.isComplete());
        Assert.assertFalse(prompt.isDismissed());
        Assert.assertFalse(prompt.isDismissing());
    }

    @Test
    public void testStateGetters_FINISHED() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_FINISHED;
        Assert.assertFalse(prompt.isStarting());
        Assert.assertTrue(prompt.isComplete());
        Assert.assertTrue(prompt.isDismissed());
        Assert.assertFalse(prompt.isDismissing());
    }

    @Test
    public void testStateGetters_DISMISSING() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_DISMISSING;
        Assert.assertFalse(prompt.isStarting());
        Assert.assertTrue(prompt.isComplete());
        Assert.assertFalse(prompt.isDismissed());
        Assert.assertTrue(prompt.isDismissing());
    }

    @Test
    public void testStateGetters_DISMISSED() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").create();
        Assert.assertNotNull(prompt);
        prompt.mState = STATE_DISMISSED;
        Assert.assertFalse(prompt.isStarting());
        Assert.assertTrue(prompt.isComplete());
        Assert.assertTrue(prompt.isDismissed());
        Assert.assertFalse(prompt.isDismissing());
    }

    @Test
    public void testStateGetters_FINISHING() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").show();
        Assert.assertNotNull(prompt);
        prompt.show();
        prompt.mState = STATE_FINISHING;
        Assert.assertFalse(prompt.isStarting());
        Assert.assertTrue(prompt.isComplete());
        Assert.assertFalse(prompt.isDismissed());
        Assert.assertTrue(prompt.isDismissing());
    }

    @Test
    public void testNullFocalPath() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setPromptFocal(new PromptFocal() {
            @Override
            public void setColour(int colour) {
            }

            @NonNull
            @Override
            public RectF getBounds() {
                return new RectF(0, 0, 10, 10);
            }

            @Override
            public void prepare(@NonNull
            PromptOptions options, @NonNull
            View target, int[] promptViewPosition) {
            }

            @Override
            public void prepare(@NonNull
            PromptOptions options, float targetX, float targetY) {
            }

            @Override
            public void updateRipple(float revealModifier, float alphaModifier) {
            }

            @Override
            public void update(@NonNull
            PromptOptions options, float revealModifier, float alphaModifier) {
            }

            @Override
            public void draw(@NonNull
            Canvas canvas) {
            }

            @Override
            public boolean contains(float x, float y) {
                return false;
            }
        }).show();
        Assert.assertNotNull(prompt);
        prompt.show();
        Assert.assertTrue(prompt.isStarting());
    }

    @Test
    public void testIdleAnimationDisabled() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setIdleAnimationEnabled(false).create();
        Assert.assertNotNull(prompt);
        prompt.show();
        Assert.assertNull(prompt.mAnimationFocalBreathing);
    }

    @Test
    @AccessibilityChecks
    public void testPromptAccessibility() {
        MaterialTapTargetPrompt prompt = createMockBuilder(MaterialTapTargetPromptUnitTest.SCREEN_WIDTH, MaterialTapTargetPromptUnitTest.SCREEN_HEIGHT).setTarget(10, 10).setPrimaryText("Primary text").setIdleAnimationEnabled(false).create();
        Assert.assertNotNull(prompt);
        prompt.show();
        AccessibilityUtil.setThrowExceptionForErrors(true);
        AccessibilityUtil.checkView(prompt.mView);
        prompt.mView.sendAccessibilityEvent(TYPE_VIEW_FOCUSED);
    }

    @Test
    @Deprecated
    public void testAnimatorListener() {
        final MaterialTapTargetPrompt.AnimatorListener al = new MaterialTapTargetPrompt.AnimatorListener();
        final Animator animator = new ValueAnimator();
        al.onAnimationStart(animator);
        al.onAnimationRepeat(animator);
        al.onAnimationEnd(animator);
        al.onAnimationCancel(animator);
    }
}


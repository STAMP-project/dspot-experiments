/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class MountStateRemountEventHandlerTest {
    private ComponentContext mContext;

    @Test
    public void testReuseClickListenerOnSameView() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).clickHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentClickListener clickListener = MountState.getComponentClickListener(lithoView);
        assertThat(clickListener).isNotNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).clickHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat((clickListener == (MountState.getComponentClickListener(lithoView)))).isTrue();
    }

    @Test
    public void testReuseLongClickListenerOnSameView() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).longClickHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentLongClickListener longClickListener = MountState.getComponentLongClickListener(lithoView);
        assertThat(longClickListener).isNotNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).longClickHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat((longClickListener == (MountState.getComponentLongClickListener(lithoView)))).isTrue();
    }

    @Test
    public void testReuseFocusChangeListenerListenerOnSameView() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).focusChangeHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        ComponentFocusChangeListener focusChangeListener = MountState.getComponentFocusChangeListener(lithoView);
        assertThat(focusChangeListener).isNotNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).focusChangeHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat((focusChangeListener == (MountState.getComponentFocusChangeListener(lithoView)))).isTrue();
    }

    @Test
    public void testReuseTouchListenerOnSameView() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).touchHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentTouchListener touchListener = MountState.getComponentTouchListener(lithoView);
        assertThat(touchListener).isNotNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).touchHandler(c.newEventHandler(2)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(MountState.getComponentTouchListener(lithoView)).isEqualTo(touchListener);
    }

    @Test
    public void testUnsetClickHandler() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).clickHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(MountState.getComponentClickListener(lithoView)).isNotNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentClickListener listener = MountState.getComponentClickListener(lithoView);
        assertThat(listener).isNotNull();
        assertThat(listener.getEventHandler()).isNull();
    }

    @Test
    public void testUnsetLongClickHandler() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).longClickHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(MountState.getComponentLongClickListener(lithoView)).isNotNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentLongClickListener listener = MountState.getComponentLongClickListener(lithoView);
        assertThat(listener).isNotNull();
        assertThat(listener.getEventHandler()).isNull();
    }

    @Test
    public void testUnsetFocusChangeHandler() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).focusChangeHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(MountState.getComponentFocusChangeListener(lithoView)).isNotNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentFocusChangeListener listener = MountState.getComponentFocusChangeListener(lithoView);
        assertThat(listener).isNotNull();
        assertThat(listener.getEventHandler()).isNull();
    }

    @Test
    public void testUnsetTouchHandler() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).touchHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentTouchListener listener = MountState.getComponentTouchListener(lithoView);
        assertThat(listener.getEventHandler()).isNull();
    }

    @Test
    public void testSetClickHandler() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(MountState.getComponentClickListener(lithoView)).isNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).clickHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentClickListener listener = MountState.getComponentClickListener(lithoView);
        assertThat(listener).isNotNull();
        assertThat(listener.getEventHandler()).isNotNull();
    }

    @Test
    public void testSetLongClickHandler() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(MountState.getComponentLongClickListener(lithoView)).isNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).longClickHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentLongClickListener listener = MountState.getComponentLongClickListener(lithoView);
        assertThat(listener).isNotNull();
        assertThat(listener.getEventHandler()).isNotNull();
    }

    @Test
    public void testSetFocusChangeHandler() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(MountState.getComponentFocusChangeListener(lithoView)).isNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).focusChangeHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentFocusChangeListener listener = MountState.getComponentFocusChangeListener(lithoView);
        assertThat(listener).isNotNull();
        assertThat(listener.getEventHandler()).isNotNull();
    }

    @Test
    public void testSetTouchHandler() {
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        assertThat(MountState.getComponentTouchListener(lithoView)).isNull();
        lithoView.getComponentTree().setRoot(new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).touchHandler(c.newEventHandler(1)).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        });
        final ComponentTouchListener listener = MountState.getComponentTouchListener(lithoView);
        assertThat(listener).isNotNull();
        assertThat(listener.getEventHandler()).isNotNull();
    }
}


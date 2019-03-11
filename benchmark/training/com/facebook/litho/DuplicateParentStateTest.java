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


import LayoutState.CalculateLayoutSource.TEST;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class DuplicateParentStateTest {
    private int mUnspecifiedSizeSpec;

    @Test
    public void testDuplicateParentStateAvoidedIfRedundant() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).duplicateParentState(true).clickHandler(c.newEventHandler(1)).child(Column.create(c).duplicateParentState(false).child(TestDrawableComponent.create(c).duplicateParentState(true))).child(Column.create(c).duplicateParentState(true).child(TestDrawableComponent.create(c).duplicateParentState(true))).child(Column.create(c).clickHandler(c.newEventHandler(2)).child(TestDrawableComponent.create(c).duplicateParentState(true))).child(Column.create(c).clickHandler(c.newEventHandler(3)).child(TestDrawableComponent.create(c).duplicateParentState(false))).child(Column.create(c).clickHandler(c.newEventHandler(3)).backgroundColor(RED).foregroundColor(RED)).child(Column.create(c).backgroundColor(BLUE).foregroundColor(BLUE)).build();
            }
        };
        LayoutState layoutState = LayoutState.calculate(new ComponentContext(application), component, (-1), mUnspecifiedSizeSpec, mUnspecifiedSizeSpec, TEST);
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(12);
        Assert.assertTrue("Clickable root output has duplicate state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(0).getFlags()));
        Assert.assertFalse("Parent doesn't duplicate host state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(1).getFlags()));
        Assert.assertTrue("Parent does duplicate host state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(2).getFlags()));
        Assert.assertTrue("Drawable duplicates clickable parent state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(4).getFlags()));
        Assert.assertFalse("Drawable doesn't duplicate clickable parent state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(6).getFlags()));
        Assert.assertTrue("Background should duplicate clickable node state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(8).getFlags()));
        Assert.assertTrue("Foreground should duplicate clickable node state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(9).getFlags()));
        Assert.assertFalse("Background should duplicate non-clickable node state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(10).getFlags()));
        Assert.assertFalse("Foreground should duplicate non-clickable node state", MountItem.isDuplicateParentState(layoutState.getMountableOutputAt(11).getFlags()));
    }
}


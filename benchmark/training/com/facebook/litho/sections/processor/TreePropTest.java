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
package com.facebook.litho.sections.processor;


import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.treeprop.TreePropNumberType;
import com.facebook.litho.testing.treeprop.TreePropStringType;
import com.facebook.litho.testing.treeprop.TreePropTestParent;
import com.facebook.litho.testing.treeprop.TreePropTestResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import static MeasureSpec.makeMeasureSpec;


/**
 * Tests passing {@link TreeProp}s down a Component tree.
 */
@RunWith(ComponentsTestRunner.class)
public class TreePropTest {
    private ComponentContext mContext;

    /**
     * Tests that a TreeProp is propagated down a Component Tree,
     * is scoped correctly, and can be overwritten.
     */
    @Test
    public void testTreePropsPropagated() {
        final TreePropTestResult propALeaf1 = new TreePropTestResult();
        final TreePropTestResult propBLeaf1 = new TreePropTestResult();
        final TreePropTestResult probBLeaf2 = new TreePropTestResult();
        final TreePropTestResult propAMount = new TreePropTestResult();
        final TreePropNumberType treePropA = new TreePropNumberType(9);
        final TreePropStringType treePropB = new TreePropStringType("propB");
        final TreePropStringType treePropBChanged = new TreePropStringType("propB_changed");
        Component component = TreePropTestParent.create(mContext).propA(treePropA).propB(treePropB).resultPropALeaf1(propALeaf1).resultPropBLeaf1(propBLeaf1).resultPropBLeaf2(probBLeaf2).resultPropAMount(propAMount).build();
        LithoView lithoView = new LithoView(mContext);
        lithoView.setComponent(component);
        lithoView.measure(makeMeasureSpec(1000, MeasureSpec.EXACTLY), makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        assertThat(propALeaf1.mProp).isEqualTo(treePropA);
        // TreePropTestMiddleSpec modifies "propB".
        assertThat(propBLeaf1.mProp).isEqualTo(treePropBChanged);
        // The second LeafSpec does not see the modification to "propB"
        // because its not a descendant of MiddleSpec.
        assertThat(probBLeaf2.mProp).isEqualTo(treePropB);
        assertThat(propAMount.mProp).isEqualTo(treePropA);
    }
}


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


import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class ResolveResTest {
    private ComponentContext mContext;

    @Test
    public void testDefaultDimenWidthRes() {
        Column column = Column.create(mContext).widthRes(test_dimen).build();
        InternalNode node = Layout.create(mContext, column);
        node.calculateLayout();
        int dimen = mContext.getResources().getDimensionPixelSize(test_dimen);
        assertThat(node.getWidth()).isEqualTo(dimen);
    }

    @Test
    public void testDefaultDimenPaddingRes() {
        Column column = Column.create(mContext).paddingRes(LEFT, test_dimen).build();
        InternalNode node = Layout.create(mContext, column);
        node.calculateLayout();
        int dimen = mContext.getResources().getDimensionPixelSize(test_dimen);
        assertThat(node.getWidth()).isEqualTo(dimen);
    }

    @Test
    public void testFloatDimenWidthRes() {
        Column column = Column.create(mContext).widthRes(test_dimen_float).build();
        InternalNode node = Layout.create(mContext, column);
        node.calculateLayout();
        int dimen = mContext.getResources().getDimensionPixelSize(test_dimen_float);
        assertThat(node.getWidth()).isEqualTo(dimen);
    }

    @Test
    public void testFloatDimenPaddingRes() {
        Column column = Column.create(mContext).paddingRes(LEFT, test_dimen_float).build();
        InternalNode node = Layout.create(mContext, column);
        node.calculateLayout();
        int dimen = mContext.getResources().getDimensionPixelSize(test_dimen_float);
        assertThat(node.getPaddingLeft()).isEqualTo(dimen);
    }
}


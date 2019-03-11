/**
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.editor;


import org.junit.Assert;
import org.junit.Test;
import org.terasology.TerasologyTestingEnvironment;
import org.terasology.rendering.nui.editor.layers.PlaceholderScreen;
import org.terasology.rendering.nui.layouts.RowLayout;
import org.terasology.rendering.nui.layouts.RowLayoutHint;
import org.terasology.rendering.nui.layouts.relative.HorizontalInfo;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.layouts.relative.RelativeLayoutHint;
import org.terasology.rendering.nui.layouts.relative.VerticalInfo;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.treeView.JsonTree;


public class ContextMenuUtilsTest extends TerasologyTestingEnvironment {
    private static JsonTree inputTree;

    @Test
    public void testNodeTypes() {
        JsonTree currentNode = ContextMenuUtilsTest.inputTree;
        Assert.assertEquals(PlaceholderScreen.class, getNodeType(currentNode));
        currentNode = currentNode.getChildWithKey("contents");
        Assert.assertEquals(RelativeLayout.class, getNodeType(currentNode));
        currentNode = currentNode.getChildWithKey("contents");
        Assert.assertEquals(UIButton.class, getNodeType(currentNode.getChildAt(0)));
        Assert.assertEquals(RelativeLayoutHint.class, getNodeType(currentNode.getChildAt(0).getChildWithKey("layoutInfo")));
        Assert.assertEquals(VerticalInfo.class, getNodeType(currentNode.getChildAt(0).getChildWithKey("layoutInfo").getChildWithKey("position-top")));
        Assert.assertEquals(HorizontalInfo.class, getNodeType(currentNode.getChildAt(0).getChildWithKey("layoutInfo").getChildWithKey("position-horizontal-center")));
        currentNode = currentNode.getChildAt(1);
        Assert.assertEquals(RowLayout.class, getNodeType(currentNode));
        Assert.assertEquals(RelativeLayoutHint.class, getNodeType(currentNode.getChildWithKey("layoutInfo")));
        currentNode = currentNode.getChildWithKey("contents").getChildAt(0);
        Assert.assertEquals(UILabel.class, getNodeType(currentNode));
        Assert.assertEquals(RowLayoutHint.class, getNodeType(currentNode.getChildWithKey("layoutInfo")));
    }
}


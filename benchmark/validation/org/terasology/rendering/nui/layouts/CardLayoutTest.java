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
package org.terasology.rendering.nui.layouts;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.UIWidget;


public class CardLayoutTest {
    private CardLayout cardLayout;

    private Canvas canvas;

    private UIWidget widget1;

    private UIWidget widget2;

    private UIWidget widget3;

    @Test
    public void testSwitchCard() throws Exception {
        Vector2i result = cardLayout.getPreferredContentSize(canvas, canvas.size());
        // Preferred width should be the longest preferred width among widgets
        Assert.assertEquals(50, result.x);
        // Preferred height should be the tallest preferred height among widgets
        Assert.assertEquals(15, result.y);
        // Switch to widget1
        cardLayout.setDisplayedCard("widget1");
        cardLayout.onDraw(canvas);
        Mockito.verify(canvas).drawWidget(widget1);
        // Switch to widget2
        cardLayout.setDisplayedCard("widget2");
        cardLayout.onDraw(canvas);
        Mockito.verify(canvas).drawWidget(widget2);
        // Switch to widget3
        cardLayout.setDisplayedCard("widget3");
        cardLayout.onDraw(canvas);
        Mockito.verify(canvas).drawWidget(widget3);
    }
}


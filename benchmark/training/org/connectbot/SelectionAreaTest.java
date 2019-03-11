/**
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.connectbot;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.connectbot.bean.SelectionArea;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Kenny Root
 */
@RunWith(AndroidJUnit4.class)
public class SelectionAreaTest {
    private static final int WIDTH = 80;

    private static final int HEIGHT = 24;

    @Test
    public void createSelectionArea() {
        SelectionArea sa = new SelectionArea();
        Assert.assertTrue(((sa.getLeft()) == 0));
        Assert.assertTrue(((sa.getRight()) == 0));
        Assert.assertTrue(((sa.getTop()) == 0));
        Assert.assertTrue(((sa.getBottom()) == 0));
        Assert.assertTrue(sa.isSelectingOrigin());
    }

    @Test
    public void checkMovement() {
        SelectionArea sa = new SelectionArea();
        sa.setBounds(SelectionAreaTest.WIDTH, SelectionAreaTest.HEIGHT);
        sa.incrementColumn();
        // Should be (1,0) to (1,0)
        Assert.assertTrue(((sa.getLeft()) == 1));
        Assert.assertTrue(((sa.getTop()) == 0));
        Assert.assertTrue(((sa.getRight()) == 1));
        Assert.assertTrue(((sa.getBottom()) == 0));
        sa.finishSelectingOrigin();
        Assert.assertFalse(sa.isSelectingOrigin());
        sa.incrementColumn();
        sa.incrementColumn();
        // Should be (1,0) to (3,0)
        Assert.assertTrue(((sa.getLeft()) == 1));
        Assert.assertTrue(((sa.getTop()) == 0));
        Assert.assertTrue(((sa.getRight()) == 3));
        Assert.assertTrue(((sa.getBottom()) == 0));
    }

    @Test
    public void boundsAreCorrect() {
        SelectionArea sa = new SelectionArea();
        sa.setBounds(SelectionAreaTest.WIDTH, SelectionAreaTest.HEIGHT);
        for (int i = 0; i <= (SelectionAreaTest.WIDTH); i++)
            sa.decrementColumn();

        Assert.assertTrue(("Left bound should be 0, but instead is " + (sa.getLeft())), ((sa.getLeft()) == 0));
        for (int i = 0; i <= (SelectionAreaTest.HEIGHT); i++)
            sa.decrementRow();

        Assert.assertTrue(("Top bound should be 0, but instead is " + (sa.getLeft())), ((sa.getTop()) == 0));
        sa.finishSelectingOrigin();
        for (int i = 0; i <= ((SelectionAreaTest.WIDTH) * 2); i++)
            sa.incrementColumn();

        Assert.assertTrue(("Left bound should be 0, but instead is " + (sa.getLeft())), ((sa.getLeft()) == 0));
        Assert.assertTrue(((("Right bound should be " + ((SelectionAreaTest.WIDTH) - 1)) + ", but instead is ") + (sa.getRight())), ((sa.getRight()) == ((SelectionAreaTest.WIDTH) - 1)));
        for (int i = 0; i <= ((SelectionAreaTest.HEIGHT) * 2); i++)
            sa.incrementRow();

        Assert.assertTrue(((("Bottom bound should be " + ((SelectionAreaTest.HEIGHT) - 1)) + ", but instead is ") + (sa.getBottom())), ((sa.getBottom()) == ((SelectionAreaTest.HEIGHT) - 1)));
        Assert.assertTrue(("Top bound should be 0, but instead is " + (sa.getTop())), ((sa.getTop()) == 0));
    }

    @Test
    public void setThenMove() {
        SelectionArea sa = new SelectionArea();
        sa.setBounds(SelectionAreaTest.WIDTH, SelectionAreaTest.HEIGHT);
        int targetColumn = (SelectionAreaTest.WIDTH) / 2;
        int targetRow = (SelectionAreaTest.HEIGHT) / 2;
        sa.setColumn(targetColumn);
        sa.setRow(targetRow);
        sa.incrementRow();
        Assert.assertTrue(((("Row should be " + (targetRow + 1)) + ", but instead is ") + (sa.getTop())), ((sa.getTop()) == (targetRow + 1)));
        sa.decrementColumn();
        Assert.assertTrue(((("Column shold be " + (targetColumn - 1)) + ", but instead is ") + (sa.getLeft())), ((sa.getLeft()) == (targetColumn - 1)));
        sa.finishSelectingOrigin();
        sa.setRow(0);
        sa.setColumn(0);
        sa.incrementRow();
        sa.decrementColumn();
        Assert.assertTrue(("Top row should be 1, but instead is " + (sa.getTop())), ((sa.getTop()) == 1));
        Assert.assertTrue(("Left column shold be 0, but instead is " + (sa.getLeft())), ((sa.getLeft()) == 0));
        Assert.assertTrue(((("Bottom row should be " + (targetRow + 1)) + ", but instead is ") + (sa.getBottom())), ((sa.getBottom()) == (targetRow + 1)));
        Assert.assertTrue(((("Right column shold be " + (targetColumn - 1)) + ", but instead is ") + (sa.getRight())), ((sa.getRight()) == (targetColumn - 1)));
    }
}


/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.xul.swt.tab;


import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Event;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TabSetTest {
    /**
     * PDI-14411 NPE on Ctrl-W
     */
    @Test
    public void testCloseFirstTabOfTwo() {
        final CTabFolder cTabFolder = Mockito.mock(CTabFolder.class);
        final TabSet tabSet = createTabSet(cTabFolder);
        final CTabItem cTabItem1 = Mockito.mock(CTabItem.class);
        TabItem firstItem = createItem(tabSet, "first", "1st", cTabItem1);
        final CTabItem cTabItem2 = Mockito.mock(CTabItem.class);
        TabItem secondItem = createItem(tabSet, "second", "2nd", cTabItem2);
        Assert.assertEquals(0, tabSet.indexOf(firstItem));
        Assert.assertEquals(1, tabSet.indexOf(secondItem));
        tabSet.setSelected(firstItem);
        Assert.assertEquals(0, tabSet.getSelectedIndex());
        wireDisposalSelection(cTabFolder, tabSet, cTabItem1, cTabItem2);
        firstItem.dispose();
        Assert.assertEquals((-1), tabSet.indexOf(firstItem));
        Assert.assertNotNull("selected is null", tabSet.getSelected());
    }

    /**
     * Ctrl-W on first and second in succession would close first and third
     */
    @Test
    public void testCloseFirstTabOfThree() {
        final CTabFolder cTabFolder = Mockito.mock(CTabFolder.class);
        final TabSet tabSet = createTabSet(cTabFolder);
        final CTabItem cTabItem1 = Mockito.mock(CTabItem.class);
        TabItem firstItem = createItem(tabSet, "first", "1st", cTabItem1);
        final CTabItem cTabItem2 = Mockito.mock(CTabItem.class);
        TabItem secondItem = createItem(tabSet, "second", "2nd", cTabItem2);
        TabItem thirdItem = createItem(tabSet, "third", "3rd", Mockito.mock(CTabItem.class));
        Assert.assertEquals(0, tabSet.indexOf(firstItem));
        Assert.assertEquals(1, tabSet.indexOf(secondItem));
        Assert.assertEquals(2, tabSet.indexOf(thirdItem));
        wireDisposalSelection(cTabFolder, tabSet, cTabItem1, cTabItem2);
        tabSet.setSelected(firstItem);
        Assert.assertEquals(0, tabSet.getSelectedIndex());
        firstItem.dispose();
        Assert.assertEquals("should select second", secondItem, tabSet.getSelected());
    }

    /**
     * PDI-16196 index out of bounds after closing tabs with the same name
     */
    @Test
    public void testDuplicateNameCloseTab() {
        final CTabFolder cTabFolder = Mockito.mock(CTabFolder.class);
        final TabSet tabSet = createTabSet(cTabFolder);
        final CTabItem cTabItem1 = Mockito.mock(CTabItem.class);
        TabItem firstItem = createItem(tabSet, "equalName", "equals", cTabItem1);
        final CTabItem cTabItem2 = Mockito.mock(CTabItem.class);
        TabItem secondItem = createItem(tabSet, "equalName", "equals", cTabItem2);
        final CTabItem cTabItem3 = Mockito.mock(CTabItem.class);
        TabItem thirdItem = createItem(tabSet, "different", "different", cTabItem3);
        Assert.assertEquals(0, tabSet.indexOf(firstItem));
        Assert.assertEquals(1, tabSet.indexOf(secondItem));
        wireDisposalSelection(cTabFolder, tabSet, cTabItem1, cTabItem3);
        wireDisposalSelection(cTabFolder, tabSet, cTabItem2, cTabItem3);
        firstItem.dispose();
        secondItem.dispose();
        tabSet.setSelected(firstItem);
        Assert.assertEquals((-1), tabSet.getSelectedIndex());
        Event evt = new Event();
        evt.item = cTabItem1;
        evt.widget = cTabFolder;
        tabSet.widgetSelected(new org.eclipse.swt.events.SelectionEvent(evt));
    }

    @Test
    public void testRegularCloseTab() {
        final CTabFolder cTabFolder = Mockito.mock(CTabFolder.class);
        final TabSet tabSet = createTabSet(cTabFolder);
        final CTabItem cTabItem1 = Mockito.mock(CTabItem.class);
        TabItem firstItem = createItem(tabSet, "first", "1st", cTabItem1);
        final CTabItem cTabItem2 = Mockito.mock(CTabItem.class);
        TabItem secondItem = createItem(tabSet, "second", "2nd", cTabItem2);
        TabItem thirdItem = createItem(tabSet, "third", "3rd", Mockito.mock(CTabItem.class));
        Assert.assertEquals(0, tabSet.indexOf(firstItem));
        Assert.assertEquals(1, tabSet.indexOf(secondItem));
        Assert.assertEquals(2, tabSet.indexOf(thirdItem));
        // after close the previous tab is selected if available
        wireDisposalSelection(cTabFolder, tabSet, cTabItem2, cTabItem1);
        tabSet.setSelected(secondItem);
        Assert.assertEquals(1, tabSet.getSelectedIndex());
        secondItem.dispose();
        Assert.assertEquals("should select first", firstItem, tabSet.getSelected());
    }
}


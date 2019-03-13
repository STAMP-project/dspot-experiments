package com.vaadin.tests.components.nativeselect;


import com.vaadin.testbench.elements.AbstractComponentElement.ReadOnlyException;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class NativeSelectTest extends MultiBrowserTest {
    @Test
    public void initialLoad_containsCorrectItems() {
        assertItems(20);
    }

    @Test
    public void initialItems_reduceItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "5");
        assertItems(5);
    }

    @Test
    public void initialItems_increaseItemCount_containsCorrectItems() {
        selectMenuPath("Component", "Data provider", "Items", "100");
        assertItems(100);
    }

    @Test
    public void selectItemProgrammatically_correctItemSelected() {
        selectMenuPath("Component", "Selection", "Select", "Item 2");
        Assert.assertEquals("Selected item", "Item 2", getSelect().getValue());
    }

    @Test
    public void selectionListenerAdded_selectItem_listenerInvoked() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        getSelect().selectByText("Item 5");
        Assert.assertEquals("1. Selected: Item 5", getLogRow(0));
    }

    @Test
    public void selectionListenerRemoved_selectItem_listenerNotInvoked() {
        selectMenuPath("Component", "Listeners", "Selection listener");
        selectMenuPath("Component", "Listeners", "Selection listener");
        getSelect().selectByText("Item 5");
        Assert.assertEquals("1. Command: /Selection listener(false)", getLogRow(0));
    }

    @Test(expected = ReadOnlyException.class)
    public void setReadOnly_trySelectItem_throws() {
        selectMenuPath("Component", "State", "Readonly");
        getSelect().selectByText("Item 5");
    }
}


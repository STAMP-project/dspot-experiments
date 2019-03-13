package com.vaadin.tests.data.selection;


import com.vaadin.data.provider.StrBean;
import com.vaadin.ui.AbstractMultiSelect;
import org.junit.Assert;
import org.junit.Test;


public class StaleMultiSelectionTest extends AbstractStaleSelectionTest<AbstractMultiSelect<StrBean>> {
    @Test
    public void testSelectionUpdateOnRefreshItem() {
        StrBean toReplace = data.get(0);
        assertNotStale(toReplace);
        select.select(toReplace);
        StrBean replacement = new StrBean("Replacement bean", toReplace.getId(), (-1));
        dataProvider.refreshItem(replacement);
        assertIsStale(toReplace);
        select.getSelectedItems().forEach(( item) -> assertFalse("Selection should not contain stale values", dataProvider.isStale(item)));
        Object oldId = dataProvider.getId(toReplace);
        Assert.assertTrue("Selection did not contain an item with matching Id.", select.getSelectedItems().stream().map(dataProvider::getId).anyMatch(oldId::equals));
        Assert.assertTrue("Stale element is not considered selected.", select.isSelected(toReplace));
    }
}


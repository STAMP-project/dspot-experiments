package com.vaadin.tests.data.selection;


import com.vaadin.data.provider.StrBean;
import com.vaadin.ui.AbstractSingleSelect;
import org.junit.Assert;
import org.junit.Test;


public class StaleSingleSelectionTest extends AbstractStaleSelectionTest<AbstractSingleSelect<StrBean>> {
    @Test
    public void testGridSingleSelectionUpdateOnRefreshItem() {
        StrBean toReplace = data.get(0);
        assertNotStale(toReplace);
        select.setValue(toReplace);
        StrBean replacement = new StrBean("Replacement bean", toReplace.getId(), (-1));
        dataProvider.refreshItem(replacement);
        assertIsStale(toReplace);
        Assert.assertFalse("Selection should not contain stale values", dataProvider.isStale(select.getValue()));
        Assert.assertEquals("Selected item id did not match original.", toReplace.getId(), dataProvider.getId(select.getValue()));
    }
}


package com.vaadin.tests.data.selection;


import SelectionMode.MULTI;
import SelectionMode.SINGLE;
import com.vaadin.data.provider.ReplaceListDataProvider;
import com.vaadin.data.provider.StrBean;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridSelectionModel;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GridStaleElementTest {
    private Grid<StrBean> grid = new Grid();

    private ReplaceListDataProvider dataProvider;

    private List<StrBean> data = StrBean.generateRandomBeans(2);

    @Test
    public void testGridMultiSelectionUpdateOnRefreshItem() {
        StrBean toReplace = data.get(0);
        assertNotStale(toReplace);
        GridSelectionModel<StrBean> model = grid.setSelectionMode(MULTI);
        model.select(toReplace);
        StrBean replacement = new StrBean("Replacement bean", toReplace.getId(), (-1));
        dataProvider.refreshItem(replacement);
        assertStale(toReplace);
        model.getSelectedItems().forEach(( item) -> assertFalse("Selection should not contain stale values", dataProvider.isStale(item)));
        Object oldId = dataProvider.getId(toReplace);
        Assert.assertTrue("Selection did not contain an item with matching Id.", model.getSelectedItems().stream().map(dataProvider::getId).anyMatch(oldId::equals));
        Assert.assertTrue("Stale element is not considered selected.", model.isSelected(toReplace));
    }

    @Test
    public void testGridSingleSelectionUpdateOnRefreshItem() {
        StrBean toReplace = data.get(0);
        assertNotStale(toReplace);
        GridSelectionModel<StrBean> model = grid.setSelectionMode(SINGLE);
        model.select(toReplace);
        StrBean replacement = new StrBean("Replacement bean", toReplace.getId(), (-1));
        dataProvider.refreshItem(replacement);
        assertStale(toReplace);
        model.getSelectedItems().forEach(( i) -> assertFalse("Selection should not contain stale values", dataProvider.isStale(i)));
        Assert.assertTrue("Selection did not contain an item with matching Id.", model.getSelectedItems().stream().map(dataProvider::getId).filter(( i) -> dataProvider.getId(toReplace).equals(i)).findFirst().isPresent());
        Assert.assertTrue("Stale element is not considered selected.", model.isSelected(toReplace));
    }
}


package com.vaadin.tests.components.grid;


import com.vaadin.data.provider.DataProvider;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to validate clean detaching in Grid with ComponentRenderer.
 */
public class GridComponentRendererTest {
    private static final Person PERSON = Person.createTestPerson1();

    private Grid<Person> grid;

    private List<Person> backend;

    private DataProvider<Person, ?> dataProvider;

    private Label testComponent;

    private Label oldComponent;

    @Test
    public void testComponentChangeOnRefresh() {
        generateDataForClient(true);
        dataProvider.refreshItem(GridComponentRendererTest.PERSON);
        generateDataForClient(false);
        Assert.assertNotNull("Old component should exist.", oldComponent);
    }

    @Test
    public void testComponentChangeOnSelection() {
        generateDataForClient(true);
        grid.select(GridComponentRendererTest.PERSON);
        generateDataForClient(false);
        Assert.assertNotNull("Old component should exist.", oldComponent);
    }

    @Test
    public void testComponentChangeOnDataProviderChange() {
        generateDataForClient(true);
        grid.setItems(GridComponentRendererTest.PERSON);
        Assert.assertEquals("Test component was not detached on DataProvider change.", null, testComponent.getParent());
    }
}


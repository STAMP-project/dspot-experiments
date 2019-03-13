package com.vaadin.data.provider;


import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.NumberRenderer;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.Test;


public class SortOrderBuildersTest {
    @Test
    public void gridSortOrderBuilder() {
        Grid<String> grid = new Grid();
        Column<String, String> col1 = grid.addColumn(( string) -> string);
        Column<String, Number> col2 = grid.addColumn(( string) -> 1, new NumberRenderer());
        Column<String, ?> col3 = grid.addColumn(( string) -> LocalDate.of(0, 0, 0));
        // construct with asc
        verifySortOrders(Arrays.asList(new GridSortOrder(col1, SortDirection.ASCENDING), new GridSortOrder(col2, SortDirection.DESCENDING), new GridSortOrder(col3, SortDirection.ASCENDING)), GridSortOrder.asc(col1).thenDesc(col2).thenAsc(col3).build());
        // construct with desc
        verifySortOrders(Arrays.asList(new GridSortOrder(col1, SortDirection.DESCENDING), new GridSortOrder(col2, SortDirection.DESCENDING), new GridSortOrder(col3, SortDirection.ASCENDING)), GridSortOrder.desc(col1).thenDesc(col2).thenAsc(col3).build());
    }

    @Test
    public void querySortOrderBuilder() {
        verifySortOrders(Arrays.asList(new QuerySortOrder("a", SortDirection.ASCENDING), new QuerySortOrder("b", SortDirection.DESCENDING), new QuerySortOrder("c", SortDirection.ASCENDING)), QuerySortOrder.asc("a").thenDesc("b").thenAsc("c").build());
        verifySortOrders(Arrays.asList(new QuerySortOrder("a", SortDirection.DESCENDING), new QuerySortOrder("b", SortDirection.DESCENDING), new QuerySortOrder("c", SortDirection.ASCENDING)), QuerySortOrder.desc("a").thenDesc("b").thenAsc("c").build());
    }
}


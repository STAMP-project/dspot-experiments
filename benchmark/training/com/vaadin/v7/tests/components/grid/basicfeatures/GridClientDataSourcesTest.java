package com.vaadin.v7.tests.components.grid.basicfeatures;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


@TestCategory("grid")
public class GridClientDataSourcesTest extends MultiBrowserTest {
    @Test
    public void normalRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        assertCellPresent("cell 0 #0");
        scrollToBottom();
        assertCellPresent("cell 199 #0");
        assertCellNotPresent("cell 200 #0");
    }

    @Test
    public void growOnRequestRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        selectMenuPath("DataSources", "RESTish", "Next request +10");
        scrollToBottom();
        /* second scroll needed because of scrollsize change after scrolling */
        scrollToBottom();
        assertCellPresent("cell 209 #1");
        assertCellNotPresent("cell 210 #1");
    }

    @Test
    public void shrinkOnRequestRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        scrollToBottom();
        selectMenuPath("DataSources", "RESTish", "Next request -10");
        scrollToTop();
        assertCellPresent("cell 0 #1");
    }

    @Test
    public void pushChangeRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        selectMenuPath("DataSources", "RESTish", "Push data change");
        assertCellPresent("cell 0 #1");
        assertCellNotPresent("cell 0 #0");
    }

    @Test
    public void growOnPushRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        selectMenuPath("DataSources", "RESTish", "Push data change +10");
        assertCellPresent("cell 0 #1");
        assertCellNotPresent("cell 0 #0");
        scrollToBottom();
        assertCellPresent("cell 209 #1");
    }

    @Test
    public void shrinkOnPushRestishDatasource() throws Exception {
        selectMenuPath("DataSources", "RESTish", "Use");
        scrollToBottom();
        selectMenuPath("DataSources", "RESTish", "Push data change -10");
        assertCellPresent("cell 189 #1");
        assertCellNotPresent("cell 189 #0");
        assertCellNotPresent("cell 199 #1");
        assertCellNotPresent("cell 199 #0");
    }
}


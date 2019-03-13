package com.vaadin.v7.tests.server.component.treetable;


import RowHeaderMode.HIDDEN;
import RowHeaderMode.ICON_ONLY;
import com.vaadin.v7.shared.ui.treetable.TreeTableState;
import com.vaadin.v7.ui.Table.RowHeaderMode;
import com.vaadin.v7.ui.TreeTable;
import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TreeTable}
 *
 * @author Vaadin Ltd
 */
public class TreeTableTest {
    @Test
    public void rowHeadersAreEnabled_iconRowHeaderMode_rowHeadersAreDisabled() {
        TreeTableTest.TestTreeTable tree = new TreeTableTest.TestTreeTable();
        tree.setRowHeaderMode(ICON_ONLY);
        Assert.assertFalse("Row headers are enabled for Icon header mode", tree.rowHeadersAreEnabled());
    }

    @Test
    public void rowHeadersAreEnabled_hiddenRowHeaderMode_rowHeadersAreDisabled() {
        TreeTableTest.TestTreeTable tree = new TreeTableTest.TestTreeTable();
        tree.setRowHeaderMode(HIDDEN);
        Assert.assertFalse("Row headers are enabled for Hidden header mode", tree.rowHeadersAreEnabled());
    }

    @Test
    public void rowHeadersAreEnabled_otherRowHeaderModes_rowHeadersAreEnabled() {
        TreeTableTest.TestTreeTable tree = new TreeTableTest.TestTreeTable();
        EnumSet<RowHeaderMode> modes = EnumSet.allOf(RowHeaderMode.class);
        modes.remove(ICON_ONLY);
        modes.remove(HIDDEN);
        for (RowHeaderMode mode : modes) {
            tree.setRowHeaderMode(mode);
            Assert.assertTrue((("Row headers are disabled for " + mode) + " header mode"), tree.rowHeadersAreEnabled());
        }
    }

    @Test
    public void getState_treeTableHasCustomState() {
        TreeTableTest.TestTreeTable table = new TreeTableTest.TestTreeTable();
        TreeTableState state = table.getState();
        Assert.assertEquals("Unexpected state class", TreeTableState.class, state.getClass());
    }

    @Test
    public void getPrimaryStyleName_treeTableHasCustomPrimaryStyleName() {
        TreeTable table = new TreeTable();
        TreeTableState state = new TreeTableState();
        Assert.assertEquals("Unexpected primary style name", state.primaryStyleName, table.getPrimaryStyleName());
    }

    @Test
    public void treeTableStateHasCustomPrimaryStyleName() {
        TreeTableState state = new TreeTableState();
        Assert.assertEquals("Unexpected primary style name", "v-table", state.primaryStyleName);
    }

    private static class TestTreeTable extends TreeTable {
        @Override
        protected boolean rowHeadersAreEnabled() {
            return super.rowHeadersAreEnabled();
        }

        @Override
        public TreeTableState getState() {
            return super.getState();
        }
    }
}


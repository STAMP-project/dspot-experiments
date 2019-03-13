package eu.davidea.flexibleadapter;


import androidx.annotation.NonNull;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.samples.flexibleadapter.items.HeaderItem;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author Davide
 * @since 24/05/2017
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class ClearItemsTest {
    FlexibleAdapter<AbstractFlexibleItem> mAdapter;

    List<AbstractFlexibleItem> mItems;

    AbstractFlexibleItem scrollableHeader;

    AbstractFlexibleItem scrollableFooter;

    @Test
    public void testClearAllBut_Empty() {
        mAdapter.clearAllBut();
        Assert.assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void testClearAdapter_NotEmpty() {
        FlexibleAdapter<AbstractFlexibleItem> adapter = new FlexibleAdapter<AbstractFlexibleItem>(mItems) {
            @Override
            public IHeader getHeaderOf(@NonNull
            AbstractFlexibleItem item) {
                Assert.assertNotNull(item);
                return super.getHeaderOf(item);
            }
        };
        Assert.assertEquals(mItems.size(), adapter.getItemCount());
        adapter.clear();
        Assert.assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testClearAdapter_Empty() {
        FlexibleAdapter<AbstractFlexibleItem> adapter = new FlexibleAdapter<AbstractFlexibleItem>(null) {
            @Override
            public IHeader getHeaderOf(@NonNull
            AbstractFlexibleItem item) {
                Assert.assertNotNull(item);
                return super.getHeaderOf(item);
            }
        };
        adapter.updateDataSet(new ArrayList(), false);
        adapter.clear();
        Assert.assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testClearAllBut_OnlyScrollableItems() {
        // Add scrollable items
        mAdapter.addScrollableHeader(scrollableHeader);
        mAdapter.addScrollableFooter(scrollableFooter);
        Assert.assertEquals(37, mAdapter.getItemCount());
        Assert.assertEquals(35, mAdapter.getMainItemCount());
        // Clear all, retains only scrollable items
        mAdapter.clearAllBut();
        Assert.assertEquals(2, mAdapter.getItemCount());
        Assert.assertEquals(0, mAdapter.getMainItemCount());
        Assert.assertEquals(scrollableHeader, mAdapter.getItem(0));
        Assert.assertEquals(scrollableFooter, mAdapter.getItem(1));
    }

    @Test
    public void testClearAllBut_WithoutScrollableItems() {
        // Grab the LayoutRes to retain
        AbstractFlexibleItem headerItem = mAdapter.getItem(0);
        Assert.assertEquals(35, mAdapter.getItemCount());
        Assert.assertEquals(35, mAdapter.getMainItemCount());
        // Clear all simple items, retains header items
        mAdapter.clearAllBut(headerItem.getItemViewType());
        Assert.assertEquals(5, mAdapter.getItemCount());
        Assert.assertEquals(5, mAdapter.getMainItemCount());
        Assert.assertTrue(((mAdapter.getItem(0)) instanceof HeaderItem));
        Assert.assertTrue(((mAdapter.getItem(((mAdapter.getItemCount()) - 1))) instanceof HeaderItem));
    }

    @Test
    public void testClearAllBut_WithScrollableItems() {
        // Grab the LayoutRes to retain
        AbstractFlexibleItem headerItem = mAdapter.getItem(0);
        // Add scrollable items
        mAdapter.addScrollableHeader(scrollableHeader);
        mAdapter.addScrollableFooter(scrollableFooter);
        Assert.assertEquals(37, mAdapter.getItemCount());
        Assert.assertEquals(35, mAdapter.getMainItemCount());
        // Clear all simple items, retains header items (...and scrollable items)
        mAdapter.clearAllBut(headerItem.getItemViewType());
        Assert.assertEquals(7, mAdapter.getItemCount());
        Assert.assertEquals(5, mAdapter.getMainItemCount());
        Assert.assertEquals(scrollableHeader, mAdapter.getItem(0));
        Assert.assertEquals(scrollableFooter, mAdapter.getItem(((mAdapter.getItemCount()) - 1)));
    }

    @Test
    public void testRemoveItemsOfType() {
        // Grab the LayoutRes to delete
        AbstractFlexibleItem simpleItem = mAdapter.getItem(1);
        Assert.assertEquals(35, mAdapter.getItemCount());
        // Delete all items of type simple
        mAdapter.removeItemsOfType(simpleItem.getItemViewType());
        Assert.assertEquals(5, mAdapter.getItemCount());
    }
}


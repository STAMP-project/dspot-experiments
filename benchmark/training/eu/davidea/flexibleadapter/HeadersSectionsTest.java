package eu.davidea.flexibleadapter;


import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IHeader;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author Davide Steduto
 * @since 23/06/2016
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class HeadersSectionsTest {
    private static final int ITEM_SIZE = 30;

    private static final int HEADER_SIZE = 5;

    FlexibleAdapter<AbstractFlexibleItem> mAdapter;

    List<AbstractFlexibleItem> mItems;

    @Test
    public void testItemCount() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        Assert.assertEquals(mItems.size(), mAdapter.getItemCount());
    }

    @Test
    public void testSetDisplayHeadersAtStartUp() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        Assert.assertEquals(0, mAdapter.getHeaderItems().size());
        // 1st call to display headers
        mAdapter.setDisplayHeadersAtStartUp(true);
        System.out.println(("1st call Headers = " + (mAdapter.getHeaderItems().size())));
        Assert.assertEquals(5, mAdapter.getHeaderItems().size());
        // 2nd call to display headers
        mAdapter.setDisplayHeadersAtStartUp(true);
        System.out.println(("2nd call Headers = " + (mAdapter.getHeaderItems().size())));
        Assert.assertEquals(HeadersSectionsTest.HEADER_SIZE, mAdapter.getHeaderItems().size());
        // 3rd call to display headers
        mAdapter.showAllHeaders();
        System.out.println(("3rd call Headers = " + (mAdapter.getHeaderItems().size())));
        Assert.assertEquals(HeadersSectionsTest.HEADER_SIZE, mAdapter.getHeaderItems().size());
    }

    @Test
    public void testEmptyAdapterAddItemsWithHeader() throws Exception {
        mAdapter = new FlexibleAdapter(null);
        mAdapter.addItems(0, mItems);
        Assert.assertEquals(mItems.size(), mAdapter.getItemCount());
        mAdapter.setDisplayHeadersAtStartUp(true);
        Assert.assertEquals(HeadersSectionsTest.HEADER_SIZE, mAdapter.getHeaderItems().size());
    }

    @Test
    public void testIsHeader() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        mAdapter.setDisplayHeadersAtStartUp(true);
        AbstractFlexibleItem item = mAdapter.getItem(0);
        Assert.assertNotNull(item);
        Assert.assertTrue(mAdapter.isHeader(item));
    }

    @Test
    public void testHasHeader() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        mAdapter.setDisplayHeadersAtStartUp(true);
        AbstractFlexibleItem item = mAdapter.getItem(1);
        Assert.assertNotNull(item);
        Assert.assertTrue(mAdapter.hasHeader(item));
    }

    @Test
    public void testHasSameHeader() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        mAdapter.setDisplayHeadersAtStartUp(true);
        AbstractFlexibleItem item1 = mAdapter.getItem(1);
        IHeader header = mAdapter.getHeaderOf(item1);
        Assert.assertNotNull(header);
        AbstractFlexibleItem item2 = mAdapter.getItem(3);
        Assert.assertNotNull(item2);
        Assert.assertTrue(mAdapter.hasSameHeader(item2, header));
    }

    @Test
    public void testGetHeaderOf() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        mAdapter.setDisplayHeadersAtStartUp(true);
        IHeader header = mAdapter.getHeaderOf(mItems.get(1));
        Assert.assertNotNull(header);
    }

    @Test
    public void testShowAndHideAllHeaders() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        mAdapter.showAllHeaders();
        Assert.assertEquals(HeadersSectionsTest.HEADER_SIZE, mAdapter.getHeaderItems().size());
        mAdapter.hideAllHeaders();
        Assert.assertEquals(0, mAdapter.getHeaderItems().size());
    }

    @Test
    public void testGetSectionItemPositions() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        mAdapter.setDisplayHeadersAtStartUp(true);
        IHeader header = mAdapter.getHeaderOf(mItems.get(1));
        List<Integer> positions = mAdapter.getSectionItemPositions(header);
        Assert.assertNotNull(positions);
        Assert.assertTrue(((positions.size()) > 0));
        Integer count = 1;
        for (Integer position : positions) {
            Assert.assertEquals((count++), position);
        }
    }

    @Test
    public void testSameTypePositionOf() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        mAdapter.setDisplayHeadersAtStartUp(true);
        // Checking item
        AbstractFlexibleItem item = mItems.get(((mItems.size()) - 1));
        int position = mAdapter.getSameTypePositionOf(item);
        Assert.assertEquals(mItems.indexOf(item), position);
        // Checking section
        IHeader header = mAdapter.getHeaderOf(mItems.get(position));
        int headerPosition = (mAdapter.getSameTypePositionOf(header)) + 1;
        Assert.assertEquals(HeadersSectionsTest.HEADER_SIZE, headerPosition);
    }

    @Test
    public void testSubPositionOf() throws Exception {
        mAdapter = new FlexibleAdapter(mItems);
        mAdapter.setDisplayHeadersAtStartUp(true);
        AbstractFlexibleItem item = mItems.get(15);
        int subPosition = mAdapter.getSubPositionOf(item);
        Assert.assertEquals(3, subPosition);
    }
}


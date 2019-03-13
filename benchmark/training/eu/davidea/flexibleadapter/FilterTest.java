package eu.davidea.flexibleadapter;


import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.samples.flexibleadapter.services.DatabaseService;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author Davide Steduto
 * @since 18/10/2016
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class FilterTest {
    private FlexibleAdapter<AbstractFlexibleItem> mAdapter;

    private List<AbstractFlexibleItem> mItems;

    @Test
    public void testFilter() throws Throwable {
        CountDownLatch signal = new CountDownLatch(1);
        createSignalAdapter(signal);
        mAdapter.setFilter("1");// No delay

        mAdapter.filterItems(DatabaseService.getInstance().getDatabaseList());
        signal.await(100L, TimeUnit.MILLISECONDS);
    }
}


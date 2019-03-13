package eu.davidea.flexibleadapter.livedata;


import FlexibleItemProvider.Factory;
import androidx.annotation.NonNull;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.livedata.items.HeaderHolder;
import eu.davidea.flexibleadapter.livedata.items.ItemHolder;
import eu.davidea.flexibleadapter.livedata.models.HeaderModel;
import eu.davidea.flexibleadapter.livedata.models.ItemModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/* Test Case Definitions

JUnit 4                                  JUnit 5
-------------------------------------    ---------------------------
@org.junit.Test                       -> @org.junit.jupiter.api.Test
@Ignore                               -> @Disabled
@Category(Class)                      -> @Tag(String)
@Parameters + @RunWith(Parameterized) -> @ParameterizedTest + <Source>
Assert.assertXXX                      -> Assertions.assertXXX
n/a                                   -> @DisplayName
n/a                                   -> @Nested
n/a                                   -> @TestFactory
 */
/**
 *
 *
 * @author Davide Steduto
 * @since 07/10/2017
 */
public class FlexibleFactoryTest {
    @Test
    public void createHeaderHolder() {
        HeaderModel header = new HeaderModel("H1");
        HeaderHolder headerHolder = FlexibleFactory.create(HeaderHolder.class, header);
        Assertions.assertNotNull(headerHolder);
        Assertions.assertEquals(header, headerHolder.getModel());
    }

    @Test
    public void createItemHolder() {
        HeaderModel header = new HeaderModel("H1");
        HeaderHolder headerHolder = FlexibleFactory.create(HeaderHolder.class, header);
        ItemModel item = new ItemModel("I1", "H2");
        ItemHolder itemHolder = FlexibleFactory.create(ItemHolder.class, item, headerHolder);
        Assertions.assertNotNull(itemHolder);
        Assertions.assertEquals(item, itemHolder.getModel());
        Assertions.assertEquals(headerHolder, getHeader());
        Assertions.assertEquals(header, getHeader().getModel());
    }

    @Test
    public void createWithMismatchingParam() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            FlexibleFactory.create(HeaderHolder.class, new Object());
        });
    }

    @Test
    public void createSectionableItems() throws Exception {
        Map<String, HeaderModel> headers = new HashMap<>(2);
        headers.put("H1", new HeaderModel("H1"));
        headers.put("H2", new HeaderModel("H2"));
        List<ItemModel> items = new ArrayList<>(2);
        ItemModel item1 = new ItemModel("I1", "H1");
        ItemModel item2 = new ItemModel("I2", "H2");
        items.add(item1);
        items.add(item2);
        FlexibleFactoryTest.SectionableFactory sectionableFactory = new FlexibleFactoryTest.SectionableFactory(headers);
        List<AbstractFlexibleItem> adapterItems = FlexibleItemProvider.with(sectionableFactory).from(items);
        Assertions.assertTrue(((adapterItems.get(0)) instanceof ItemHolder));
        Assertions.assertEquals(items.size(), adapterItems.size());
        Assertions.assertEquals(item1, ((ItemHolder) (adapterItems.get(0))).getModel());
        Assertions.assertEquals(item2, ((ItemHolder) (adapterItems.get(1))).getModel());
        Assertions.assertEquals(headers.get("H1"), getHeader().getModel());
        Assertions.assertEquals(headers.get("H2"), getHeader().getModel());
    }

    /**
     * Custom Factory, that transform an ItemModel to an IFlexible Item.
     * This Factory produces a Sectionable item and the relative Header.
     */
    private class SectionableFactory implements Factory<ItemModel, AbstractFlexibleItem> {
        Map<String, HeaderModel> headers;

        SectionableFactory(Map<String, HeaderModel> headers) {
            this.headers = headers;
        }

        private IHeader getHeader(ItemModel itemModel) {
            return FlexibleFactory.create(HeaderHolder.class, headers.get(itemModel.getType()));
        }

        @NonNull
        @Override
        public AbstractFlexibleItem create(ItemModel itemModel) {
            return FlexibleFactory.create(ItemHolder.class, itemModel, getHeader(itemModel));
        }
    }
}


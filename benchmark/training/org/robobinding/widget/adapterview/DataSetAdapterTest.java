package org.robobinding.widget.adapterview;


import android.view.View;
import android.view.ViewGroup;
import com.google.common.collect.Lists;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.BindableView;
import org.robobinding.itempresentationmodel.DataSetChangeListeners;
import org.robobinding.itempresentationmodel.RefreshableItemPresentationModel;
import org.robobinding.itempresentationmodel.ViewTypeSelectionContext;
import org.robobinding.presentationmodel.AbstractItemPresentationModelObject;
import org.robobinding.property.DataSetPropertyChangeListener;
import org.robobinding.property.DataSetValueModel;
import org.robobinding.util.RandomValues;
import org.robobinding.viewattribute.ViewTag;
import org.robobinding.viewattribute.ViewTags;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
@RunWith(MockitoJUnitRunner.class)
public class DataSetAdapterTest {
    private DataSetAdapterTest.MockDataSetValueModel valueModel;

    @Test
    public void whenGenerateItemView_thenInflateTheCorrectViewWithItemPresentationModelAttached() {
        View view = Mockito.mock(View.class);
        ItemLayoutBinder itemLayoutBinder = DataSetAdapterTest.aItemLayoutBinder().inflateAndReturnRootView(view);
        ViewTag<RefreshableItemPresentationModel> viewTag = mockViewTag();
        ViewTags<RefreshableItemPresentationModel> viewTags = DataSetAdapterTest.aViewTags().tagForViewAndReturn(view, viewTag);
        DataSetAdapter dataSetAdapter = aDataSetAdapter().withItemLayoutBinder(itemLayoutBinder).withViewTags(viewTags).build();
        View result = dataSetAdapter.getView(0, null, null);
        Assert.assertThat(result, Matchers.sameInstance(view));
        Mockito.verify(viewTag).set(ArgumentMatchers.notNull(RefreshableItemPresentationModel.class));
    }

    private static class ItemLayoutBinderBuilder {
        private ItemLayoutBinder itemLayoutBinder;

        public ItemLayoutBinderBuilder() {
            itemLayoutBinder = Mockito.mock(ItemLayoutBinder.class);
        }

        public ItemLayoutBinder inflateAndReturnRootView(View rootView) {
            BindableView bindableView = Mockito.mock(BindableView.class);
            Mockito.when(bindableView.getRootView()).thenReturn(rootView);
            Mockito.when(itemLayoutBinder.inflate(ArgumentMatchers.any(ViewGroup.class), ArgumentMatchers.anyInt())).thenReturn(bindableView);
            return itemLayoutBinder;
        }
    }

    private static class ViewTagsBuilder {
        private ViewTags<RefreshableItemPresentationModel> viewTags;

        @SuppressWarnings("unchecked")
        public ViewTagsBuilder() {
            viewTags = Mockito.mock(ViewTags.class);
        }

        public ViewTags<RefreshableItemPresentationModel> tagForViewAndReturn(View view, ViewTag<RefreshableItemPresentationModel> viewTag) {
            Mockito.when(viewTags.tagFor(view)).thenReturn(viewTag);
            return viewTags;
        }
    }

    @Test
    public void whenGenerateDropdownView_thenInflateTheCorrectViewWithItemPresentationModelAttached() {
        View view = Mockito.mock(View.class);
        ItemLayoutBinder dropdownLayoutBinder = DataSetAdapterTest.aItemLayoutBinder().inflateAndReturnRootView(view);
        ViewTag<RefreshableItemPresentationModel> viewTag = mockViewTag();
        ViewTags<RefreshableItemPresentationModel> viewTags = DataSetAdapterTest.aViewTags().tagForViewAndReturn(view, viewTag);
        DataSetAdapter dataSetAdapter = aDataSetAdapter().withDropdownLayoutBinder(dropdownLayoutBinder).withViewTags(viewTags).build();
        View result = dataSetAdapter.getDropDownView(0, null, null);
        Assert.assertThat(result, Matchers.sameInstance(view));
        Mockito.verify(viewTag).set(ArgumentMatchers.notNull(RefreshableItemPresentationModel.class));
    }

    @Test
    public void dataSetAdapterCountShouldReflectValueModel() {
        DataSetAdapter dataSetAdapter = aDataSetAdapter().build();
        Assert.assertThat(dataSetAdapter.getCount(), CoreMatchers.is(valueModel.size()));
    }

    public static class MockDataSetValueModel implements DataSetValueModel {
        private DataSetChangeListeners listeners;

        private List<Object> items;

        public MockDataSetValueModel() {
            listeners = new DataSetChangeListeners();
            initializeItems();
        }

        private void initializeItems() {
            items = Lists.newArrayList();
            for (int i = 0; i < (RandomValues.anyIntegerGreaterThanZero()); i++) {
                items.add(new Object());
            }
        }

        @Override
        public void addPropertyChangeListener(DataSetPropertyChangeListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removePropertyChangeListener(DataSetPropertyChangeListener listener) {
        }

        @Override
        public int size() {
            return items.size();
        }

        @Override
        public Object get(int position) {
            return null;
        }

        @Override
        public RefreshableItemPresentationModel newRefreshableItemPresentationModel(int itemViewType) {
            return Mockito.mock(AbstractItemPresentationModelObject.class);
        }

        @Override
        public int selectViewType(ViewTypeSelectionContext<Object> context) {
            return 0;
        }

        @Override
        public boolean preInitializingViewsWithDefault(boolean defaultValue) {
            return false;
        }
    }

    private static class ObjectDataSetAdapterBuilder {
        private DataSetValueModel valueModel;

        private ItemLayoutBinder itemLayoutBinder;

        private ItemLayoutBinder dropdownLayoutBinder;

        private ItemLayoutSelector layoutSelector;

        private int dropdownLayoutId;

        private ViewTags<RefreshableItemPresentationModel> viewTags;

        private boolean preInitializeViews;

        public ObjectDataSetAdapterBuilder(DataSetValueModel valueModel) {
            this.valueModel = valueModel;
            itemLayoutBinder = null;
            dropdownLayoutBinder = null;
            layoutSelector = new SingleItemLayoutSelector(1);
            dropdownLayoutId = 1;
            viewTags = null;
            preInitializeViews = false;
        }

        public DataSetAdapterTest.ObjectDataSetAdapterBuilder withViewTags(ViewTags<RefreshableItemPresentationModel> newValue) {
            this.viewTags = newValue;
            return this;
        }

        public DataSetAdapterTest.ObjectDataSetAdapterBuilder withItemLayoutBinder(ItemLayoutBinder newValue) {
            this.itemLayoutBinder = newValue;
            return this;
        }

        public DataSetAdapterTest.ObjectDataSetAdapterBuilder withDropdownLayoutBinder(ItemLayoutBinder newValue) {
            this.dropdownLayoutBinder = newValue;
            return this;
        }

        public DataSetAdapter build() {
            return new DataSetAdapter(valueModel, itemLayoutBinder, dropdownLayoutBinder, layoutSelector, dropdownLayoutId, viewTags, preInitializeViews);
        }
    }
}


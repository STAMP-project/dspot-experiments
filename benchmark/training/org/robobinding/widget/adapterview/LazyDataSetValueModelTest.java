package org.robobinding.widget.adapterview;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.robobinding.itempresentationmodel.DataSetChangeListeners;
import org.robobinding.itempresentationmodel.RefreshableItemPresentationModel;
import org.robobinding.itempresentationmodel.ViewTypeSelectionContext;
import org.robobinding.property.DataSetPropertyChangeListener;
import org.robobinding.property.DataSetValueModel;
import org.robobinding.util.RandomValues;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class LazyDataSetValueModelTest {
    @Test
    public void sizeShouldBeZeroAfterInitialization() {
        LazyDataSetValueModelTest.DataSetValueModelUnderTest delegateValueModel = new LazyDataSetValueModelTest.DataSetValueModelUnderTest(RandomValues.anyInteger());
        LazyDataSetValueModel valueModel = new LazyDataSetValueModel(delegateValueModel);
        Assert.assertThat(valueModel.size(), CoreMatchers.is(0));
    }

    @Test
    public void onValueModelChange_sizeShouldEqualToThatOfDelegateValueModel() {
        int size = RandomValues.anyInteger();
        LazyDataSetValueModelTest.DataSetValueModelUnderTest delegateValueModel = new LazyDataSetValueModelTest.DataSetValueModelUnderTest(size);
        LazyDataSetValueModel valueModel = new LazyDataSetValueModel(delegateValueModel);
        delegateValueModel.fireChange();
        Assert.assertThat(valueModel.size(), CoreMatchers.is(size));
    }

    private static class DataSetValueModelUnderTest implements DataSetValueModel {
        private final int size;

        private DataSetChangeListeners listeners;

        public DataSetValueModelUnderTest(int size) {
            this.size = size;
            listeners = new DataSetChangeListeners();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void addPropertyChangeListener(DataSetPropertyChangeListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removePropertyChangeListener(DataSetPropertyChangeListener listener) {
            listeners.remove(listener);
        }

        public void fireChange() {
            listeners.notifyChanged(null);
        }

        @Override
        public int selectViewType(ViewTypeSelectionContext<Object> context) {
            return 0;
        }

        @Override
        public Object get(int index) {
            return null;
        }

        @Override
        public RefreshableItemPresentationModel newRefreshableItemPresentationModel(int itemViewType) {
            return null;
        }

        @Override
        public boolean preInitializingViewsWithDefault(boolean defaultValue) {
            return false;
        }
    }
}


package org.robobinding.property;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.robobinding.itempresentationmodel.DataSetChangeListener;
import org.robobinding.itempresentationmodel.DataSetObservable;

import static org.robobinding.property.AbstractDataSetTest.DataSet.<init>;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class ObservableDataSetTest {
    @Test
    public void whenInitializeDataSet_thenListenerRegistered() {
        ObservableDataSetTest.DataSetObservableUnderTest observable = new ObservableDataSetTest.DataSetObservableUnderTest();
        ObservableDataSetTest.GetSet getSet = new ObservableDataSetTest.GetSet(observable);
        new ObservableDataSet(null, getSet);
        Assert.assertThat(observable.listener, Matchers.notNullValue());
    }

    @Test
    public void whenChangeToObservable2_thenObservable1ListenerRemovedAndObservable2ListenerRegistered() {
        ObservableDataSetTest.DataSetObservableUnderTest observable1 = new ObservableDataSetTest.DataSetObservableUnderTest();
        ObservableDataSetTest.GetSet getSet = new ObservableDataSetTest.GetSet(observable1);
        ObservableDataSet dataSet = new ObservableDataSet(null, getSet);
        ObservableDataSetTest.DataSetObservableUnderTest observable2 = new ObservableDataSetTest.DataSetObservableUnderTest();
        getSet.setValue(observable2);
        dataSet.propertyChanged();
        Assert.assertThat(observable1.listener, Matchers.nullValue());
        Assert.assertThat(observable2.listener, Matchers.notNullValue());
    }

    @Test
    public void whenUpdateDataSet_thenDataSetPropertyReflectsChanges() {
        ObservableDataSetTest.GetSet getSet = new ObservableDataSetTest.GetSet();
        AbstractDataSetTest.DataSet dataSetProperty = new AbstractDataSetTest.DataSet(getSet);
        DataSetObservable newValue = new ObservableDataSetTest.DataSetObservableUnderTest();
        getSet.setValue(newValue);
        propertyChanged();
        Assert.assertSame(newValue, getDataSet());
    }

    static class GetSet extends AbstractGetSet<DataSetObservable> {
        public DataSetObservable value;

        public GetSet() {
            super(null);
        }

        public GetSet(DataSetObservable value) {
            super(null);
            this.value = value;
        }

        @Override
        public DataSetObservable getValue() {
            return value;
        }

        @Override
        public void setValue(DataSetObservable newValue) {
            this.value = newValue;
        }
    }

    static class DataSetObservableUnderTest implements DataSetObservable {
        public DataSetChangeListener listener;

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Object get(int index) {
            return null;
        }

        @Override
        public void addListener(DataSetChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void removeListener(DataSetChangeListener listener) {
            if ((this.listener) == listener) {
                this.listener = null;
            }
        }
    }
}


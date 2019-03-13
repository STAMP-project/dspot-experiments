package org.robobinding.property;


import org.junit.Assert;
import org.junit.Test;
import org.robobinding.itempresentationmodel.DataSetChangeListener;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class AbstractDataSetTest {
    @Test
    public void givenGetDataSet_whenGetDataSetAgain_thenReturnSameInstance() {
        AbstractDataSetTest.GetSet getSet = new AbstractDataSetTest.GetSet(new Object());
        AbstractDataSetTest.DataSet dataSetProperty = new AbstractDataSetTest.DataSet(getSet);
        Object dataSetFirstTime = getDataSet();
        Object dataSetSecondTime = getDataSet();
        Assert.assertSame(dataSetSecondTime, dataSetFirstTime);
    }

    @Test
    public void whenUpdateDataSet_thenDataSetPropertyReflectsChanges() {
        AbstractDataSetTest.GetSet getSet = new AbstractDataSetTest.GetSet(new Object());
        AbstractDataSetTest.DataSet dataSetProperty = new AbstractDataSetTest.DataSet(getSet);
        Object newValue = new Object();
        getSet.value = newValue;
        propertyChanged();
        Assert.assertSame(newValue, dataSetProperty.getDataSet());
    }

    static class DataSet extends AbstractDataSet {
        public DataSet(AbstractGetSet<?> getSet) {
            super(null, getSet);
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Object get(int position) {
            return null;
        }

        @Override
        public void addListener(DataSetChangeListener listener) {
        }

        @Override
        public void removeListener(DataSetChangeListener listener) {
        }
    }

    static class GetSet extends AbstractGetSet<Object> {
        public Object value;

        public GetSet(Object value) {
            super(null);
            this.value = value;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }
}


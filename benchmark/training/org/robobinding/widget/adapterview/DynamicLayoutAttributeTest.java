package org.robobinding.widget.adapterview;


import android.widget.AdapterView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.util.RandomValues;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class DynamicLayoutAttributeTest {
    @Mock
    private RowLayoutUpdater rowLayoutUpdater;

    @Mock
    private DataSetAdapterUpdater dataSetAdapterUpdater;

    @Mock
    private AdapterView<?> view;

    @Test
    public void whenUpdateView_thenUpdateItemLayoutOnRowLayoutUpdater() {
        int newItemLayoutId = RandomValues.anyInteger();
        DynamicLayoutAttribute dynamicLayoutAttribute = new DynamicLayoutAttribute(rowLayoutUpdater, dataSetAdapterUpdater);
        dynamicLayoutAttribute.updateView(view, newItemLayoutId);
        Mockito.verify(rowLayoutUpdater).updateRowLayout(newItemLayoutId);
    }

    @Test
    public void whenUpdateView_thenExecuteUpdateOnDataSetAdapterUpdater() {
        int newItemLayoutId = RandomValues.anyInteger();
        DynamicLayoutAttribute dynamicLayoutAttribute = new DynamicLayoutAttribute(rowLayoutUpdater, dataSetAdapterUpdater);
        dynamicLayoutAttribute.updateView(view, newItemLayoutId);
        Mockito.verify(dataSetAdapterUpdater).update();
    }
}


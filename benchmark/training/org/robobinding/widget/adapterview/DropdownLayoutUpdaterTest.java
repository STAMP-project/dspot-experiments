package org.robobinding.widget.adapterview;


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
 */
@RunWith(MockitoJUnitRunner.class)
public class DropdownLayoutUpdaterTest {
    @Mock
    private DataSetAdapterBuilder dataSetAdapterBuilder;

    @Test
    public void whenUpdatingRowLayout_thenSetDropdownLayoutOnDataSetAdapterBuilder() {
        int layoutId = RandomValues.anyInteger();
        DropdownLayoutUpdater dropdownLayoutUpdater = new DropdownLayoutUpdater(dataSetAdapterBuilder);
        dropdownLayoutUpdater.updateRowLayout(layoutId);
        Mockito.verify(dataSetAdapterBuilder).setDropdownLayoutId(layoutId);
    }
}


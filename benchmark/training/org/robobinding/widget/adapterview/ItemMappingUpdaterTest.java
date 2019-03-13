package org.robobinding.widget.adapterview;


import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.PredefinedPendingAttributesForView;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemMappingUpdaterTest {
    @Mock
    private DataSetAdapterBuilder dataSetAdapterBuilder;

    @Mock
    private Collection<PredefinedPendingAttributesForView> viewMappings;

    @Test
    public void whenUpdateViewMappings_thenSetItemPredefinedPendingAttributesForViewGroupOnDataSetAdapterBuilder() {
        ItemMappingUpdater updater = new ItemMappingUpdater(dataSetAdapterBuilder);
        updater.updateViewMappings(viewMappings);
        Mockito.verify(dataSetAdapterBuilder).setItemPredefinedMappings(viewMappings);
    }
}


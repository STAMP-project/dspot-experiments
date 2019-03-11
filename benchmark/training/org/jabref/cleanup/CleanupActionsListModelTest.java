package org.jabref.cleanup;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.jabref.gui.cleanup.CleanupActionsListModel;
import org.jabref.logic.formatter.bibtexfields.ClearFormatter;
import org.jabref.model.cleanup.FieldFormatterCleanup;
import org.jabref.model.cleanup.FieldFormatterCleanups;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class CleanupActionsListModelTest {
    @Test
    public void resetFiresItemsChanged() throws Exception {
        CleanupActionsListModel model = new CleanupActionsListModel(Collections.emptyList());
        ListDataListener listener = Mockito.mock(ListDataListener.class);
        model.addListDataListener(listener);
        FieldFormatterCleanups defaultFormatters = Mockito.mock(FieldFormatterCleanups.class);
        model.reset(defaultFormatters);
        ArgumentCaptor<ListDataEvent> argument = ArgumentCaptor.forClass(ListDataEvent.class);
        Mockito.verify(listener).contentsChanged(argument.capture());
        Assertions.assertEquals(ListDataEvent.CONTENTS_CHANGED, argument.getValue().getType());
    }

    @Test
    public void resetSetsFormattersToPassedList() throws Exception {
        CleanupActionsListModel model = new CleanupActionsListModel(Collections.emptyList());
        FieldFormatterCleanups defaultFormatters = Mockito.mock(FieldFormatterCleanups.class);
        List<FieldFormatterCleanup> formatters = Arrays.asList(new FieldFormatterCleanup("test", new ClearFormatter()));
        Mockito.when(defaultFormatters.getConfiguredActions()).thenReturn(formatters);
        model.reset(defaultFormatters);
        Assertions.assertEquals(formatters, model.getAllActions());
    }

    @Test
    public void removedAtIndexOkay() {
        CleanupActionsListModel model = new CleanupActionsListModel(getDefaultFieldFormatterCleanups());
        ListDataListener listener = Mockito.mock(ListDataListener.class);
        model.addListDataListener(listener);
        model.removeAtIndex(0);
        ArgumentCaptor<ListDataEvent> argument = ArgumentCaptor.forClass(ListDataEvent.class);
        Mockito.verify(listener).intervalRemoved(argument.capture());
        Assertions.assertEquals(ListDataEvent.INTERVAL_REMOVED, argument.getValue().getType());
    }

    @Test
    public void removedAtIndexMinus1DoesNothing() {
        CleanupActionsListModel model = new CleanupActionsListModel(getDefaultFieldFormatterCleanups());
        ListDataListener listener = Mockito.mock(ListDataListener.class);
        model.addListDataListener(listener);
        model.removeAtIndex((-1));
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void removedAtIndexgreaterListSizeDoesNothing() {
        CleanupActionsListModel model = new CleanupActionsListModel(getDefaultFieldFormatterCleanups());
        ListDataListener listener = Mockito.mock(ListDataListener.class);
        model.addListDataListener(listener);
        model.removeAtIndex(((getDefaultFieldFormatterCleanups().size()) + 1));
        Mockito.verifyZeroInteractions(listener);
    }
}


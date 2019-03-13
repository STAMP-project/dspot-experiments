package org.jabref.gui.exporter;


import Actions.SAVE;
import Actions.SAVE_AS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.jabref.gui.BasePanel;
import org.jabref.gui.DialogService;
import org.jabref.gui.JabRefFrame;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabaseContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SaveAllActionTest {
    private BasePanel firstPanel = Mockito.mock(BasePanel.class);

    private BasePanel secondPanel = Mockito.mock(BasePanel.class);

    private JabRefFrame jabRefFrame = Mockito.mock(JabRefFrame.class);

    private DialogService dialogService = Mockito.mock(DialogService.class);

    private BibDatabaseContext bibDatabaseContext = Mockito.mock(BibDatabaseContext.class);

    private Optional<Path> databasePath = Optional.of(Paths.get("C:\\Users\\John_Doe\\Jabref"));

    private SaveAllAction saveAllAction;

    @Test
    public void executeShouldRunSaveCommandInEveryPanel() {
        Mockito.doNothing().when(dialogService).notify(ArgumentMatchers.anyString());
        saveAllAction.execute();
        Mockito.verify(firstPanel, Mockito.times(1)).runCommand(SAVE);
        Mockito.verify(secondPanel, Mockito.times(1)).runCommand(SAVE);
    }

    @Test
    public void executeShouldNotifyAboutSavingProcess() {
        Mockito.when(bibDatabaseContext.getDatabasePath()).thenReturn(databasePath);
        saveAllAction.execute();
        Mockito.verify(dialogService, Mockito.times(1)).notify(Localization.lang("Saving all libraries..."));
        Mockito.verify(dialogService, Mockito.times(1)).notify(Localization.lang("Save all finished."));
    }

    @Test
    public void executeShouldShowSaveAsWindowIfDatabaseNotSelected() {
        Mockito.when(bibDatabaseContext.getDatabasePath()).thenReturn(Optional.empty());
        Mockito.doNothing().when(dialogService).notify(ArgumentMatchers.anyString());
        saveAllAction.execute();
        Mockito.verify(firstPanel, Mockito.times(1)).runCommand(SAVE_AS);
        Mockito.verify(secondPanel, Mockito.times(1)).runCommand(SAVE_AS);
    }
}


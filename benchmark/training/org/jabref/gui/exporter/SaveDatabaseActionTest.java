package org.jabref.gui.exporter;


import DatabaseLocation.LOCAL;
import JabRefPreferences.LOCAL_AUTO_SAVE;
import JabRefPreferences.WORKING_DIRECTORY;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.jabref.gui.BasePanel;
import org.jabref.gui.DialogService;
import org.jabref.gui.JabRefFrame;
import org.jabref.gui.util.FileDialogConfiguration;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.preferences.JabRefPreferences;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


class SaveDatabaseActionTest {
    private static final String TEST_FILE_PATH = "C:\\Users\\John_Doe\\Jabref";

    private final File file = new File(SaveDatabaseActionTest.TEST_FILE_PATH);

    private Optional<Path> path = Optional.of(file.toPath());

    private DialogService dialogService = Mockito.mock(DialogService.class);

    private JabRefPreferences preferences = Mockito.mock(JabRefPreferences.class);

    private BasePanel basePanel = Mockito.mock(BasePanel.class);

    private JabRefFrame jabRefFrame = Mockito.mock(JabRefFrame.class);

    private BibDatabaseContext dbContext = Mockito.spy(BibDatabaseContext.class);

    private SaveDatabaseAction saveDatabaseAction;

    @Test
    public void saveAsShouldSetWorkingDirectory() {
        Mockito.when(preferences.get(WORKING_DIRECTORY)).thenReturn(SaveDatabaseActionTest.TEST_FILE_PATH);
        Mockito.when(dialogService.showFileSaveDialog(ArgumentMatchers.any(FileDialogConfiguration.class))).thenReturn(path);
        Mockito.doNothing().when(saveDatabaseAction).saveAs(ArgumentMatchers.any());
        saveDatabaseAction.saveAs();
        Mockito.verify(preferences, Mockito.times(1)).setWorkingDir(path.get().getParent());
    }

    @Test
    public void saveAsShouldNotSetWorkingDirectoryIfNotSelected() {
        Mockito.when(preferences.get(WORKING_DIRECTORY)).thenReturn(SaveDatabaseActionTest.TEST_FILE_PATH);
        Mockito.when(dialogService.showFileSaveDialog(ArgumentMatchers.any(FileDialogConfiguration.class))).thenReturn(Optional.empty());
        Mockito.doNothing().when(saveDatabaseAction).saveAs(ArgumentMatchers.any());
        saveDatabaseAction.saveAs();
        Mockito.verify(preferences, Mockito.times(0)).setWorkingDir(path.get().getParent());
    }

    @Test
    public void saveAsShouldSetNewDatabasePathIntoContext() {
        Mockito.when(dbContext.getDatabasePath()).thenReturn(Optional.empty());
        Mockito.when(dbContext.getLocation()).thenReturn(LOCAL);
        Mockito.when(preferences.getBoolean(LOCAL_AUTO_SAVE)).thenReturn(false);
        saveDatabaseAction.saveAs(file.toPath());
        Mockito.verify(dbContext, Mockito.times(1)).setDatabaseFile(file.toPath());
    }

    @Test
    public void saveShouldShowSaveAsIfDatabaseNotSelected() {
        Mockito.when(dbContext.getDatabasePath()).thenReturn(Optional.empty());
        Mockito.when(dbContext.getLocation()).thenReturn(LOCAL);
        Mockito.when(preferences.getBoolean(LOCAL_AUTO_SAVE)).thenReturn(false);
        Mockito.when(dialogService.showFileSaveDialog(ArgumentMatchers.any())).thenReturn(path);
        Mockito.doNothing().when(saveDatabaseAction).saveAs(file.toPath());
        saveDatabaseAction.save();
        Mockito.verify(saveDatabaseAction, Mockito.times(1)).saveAs(file.toPath());
    }

    @Test
    public void saveShouldNotSaveDatabaseIfPathNotSet() {
        Mockito.when(dbContext.getDatabasePath()).thenReturn(Optional.empty());
        boolean result = saveDatabaseAction.save();
        Assert.assertFalse(result);
    }
}


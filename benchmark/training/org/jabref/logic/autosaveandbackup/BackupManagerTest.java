package org.jabref.logic.autosaveandbackup;


import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BackupManagerTest {
    @Test
    public void backupFileNameIsCorrectlyGeneratedWithinTmpDirectory() {
        Path bibPath = Paths.get("tmp", "test.bib");
        Path savPath = BackupManager.getBackupPath(bibPath);
        Assertions.assertEquals(Paths.get("tmp", "test.bib.sav"), savPath);
    }
}


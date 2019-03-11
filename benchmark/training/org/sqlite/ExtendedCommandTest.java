/**
 * --------------------------------------
 */
/**
 * sqlite-jdbc Project
 */
/**
 *
 */
/**
 * ExtendedCommandTest.java
 */
/**
 * Since: Mar 12, 2010
 */
/**
 *
 */
/**
 * $URL$
 */
/**
 * $Author$
 */
/**
 * --------------------------------------
 */
package org.sqlite;


import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import org.sqlite.ExtendedCommand.BackupCommand;
import org.sqlite.ExtendedCommand.RestoreCommand;


public class ExtendedCommandTest {
    @Test
    public void parseBackupCmd() throws SQLException {
        BackupCommand b = ExtendedCommandTest.parseBackupCommand("backup mydb to somewhere/backupfolder/mydb.sqlite");
        Assert.assertEquals("mydb", b.srcDB);
        Assert.assertEquals("somewhere/backupfolder/mydb.sqlite", b.destFile);
        b = ExtendedCommandTest.parseBackupCommand("backup main to \"tmp folder with space\"");
        Assert.assertEquals("main", b.srcDB);
        Assert.assertEquals("tmp folder with space", b.destFile);
        b = ExtendedCommandTest.parseBackupCommand("backup main to 'tmp folder with space'");
        Assert.assertEquals("main", b.srcDB);
        Assert.assertEquals("tmp folder with space", b.destFile);
        b = ExtendedCommandTest.parseBackupCommand("backup to target/sample.db");
        Assert.assertEquals("main", b.srcDB);
        Assert.assertEquals("target/sample.db", b.destFile);
    }

    @Test
    public void parseRestoreCmd() throws SQLException {
        RestoreCommand b = ExtendedCommandTest.parseRestoreCommand("restore mydb from somewhere/backupfolder/mydb.sqlite");
        Assert.assertEquals("mydb", b.targetDB);
        Assert.assertEquals("somewhere/backupfolder/mydb.sqlite", b.srcFile);
        b = ExtendedCommandTest.parseRestoreCommand("restore main from \"tmp folder with space\"");
        Assert.assertEquals("main", b.targetDB);
        Assert.assertEquals("tmp folder with space", b.srcFile);
        b = ExtendedCommandTest.parseRestoreCommand("restore main from 'tmp folder with space'");
        Assert.assertEquals("main", b.targetDB);
        Assert.assertEquals("tmp folder with space", b.srcFile);
        b = ExtendedCommandTest.parseRestoreCommand("restore from target/sample.db");
        Assert.assertEquals("main", b.targetDB);
        Assert.assertEquals("target/sample.db", b.srcFile);
    }
}


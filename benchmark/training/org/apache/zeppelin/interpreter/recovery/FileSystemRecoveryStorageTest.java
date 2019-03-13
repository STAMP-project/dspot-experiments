package org.apache.zeppelin.interpreter.recovery;


import InterpreterOption.ISOLATED;
import InterpreterOption.SHARED;
import java.io.File;
import java.io.IOException;
import org.apache.zeppelin.interpreter.AbstractInterpreterTest;
import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.apache.zeppelin.interpreter.remote.RemoteInterpreter;
import org.junit.Assert;
import org.junit.Test;


public class FileSystemRecoveryStorageTest extends AbstractInterpreterTest {
    private File recoveryDir = null;

    @Test
    public void testSingleInterpreterProcess() throws IOException, InterpreterException {
        InterpreterSetting interpreterSetting = interpreterSettingManager.getByName("test");
        interpreterSetting.getOption().setPerUser(SHARED);
        Interpreter interpreter1 = interpreterSetting.getDefaultInterpreter("user1", "note1");
        RemoteInterpreter remoteInterpreter1 = ((RemoteInterpreter) (interpreter1));
        InterpreterContext context1 = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").build();
        remoteInterpreter1.interpret("hello", context1);
        Assert.assertEquals(1, interpreterSettingManager.getRecoveryStorage().restore().size());
        interpreterSetting.close();
        Assert.assertEquals(0, interpreterSettingManager.getRecoveryStorage().restore().size());
    }

    @Test
    public void testMultipleInterpreterProcess() throws IOException, InterpreterException {
        InterpreterSetting interpreterSetting = interpreterSettingManager.getByName("test");
        interpreterSetting.getOption().setPerUser(ISOLATED);
        Interpreter interpreter1 = interpreterSetting.getDefaultInterpreter("user1", "note1");
        RemoteInterpreter remoteInterpreter1 = ((RemoteInterpreter) (interpreter1));
        InterpreterContext context1 = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").build();
        remoteInterpreter1.interpret("hello", context1);
        Assert.assertEquals(1, interpreterSettingManager.getRecoveryStorage().restore().size());
        Interpreter interpreter2 = interpreterSetting.getDefaultInterpreter("user2", "note2");
        RemoteInterpreter remoteInterpreter2 = ((RemoteInterpreter) (interpreter2));
        InterpreterContext context2 = InterpreterContext.builder().setNoteId("noteId").setParagraphId("paragraphId").build();
        remoteInterpreter2.interpret("hello", context2);
        Assert.assertEquals(2, interpreterSettingManager.getRecoveryStorage().restore().size());
        interpreterSettingManager.restart(interpreterSetting.getId(), "note1", "user1");
        Assert.assertEquals(1, interpreterSettingManager.getRecoveryStorage().restore().size());
        interpreterSetting.close();
        Assert.assertEquals(0, interpreterSettingManager.getRecoveryStorage().restore().size());
    }
}


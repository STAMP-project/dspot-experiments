package com.developerphil.adbidea.adb.command;


import StartDefaultActivityCommand.StartActivityReceiver;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class StartDefaultActivityCommandTest {
    private static final String[] TRAILING_EMPTY_LINE = new String[]{ "" };

    @Test
    public void testReceiverSuccess() throws Exception {
        String[] lines = new String[]{ "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.example.untitled/.MyActivity }" };
        StartDefaultActivityCommand.StartActivityReceiver receiver = new StartDefaultActivityCommand.StartActivityReceiver();
        Assert.assertThat(receiver.isSuccess(), Is.is(false));
        receiver.processNewLines(lines);
        receiver.processNewLines(StartDefaultActivityCommandTest.TRAILING_EMPTY_LINE);
        Assert.assertThat(receiver.isSuccess(), Is.is(true));
    }

    @Test
    public void isSuccessWhenAppIsAlreadyStarted() throws Exception {
        String[] lines = new String[]{ "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.example.untitled/.MyActivity }", "Warning: Activity not started, its current task has been brought to the front" };
        StartDefaultActivityCommand.StartActivityReceiver receiver = new StartDefaultActivityCommand.StartActivityReceiver();
        receiver.processNewLines(lines);
        receiver.processNewLines(StartDefaultActivityCommandTest.TRAILING_EMPTY_LINE);
        Assert.assertThat(receiver.isSuccess(), Is.is(true));
    }

    @Test
    public void isFailureWhenAppIsUninstalled() throws Exception {
        String[] lines = new String[]{ "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.cxategory.LAUNCHER] cmp=com.example.untitled/.MyActivity }", "Error type 3", "Error: Activity class {com.example.untitled/com.example.untitled.MyActivity} does not exist." };
        StartDefaultActivityCommand.StartActivityReceiver receiver = new StartDefaultActivityCommand.StartActivityReceiver();
        receiver.processNewLines(lines);
        receiver.processNewLines(StartDefaultActivityCommandTest.TRAILING_EMPTY_LINE);
        Assert.assertThat(receiver.isSuccess(), Is.is(false));
        Assert.assertThat(receiver.getMessage(), Is.is(IsEqual.equalTo(("Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.cxategory.LAUNCHER] cmp=com.example.untitled/.MyActivity }\n" + ("Error type 3\n" + "Error: Activity class {com.example.untitled/com.example.untitled.MyActivity} does not exist.")))));
    }
}


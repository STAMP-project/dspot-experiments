package com.developerphil.adbidea.adb.command.receiver;


import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;


public class GenericReceiverTest {
    @Test
    public void testReceiverRecordsAdbOutput() throws Exception {
        GenericReceiver receiver = new GenericReceiver();
        Assert.assertTrue(receiver.getAdbOutputLines().isEmpty());
        receiver.processNewLines(new String[]{ "1", "2", "3" });
        Assert.assertThat(receiver.getAdbOutputLines(), JUnitMatchers.hasItems("1", "2", "3"));
        receiver.processNewLines(new String[]{ "4" });
        Assert.assertThat(receiver.getAdbOutputLines(), JUnitMatchers.hasItems("1", "2", "3", "4"));
    }
}


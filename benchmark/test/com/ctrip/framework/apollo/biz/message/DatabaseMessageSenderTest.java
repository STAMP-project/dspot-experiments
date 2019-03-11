package com.ctrip.framework.apollo.biz.message;


import Topics.APOLLO_RELEASE_TOPIC;
import com.ctrip.framework.apollo.biz.AbstractUnitTest;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.repository.ReleaseMessageRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DatabaseMessageSenderTest extends AbstractUnitTest {
    private DatabaseMessageSender messageSender;

    @Mock
    private ReleaseMessageRepository releaseMessageRepository;

    @Test
    public void testSendMessage() throws Exception {
        String someMessage = "some-message";
        long someId = 1;
        ReleaseMessage someReleaseMessage = Mockito.mock(ReleaseMessage.class);
        Mockito.when(someReleaseMessage.getId()).thenReturn(someId);
        Mockito.when(releaseMessageRepository.save(ArgumentMatchers.any(ReleaseMessage.class))).thenReturn(someReleaseMessage);
        ArgumentCaptor<ReleaseMessage> captor = ArgumentCaptor.forClass(ReleaseMessage.class);
        messageSender.sendMessage(someMessage, APOLLO_RELEASE_TOPIC);
        Mockito.verify(releaseMessageRepository, Mockito.times(1)).save(captor.capture());
        Assert.assertEquals(someMessage, captor.getValue().getMessage());
    }

    @Test
    public void testSendUnsupportedMessage() throws Exception {
        String someMessage = "some-message";
        String someUnsupportedTopic = "some-invalid-topic";
        messageSender.sendMessage(someMessage, someUnsupportedTopic);
        Mockito.verify(releaseMessageRepository, Mockito.never()).save(ArgumentMatchers.any(ReleaseMessage.class));
    }

    @Test(expected = RuntimeException.class)
    public void testSendMessageFailed() throws Exception {
        String someMessage = "some-message";
        Mockito.when(releaseMessageRepository.save(ArgumentMatchers.any(ReleaseMessage.class))).thenThrow(new RuntimeException());
        messageSender.sendMessage(someMessage, APOLLO_RELEASE_TOPIC);
    }
}


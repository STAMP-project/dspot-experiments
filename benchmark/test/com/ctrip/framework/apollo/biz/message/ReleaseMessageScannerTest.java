package com.ctrip.framework.apollo.biz.message;


import com.ctrip.framework.apollo.biz.AbstractUnitTest;
import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.repository.ReleaseMessageRepository;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ReleaseMessageScannerTest extends AbstractUnitTest {
    private ReleaseMessageScanner releaseMessageScanner;

    @Mock
    private ReleaseMessageRepository releaseMessageRepository;

    @Mock
    private BizConfig bizConfig;

    private int databaseScanInterval;

    @Test
    public void testScanMessageAndNotifyMessageListener() throws Exception {
        SettableFuture<ReleaseMessage> someListenerFuture = SettableFuture.create();
        ReleaseMessageListener someListener = ( message, channel) -> someListenerFuture.set(message);
        releaseMessageScanner.addMessageListener(someListener);
        String someMessage = "someMessage";
        long someId = 100;
        ReleaseMessage someReleaseMessage = assembleReleaseMessage(someId, someMessage);
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0L)).thenReturn(Lists.newArrayList(someReleaseMessage));
        ReleaseMessage someListenerMessage = someListenerFuture.get(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(someMessage, someListenerMessage.getMessage());
        Assert.assertEquals(someId, someListenerMessage.getId());
        SettableFuture<ReleaseMessage> anotherListenerFuture = SettableFuture.create();
        ReleaseMessageListener anotherListener = ( message, channel) -> anotherListenerFuture.set(message);
        releaseMessageScanner.addMessageListener(anotherListener);
        String anotherMessage = "anotherMessage";
        long anotherId = someId + 1;
        ReleaseMessage anotherReleaseMessage = assembleReleaseMessage(anotherId, anotherMessage);
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(someId)).thenReturn(Lists.newArrayList(anotherReleaseMessage));
        ReleaseMessage anotherListenerMessage = anotherListenerFuture.get(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(anotherMessage, anotherListenerMessage.getMessage());
        Assert.assertEquals(anotherId, anotherListenerMessage.getId());
    }
}


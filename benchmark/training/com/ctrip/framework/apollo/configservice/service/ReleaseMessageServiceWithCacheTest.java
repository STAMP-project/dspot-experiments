package com.ctrip.framework.apollo.configservice.service;


import Topics.APOLLO_RELEASE_TOPIC;
import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.repository.ReleaseMessageRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ReleaseMessageServiceWithCacheTest {
    private ReleaseMessageServiceWithCache releaseMessageServiceWithCache;

    @Mock
    private ReleaseMessageRepository releaseMessageRepository;

    @Mock
    private BizConfig bizConfig;

    private int scanInterval;

    private TimeUnit scanIntervalTimeUnit;

    @Test
    public void testWhenNoReleaseMessages() throws Exception {
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0L)).thenReturn(Collections.emptyList());
        releaseMessageServiceWithCache.afterPropertiesSet();
        String someMessage = "someMessage";
        String anotherMessage = "anotherMessage";
        Set<String> messages = Sets.newHashSet(someMessage, anotherMessage);
        Assert.assertNull(releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(messages));
        Assert.assertTrue(releaseMessageServiceWithCache.findLatestReleaseMessagesGroupByMessages(messages).isEmpty());
    }

    @Test
    public void testWhenHasReleaseMsgAndHasRepeatMsg() throws Exception {
        String someMsgContent = "msg1";
        ReleaseMessage someMsg = assembleReleaseMsg(1, someMsgContent);
        String anotherMsgContent = "msg2";
        ReleaseMessage anotherMsg = assembleReleaseMsg(2, anotherMsgContent);
        ReleaseMessage anotherRepeatMsg = assembleReleaseMsg(3, anotherMsgContent);
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0L)).thenReturn(Arrays.asList(someMsg, anotherMsg, anotherRepeatMsg));
        releaseMessageServiceWithCache.afterPropertiesSet();
        Mockito.verify(bizConfig).releaseMessageCacheScanInterval();
        ReleaseMessage latestReleaseMsg = releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(Sets.newHashSet(someMsgContent, anotherMsgContent));
        Assert.assertNotNull(latestReleaseMsg);
        Assert.assertEquals(3, latestReleaseMsg.getId());
        Assert.assertEquals(anotherMsgContent, latestReleaseMsg.getMessage());
        List<ReleaseMessage> latestReleaseMsgGroupByMsgContent = releaseMessageServiceWithCache.findLatestReleaseMessagesGroupByMessages(Sets.newHashSet(someMsgContent, anotherMsgContent));
        Assert.assertEquals(2, latestReleaseMsgGroupByMsgContent.size());
        Assert.assertEquals(1, latestReleaseMsgGroupByMsgContent.get(1).getId());
        Assert.assertEquals(someMsgContent, latestReleaseMsgGroupByMsgContent.get(1).getMessage());
        Assert.assertEquals(3, latestReleaseMsgGroupByMsgContent.get(0).getId());
        Assert.assertEquals(anotherMsgContent, latestReleaseMsgGroupByMsgContent.get(0).getMessage());
    }

    @Test
    public void testWhenReleaseMsgSizeBiggerThan500() throws Exception {
        String someMsgContent = "msg1";
        List<ReleaseMessage> firstBatchReleaseMsg = new ArrayList<>(500);
        for (int i = 0; i < 500; i++) {
            firstBatchReleaseMsg.add(assembleReleaseMsg((i + 1), someMsgContent));
        }
        String antherMsgContent = "msg2";
        ReleaseMessage antherMsg = assembleReleaseMsg(501, antherMsgContent);
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0L)).thenReturn(firstBatchReleaseMsg);
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(500L)).thenReturn(Collections.singletonList(antherMsg));
        releaseMessageServiceWithCache.afterPropertiesSet();
        Mockito.verify(releaseMessageRepository, Mockito.times(1)).findFirst500ByIdGreaterThanOrderByIdAsc(500L);
        ReleaseMessage latestReleaseMsg = releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(Sets.newHashSet(someMsgContent, antherMsgContent));
        Assert.assertNotNull(latestReleaseMsg);
        Assert.assertEquals(501, latestReleaseMsg.getId());
        Assert.assertEquals(antherMsgContent, latestReleaseMsg.getMessage());
        List<ReleaseMessage> latestReleaseMsgGroupByMsgContent = releaseMessageServiceWithCache.findLatestReleaseMessagesGroupByMessages(Sets.newHashSet(someMsgContent, antherMsgContent));
        Assert.assertEquals(2, latestReleaseMsgGroupByMsgContent.size());
        Assert.assertEquals(500, latestReleaseMsgGroupByMsgContent.get(1).getId());
        Assert.assertEquals(501, latestReleaseMsgGroupByMsgContent.get(0).getId());
    }

    @Test
    public void testNewReleaseMessagesBeforeHandleMessage() throws Exception {
        String someMessageContent = "someMessage";
        long someMessageId = 1;
        ReleaseMessage someMessage = assembleReleaseMsg(someMessageId, someMessageContent);
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0L)).thenReturn(Lists.newArrayList(someMessage));
        releaseMessageServiceWithCache.afterPropertiesSet();
        ReleaseMessage latestReleaseMsg = releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(Sets.newHashSet(someMessageContent));
        List<ReleaseMessage> latestReleaseMsgGroupByMsgContent = releaseMessageServiceWithCache.findLatestReleaseMessagesGroupByMessages(Sets.newHashSet(someMessageContent));
        Assert.assertEquals(someMessageId, latestReleaseMsg.getId());
        Assert.assertEquals(someMessageContent, latestReleaseMsg.getMessage());
        Assert.assertEquals(latestReleaseMsg, latestReleaseMsgGroupByMsgContent.get(0));
        long newMessageId = 2;
        ReleaseMessage newMessage = assembleReleaseMsg(newMessageId, someMessageContent);
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(someMessageId)).thenReturn(Lists.newArrayList(newMessage));
        scanIntervalTimeUnit.sleep(((scanInterval) * 10));
        ReleaseMessage newLatestReleaseMsg = releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(Sets.newHashSet(someMessageContent));
        List<ReleaseMessage> newLatestReleaseMsgGroupByMsgContent = releaseMessageServiceWithCache.findLatestReleaseMessagesGroupByMessages(Sets.newHashSet(someMessageContent));
        Assert.assertEquals(newMessageId, newLatestReleaseMsg.getId());
        Assert.assertEquals(someMessageContent, newLatestReleaseMsg.getMessage());
        Assert.assertEquals(newLatestReleaseMsg, newLatestReleaseMsgGroupByMsgContent.get(0));
    }

    @Test
    public void testNewReleasesWithHandleMessage() throws Exception {
        String someMessageContent = "someMessage";
        long someMessageId = 1;
        ReleaseMessage someMessage = assembleReleaseMsg(someMessageId, someMessageContent);
        Mockito.when(releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0L)).thenReturn(Lists.newArrayList(someMessage));
        releaseMessageServiceWithCache.afterPropertiesSet();
        ReleaseMessage latestReleaseMsg = releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(Sets.newHashSet(someMessageContent));
        List<ReleaseMessage> latestReleaseMsgGroupByMsgContent = releaseMessageServiceWithCache.findLatestReleaseMessagesGroupByMessages(Sets.newHashSet(someMessageContent));
        Assert.assertEquals(someMessageId, latestReleaseMsg.getId());
        Assert.assertEquals(someMessageContent, latestReleaseMsg.getMessage());
        Assert.assertEquals(latestReleaseMsg, latestReleaseMsgGroupByMsgContent.get(0));
        long newMessageId = 2;
        ReleaseMessage newMessage = assembleReleaseMsg(newMessageId, someMessageContent);
        releaseMessageServiceWithCache.handleMessage(newMessage, APOLLO_RELEASE_TOPIC);
        ReleaseMessage newLatestReleaseMsg = releaseMessageServiceWithCache.findLatestReleaseMessageForMessages(Sets.newHashSet(someMessageContent));
        List<ReleaseMessage> newLatestReleaseMsgGroupByMsgContent = releaseMessageServiceWithCache.findLatestReleaseMessagesGroupByMessages(Sets.newHashSet(someMessageContent));
        Assert.assertEquals(newMessageId, newLatestReleaseMsg.getId());
        Assert.assertEquals(someMessageContent, newLatestReleaseMsg.getMessage());
        Assert.assertEquals(newLatestReleaseMsg, newLatestReleaseMsgGroupByMsgContent.get(0));
    }
}


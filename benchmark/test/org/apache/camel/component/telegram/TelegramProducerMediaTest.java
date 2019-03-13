/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.telegram;


import TelegramConstants.TELEGRAM_MEDIA_TITLE_CAPTION;
import TelegramConstants.TELEGRAM_MEDIA_TYPE;
import TelegramConstants.TELEGRAM_PARSE_MODE;
import TelegramMediaType.AUDIO;
import TelegramMediaType.DOCUMENT;
import TelegramMediaType.PHOTO_JPG;
import TelegramMediaType.PHOTO_PNG;
import TelegramMediaType.TEXT;
import TelegramMediaType.VIDEO;
import TelegramParseMode.HTML;
import TelegramParseMode.MARKDOWN;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.telegram.model.OutgoingAudioMessage;
import org.apache.camel.component.telegram.model.OutgoingDocumentMessage;
import org.apache.camel.component.telegram.model.OutgoingPhotoMessage;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.component.telegram.model.OutgoingVideoMessage;
import org.apache.camel.component.telegram.util.TelegramTestSupport;
import org.apache.camel.component.telegram.util.TelegramTestUtil;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests a producer that sends media information.
 */
public class TelegramProducerMediaTest extends TelegramTestSupport {
    @EndpointInject(uri = "direct:telegram")
    private Endpoint endpoint;

    @Test
    public void testRouteWithPngImage() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        ex.getIn().setHeader(TELEGRAM_MEDIA_TITLE_CAPTION, "Photo");
        ex.getIn().setHeader(TELEGRAM_MEDIA_TYPE, PHOTO_PNG.name());
        byte[] image = TelegramTestUtil.createSampleImage("PNG");
        ex.getIn().setBody(image);
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingPhotoMessage> captor = ArgumentCaptor.forClass(OutgoingPhotoMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals(image, captor.getValue().getPhoto());
        assertEquals("photo.png", captor.getValue().getFilenameWithExtension());
        assertEquals("Photo", captor.getValue().getCaption());
    }

    @Test
    public void testRouteWithJpgImage() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        ex.getIn().setHeader(TELEGRAM_MEDIA_TITLE_CAPTION, "Photo");
        ex.getIn().setHeader(TELEGRAM_MEDIA_TYPE, PHOTO_JPG);// without using .name()

        byte[] image = TelegramTestUtil.createSampleImage("JPG");
        ex.getIn().setBody(image);
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingPhotoMessage> captor = ArgumentCaptor.forClass(OutgoingPhotoMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals(image, captor.getValue().getPhoto());
        assertEquals("photo.jpg", captor.getValue().getFilenameWithExtension());
        assertEquals("Photo", captor.getValue().getCaption());
    }

    @Test
    public void testRouteWithAudio() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        ex.getIn().setHeader(TELEGRAM_MEDIA_TITLE_CAPTION, "Audio");
        ex.getIn().setHeader(TELEGRAM_MEDIA_TYPE, AUDIO);
        byte[] audio = TelegramTestUtil.createSampleAudio();
        ex.getIn().setBody(audio);
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingAudioMessage> captor = ArgumentCaptor.forClass(OutgoingAudioMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals(audio, captor.getValue().getAudio());
        assertEquals("audio.mp3", captor.getValue().getFilenameWithExtension());
        assertEquals("Audio", captor.getValue().getTitle());
    }

    @Test
    public void testRouteWithVideo() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        ex.getIn().setHeader(TELEGRAM_MEDIA_TITLE_CAPTION, "Video");
        ex.getIn().setHeader(TELEGRAM_MEDIA_TYPE, VIDEO.name());
        byte[] video = TelegramTestUtil.createSampleVideo();
        ex.getIn().setBody(video);
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingVideoMessage> captor = ArgumentCaptor.forClass(OutgoingVideoMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals(video, captor.getValue().getVideo());
        assertEquals("video.mp4", captor.getValue().getFilenameWithExtension());
        assertEquals("Video", captor.getValue().getCaption());
    }

    @Test
    public void testRouteWithDocument() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        ex.getIn().setHeader(TELEGRAM_MEDIA_TITLE_CAPTION, "Document");
        ex.getIn().setHeader(TELEGRAM_MEDIA_TYPE, DOCUMENT);
        byte[] document = TelegramTestUtil.createSampleDocument();
        ex.getIn().setBody(document);
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingDocumentMessage> captor = ArgumentCaptor.forClass(OutgoingDocumentMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals(document, captor.getValue().getDocument());
        assertEquals("file", captor.getValue().getFilenameWithExtension());
        assertEquals("Document", captor.getValue().getCaption());
    }

    @Test
    public void testRouteWithText() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        ex.getIn().setHeader(TELEGRAM_MEDIA_TYPE, TEXT.name());
        ex.getIn().setBody("Hello");
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingTextMessage> captor = ArgumentCaptor.forClass(OutgoingTextMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals("Hello", captor.getValue().getText());
        assertNull(captor.getValue().getParseMode());
    }

    @Test
    public void testRouteWithTextAndCustomKeyBoard() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        OutgoingTextMessage msg = new OutgoingTextMessage.Builder().text("Hello").build();
        withInlineKeyboardContainingTwoRows(msg);
        ex.getIn().setBody(msg);
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingTextMessage> captor = ArgumentCaptor.forClass(OutgoingTextMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals("Hello", captor.getValue().getText());
        assertEquals(2, captor.getValue().getReplyKeyboardMarkup().getKeyboard().size());
        assertEquals(true, captor.getValue().getReplyKeyboardMarkup().getOneTimeKeyboard());
        assertNull(captor.getValue().getParseMode());
    }

    @Test
    public void testRouteWithTextHtml() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        ex.getIn().setHeader(TELEGRAM_MEDIA_TYPE, TEXT.name());
        ex.getIn().setHeader(TELEGRAM_PARSE_MODE, HTML.name());
        ex.getIn().setBody("Hello");
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingTextMessage> captor = ArgumentCaptor.forClass(OutgoingTextMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals("Hello", captor.getValue().getText());
        assertEquals("HTML", captor.getValue().getParseMode());
    }

    @Test
    public void testRouteWithTextMarkdown() throws Exception {
        TelegramService service = mockTelegramService();
        Exchange ex = endpoint.createExchange();
        ex.getIn().setHeader(TELEGRAM_MEDIA_TYPE, TEXT.name());
        ex.getIn().setHeader(TELEGRAM_PARSE_MODE, MARKDOWN);
        ex.getIn().setBody("Hello");
        context().createProducerTemplate().send(endpoint, ex);
        ArgumentCaptor<OutgoingTextMessage> captor = ArgumentCaptor.forClass(OutgoingTextMessage.class);
        Mockito.verify(service).sendMessage(ArgumentMatchers.eq("mock-token"), captor.capture());
        assertEquals("my-id", captor.getValue().getChatId());
        assertEquals("Hello", captor.getValue().getText());
        assertEquals("Markdown", captor.getValue().getParseMode());
    }
}


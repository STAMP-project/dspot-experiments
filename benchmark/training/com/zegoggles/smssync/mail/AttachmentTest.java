package com.zegoggles.smssync.mail;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AttachmentTest {
    @Test
    public void shouldEncodeRFC2231() throws Exception {
        assertThat(Attachment.encodeRFC2231("key", "value")).isEqualTo("; key=value");
        assertThat(Attachment.encodeRFC2231("key", "\"*\u00fcber*")).isEqualTo("; key*=UTF-8''%22%2A%C3%BCber%2A");
    }
}


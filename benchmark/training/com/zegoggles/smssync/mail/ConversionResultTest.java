package com.zegoggles.smssync.mail;


import Headers.DATE;
import com.fsck.k9.mail.Message;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static DataType.SMS;


@RunWith(RobolectricTestRunner.class)
public class ConversionResultTest {
    @Test
    public void emptyResult() throws Exception {
        ConversionResult result = new ConversionResult(SMS);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void shouldAddMessage() throws Exception {
        ConversionResult result = new ConversionResult(SMS);
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getHeader(ArgumentMatchers.anyString())).thenReturn(new String[]{  });
        Map<String, String> map = new HashMap<String, String>();
        result.add(message, map);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getMaxDate()).isEqualTo((-1));
    }

    @Test
    public void shouldAddMessageWithValidDate() throws Exception {
        ConversionResult result = new ConversionResult(SMS);
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getHeader(DATE)).thenReturn(new String[]{ "12345" });
        Map<String, String> map = new HashMap<String, String>();
        result.add(message, map);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getMaxDate()).isEqualTo(12345);
    }

    @Test
    public void shouldAddMessageWithInvalidDate() throws Exception {
        ConversionResult result = new ConversionResult(SMS);
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getHeader(DATE)).thenReturn(new String[]{ "foo" });
        Map<String, String> map = new HashMap<String, String>();
        result.add(message, map);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getMaxDate()).isEqualTo((-1));
    }

    @Test
    public void shouldAddMessageAndRememberMaxDate() throws Exception {
        ConversionResult result = new ConversionResult(SMS);
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getHeader(DATE)).thenReturn(new String[]{ "12345" });
        Map<String, String> map = new HashMap<String, String>();
        result.add(message, map);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getMaxDate()).isEqualTo(12345);
        Message newerMessage = Mockito.mock(Message.class);
        Mockito.when(newerMessage.getHeader(DATE)).thenReturn(new String[]{ "123456789" });
        result.add(newerMessage, map);
        assertThat(result.getMaxDate()).isEqualTo(123456789);
    }
}


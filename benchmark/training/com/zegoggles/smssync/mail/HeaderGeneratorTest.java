package com.zegoggles.smssync.mail;


import CallLog.Calls.DURATION;
import DataType.CALLLOG;
import DataType.MMS;
import DataType.SMS;
import Headers.ADDRESS;
import Headers.BACKUP_TIME;
import Headers.DATATYPE;
import Headers.ID;
import Headers.VERSION;
import Telephony.BaseMmsColumns.MESSAGE_TYPE;
import Telephony.TextBasedSmsColumns.DATE;
import Telephony.TextBasedSmsColumns.PROTOCOL;
import Telephony.TextBasedSmsColumns.READ;
import Telephony.TextBasedSmsColumns.SERVICE_CENTER;
import Telephony.TextBasedSmsColumns.STATUS;
import Telephony.TextBasedSmsColumns.THREAD_ID;
import Telephony.TextBasedSmsColumns.TYPE;
import android.provider.BaseColumns._ID;
import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class HeaderGeneratorTest {
    private HeaderGenerator generator;

    @Test
    public void testShouldGenerateStandardHeaders() throws Exception {
        Message message = new MimeMessage();
        Map<String, String> map = new HashMap<String, String>();
        Date sent = new Date();
        PersonRecord person = new PersonRecord(0, null, null, null);
        generator.setHeaders(message, map, SMS, "1234", person, sent, 0);
        assertThat(Headers.get(message, ADDRESS)).isEqualTo("1234");
        assertThat(Headers.get(message, DATATYPE)).isEqualTo("SMS");
        assertThat(Headers.get(message, BACKUP_TIME)).isNotEmpty();
        assertThat(Headers.get(message, VERSION)).isNotEmpty();
        assertThat(message.getMessageId()).contains("sms-backup-plus.local");
        assertThat(message.getSentDate()).isEqualTo(sent);
        assertThat(message.getReferences()).isNotEmpty();
    }

    @Test
    public void testShouldGenerateSMSHeaders() throws Exception {
        Message message = new MimeMessage();
        Map<String, String> map = new HashMap<String, String>();
        Date sent = new Date();
        PersonRecord person = new PersonRecord(0, null, null, null);
        map.put(_ID, "someId");
        map.put(TYPE, "type");
        map.put(DATE, "date");
        map.put(THREAD_ID, "tid");
        map.put(READ, "read");
        map.put(STATUS, "status");
        map.put(PROTOCOL, "protocol");
        map.put(SERVICE_CENTER, "svc");
        generator.setHeaders(message, map, SMS, "1234", person, sent, 0);
        assertThat(Headers.get(message, ID)).isEqualTo("someId");
        assertThat(Headers.get(message, Headers.TYPE)).isEqualTo("type");
        assertThat(Headers.get(message, Headers.DATE)).isEqualTo("date");
        assertThat(Headers.get(message, Headers.THREAD_ID)).isEqualTo("tid");
        assertThat(Headers.get(message, Headers.READ)).isEqualTo("read");
        assertThat(Headers.get(message, Headers.STATUS)).isEqualTo("status");
        assertThat(Headers.get(message, Headers.PROTOCOL)).isEqualTo("protocol");
        assertThat(Headers.get(message, Headers.SERVICE_CENTER)).isEqualTo("svc");
    }

    @Test
    public void testShouldGenerateCallLogHeaders() throws Exception {
        Message message = new MimeMessage();
        Map<String, String> map = new HashMap<String, String>();
        Date sent = new Date();
        PersonRecord person = new PersonRecord(0, null, null, null);
        map.put(CallLog.Calls._ID, "id");
        map.put(CallLog.Calls.TYPE, "type");
        map.put(DURATION, "duration");
        map.put(CallLog.Calls.DATE, "date");
        generator.setHeaders(message, map, CALLLOG, "1234", person, sent, 0);
        assertThat(Headers.get(message, ID)).isEqualTo("id");
        assertThat(Headers.get(message, Headers.TYPE)).isEqualTo("type");
        assertThat(Headers.get(message, Headers.DURATION)).isEqualTo("duration");
        assertThat(Headers.get(message, Headers.DATE)).isEqualTo("date");
    }

    @Test
    public void testShouldGenerateMMSHeaders() throws Exception {
        Message message = new MimeMessage();
        Map<String, String> map = new HashMap<String, String>();
        Date sent = new Date();
        PersonRecord person = new PersonRecord(0, null, null, null);
        map.put(Telephony.BaseMmsColumns._ID, "id");
        map.put(MESSAGE_TYPE, "type");
        map.put(Telephony.BaseMmsColumns.THREAD_ID, "tid");
        map.put(Telephony.BaseMmsColumns.DATE, "date");
        map.put(Telephony.BaseMmsColumns.READ, "read");
        generator.setHeaders(message, map, MMS, "1234", person, sent, 0);
        assertThat(Headers.get(message, ID)).isEqualTo("id");
        assertThat(Headers.get(message, Headers.TYPE)).isEqualTo("type");
        assertThat(Headers.get(message, Headers.THREAD_ID)).isEqualTo("tid");
        assertThat(Headers.get(message, Headers.READ)).isEqualTo("read");
        assertThat(Headers.get(message, Headers.DATE)).isEqualTo("date");
    }

    @Test
    public void testShouldSetHeadersWithNullAddress() throws Exception {
        Message message = new MimeMessage();
        Map<String, String> map = new HashMap<String, String>();
        Date sent = new Date();
        PersonRecord person = new PersonRecord(0, null, null, null);
        generator.setHeaders(message, map, SMS, null, person, sent, 0);
    }
}


package com.zegoggles.smssync.mail;


import DataType.MMS;
import DataType.SMS;
import Message.RecipientType.TO;
import MimeHeader.HEADER_CONTENT_TRANSFER_ENCODING;
import MmsSupport.MmsDetails;
import Telephony.TextBasedSmsColumns.DATE;
import Telephony.TextBasedSmsColumns.TYPE;
import android.net.Uri;
import android.provider.CallLog.Calls;
import com.fsck.k9.mail.Address;
import com.fsck.k9.mail.Message;
import com.zegoggles.smssync.contacts.ContactGroupIds;
import com.zegoggles.smssync.preferences.AddressStyle;
import com.zegoggles.smssync.preferences.CallLogTypes;
import com.zegoggles.smssync.preferences.DataTypePreferences;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.james.mime4j.util.MimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


@RunWith(RobolectricTestRunner.class)
public class MessageGeneratorTest {
    private MessageGenerator generator;

    @Mock
    private PersonLookup personLookup;

    @Mock
    private HeaderGenerator headerGenerator;

    @Mock
    private MmsSupport mmsSupport;

    @Mock
    private Address me;

    @Mock
    private ContactGroupIds groupIds;

    @Mock
    private DataTypePreferences dataTypePreferences;

    @Test
    public void testShouldReturnNullIfMessageHasNoAddress() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        Message msg = generator.messageForDataType(map, SMS);
        assertThat(msg).isNull();
    }

    @Test
    public void testShouldGenerateSubjectWithNameForSMS() throws Exception {
        PersonRecord record = new PersonRecord(1, "Test Testor", null, null);
        Message msg = generator.messageForDataType(mockMessage("1234", record), SMS);
        assertThat(msg).isNotNull();
        assertThat(msg.getSubject()).isEqualTo("SMS with Test Testor");
    }

    @Test
    public void testShouldGenerateSMSMessageWithCorrectEncoding() throws Exception {
        PersonRecord record = new PersonRecord(1, "Test Testor", null, null);
        Message msg = generator.messageForDataType(mockMessage("1234", record), SMS);
        assertThat(msg.getHeader(HEADER_CONTENT_TRANSFER_ENCODING)).isEqualTo(new String[]{ MimeUtil.ENC_QUOTED_PRINTABLE });
    }

    @Test
    public void testShouldGenerateSubjectWithNameForMMS() throws Exception {
        PersonRecord personRecord = new PersonRecord(1, "Foo Bar", "foo@bar.com", "1234");
        MmsSupport.MmsDetails details = new MmsSupport.MmsDetails(true, "foo", personRecord, new Address("foo@bar.com"));
        Mockito.when(mmsSupport.getDetails(ArgumentMatchers.any(Uri.class), ArgumentMatchers.any(AddressStyle.class))).thenReturn(details);
        Message msg = generator.messageForDataType(mockMessage("1234", personRecord), MMS);
        assertThat(msg).isNotNull();
        assertThat(msg.getSubject()).isEqualTo("SMS with Foo Bar");
    }

    @Test
    public void testShouldGenerateMMSMessageWithCorrectEncoding() throws Exception {
        PersonRecord personRecord = new PersonRecord(1, "Foo Bar", "foo@bar.com", "1234");
        MmsSupport.MmsDetails details = new MmsSupport.MmsDetails(true, "foo", personRecord, new Address("foo@bar.com"));
        Mockito.when(mmsSupport.getDetails(ArgumentMatchers.any(Uri.class), ArgumentMatchers.any(AddressStyle.class))).thenReturn(details);
        Message msg = generator.messageForDataType(mockMessage("1234", personRecord), MMS);
        assertThat(msg.getHeader(HEADER_CONTENT_TRANSFER_ENCODING)).isEqualTo(new String[]{ MimeUtil.ENC_7BIT });
    }

    @Test
    public void testShouldGenerateMessageForCallLogOutgoing() throws Exception {
        PersonRecord record = new PersonRecord((-1), "Test Testor", null, null);
        Message msg = generator.messageForDataType(mockCalllogMessage("1234", Calls.OUTGOING_TYPE, record), DataType.CALLLOG);
        assertThat(msg).isNotNull();
        assertThat(msg.getSubject()).isEqualTo("Call with Test Testor");
        assertThat(msg.getFrom()[0]).isEqualTo(me);
        assertThat(msg.getRecipients(TO)[0].toString()).isEqualTo("Test Testor <unknown.number@unknown.email>");
    }

    @Test
    public void testShouldGenerateMessageForCallLogIncoming() throws Exception {
        PersonRecord record = new PersonRecord((-1), "Test Testor", null, null);
        Message message = generator.messageForDataType(mockCalllogMessage("1234", Calls.INCOMING_TYPE, record), DataType.CALLLOG);
        assertMessage(message);
    }

    @Test
    public void testShouldGenerateMessageForCallLogMissed() throws Exception {
        PersonRecord record = new PersonRecord((-1), "Test Testor", null, null);
        Message message = generator.messageForDataType(mockCalllogMessage("1234", Calls.MISSED_TYPE, record), DataType.CALLLOG);
        assertMessage(message);
    }

    @Test
    public void testShouldGenerateMessageForCallLogIncomingUnknown() throws Exception {
        PersonRecord record = new PersonRecord(0, null, null, "-1");
        Message msg = generator.messageForDataType(mockCalllogMessage("", Calls.INCOMING_TYPE, record), DataType.CALLLOG);
        assertThat(msg).isNotNull();
        assertThat(msg.getSubject()).isEqualTo("Call with Unknown");
        assertThat(msg.getFrom()[0].toString()).isEqualTo("Unknown <unknown.number@unknown.email>");
        assertThat(msg.getRecipients(TO)[0]).isEqualTo(me);
    }

    @Test
    public void testShouldGenerateCallLogMessageWithCorrectEncoding() throws Exception {
        PersonRecord record = new PersonRecord((-1), "Test Testor", null, null);
        Message msg = generator.messageForDataType(mockCalllogMessage("1234", Calls.OUTGOING_TYPE, record), DataType.CALLLOG);
        assertThat(msg.getHeader(HEADER_CONTENT_TRANSFER_ENCODING)).isEqualTo(new String[]{ MimeUtil.ENC_QUOTED_PRINTABLE });
    }

    @Test
    public void testShouldGenerateSubjectWithNameAndNumberForSMS() throws Exception {
        PersonRecord record = new PersonRecord(1, "Test Testor", "test@test.com", "1234");
        Message msg = generator.messageForDataType(mockMessage("1234", record), SMS);
        assertThat(msg).isNotNull();
        assertThat(msg.getSubject()).isEqualTo("SMS with Test Testor");
    }

    @Test
    public void shouldGenerateCorrectFromHeaderWithUsersEmailAddress() throws Exception {
        PersonRecord record = new PersonRecord(1, "Test Testor", "test@test.com", "1234");
        Message msg = generator.messageForDataType(mockMessage("1234", record), SMS);
        assertThat(msg).isNotNull();
        assertThat(msg.getFrom()[0]).isEqualTo(me);
    }

    @Test
    public void shouldGenerateCorrectToHeader() throws Exception {
        PersonRecord record = new PersonRecord(1, "Test Testor", "test@test.com", "1234");
        Message msg = generator.messageForDataType(mockMessage("1234", record), SMS);
        assertThat(msg).isNotNull();
        assertThat(msg.getRecipients(TO)[0].toString()).isEqualTo("Test Testor <test@test.com>");
    }

    @Test
    public void shouldGenerateCorrectHeaders() throws Exception {
        PersonRecord record = new PersonRecord(1, "Test Testor", "test@test.com", "1234");
        Map<String, String> map = mockMessage("1234", record);
        Date date = new Date();
        map.put(DATE, String.valueOf(date.getTime()));
        map.put(TYPE, "0");
        Message msg = generator.messageForDataType(map, SMS);
        assertThat(msg).isNotNull();
        Mockito.verify(headerGenerator).setHeaders(ArgumentMatchers.any(Message.class), ArgumentMatchers.any(Map.class), ArgumentMatchers.eq(SMS), ArgumentMatchers.anyString(), ArgumentMatchers.eq(record), ArgumentMatchers.eq(date), ArgumentMatchers.eq(0));
    }

    @Test
    public void shouldGenerateCorrectToHeaderWhenUserisRecipient() throws Exception {
        PersonRecord record = new PersonRecord(1, "Test Testor", "test@test.com", "1234");
        Map<String, String> map = mockMessage("1234", record);
        map.put(TYPE, "1");
        Message msg = generator.messageForDataType(map, SMS);
        assertThat(msg).isNotNull();
        assertThat(msg.getFrom()[0].toString()).isEqualTo("Test Testor <test@test.com>");
        assertThat(msg.getRecipients(TO)[0]).isEqualTo(me);
    }

    @Test
    public void testShouldUseNumberIfNameIsUnknown() throws Exception {
        PersonRecord record = new PersonRecord((-1), null, null, "1234");
        Message msg = generator.messageForDataType(mockMessage("1234", record), SMS);
        assertThat(msg).isNotNull();
        assertThat(msg.getSubject()).isEqualTo("SMS with 1234");
    }

    @Test
    public void shouldOnlyIncludePeopleFromContactIdsIfSpecified() throws Exception {
        MessageGenerator generator = new MessageGenerator(RuntimeEnvironment.application, me, AddressStyle.NAME, headerGenerator, personLookup, false, groupIds, mmsSupport, CallLogTypes.EVERYTHING, dataTypePreferences);
        PersonRecord record = new PersonRecord(1, "Test Testor", "test@test.com", "1234");
        Map<String, String> map = mockMessage("1234", record);
        map.put(TYPE, "1");
        Mockito.when(groupIds.contains(record)).thenReturn(false);
        assertThat(generator.messageForDataType(map, SMS)).isNull();
        Mockito.when(groupIds.contains(record)).thenReturn(true);
        assertThat(generator.messageForDataType(map, SMS)).isNotNull();
    }
}


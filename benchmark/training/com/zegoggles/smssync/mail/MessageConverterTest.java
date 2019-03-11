package com.zegoggles.smssync.mail;


import CallLog.Calls.CACHED_NAME;
import CallLog.Calls.CACHED_NUMBER_TYPE;
import CallLog.Calls.DURATION;
import CallLog.Calls.NEW;
import CallLog.Calls.NUMBER;
import DataType.SMS;
import Flag.SEEN;
import MarkAsReadTypes.MESSAGE_STATUS;
import MarkAsReadTypes.UNREAD;
import Telephony.TextBasedSmsColumns;
import Telephony.TextBasedSmsColumns.ADDRESS;
import Telephony.TextBasedSmsColumns.BODY;
import Telephony.TextBasedSmsColumns.DATE;
import Telephony.TextBasedSmsColumns.PROTOCOL;
import Telephony.TextBasedSmsColumns.READ;
import Telephony.TextBasedSmsColumns.SERVICE_CENTER;
import Telephony.TextBasedSmsColumns.STATUS;
import Telephony.TextBasedSmsColumns.THREAD_ID;
import Telephony.TextBasedSmsColumns.TYPE;
import android.content.ContentValues;
import android.database.MatrixCursor;
import com.fsck.k9.mail.Address;
import com.fsck.k9.mail.MessagingException;
import com.fsck.k9.mail.internet.MimeMessage;
import com.zegoggles.smssync.contacts.ContactAccessor;
import com.zegoggles.smssync.preferences.AddressStyle;
import com.zegoggles.smssync.preferences.Preferences;
import java.io.ByteArrayInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


@RunWith(RobolectricTestRunner.class)
public class MessageConverterTest {
    private MessageConverter messageConverter;

    @Mock
    private Preferences preferences;

    @Mock
    private PersonLookup personLookup;

    @Mock
    private ContactAccessor contactAccessor;

    @Test(expected = MessagingException.class)
    public void testMessageToContentValuesWithNullMessageThrowsMessagingException() throws Exception {
        messageConverter.messageToContentValues(null);
    }

    @Test(expected = MessagingException.class)
    public void testMessageToContentValuesWithUnknownMessageTypeThrowsException() throws Exception {
        final String message = "Subject: Call with +12121\n" + (((((((((("From: +12121 <+12121@unknown.email>\n" + "To: test@example.com\n") + "MIME-Version: 1.0\n") + "Content-Type: text/plain;\n") + " charset=utf-8\n") + "References: <3j20u1wmbcyik9lw0yaf8bfc.+12121@sms-backup-plus.local>\n") + "Message-ID: <5c0e190205376da44656936fd7d9900c@sms-backup-plus.local>\n") + "X-smssync-datatype: INVALID\n") + "Content-Transfer-Encoding: quoted-printable\n") + "\n") + "Some Text");
        final MimeMessage mimeMessage = MimeMessage.parseMimeMessage(new ByteArrayInputStream(message.getBytes()), true);
        messageConverter.messageToContentValues(mimeMessage);
    }

    @Test
    public void testMessageToContentValuesWithSMS() throws Exception {
        final ContentValues values = messageConverter.messageToContentValues(createSMSMessage());
        assertThat(values.getAsString(ADDRESS)).isEqualTo("+121332");
        assertThat(values.getAsString(TYPE)).isEqualTo("2");
        assertThat(values.getAsString(PROTOCOL)).isNull();
        assertThat(values.getAsString(SERVICE_CENTER)).isNull();
        assertThat(values.getAsString(DATE)).isEqualTo("1420759456762");
        assertThat(values.getAsString(STATUS)).isEqualTo("-1");
        assertThat(values.getAsString(THREAD_ID)).isNull();
        assertThat(values.getAsString(READ)).isEqualTo("1");
        assertThat(values.getAsString(BODY)).isEqualTo("Das?As?");
    }

    @Test
    public void testMessageToContentValuesWithCalllog() throws Exception {
        PersonRecord record = new PersonRecord(1, "The name", "email@foo.com", "+1234");
        Mockito.when(personLookup.lookupPerson("+12121")).thenReturn(record);
        final ContentValues values = messageConverter.messageToContentValues(createCallLogMessage());
        assertThat(values.getAsString(NUMBER)).isEqualTo("+12121");
        assertThat(values.getAsString(CallLog.Calls.TYPE)).isEqualTo("3");
        assertThat(values.getAsString(CallLog.Calls.DATE)).isEqualTo("1419163218194");
        assertThat(values.getAsLong(DURATION)).isEqualTo(44L);
        assertThat(values.getAsInteger(NEW)).isEqualTo(0);
        assertThat(values.getAsString(CACHED_NAME)).isEqualTo("The name");
        assertThat(values.getAsInteger(CACHED_NUMBER_TYPE)).isEqualTo((-2));
    }

    @Test
    public void testMessageToContentValuesWithCalllogFromUnknownPerson() throws Exception {
        PersonRecord record = new PersonRecord((-1), null, null, null);
        Mockito.when(personLookup.lookupPerson("+12121")).thenReturn(record);
        final ContentValues values = messageConverter.messageToContentValues(createCallLogMessage());
        assertThat(values.containsKey(CACHED_NAME)).isFalse();
        assertThat(values.containsKey(CACHED_NUMBER_TYPE)).isFalse();
    }

    @Test
    public void testConvertMessagesSeenFlagFromMessageStatusWithSMS() throws Exception {
        MatrixCursor cursor = new MatrixCursor(new String[]{ TextBasedSmsColumns.ADDRESS, TextBasedSmsColumns.READ });
        cursor.addRow(new Object[]{ "foo", "0" });
        cursor.addRow(new Object[]{ "foo", "1" });
        cursor.moveToFirst();
        PersonRecord record = Mockito.mock(PersonRecord.class);
        Mockito.when(personLookup.lookupPerson(ArgumentMatchers.any(String.class))).thenReturn(record);
        Mockito.when(record.getAddress(ArgumentMatchers.any(AddressStyle.class))).thenReturn(new Address("foo"));
        Mockito.when(preferences.getMarkAsReadType()).thenReturn(MESSAGE_STATUS);
        messageConverter = new MessageConverter(RuntimeEnvironment.application, preferences, "foo@example.com", personLookup, contactAccessor);
        ConversionResult res = messageConverter.convertMessages(cursor, SMS);
        assertThat(res.getMessages().get(0).isSet(SEEN)).isFalse();
        cursor.moveToNext();
        res = messageConverter.convertMessages(cursor, SMS);
        assertThat(res.getMessages().get(0).isSet(SEEN)).isTrue();
    }

    @Test
    public void testConvertMessagesSeenFlagUnreadWithSMS() throws Exception {
        MatrixCursor cursor = new MatrixCursor(new String[]{ TextBasedSmsColumns.ADDRESS, TextBasedSmsColumns.READ });
        cursor.addRow(new Object[]{ "foo", "0" });
        cursor.addRow(new Object[]{ "foo", "1" });
        cursor.moveToFirst();
        PersonRecord record = Mockito.mock(PersonRecord.class);
        Mockito.when(personLookup.lookupPerson(ArgumentMatchers.any(String.class))).thenReturn(record);
        Mockito.when(record.getAddress(ArgumentMatchers.any(AddressStyle.class))).thenReturn(new Address("foo"));
        Mockito.when(preferences.getMarkAsReadType()).thenReturn(UNREAD);
        messageConverter = new MessageConverter(RuntimeEnvironment.application, preferences, "foo@example.com", personLookup, contactAccessor);
        ConversionResult res = messageConverter.convertMessages(cursor, SMS);
        assertThat(res.getMessages().get(0).isSet(SEEN)).isFalse();
        cursor.moveToNext();
        res = messageConverter.convertMessages(cursor, SMS);
        assertThat(res.getMessages().get(0).isSet(SEEN)).isFalse();
    }

    @Test
    public void testConvertMessagesSeenFlagReadWithSMS() throws Exception {
        MatrixCursor cursor = new MatrixCursor(new String[]{ TextBasedSmsColumns.ADDRESS, TextBasedSmsColumns.READ });
        cursor.addRow(new Object[]{ "foo", "0" });
        cursor.addRow(new Object[]{ "foo", "1" });
        cursor.moveToFirst();
        PersonRecord record = Mockito.mock(PersonRecord.class);
        Mockito.when(personLookup.lookupPerson(ArgumentMatchers.any(String.class))).thenReturn(record);
        Mockito.when(record.getAddress(ArgumentMatchers.any(AddressStyle.class))).thenReturn(new Address("foo"));
        Mockito.when(preferences.getMarkAsReadType()).thenReturn(MarkAsReadTypes.READ);
        messageConverter = new MessageConverter(RuntimeEnvironment.application, preferences, "foo@example.com", personLookup, contactAccessor);
        ConversionResult res = messageConverter.convertMessages(cursor, SMS);
        assertThat(res.getMessages().get(0).isSet(SEEN)).isTrue();
        cursor.moveToNext();
        res = messageConverter.convertMessages(cursor, SMS);
        assertThat(res.getMessages().get(0).isSet(SEEN)).isTrue();
    }
}


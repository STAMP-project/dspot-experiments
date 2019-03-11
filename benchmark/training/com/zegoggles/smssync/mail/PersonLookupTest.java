package com.zegoggles.smssync.mail;


import ContactsContract.CommonDataKinds.Email;
import ContactsContract.CommonDataKinds.Email.CONTENT_URI;
import android.content.ContentResolver;
import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class PersonLookupTest {
    private PersonLookup lookup;

    @Mock
    private ContentResolver resolver;

    @Test
    public void shouldLookupUnknownPerson() throws Exception {
        Mockito.when(resolver.query(ArgumentMatchers.any(Uri.class), ArgumentMatchers.any(String[].class), ((String) (ArgumentMatchers.isNull())), ((String[]) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())))).thenReturn(null);
        PersonRecord record = lookup.lookupPerson("1234");
        assertThat(record).isNotNull();
        assertThat(record.isUnknown()).isTrue();
        assertThat(record.getEmail()).isEqualTo("1234@unknown.email");
        assertThat(record.getName()).isEqualTo("1234");
    }

    @Test
    public void shouldLookupExistingPerson() throws Exception {
        Mockito.when(resolver.query(ArgumentMatchers.eq(Uri.parse("content://com.android.contacts/phone_lookup/1234")), ArgumentMatchers.any(String[].class), ((String) (ArgumentMatchers.isNull())), ((String[]) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())))).thenReturn(name("Testor Test"));
        PersonRecord record = lookup.lookupPerson("1234");
        assertThat(record).isNotNull();
        assertThat(record.isUnknown()).isFalse();
        assertThat(record.getEmail()).isEqualTo("1234@unknown.email");
        assertThat(record.getName()).isEqualTo("Testor Test");
    }

    @Test
    public void shouldLookupExistingPersonWithEmail() throws Exception {
        Mockito.when(resolver.query(ArgumentMatchers.eq(Uri.parse("content://com.android.contacts/phone_lookup/1234")), ArgumentMatchers.any(String[].class), ((String) (ArgumentMatchers.isNull())), ((String[]) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())))).thenReturn(name("Testor Test"));
        Mockito.when(resolver.query(CONTENT_URI, new String[]{ Email.DATA }, ((Email.CONTACT_ID) + " = ?"), new String[]{ String.valueOf(1) }, ((Email.IS_PRIMARY) + " DESC"))).thenReturn(email("foo@test.com"));
        PersonRecord record = lookup.lookupPerson("1234");
        assertThat(record).isNotNull();
        assertThat(record.getEmail()).isEqualTo("foo@test.com");
    }

    @Test
    public void shouldLookupExistingPersonUsingGmailAsPrimaryEmail() throws Exception {
        Mockito.when(resolver.query(ArgumentMatchers.eq(Uri.parse("content://com.android.contacts/phone_lookup/1234")), ArgumentMatchers.any(String[].class), ((String) (ArgumentMatchers.isNull())), ((String[]) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())))).thenReturn(name("Testor Test"));
        Mockito.when(resolver.query(CONTENT_URI, new String[]{ Email.DATA }, ((Email.CONTACT_ID) + " = ?"), new String[]{ String.valueOf(1) }, ((Email.IS_PRIMARY) + " DESC"))).thenReturn(email("foo@test.com", "foo@gmail.com"));
        PersonRecord record = lookup.lookupPerson("1234");
        assertThat(record).isNotNull();
        assertThat(record.getEmail()).isEqualTo("foo@gmail.com");
    }

    @Test
    public void shouldIgnoreIllegalArgumentException() {
        // https://github.com/jberkel/sms-backup-plus/issues/870
        Mockito.when(resolver.query(ArgumentMatchers.any(Uri.class), ArgumentMatchers.any(String[].class), ((String) (ArgumentMatchers.isNull())), ((String[]) (ArgumentMatchers.isNull())), ((String) (ArgumentMatchers.isNull())))).thenThrow(new IllegalArgumentException("column 'data1' does not exist"));
        PersonRecord record = lookup.lookupPerson("1234");
        assertThat(record.isUnknown()).isTrue();
    }
}


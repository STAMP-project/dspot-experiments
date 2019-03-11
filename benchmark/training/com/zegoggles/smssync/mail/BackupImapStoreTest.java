package com.zegoggles.smssync.mail;


import android.annotation.SuppressLint;
import com.fsck.k9.mail.MessagingException;
import com.fsck.k9.mail.ssl.DefaultTrustedSocketFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


@RunWith(RobolectricTestRunner.class)
@SuppressLint("AuthLeak")
public class BackupImapStoreTest {
    @Test
    public void shouldTestForValidUri() throws Exception {
        assertThat(BackupImapStore.isValidUri("imap+ssl+://xoauth:foooo@imap.gmail.com:993")).isTrue();
        assertThat(BackupImapStore.isValidUri("imap://xoauth:foooo@imap.gmail.com")).isTrue();
        assertThat(BackupImapStore.isValidUri("imap+ssl+://xoauth:user:token@:993")).isFalse();
        assertThat(BackupImapStore.isValidUri("imap+ssl://user%40domain:password@imap.gmail.com:993")).isFalse();
        assertThat(BackupImapStore.isValidUri("imap+tls+://user:password@imap.gmail.com:993")).isTrue();
        assertThat(BackupImapStore.isValidUri("imap+tls://user:password@imap.gmail.com:993")).isFalse();
        assertThat(BackupImapStore.isValidUri("imap://user:password@imap.gmail.com:993")).isTrue();
        assertThat(BackupImapStore.isValidUri("http://xoauth:foooo@imap.gmail.com:993")).isFalse();
    }

    @Test
    public void shouldTestForValidFolder() throws Exception {
        assertThat(BackupImapStore.isValidImapFolder(null)).isFalse();
        assertThat(BackupImapStore.isValidImapFolder("")).isFalse();
        assertThat(BackupImapStore.isValidImapFolder("foo")).isTrue();
        assertThat(BackupImapStore.isValidImapFolder("foo bar")).isTrue();
        assertThat(BackupImapStore.isValidImapFolder(" foo")).isFalse();
        assertThat(BackupImapStore.isValidImapFolder("foo ")).isFalse();
        assertThat(BackupImapStore.isValidImapFolder("foo/nested")).isTrue();
        assertThat(BackupImapStore.isValidImapFolder("/foo/nested")).isFalse();
    }

    @Test
    public void testAccountHasStoreUri() throws Exception {
        String uri = "imap://xoauth:foooo@imap.gmail.com";
        BackupImapStore store = new BackupImapStore(RuntimeEnvironment.application, uri, false);
        assertThat(store.getStoreUri()).isEqualTo(uri);
    }

    @Test
    public void testShouldCreateCorrectTrustFactoryForTrustedSSLUrl() throws Exception {
        String uri = "imap+ssl+://xoauth:foooo@imap.gmail.com";
        BackupImapStore store = new BackupImapStore(RuntimeEnvironment.application, uri, false);
        assertThat(store.getTrustedSocketFactory()).isInstanceOf(DefaultTrustedSocketFactory.class);
    }

    @Test
    public void testShouldCreateCorrectTrustFactoryForTrustAllSSLUrl() throws Exception {
        String uri = "imap+ssl://xoauth:foooo@imap.gmail.com";
        BackupImapStore store = new BackupImapStore(RuntimeEnvironment.application, uri, true);
        assertThat(store.getTrustedSocketFactory()).isInstanceOf(AllTrustedSocketFactory.class);
    }

    @Test
    public void testShouldCreateCorrectTrustFactoryForTrustedTLSUrl() throws Exception {
        String uri = "imap+tls+://xoauth:foooo@imap.gmail.com";
        BackupImapStore store = new BackupImapStore(RuntimeEnvironment.application, uri, false);
        assertThat(store.getTrustedSocketFactory()).isInstanceOf(DefaultTrustedSocketFactory.class);
    }

    @Test
    public void shouldHaveToStringWithObfuscatedStoreURI() throws Exception {
        BackupImapStore store = new BackupImapStore(RuntimeEnvironment.application, "imap://xoauth:foooo@imap.gmail.com", false);
        assertThat(store.getStoreUriForLogging()).isEqualTo("imap://xoauth:XXXXX@imap.gmail.com");
    }

    @Test
    public void shouldHaveToStringWithObfuscatedStoreURIWithPort() throws Exception {
        BackupImapStore store = new BackupImapStore(RuntimeEnvironment.application, "imap://xoauth:foooo@imap.gmail.com:456", false);
        assertThat(store.getStoreUriForLogging()).isEqualTo("imap://xoauth:XXXXX@imap.gmail.com:456");
    }

    @Test(expected = MessagingException.class)
    public void shouldThrowExceptionIfUsernameIsMissing() throws Exception {
        new BackupImapStore(RuntimeEnvironment.application, "imap://imap.gmail.com:1234", false);
    }

    @Test(expected = MessagingException.class)
    public void shouldThrowExceptionIfPasswordIsMissing() throws Exception {
        new BackupImapStore(RuntimeEnvironment.application, "imap://plain:foo:@imap.gmail.com:1234", false);
    }

    @Test
    public void shouldHaveToStringWithStoreUriForLogging() throws Exception {
        BackupImapStore store = new BackupImapStore(RuntimeEnvironment.application, "imap://xoauth:foooo@imap.gmail.com", false);
        assertThat(store.toString()).isEqualTo("BackupImapStore{uri=imap://xoauth:XXXXX@imap.gmail.com}");
    }
}


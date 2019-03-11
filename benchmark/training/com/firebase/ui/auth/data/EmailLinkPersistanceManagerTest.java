package com.firebase.ui.auth.data;


import RuntimeEnvironment.application;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.testhelpers.TestConstants;
import com.firebase.ui.auth.util.data.EmailLinkPersistenceManager;
import com.firebase.ui.auth.util.data.EmailLinkPersistenceManager.SessionRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link EmailLinkPersistenceManager}.
 */
@RunWith(RobolectricTestRunner.class)
public class EmailLinkPersistanceManagerTest {
    EmailLinkPersistenceManager mPersistenceManager;

    @Test
    public void testSaveAndRetrieveEmailForLink() {
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        SessionRecord sessionRecord = mPersistenceManager.retrieveSessionRecord(application);
        assertThat(sessionRecord.getEmail()).isEqualTo(TestConstants.EMAIL);
        assertThat(sessionRecord.getSessionId()).isEqualTo(TestConstants.SESSION_ID);
        assertThat(sessionRecord.getAnonymousUserId()).isEqualTo(TestConstants.UID);
    }

    @Test
    public void testSaveAndRetrieveIdpResonseForLinking_saveEmailFirst() {
        IdpResponse response = buildIdpResponse();
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        mPersistenceManager.saveIdpResponseForLinking(application, response);
        SessionRecord sessionRecord = mPersistenceManager.retrieveSessionRecord(application);
        assertThat(sessionRecord.getEmail()).isEqualTo(TestConstants.EMAIL);
        assertThat(sessionRecord.getSessionId()).isEqualTo(TestConstants.SESSION_ID);
        assertThat(sessionRecord.getAnonymousUserId()).isEqualTo(TestConstants.UID);
        assertThat(sessionRecord.getIdpResponseForLinking()).isEqualTo(response);
    }

    @Test
    public void testSaveAndRetrieveIdpResonseForLinking_noSavedEmail_expectNothingSaved() {
        IdpResponse response = buildIdpResponse();
        mPersistenceManager.saveIdpResponseForLinking(application, response);
        SessionRecord sessionRecord = mPersistenceManager.retrieveSessionRecord(application);
        assertThat(sessionRecord).isNull();
    }
}


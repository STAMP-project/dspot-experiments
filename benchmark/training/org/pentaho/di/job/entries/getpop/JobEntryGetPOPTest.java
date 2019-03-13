/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.job.entries.getpop;


import Flag.SEEN;
import JobEntryGetPOP.FOLDER_ATTACHMENTS;
import JobEntryGetPOP.FOLDER_OUTPUT;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.Job;
import org.pentaho.di.utils.TestUtils;


public class JobEntryGetPOPTest {
    @Mock
    MailConnection mailConn;

    @Mock
    Job parentJob;

    @Mock
    Message message;

    JobEntryGetPOP entry = new JobEntryGetPOP();

    /**
     * PDI-10942 - Job get emails JobEntry does not mark emails as 'read' when load emails content.
     *
     * Test that we always open remote folder in rw mode, and after email attachment is loaded email is marked as read.
     * Set for openFolder rw mode if this is pop3.
     *
     * @throws KettleException
     * 		
     * @throws MessagingException
     * 		
     */
    @Test
    public void testFetchOneFolderModePop3() throws MessagingException, KettleException {
        entry.fetchOneFolder(mailConn, true, "junitImapFolder", "junitRealOutputFolder", "junitTargetAttachmentFolder", "junitRealMoveToIMAPFolder", "junitRealFilenamePattern", 0, Mockito.mock(SimpleDateFormat.class));
        Mockito.verify(mailConn).openFolder(true);
        Mockito.verify(message).setFlag(SEEN, true);
    }

    /**
     * PDI-10942 - Job get emails JobEntry does not mark emails as 'read' when load emails content.
     *
     * Test that we always open remote folder in rw mode, and after email attachment is loaded email is marked as read.
     * protocol IMAP and default remote folder is overridden
     *
     * @throws KettleException
     * 		
     * @throws MessagingException
     * 		
     */
    @Test
    public void testFetchOneFolderModeIMAPWithNonDefFolder() throws MessagingException, KettleException {
        entry.fetchOneFolder(mailConn, false, "junitImapFolder", "junitRealOutputFolder", "junitTargetAttachmentFolder", "junitRealMoveToIMAPFolder", "junitRealFilenamePattern", 0, Mockito.mock(SimpleDateFormat.class));
        Mockito.verify(mailConn).openFolder("junitImapFolder", true);
        Mockito.verify(message).setFlag(SEEN, true);
    }

    /**
     * PDI-10942 - Job get emails JobEntry does not mark emails as 'read' when load emails content.
     *
     * Test that we always open remote folder in rw mode, and after email attachment is loaded email is marked as read.
     * protocol IMAP and default remote folder is NOT overridden
     *
     * @throws KettleException
     * 		
     * @throws MessagingException
     * 		
     */
    @Test
    public void testFetchOneFolderModeIMAPWithIsDefFolder() throws MessagingException, KettleException {
        entry.fetchOneFolder(mailConn, false, null, "junitRealOutputFolder", "junitTargetAttachmentFolder", "junitRealMoveToIMAPFolder", "junitRealFilenamePattern", 0, Mockito.mock(SimpleDateFormat.class));
        Mockito.verify(mailConn).openFolder(true);
        Mockito.verify(message).setFlag(SEEN, true);
    }

    /**
     * PDI-11943 - Get Mail Job Entry: Attachments folder not created
     *
     * Test that the Attachments folder is created when the entry is
     * configured to save attachments and messages in the same folder
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateSameAttachmentsFolder() throws IOException {
        File attachmentsDir = new File(TestUtils.createTempDir());
        attachmentsDir.deleteOnExit();
        entry.setCreateLocalFolder(true);
        entry.setSaveAttachment(true);
        entry.setOutputDirectory(attachmentsDir.getAbsolutePath());
        entry.setDifferentFolderForAttachment(false);
        String outputFolderName = "";
        String attachmentsFolderName = "";
        try {
            outputFolderName = entry.createOutputDirectory(FOLDER_OUTPUT);
            attachmentsFolderName = entry.createOutputDirectory(FOLDER_ATTACHMENTS);
        } catch (Exception e) {
            Assert.fail(("Could not create folder " + (e.getLocalizedMessage())));
        }
        Assert.assertTrue("Output Folder should be a local path", (!(Utils.isEmpty(outputFolderName))));
        Assert.assertTrue("Attachment Folder should be a local path", (!(Utils.isEmpty(attachmentsFolderName))));
        Assert.assertTrue("Output and Attachment Folder should match", outputFolderName.equals(attachmentsFolderName));
    }

    /**
     * PDI-11943 - Get Mail Job Entry: Attachments folder not created
     *
     * Test that the Attachments folder is created when the entry is
     * configured to save attachments and messages in different folders
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateDifferentAttachmentsFolder() throws IOException {
        File outputDir = new File(TestUtils.createTempDir());
        File attachmentsDir = new File(TestUtils.createTempDir());
        entry.setCreateLocalFolder(true);
        entry.setSaveAttachment(true);
        entry.setOutputDirectory(outputDir.getAbsolutePath());
        entry.setDifferentFolderForAttachment(true);
        entry.setAttachmentFolder(attachmentsDir.getAbsolutePath());
        String outputFolderName = "";
        String attachmentsFolderName = "";
        try {
            outputFolderName = entry.createOutputDirectory(FOLDER_OUTPUT);
            attachmentsFolderName = entry.createOutputDirectory(FOLDER_ATTACHMENTS);
        } catch (Exception e) {
            Assert.fail(("Could not create folder: " + (e.getLocalizedMessage())));
        }
        Assert.assertTrue("Output Folder should be a local path", (!(Utils.isEmpty(outputFolderName))));
        Assert.assertTrue("Attachment Folder should be a local path", (!(Utils.isEmpty(attachmentsFolderName))));
        Assert.assertFalse("Output and Attachment Folder should not match", outputFolderName.equals(attachmentsFolderName));
    }

    /**
     * PDI-11943 - Get Mail Job Entry: Attachments folder not created
     *
     * Test that the Attachments folder is not created when the entry is
     * configured to not create folders
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFolderIsNotCreatedWhenCreateFolderSettingIsDisabled() throws IOException {
        File outputDir = new File(TestUtils.createTempDir());
        File attachmentsDir = new File(TestUtils.createTempDir());
        // The folders already exist from TestUtils.  Delete them so they don't exist during the test
        outputDir.delete();
        attachmentsDir.delete();
        entry.setCreateLocalFolder(false);
        entry.setSaveAttachment(true);
        entry.setOutputDirectory(outputDir.getAbsolutePath());
        entry.setDifferentFolderForAttachment(true);
        entry.setAttachmentFolder(attachmentsDir.getAbsolutePath());
        try {
            entry.createOutputDirectory(FOLDER_OUTPUT);
            Assert.fail("A KettleException should have been thrown");
        } catch (Exception e) {
            if (e instanceof KettleException) {
                Assert.assertTrue("Output Folder should not be created", BaseMessages.getString(JobEntryGetPOP.class, "JobGetMailsFromPOP.Error.OutputFolderNotExist", outputDir.getAbsolutePath()).equals(Const.trim(e.getMessage())));
            } else {
                Assert.fail(("Output Folder should not have been created: " + (e.getLocalizedMessage())));
            }
        }
        try {
            entry.createOutputDirectory(FOLDER_ATTACHMENTS);
            Assert.fail("A KettleException should have been thrown");
        } catch (Exception e) {
            if (e instanceof KettleException) {
                Assert.assertTrue("Output Folder should not be created", BaseMessages.getString(JobEntryGetPOP.class, "JobGetMailsFromPOP.Error.AttachmentFolderNotExist", attachmentsDir.getAbsolutePath()).equals(Const.trim(e.getMessage())));
            } else {
                Assert.fail(("Attachments Folder should not have been created: " + (e.getLocalizedMessage())));
            }
        }
    }

    /**
     * PDI-14305 - Get Mails (POP3/IMAP) Not substituting environment variables for target directories
     *
     * Test that environment variables are appropriately substituted when creating output and attachment folders
     */
    @Test
    public void testEnvVariablesAreSubstitutedForFolders() {
        // create variables and add them to the variable space
        String outputVariableName = "myOutputVar";
        String outputVariableValue = "myOutputFolder";
        String attachmentVariableName = "myAttachmentVar";
        String attachmentVariableValue = "myOutputFolder";
        entry.setVariable(outputVariableName, outputVariableValue);
        entry.setVariable(attachmentVariableName, attachmentVariableValue);
        // create temp directories for testing using variable value
        String tempDirBase = TestUtils.createTempDir();
        File outputDir = new File(tempDirBase, outputVariableValue);
        outputDir.mkdir();
        File attachmentDir = new File(tempDirBase, attachmentVariableValue);
        attachmentDir.mkdir();
        // set output and attachment folders to path with variable
        String outputDirWithVariable = (((tempDirBase + (File.separator)) + "${") + outputVariableName) + "}";
        String attachmentDirWithVariable = (((tempDirBase + (File.separator)) + "${") + attachmentVariableName) + "}";
        entry.setOutputDirectory(outputDirWithVariable);
        entry.setAttachmentFolder(attachmentDirWithVariable);
        // directly test environment substitute functions
        Assert.assertTrue("Error in Direct substitute test for output directory", outputDir.toString().equals(entry.getRealOutputDirectory()));
        Assert.assertTrue("Error in Direct substitute test for  attachment directory", attachmentDir.toString().equals(entry.getRealAttachmentFolder()));
        // test environment substitute for output dir via createOutputDirectory method
        try {
            String outputRes = entry.createOutputDirectory(FOLDER_OUTPUT);
            Assert.assertTrue("Variables not working in createOutputDirectory: output directory", outputRes.equals(outputDir.toString()));
        } catch (Exception e) {
            Assert.fail("Unexpected exception when calling createOutputDirectory for output directory");
        }
        // test environment substitute for attachment dir via createOutputDirectory method
        try {
            String attachOutputRes = entry.createOutputDirectory(FOLDER_ATTACHMENTS);
            Assert.assertTrue("Variables not working in createOutputDirectory: attachment with options false", attachOutputRes.equals(outputDir.toString()));
            // set options that trigger alternate path for FOLDER_ATTACHMENTS option
            entry.setSaveAttachment(true);
            entry.setDifferentFolderForAttachment(true);
            String attachRes = entry.createOutputDirectory(FOLDER_ATTACHMENTS);
            Assert.assertTrue("Variables not working in createOutputDirectory: attachment with options true", attachRes.equals(outputDir.toString()));
        } catch (Exception e) {
            Assert.fail("Unexpected exception when calling createOutputDirectory for attachment directory");
        }
    }
}


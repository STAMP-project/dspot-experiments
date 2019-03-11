/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.internal;


import NativeAppCallAttachmentStore.ATTACHMENTS_DIR_NAME;
import NativeAppCallAttachmentStore.Attachment;
import com.facebook.FacebookTestCase;
import java.io.File;
import java.util.List;
import java.util.UUID;
import junit.framework.Assert;
import org.junit.Test;


public class NativeAppCallAttachmentStoreTest extends FacebookTestCase {
    private static final UUID CALL_ID = UUID.randomUUID();

    private static final String ATTACHMENT_NAME = "hello";

    @Test
    public void testAddAttachmentsForCallWithNullCallId() throws Exception {
        try {
            List<NativeAppCallAttachmentStore.Attachment> attachments = createAttachments(null, createBitmap());
            NativeAppCallAttachmentStore.addAttachments(attachments);
            Assert.fail("expected exception");
        } catch (NullPointerException ex) {
            Assert.assertTrue(ex.getMessage().contains("callId"));
        }
    }

    @Test
    public void testAddAttachmentsForCallWithNullBitmap() throws Exception {
        try {
            List<NativeAppCallAttachmentStore.Attachment> attachments = createAttachments(NativeAppCallAttachmentStoreTest.CALL_ID, null);
            NativeAppCallAttachmentStore.addAttachments(attachments);
            Assert.fail("expected exception");
        } catch (NullPointerException ex) {
            Assert.assertTrue(ex.getMessage().contains("attachmentBitmap"));
        }
    }

    @Test
    public void testGetAttachmentsDirectory() throws Exception {
        File dir = NativeAppCallAttachmentStore.getAttachmentsDirectory();
        Assert.assertNotNull(dir);
        Assert.assertTrue(dir.getAbsolutePath().contains(ATTACHMENTS_DIR_NAME));
    }

    @Test
    public void testGetAttachmentsDirectoryForCall() throws Exception {
        NativeAppCallAttachmentStore.ensureAttachmentsDirectoryExists();
        File dir = NativeAppCallAttachmentStore.getAttachmentsDirectoryForCall(NativeAppCallAttachmentStoreTest.CALL_ID, false);
        Assert.assertNotNull(dir);
        Assert.assertTrue(dir.getAbsolutePath().contains(ATTACHMENTS_DIR_NAME));
        Assert.assertTrue(dir.getAbsolutePath().contains(NativeAppCallAttachmentStoreTest.CALL_ID.toString()));
    }

    @Test
    public void testGetAttachmentFile() throws Exception {
        NativeAppCallAttachmentStore.ensureAttachmentsDirectoryExists();
        File dir = NativeAppCallAttachmentStore.getAttachmentFile(NativeAppCallAttachmentStoreTest.CALL_ID, NativeAppCallAttachmentStoreTest.ATTACHMENT_NAME, false);
        Assert.assertNotNull(dir);
        Assert.assertTrue(dir.getAbsolutePath().contains(ATTACHMENTS_DIR_NAME));
        Assert.assertTrue(dir.getAbsolutePath().contains(NativeAppCallAttachmentStoreTest.CALL_ID.toString()));
        Assert.assertTrue(dir.getAbsolutePath().contains(NativeAppCallAttachmentStoreTest.ATTACHMENT_NAME.toString()));
    }
}


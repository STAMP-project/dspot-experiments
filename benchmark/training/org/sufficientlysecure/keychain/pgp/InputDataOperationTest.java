/**
 * Copyright (C) 2014 Vincent Breitmoser <v.breitmoser@mugenguild.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sufficientlysecure.keychain.pgp;


import LogType.MSG_DATA_MIME_CHARSET_FAULTY;
import LogType.MSG_DATA_MIME_CHARSET_GUESS;
import LogType.MSG_DATA_MIME_CHARSET_UNKNOWN;
import LogType.MSG_DATA_MIME_NONE;
import RuntimeEnvironment.application;
import TemporaryFileProvider.CONTENT_URI;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openintents.openpgp.OpenPgpMetadata;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.TestHelpers;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.operations.InputDataOperation;
import org.sufficientlysecure.keychain.operations.results.InputDataResult;
import org.sufficientlysecure.keychain.service.InputDataParcel;
import org.sufficientlysecure.keychain.service.input.CryptoInputParcel;


@RunWith(KeychainTestRunner.class)
public class InputDataOperationTest {
    public static final Uri FAKE_CONTENT_INPUT_URI_1 = Uri.parse("content://fake/1");

    static PrintStream oldShadowStream;

    @Test
    public void testMimeDecoding() throws Exception {
        String mimeMail = "Content-Type: multipart/mixed; boundary=\"=-26BafqxfXmhVNMbYdoIi\"\n" + ((((((((((((((("\n" + "--=-26BafqxfXmhVNMbYdoIi\n") + "Content-Type: text/plain; charset=utf-8\n") + "Content-Transfer-Encoding: quoted-printable\n") + "Content-Disposition: attachment; filename=data.txt\n") + "\n") + "message part 1\n") + "\n") + "--=-26BafqxfXmhVNMbYdoIi\n") + "Content-Type: text/testvalue; charset=iso-8859-1\n") + "Content-Description: Dummy content description\n") + "\n") + "message part 2.1\n") + "message part 2.2\n") + "\n") + "--=-26BafqxfXmhVNMbYdoIi--");
        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream outStream2 = new ByteArrayOutputStream();
        ContentResolver mockResolver = Mockito.mock(ContentResolver.class);
        // fake openOutputStream first and second
        Mockito.when(mockResolver.openOutputStream(ArgumentMatchers.any(Uri.class), ArgumentMatchers.eq("w"))).thenReturn(outStream1, outStream2);
        // fake openInputStream
        Uri fakeInputUri = Uri.parse("content://fake/1");
        Mockito.when(mockResolver.openInputStream(fakeInputUri)).thenReturn(new ByteArrayInputStream(mimeMail.getBytes()));
        Uri fakeOutputUri1 = Uri.parse("content://fake/out/1");
        Uri fakeOutputUri2 = Uri.parse("content://fake/out/2");
        Mockito.when(mockResolver.insert(ArgumentMatchers.eq(CONTENT_URI), ArgumentMatchers.any(ContentValues.class))).thenReturn(fakeOutputUri1, fakeOutputUri2);
        // application which returns mockresolver
        Application spyApplication = Mockito.spy(application);
        Mockito.when(spyApplication.getContentResolver()).thenReturn(mockResolver);
        InputDataOperation op = new InputDataOperation(spyApplication, KeyWritableRepository.create(application), null);
        InputDataParcel input = InputDataParcel.createInputDataParcel(fakeInputUri, null);
        InputDataResult result = op.execute(input, CryptoInputParcel.createCryptoInputParcel());
        // must be successful, no verification, have two output URIs
        Assert.assertTrue(result.success());
        Assert.assertNull(result.mDecryptVerifyResult);
        ArrayList<Uri> outUris = result.getOutputUris();
        Assert.assertEquals("must have two output URIs", 2, outUris.size());
        Assert.assertEquals("first uri must be the one we provided", fakeOutputUri1, outUris.get(0));
        Mockito.verify(mockResolver).openOutputStream(result.getOutputUris().get(0), "w");
        Assert.assertEquals("second uri must be the one we provided", fakeOutputUri2, outUris.get(1));
        Mockito.verify(mockResolver).openOutputStream(result.getOutputUris().get(1), "w");
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "data.txt");
        contentValues.put("mimetype", "text/plain");
        Mockito.verify(mockResolver).insert(ArgumentMatchers.eq(CONTENT_URI), TestHelpers.cvContains(contentValues));
        contentValues.put("name", ((String) (null)));
        contentValues.put("mimetype", "text/testvalue");
        Mockito.verify(mockResolver).insert(ArgumentMatchers.eq(CONTENT_URI), TestHelpers.cvContains(contentValues));
        // quoted-printable returns windows style line endings for some reason?
        Assert.assertEquals("first part must have expected content", "message part 1\r\n", new String(outStream1.toByteArray()));
        Assert.assertEquals("second part must have expected content", "message part 2.1\nmessage part 2.2\n", new String(outStream2.toByteArray()));
        OpenPgpMetadata metadata = result.mMetadata.get(0);
        Assert.assertEquals("text/plain", metadata.getMimeType());
        Assert.assertEquals("utf-8", metadata.getCharset());
        metadata = result.mMetadata.get(1);
        Assert.assertEquals("text/testvalue", metadata.getMimeType());
        Assert.assertEquals("iso-8859-1", metadata.getCharset());
    }

    @Test
    public void testMimeDecodingExplicitFaultyCharset() throws Exception {
        String mimeContent = "Content-Type: text/plain; charset=utf-8\n" + ("\n" + "message with binary data in it\n");
        byte[] data = mimeContent.getBytes();
        data[60] = ((byte) (195));
        data[61] = ((byte) (40));
        InputDataResult result = runSimpleDataInputOperation(data);
        // must be successful, no verification, have two output URIs
        Assert.assertTrue(result.success());
        Assert.assertNull(result.mDecryptVerifyResult);
        OpenPgpMetadata metadata = result.mMetadata.get(0);
        Assert.assertEquals("text/plain", metadata.getMimeType());
        Assert.assertEquals("charset should be set since it was explicitly specified", "utf-8", metadata.getCharset());
        Assert.assertTrue("faulty charset should have been detected", result.getLog().containsType(MSG_DATA_MIME_CHARSET_FAULTY));
    }

    @Test
    public void testMimeDecodingImplicitFaultyCharset() throws Exception {
        String mimeContent = "Content-Type: text/plain\n" + ("\n" + "message with binary data in it\n");
        byte[] data = mimeContent.getBytes();
        data[45] = ((byte) (195));
        data[46] = ((byte) (40));
        InputDataResult result = runSimpleDataInputOperation(data);
        // must be successful, no verification, have two output URIs
        Assert.assertTrue(result.success());
        Assert.assertNull(result.mDecryptVerifyResult);
        OpenPgpMetadata metadata = result.mMetadata.get(0);
        Assert.assertEquals("text/plain", metadata.getMimeType());
        Assert.assertNull("charset was bad so it should not be set", metadata.getCharset());
        Assert.assertTrue("faulty charset should have been detected", result.getLog().containsType(MSG_DATA_MIME_CHARSET_UNKNOWN));
    }

    @Test
    public void testMimeDecodingImplicitGuessedCharset() throws Exception {
        String mimeContent = "Content-Type: text/plain\n" + ("\n" + "proper, utf-8 encoded message \u262d\n");
        InputDataResult result = runSimpleDataInputOperation(mimeContent.getBytes());
        // must be successful, no verification, have two output URIs
        Assert.assertTrue(result.success());
        Assert.assertNull(result.mDecryptVerifyResult);
        OpenPgpMetadata metadata = result.mMetadata.get(0);
        Assert.assertEquals("text/plain", metadata.getMimeType());
        Assert.assertEquals("charset should be set since it was guessed and not faulty", "utf-8", metadata.getCharset());
        Assert.assertTrue("charset should have been guessed", result.getLog().containsType(MSG_DATA_MIME_CHARSET_GUESS));
    }

    @Test
    public void testMimeDecodingOctetStreamGuessedCharset() throws Exception {
        String mimeContent = "Content-Type: application/octet-stream\n" + ("\n" + "proper, utf-8 encoded message \u262d\n");
        InputDataResult result = runSimpleDataInputOperation(mimeContent.getBytes());
        // must be successful, no verification, have two output URIs
        Assert.assertTrue(result.success());
        Assert.assertNull(result.mDecryptVerifyResult);
        OpenPgpMetadata metadata = result.mMetadata.get(0);
        Assert.assertEquals("text/plain", metadata.getMimeType());
        Assert.assertEquals("charset should be set since it was guessed and not faulty", "utf-8", metadata.getCharset());
        Assert.assertTrue("charset should have been guessed", result.getLog().containsType(MSG_DATA_MIME_CHARSET_GUESS));
    }

    @Test
    public void testMimeDecodingWithNoContentTypeHeader() throws Exception {
        String mimeContent = "Some-Header: dummy\n" + ("\n" + "some message text\n");
        InputDataResult result = runSimpleDataInputOperation(mimeContent.getBytes());
        // must be successful, no verification, have two output URIs
        Assert.assertTrue(result.success());
        Assert.assertNull(result.mDecryptVerifyResult);
        OpenPgpMetadata metadata = result.mMetadata.get(0);
        Assert.assertNull(null, metadata.getMimeType());
        Assert.assertTrue("should not be mime parsed", result.getLog().containsType(MSG_DATA_MIME_NONE));
        Assert.assertEquals("output uri should simply be passed-through input uri", result.getOutputUris().get(0), InputDataOperationTest.FAKE_CONTENT_INPUT_URI_1);
    }
}


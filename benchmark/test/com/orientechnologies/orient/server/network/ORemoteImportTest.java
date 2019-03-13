package com.orientechnologies.orient.server.network;


import com.orientechnologies.orient.client.remote.OStorageRemote;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.server.OServer;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 19/07/16.
 */
public class ORemoteImportTest {
    private static final String SERVER_DIRECTORY = "./target/db";

    private OServer server;

    @Test
    public void testImport() throws UnsupportedEncodingException {
        ODatabaseDocumentInternal db = new ODatabaseDocumentTx(("remote:localhost/" + (ORemoteImportTest.class.getSimpleName())));
        db.open("admin", "admin");
        try {
            String content = "{\"records\": [{\"@type\": \"d\", \"@rid\": \"#9:0\",\"@version\": 1,\"@class\": \"V\"}]}";
            OStorageRemote storage = ((OStorageRemote) (db.getStorage()));
            final StringBuffer buff = new StringBuffer();
            storage.importDatabase("-merge=true", new ByteArrayInputStream(content.getBytes("UTF8")), "data.json", new OCommandOutputListener() {
                @Override
                public void onMessage(String iText) {
                    buff.append(iText);
                }
            });
            Assert.assertTrue(buff.toString().contains("Database import completed"));
        } finally {
            db.close();
        }
    }
}


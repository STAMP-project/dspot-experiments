package com.orientechnologies.orient.server.distributed;


import ODatabaseType.PLOCAL;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;
import com.orientechnologies.orient.server.OServer;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class DistributedDatabaseImportIT {
    private OServer server0;

    private OServer server1;

    private OServer server2;

    @Test
    public void test() throws IOException {
        OrientDB ctx1 = server0.getContext();
        ctx1.create("import-test", PLOCAL);
        ODatabaseSession session = ctx1.open("import-test", "admin", "admin");
        session.createClass("testa");
        ODatabaseExport export = new ODatabaseExport(((ODatabaseDocumentInternal) (session)), "target/export.tar.gz", ( iText) -> {
        });
        export.exportDatabase();
        export.close();
        session.close();
        ctx1.create("imported-test", PLOCAL);
        ODatabaseSession session1 = ctx1.open("imported-test", "admin", "admin");
        ODatabaseImport imp = new ODatabaseImport(((ODatabaseDocumentInternal) (session1)), "target/export.tar.gz", ( iText) -> {
        });
        imp.importDatabase();
        imp.close();
        session1.close();
        OrientDB ctx2 = server1.getContext();
        ODatabaseSession session2 = ctx2.open("imported-test", "admin", "admin");
        Assert.assertTrue(session2.getMetadata().getSchema().existsClass("testa"));
        session2.close();
    }
}


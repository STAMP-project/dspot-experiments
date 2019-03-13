package com.orientechnologies.orient.core.db.document;


import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


public class ODatabaseDocumentPoolOpenCloseTest {
    private ODatabaseDocument dbo;

    @Test
    public void openCloseClearThreadLocal() {
        OPartitionedDatabasePool pool = new OPartitionedDatabasePool(dbo.getURL(), "admin", "admin");
        try {
            ODatabaseDocument db = pool.acquire();
            db.close();
            Assert.assertNull(ODatabaseRecordThreadLocal.instance().getIfDefined());
        } finally {
            pool.close();
        }
    }

    @Test(expected = ODatabaseException.class)
    public void failureOpenPoolDatabase() {
        OPartitionedDatabasePool pool = new OPartitionedDatabasePool(dbo.getURL(), "admin", "admin");
        try {
            ODatabaseDocument db = pool.acquire();
            db.open("admin", "admin");
        } finally {
            pool.close();
        }
    }

    @Test
    public void checkSchemaRefresh() throws InterruptedException, ExecutionException {
        final OPartitionedDatabasePool pool = new OPartitionedDatabasePool(dbo.getURL(), "admin", "admin");
        try {
            ODatabaseDocument db = pool.acquire();
            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future f = exec.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    ODatabaseDocument db1 = pool.acquire();
                    db1.getMetadata().getSchema().createClass("Test");
                    db1.close();
                    return null;
                }
            });
            f.get();
            exec.shutdown();
            db.activateOnCurrentThread();
            OClass clazz = db.getMetadata().getSchema().getClass("Test");
            Assert.assertNotNull(clazz);
            db.close();
        } finally {
            pool.close();
        }
    }
}


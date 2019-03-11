package com.orientechnologies.lucene.engine;


import com.orientechnologies.lucene.test.BaseLuceneTest;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.io.File;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;


/**
 * Created by frank on 03/03/2016.
 */
public class OLuceneDirectoryFactoryTest extends BaseLuceneTest {
    private OLuceneDirectoryFactory.OLuceneDirectoryFactory fc;

    private ODocument meta;

    private OIndexDefinition indexDef;

    @Test
    public void shouldCreateNioFsDirectory() throws Exception {
        meta.field(DIRECTORY_TYPE, DIRECTORY_NIO);
        ODatabaseDocumentTx db = dropOrCreate(("plocal:./target/testDatabase/" + (name.getMethodName())), true);
        Directory directory = fc.createDirectory(db, "index.name", meta).getDirectory();
        assertThat(directory).isInstanceOf(NIOFSDirectory.class);
        assertThat(new File((("./target/testDatabase/" + (name.getMethodName())) + "/luceneIndexes/index.name"))).exists();
        db.drop();
    }

    @Test
    public void shouldCreateMMapFsDirectory() throws Exception {
        meta.field(DIRECTORY_TYPE, DIRECTORY_MMAP);
        ODatabaseDocumentTx db = dropOrCreate(("plocal:./target/testDatabase/" + (name.getMethodName())), true);
        Directory directory = fc.createDirectory(db, "index.name", meta).getDirectory();
        assertThat(directory).isInstanceOf(MMapDirectory.class);
        assertThat(new File((("./target/testDatabase/" + (name.getMethodName())) + "/luceneIndexes/index.name"))).exists();
        db.drop();
    }

    @Test
    public void shouldCreateRamDirectory() throws Exception {
        meta.field(DIRECTORY_TYPE, DIRECTORY_RAM);
        ODatabaseDocumentTx db = dropOrCreate(("plocal:./target/testDatabase/" + (name.getMethodName())), true);
        Directory directory = fc.createDirectory(db, "index.name", meta).getDirectory();
        assertThat(directory).isInstanceOf(RAMDirectory.class);
        db.drop();
    }

    @Test
    public void shouldCreateRamDirectoryOnMemoryDatabase() throws Exception {
        // WRONG type!!!
        meta.field(DIRECTORY_TYPE, DIRECTORY_MMAP);
        ODatabaseDocumentTx db = dropOrCreate(("memory:" + (name.getMethodName())), true);
        Directory directory = fc.createDirectory(db, "index.name", meta).getDirectory();
        assertThat(directory).isInstanceOf(RAMDirectory.class);
    }
}


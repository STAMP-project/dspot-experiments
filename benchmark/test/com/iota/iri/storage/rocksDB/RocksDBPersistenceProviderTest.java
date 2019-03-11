package com.iota.iri.storage.rocksDB;


import com.iota.iri.model.IntegerIndex;
import com.iota.iri.model.persistables.Transaction;
import com.iota.iri.storage.Indexable;
import com.iota.iri.storage.Persistable;
import com.iota.iri.utils.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RocksDBPersistenceProviderTest {
    private static RocksDBPersistenceProvider rocksDBPersistenceProvider;

    private static String dbPath = "tmpdb";

    private static String dbLogPath = "tmplogs";

    @Test
    public void testDeleteBatch() throws Exception {
        Persistable tx = new Transaction();
        byte[] bytes = new byte[Transaction.SIZE];
        Arrays.fill(bytes, ((byte) (1)));
        tx.read(bytes);
        tx.readMetadata(bytes);
        List<Pair<Indexable, Persistable>> models = IntStream.range(1, 1000).mapToObj(( i) -> new Pair(((Indexable) (new IntegerIndex(i))), tx)).collect(Collectors.toList());
        RocksDBPersistenceProviderTest.rocksDBPersistenceProvider.saveBatch(models);
        List<Pair<Indexable, ? extends Class<? extends Persistable>>> modelsToDelete = models.stream().filter(( entry) -> (getValue()) < 900).map(( entry) -> new Pair<>(entry.low, entry.hi.getClass())).collect(Collectors.toList());
        RocksDBPersistenceProviderTest.rocksDBPersistenceProvider.deleteBatch(modelsToDelete);
        for (Pair<Indexable, ? extends Class<? extends Persistable>> model : modelsToDelete) {
            Assert.assertNull((("value at index " + (getValue())) + " should be deleted"), RocksDBPersistenceProviderTest.rocksDBPersistenceProvider.get(model.hi, model.low).bytes());
        }
        List<IntegerIndex> indexes = IntStream.range(900, 1000).mapToObj(( i) -> new IntegerIndex(i)).collect(Collectors.toList());
        for (IntegerIndex index : indexes) {
            Assert.assertArrayEquals(("saved bytes are not as expected in index " + (index.getValue())), tx.bytes(), RocksDBPersistenceProviderTest.rocksDBPersistenceProvider.get(Transaction.class, index).bytes());
        }
    }
}


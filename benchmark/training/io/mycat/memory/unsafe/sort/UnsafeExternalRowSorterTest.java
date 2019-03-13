package io.mycat.memory.unsafe.sort;


import UnsafeExternalRowSorter.PrefixComputer;
import io.mycat.memory.MyCatMemory;
import io.mycat.memory.unsafe.memory.mm.DataNodeMemoryManager;
import io.mycat.memory.unsafe.memory.mm.MemoryManager;
import io.mycat.memory.unsafe.row.BufferHolder;
import io.mycat.memory.unsafe.row.StructType;
import io.mycat.memory.unsafe.row.UnsafeRow;
import io.mycat.memory.unsafe.row.UnsafeRowWriter;
import io.mycat.memory.unsafe.storage.DataNodeDiskManager;
import io.mycat.memory.unsafe.storage.SerializerManager;
import io.mycat.memory.unsafe.utils.MycatPropertyConf;
import io.mycat.memory.unsafe.utils.sort.PrefixComparator;
import io.mycat.memory.unsafe.utils.sort.PrefixComparators;
import io.mycat.memory.unsafe.utils.sort.UnsafeExternalRowSorter;
import io.mycat.sqlengine.mpp.ColMeta;
import io.mycat.sqlengine.mpp.OrderCol;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zagnix on 2016/6/19.
 */
public class UnsafeExternalRowSorterTest {
    private static final int TEST_SIZE = 100000;

    public static final Logger LOGGER = LoggerFactory.getLogger(UnsafeExternalRowSorterTest.class);

    /**
     * ???? LONG?INT?SHORT,Float?Double?String?Binary
     * ??????????????????????????
     */
    @Test
    public void testUnsafeExternalRowSorter() throws IOException, IllegalAccessException, NoSuchFieldException {
        MyCatMemory myCatMemory = new MyCatMemory();
        MemoryManager memoryManager = myCatMemory.getResultMergeMemoryManager();
        DataNodeDiskManager blockManager = myCatMemory.getBlockManager();
        SerializerManager serializerManager = myCatMemory.getSerializerManager();
        MycatPropertyConf conf = myCatMemory.getConf();
        DataNodeMemoryManager dataNodeMemoryManager = new DataNodeMemoryManager(memoryManager, Thread.currentThread().getId());
        /**
         * 1.schema ,????field???
         */
        int fieldCount = 3;
        ColMeta colMeta = null;
        Map<String, ColMeta> colMetaMap = new HashMap<String, ColMeta>(fieldCount);
        colMeta = new ColMeta(0, ColMeta.COL_TYPE_STRING);
        colMetaMap.put("id", colMeta);
        colMeta = new ColMeta(1, ColMeta.COL_TYPE_STRING);
        colMetaMap.put("name", colMeta);
        colMeta = new ColMeta(2, ColMeta.COL_TYPE_STRING);
        colMetaMap.put("age", colMeta);
        OrderCol[] orderCols = new OrderCol[1];
        OrderCol orderCol = new OrderCol(colMetaMap.get("id"), OrderCol.COL_ORDER_TYPE_ASC);
        orderCols[0] = orderCol;
        /**
         * 2 .PrefixComputer
         */
        StructType schema = new StructType(colMetaMap, fieldCount);
        schema.setOrderCols(orderCols);
        UnsafeExternalRowSorter.PrefixComputer prefixComputer = new io.mycat.memory.unsafe.utils.sort.RowPrefixComputer(schema);
        /**
         * 3 .PrefixComparator ???ASC?????DESC
         */
        final PrefixComparator prefixComparator = PrefixComparators.LONG;
        UnsafeExternalRowSorter sorter = /**
         * ???????true or false
         */
        new UnsafeExternalRowSorter(dataNodeMemoryManager, myCatMemory, schema, prefixComparator, prefixComputer, conf.getSizeAsBytes("mycat.buffer.pageSize", "1m"), true, true);
        UnsafeRow unsafeRow;
        BufferHolder bufferHolder;
        UnsafeRowWriter unsafeRowWriter;
        String line = "testUnsafeRow";
        // List<Float> floats = new ArrayList<Float>();
        List<Long> longs = new ArrayList<Long>();
        final Random rand = new Random(42);
        for (int i = 0; i < (UnsafeExternalRowSorterTest.TEST_SIZE); i++) {
            unsafeRow = new UnsafeRow(3);
            bufferHolder = new BufferHolder(unsafeRow);
            unsafeRowWriter = new UnsafeRowWriter(bufferHolder, 3);
            bufferHolder.reset();
            String key = UnsafeExternalRowSorterTest.getRandomString(((rand.nextInt(300)) + 100));
            // long v = rand.nextLong();
            // longs.add(v);
            unsafeRowWriter.write(0, key.getBytes());
            // unsafeRowWriter.write(0, BytesTools.toBytes(v));
            unsafeRowWriter.write(1, line.getBytes());
            unsafeRowWriter.write(2, ("35" + 1).getBytes());
            unsafeRow.setTotalSize(bufferHolder.totalSize());
            sorter.insertRow(unsafeRow);
        }
        Iterator<UnsafeRow> iter = sorter.sort();
        /* float [] com = new float[floats.size()];
        for (int i = 0; i <floats.size() ; i++) {
        com[i] = floats.get(i);
        }
        Arrays.sort(com);


        long[] com = new long[longs.size()];
        for (int i = 0; i < longs.size() ; i++) {
        com[i] = longs.get(i);
        }

        Arrays.sort(com);
         */
        UnsafeRow row = null;
        int indexprint = 0;
        while (iter.hasNext()) {
            row = iter.next();
            // LOGGER.error(indexprint + "    " +  row.getUTF8String(0));
            // Assert.assertEquals(com[indexprint],
            // BytesTools.toLong(row.getBinary(0)));
            // Double c = Double.parseDouble(String.valueOf(com[indexprint])) ;
            // Double c1 = Double.parseDouble(String.valueOf(BytesTools.toFloat(row.getBinary(0)))) ;
            // Assert.assertEquals(0,c.compareTo(c1));
            indexprint++;
        } 
        Assert.assertEquals(UnsafeExternalRowSorterTest.TEST_SIZE, indexprint);
    }
}


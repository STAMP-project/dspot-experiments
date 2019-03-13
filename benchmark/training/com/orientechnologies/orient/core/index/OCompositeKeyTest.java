package com.orientechnologies.orient.core.index;


import OCompositeKeySerializer.INSTANCE;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OWALChanges;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OWALChangesTree;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;


public class OCompositeKeyTest {
    @Test
    public void testEqualSameKeys() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("a");
        compositeKey.addKey("b");
        final OCompositeKey anotherCompositeKey = new OCompositeKey();
        anotherCompositeKey.addKey("a");
        anotherCompositeKey.addKey("b");
        Assert.assertTrue(compositeKey.equals(anotherCompositeKey));
        Assert.assertTrue(((compositeKey.hashCode()) == (anotherCompositeKey.hashCode())));
    }

    @Test
    public void testEqualNotSameKeys() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("a");
        compositeKey.addKey("b");
        final OCompositeKey anotherCompositeKey = new OCompositeKey();
        anotherCompositeKey.addKey("a");
        anotherCompositeKey.addKey("b");
        anotherCompositeKey.addKey("c");
        Assert.assertFalse(compositeKey.equals(anotherCompositeKey));
    }

    @Test
    public void testEqualNull() {
        final OCompositeKey compositeKey = new OCompositeKey();
        Assert.assertFalse(compositeKey.equals(null));
    }

    @Test
    public void testEqualSame() {
        final OCompositeKey compositeKey = new OCompositeKey();
        Assert.assertTrue(compositeKey.equals(compositeKey));
    }

    @Test
    public void testEqualDiffClass() {
        final OCompositeKey compositeKey = new OCompositeKey();
        Assert.assertFalse(compositeKey.equals("1"));
    }

    @Test
    public void testAddKeyComparable() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("a");
        Assert.assertEquals(compositeKey.getKeys().size(), 1);
        Assert.assertTrue(compositeKey.getKeys().contains("a"));
    }

    @Test
    public void testAddKeyComposite() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("a");
        final OCompositeKey compositeKeyToAdd = new OCompositeKey();
        compositeKeyToAdd.addKey("a");
        compositeKeyToAdd.addKey("b");
        compositeKey.addKey(compositeKeyToAdd);
        Assert.assertEquals(compositeKey.getKeys().size(), 3);
        Assert.assertTrue(compositeKey.getKeys().contains("a"));
        Assert.assertTrue(compositeKey.getKeys().contains("b"));
    }

    @Test
    public void testCompareToSame() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("a");
        compositeKey.addKey("b");
        final OCompositeKey anotherCompositeKey = new OCompositeKey();
        anotherCompositeKey.addKey("a");
        anotherCompositeKey.addKey("b");
        Assert.assertEquals(compositeKey.compareTo(anotherCompositeKey), 0);
    }

    @Test
    public void testCompareToPartiallyOneCase() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("a");
        compositeKey.addKey("b");
        final OCompositeKey anotherCompositeKey = new OCompositeKey();
        anotherCompositeKey.addKey("a");
        anotherCompositeKey.addKey("b");
        anotherCompositeKey.addKey("c");
        Assert.assertEquals(compositeKey.compareTo(anotherCompositeKey), 0);
    }

    @Test
    public void testCompareToPartiallySecondCase() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("a");
        compositeKey.addKey("b");
        compositeKey.addKey("c");
        final OCompositeKey anotherCompositeKey = new OCompositeKey();
        anotherCompositeKey.addKey("a");
        anotherCompositeKey.addKey("b");
        Assert.assertEquals(compositeKey.compareTo(anotherCompositeKey), 0);
    }

    @Test
    public void testCompareToGT() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("b");
        final OCompositeKey anotherCompositeKey = new OCompositeKey();
        anotherCompositeKey.addKey("a");
        anotherCompositeKey.addKey("b");
        Assert.assertEquals(compositeKey.compareTo(anotherCompositeKey), 1);
    }

    @Test
    public void testCompareToLT() {
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey("a");
        compositeKey.addKey("b");
        final OCompositeKey anotherCompositeKey = new OCompositeKey();
        anotherCompositeKey.addKey("b");
        Assert.assertEquals(compositeKey.compareTo(anotherCompositeKey), (-1));
    }

    @Test
    public void testCompareToSymmetryOne() {
        final OCompositeKey compositeKeyOne = new OCompositeKey();
        compositeKeyOne.addKey(1);
        compositeKeyOne.addKey(2);
        final OCompositeKey compositeKeyTwo = new OCompositeKey();
        compositeKeyTwo.addKey(1);
        compositeKeyTwo.addKey(3);
        compositeKeyTwo.addKey(1);
        Assert.assertEquals(compositeKeyOne.compareTo(compositeKeyTwo), (-1));
        Assert.assertEquals(compositeKeyTwo.compareTo(compositeKeyOne), 1);
    }

    @Test
    public void testCompareToSymmetryTwo() {
        final OCompositeKey compositeKeyOne = new OCompositeKey();
        compositeKeyOne.addKey(1);
        compositeKeyOne.addKey(2);
        final OCompositeKey compositeKeyTwo = new OCompositeKey();
        compositeKeyTwo.addKey(1);
        compositeKeyTwo.addKey(2);
        compositeKeyTwo.addKey(3);
        Assert.assertEquals(compositeKeyOne.compareTo(compositeKeyTwo), 0);
        Assert.assertEquals(compositeKeyTwo.compareTo(compositeKeyOne), 0);
    }

    @Test
    public void testCompareNullAtTheEnd() {
        final OCompositeKey compositeKeyOne = new OCompositeKey();
        compositeKeyOne.addKey(2);
        compositeKeyOne.addKey(2);
        final OCompositeKey compositeKeyTwo = new OCompositeKey();
        compositeKeyTwo.addKey(2);
        compositeKeyTwo.addKey(null);
        final OCompositeKey compositeKeyThree = new OCompositeKey();
        compositeKeyThree.addKey(2);
        compositeKeyThree.addKey(null);
        Assert.assertEquals(compositeKeyOne.compareTo(compositeKeyTwo), 1);
        Assert.assertEquals(compositeKeyTwo.compareTo(compositeKeyOne), (-1));
        Assert.assertEquals(compositeKeyTwo.compareTo(compositeKeyThree), 0);
    }

    @Test
    public void testCompareNullAtTheMiddle() {
        final OCompositeKey compositeKeyOne = new OCompositeKey();
        compositeKeyOne.addKey(2);
        compositeKeyOne.addKey(2);
        compositeKeyOne.addKey(3);
        final OCompositeKey compositeKeyTwo = new OCompositeKey();
        compositeKeyTwo.addKey(2);
        compositeKeyTwo.addKey(null);
        compositeKeyTwo.addKey(3);
        final OCompositeKey compositeKeyThree = new OCompositeKey();
        compositeKeyThree.addKey(2);
        compositeKeyThree.addKey(null);
        compositeKeyThree.addKey(3);
        Assert.assertEquals(compositeKeyOne.compareTo(compositeKeyTwo), 1);
        Assert.assertEquals(compositeKeyTwo.compareTo(compositeKeyOne), (-1));
        Assert.assertEquals(compositeKeyTwo.compareTo(compositeKeyThree), 0);
    }

    @Test
    public void testDocumentSerializationCompositeKeyNull() {
        final OCompositeKey compositeKeyOne = new OCompositeKey();
        compositeKeyOne.addKey(1);
        compositeKeyOne.addKey(null);
        compositeKeyOne.addKey(2);
        ODocument document = compositeKeyOne.toDocument();
        final OCompositeKey compositeKeyTwo = new OCompositeKey();
        compositeKeyTwo.fromDocument(document);
        Assert.assertEquals(compositeKeyOne, compositeKeyTwo);
        Assert.assertNotSame(compositeKeyOne, compositeKeyTwo);
    }

    @Test
    public void testNativeBinarySerializationCompositeKeyNull() {
        final OCompositeKey compositeKeyOne = new OCompositeKey();
        compositeKeyOne.addKey(1);
        compositeKeyOne.addKey(null);
        compositeKeyOne.addKey(2);
        int len = INSTANCE.getObjectSize(compositeKeyOne);
        byte[] data = new byte[len];
        INSTANCE.serializeNativeObject(compositeKeyOne, data, 0);
        final OCompositeKey compositeKeyTwo = INSTANCE.deserializeNativeObject(data, 0);
        Assert.assertEquals(compositeKeyOne, compositeKeyTwo);
        Assert.assertNotSame(compositeKeyOne, compositeKeyTwo);
    }

    @Test
    public void testByteBufferBinarySerializationCompositeKeyNull() {
        final int serializationOffset = 5;
        final OCompositeKey compositeKeyOne = new OCompositeKey();
        compositeKeyOne.addKey(1);
        compositeKeyOne.addKey(null);
        compositeKeyOne.addKey(2);
        final int len = INSTANCE.getObjectSize(compositeKeyOne);
        final ByteBuffer buffer = ByteBuffer.allocate((len + serializationOffset));
        buffer.position(serializationOffset);
        INSTANCE.serializeInByteBufferObject(compositeKeyOne, buffer);
        final int binarySize = (buffer.position()) - serializationOffset;
        Assert.assertEquals(binarySize, len);
        buffer.position(serializationOffset);
        Assert.assertEquals(INSTANCE.getObjectSizeInByteBuffer(buffer), len);
        buffer.position(serializationOffset);
        final OCompositeKey compositeKeyTwo = INSTANCE.deserializeFromByteBufferObject(buffer);
        Assert.assertEquals(compositeKeyOne, compositeKeyTwo);
        Assert.assertNotSame(compositeKeyOne, compositeKeyTwo);
        Assert.assertEquals(((buffer.position()) - serializationOffset), len);
    }

    @Test
    public void testWALChangesBinarySerializationCompositeKeyNull() {
        final int serializationOffset = 5;
        final OCompositeKey compositeKey = new OCompositeKey();
        compositeKey.addKey(1);
        compositeKey.addKey(null);
        compositeKey.addKey(2);
        final int len = INSTANCE.getObjectSize(compositeKey);
        final ByteBuffer buffer = ByteBuffer.allocateDirect((len + serializationOffset)).order(ByteOrder.nativeOrder());
        final byte[] data = new byte[len];
        INSTANCE.serializeNativeObject(compositeKey, data, 0);
        final OWALChanges walChanges = new OWALChangesTree();
        walChanges.setBinaryValue(buffer, data, serializationOffset);
        Assert.assertEquals(INSTANCE.getObjectSizeInByteBuffer(buffer, walChanges, serializationOffset), len);
        Assert.assertEquals(INSTANCE.deserializeFromByteBufferObject(buffer, walChanges, serializationOffset), compositeKey);
    }
}


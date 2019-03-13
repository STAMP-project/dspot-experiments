package com.squareup.leakcanary;


import com.squareup.haha.perflib.ClassInstance;
import com.squareup.haha.perflib.Field;
import com.squareup.haha.perflib.Snapshot;
import com.squareup.haha.perflib.Type;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class HahaHelperTest {
    private static final int STRING_CLASS_ID = 100;

    private static final int CHAR_ARRAY_CLASS_ID = 101;

    private static final int STRING_INSTANCE_ID = 102;

    private static final int VALUE_ARRAY_INSTANCE_ID = 103;

    private static final int BYTE_ARRAY_CLASS_ID = 104;

    private static final int VALUE_ARRAY_LENGTH = 6;

    private static final int COUNT_VALUE = 5;

    private static final int OFFSET_VALUE = 1;

    private FakeHprofBuffer buffer;

    private Snapshot snapshot;

    @Test
    public void readStringOffsetFromHeapDumpInstance_pre_O() {
        buffer.setIntsToRead(HahaHelperTest.COUNT_VALUE, HahaHelperTest.OFFSET_VALUE, HahaHelperTest.VALUE_ARRAY_INSTANCE_ID);
        buffer.setStringsToRead("abcdef");
        addStringClassToSnapshotWithFields(snapshot, new Field[]{ new Field(Type.INT, "count"), new Field(Type.INT, "offset"), new Field(Type.OBJECT, "value") });
        ClassInstance stringInstance = createStringInstance();
        createCharArrayValueInstance();
        String actual = HahaHelper.asString(stringInstance);
        Assert.assertTrue(actual.equals("bcdef"));
    }

    @Test
    public void defaultToZeroStringOffsetWhenHeapDumpInstanceIsMissingOffsetValue_pre_O() {
        buffer.setIntsToRead(HahaHelperTest.COUNT_VALUE, HahaHelperTest.VALUE_ARRAY_INSTANCE_ID);
        buffer.setStringsToRead("abcdef");
        addStringClassToSnapshotWithFields(snapshot, new Field[]{ new Field(Type.INT, "count"), new Field(Type.OBJECT, "value") });
        ClassInstance stringInstance = createStringInstance();
        createCharArrayValueInstance();
        String actual = HahaHelper.asString(stringInstance);
        Assert.assertTrue(actual.equals("abcde"));
    }

    @Test
    public void readStringAsByteArrayFromHeapDumpInstance_O() {
        // O uses default charset UTF-8
        buffer = new FakeHprofBuffer("UTF-8");
        initSnapshot(buffer);
        buffer.setIntsToRead(HahaHelperTest.COUNT_VALUE, HahaHelperTest.VALUE_ARRAY_INSTANCE_ID);
        buffer.setStringsToRead("abcdef");
        addStringClassToSnapshotWithFields_O(snapshot, new Field[]{ new Field(Type.INT, "count"), new Field(Type.OBJECT, "value") });
        ClassInstance stringInstance = createStringInstance();
        createByteArrayValueInstance();
        String actual = HahaHelper.asString(stringInstance);
        Assert.assertTrue(actual.equals("abcde"));
    }

    @Test
    public void throwExceptionWhenNotArrayValueForString() {
        buffer.setIntsToRead(HahaHelperTest.COUNT_VALUE, HahaHelperTest.OFFSET_VALUE, HahaHelperTest.VALUE_ARRAY_INSTANCE_ID);
        buffer.setStringsToRead("abcdef");
        addStringClassToSnapshotWithFields(snapshot, new Field[]{ new Field(Type.INT, "count"), new Field(Type.INT, "offset"), new Field(Type.OBJECT, "value") });
        ClassInstance stringInstance = createStringInstance();
        createObjectValueInstance();
        try {
            HahaHelper.asString(stringInstance);
            Assert.fail("this test should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException uoe) {
            String message = uoe.getMessage();
            Assert.assertTrue(message.equals(("Could not find char array in " + stringInstance)));
        }
    }
}


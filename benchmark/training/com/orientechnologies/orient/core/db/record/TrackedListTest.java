package com.orientechnologies.orient.core.db.record;


import OMultiValueChangeEvent.OChangeType;
import OMultiValueChangeEvent.OChangeType.ADD;
import OMultiValueChangeEvent.OChangeType.REMOVE;
import OMultiValueChangeEvent.OChangeType.UPDATE;
import ORecordElement.STATUS.UNMARSHALLING;
import com.orientechnologies.common.types.ORef;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.OMemoryStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TrackedListTest {
    @Test
    public void testAddNotificationOne() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                Assert.assertEquals(event.getChangeType(), ADD);
                Assert.assertNull(event.getOldValue());
                Assert.assertEquals(event.getKey().intValue(), 0);
                Assert.assertEquals(event.getValue(), "value1");
                changed.value = true;
            }
        });
        trackedList.add("value1");
        Assert.assertTrue(changed.value);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testAddNotificationTwo() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                Assert.assertEquals(event.getChangeType(), ADD);
                Assert.assertNull(event.getOldValue());
                Assert.assertEquals(event.getKey().intValue(), 2);
                Assert.assertEquals(event.getValue(), "value3");
                changed.value = true;
            }
        });
        trackedList.add("value3");
        Assert.assertTrue(changed.value);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testAddNotificationThree() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testAddNotificationFour() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.setInternalStatus(UNMARSHALLING);
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                changed.value = true;
            }
        });
        trackedList.add("value3");
        Assert.assertEquals(changed.value, Boolean.FALSE);
        Assert.assertFalse(doc.isDirty());
    }

    @Test
    public void testAddAllNotificationOne() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        final List<String> valuesToAdd = new ArrayList<String>();
        valuesToAdd.add("value1");
        valuesToAdd.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final List<OMultiValueChangeEvent<Integer, String>> firedEvents = new ArrayList<OMultiValueChangeEvent<Integer, String>>();
        firedEvents.add(new OMultiValueChangeEvent<Integer, String>(OChangeType.ADD, 0, "value1"));
        firedEvents.add(new OMultiValueChangeEvent<Integer, String>(OChangeType.ADD, 1, "value3"));
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                if (firedEvents.get(0).equals(event))
                    firedEvents.remove(0);
                else
                    Assert.fail();

            }
        });
        trackedList.addAll(valuesToAdd);
        Assert.assertEquals(firedEvents.size(), 0);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testAddAllNotificationTwo() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        final List<String> valuesToAdd = new ArrayList<String>();
        valuesToAdd.add("value1");
        valuesToAdd.add("value3");
        trackedList.addAll(valuesToAdd);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testAddAllNotificationThree() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        final List<String> valuesToAdd = new ArrayList<String>();
        valuesToAdd.add("value1");
        valuesToAdd.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.setInternalStatus(UNMARSHALLING);
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                changed.value = true;
            }
        });
        trackedList.addAll(valuesToAdd);
        Assert.assertFalse(changed.value);
        Assert.assertFalse(doc.isDirty());
    }

    @Test
    public void testAddIndexNotificationOne() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                Assert.assertEquals(event.getChangeType(), ADD);
                Assert.assertNull(event.getOldValue());
                Assert.assertEquals(event.getKey().intValue(), 1);
                Assert.assertEquals(event.getValue(), "value3");
                changed.value = true;
            }
        });
        trackedList.add(1, "value3");
        Assert.assertEquals(changed.value, Boolean.TRUE);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testAddIndexNotificationTwo() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.add(1, "value3");
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testAddIndexNotificationThree() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.setInternalStatus(UNMARSHALLING);
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                changed.value = true;
            }
        });
        trackedList.add(1, "value3");
        Assert.assertEquals(changed.value, Boolean.FALSE);
        Assert.assertFalse(doc.isDirty());
    }

    @Test
    public void testSetNotificationOne() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                Assert.assertEquals(event.getChangeType(), UPDATE);
                Assert.assertEquals(event.getOldValue(), "value2");
                Assert.assertEquals(event.getKey().intValue(), 1);
                Assert.assertEquals(event.getValue(), "value4");
                changed.value = true;
            }
        });
        trackedList.set(1, "value4");
        Assert.assertEquals(changed.value, Boolean.TRUE);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testSetNotificationTwo() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.set(1, "value4");
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testSetNotificationThree() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.setInternalStatus(UNMARSHALLING);
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                changed.value = true;
            }
        });
        trackedList.set(1, "value4");
        Assert.assertFalse(changed.value);
        Assert.assertFalse(doc.isDirty());
    }

    @Test
    public void testRemoveNotificationOne() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                Assert.assertEquals(event.getChangeType(), REMOVE);
                Assert.assertEquals(event.getOldValue(), "value2");
                Assert.assertEquals(event.getKey().intValue(), 1);
                Assert.assertNull(event.getValue());
                changed.value = true;
            }
        });
        trackedList.remove("value2");
        Assert.assertTrue(changed.value);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testRemoveNotificationTwo() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.remove("value2");
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testRemoveNotificationThree() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.setInternalStatus(UNMARSHALLING);
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                changed.value = true;
            }
        });
        trackedList.remove("value2");
        Assert.assertFalse(changed.value);
        Assert.assertFalse(doc.isDirty());
    }

    @Test
    public void testRemoveNotificationFour() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                changed.value = true;
            }
        });
        trackedList.remove("value4");
        Assert.assertFalse(changed.value);
        Assert.assertFalse(doc.isDirty());
    }

    @Test
    public void testRemoveIndexOne() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                Assert.assertEquals(event.getChangeType(), REMOVE);
                Assert.assertEquals(event.getOldValue(), "value2");
                Assert.assertEquals(event.getKey().intValue(), 1);
                Assert.assertNull(event.getValue());
                changed.value = true;
            }
        });
        trackedList.remove(1);
        Assert.assertTrue(changed.value);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testRemoveIndexTwo() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.remove(1);
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testRemoveIndexThree() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.setInternalStatus(UNMARSHALLING);
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                changed.value = true;
            }
        });
        trackedList.remove(1);
        Assert.assertFalse(changed.value);
        Assert.assertFalse(doc.isDirty());
    }

    @Test
    public void testClearOne() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final List<OMultiValueChangeEvent<Integer, String>> firedEvents = new ArrayList<OMultiValueChangeEvent<Integer, String>>();
        firedEvents.add(new OMultiValueChangeEvent<Integer, String>(OChangeType.REMOVE, 2, null, "value3"));
        firedEvents.add(new OMultiValueChangeEvent<Integer, String>(OChangeType.REMOVE, 1, null, "value2"));
        firedEvents.add(new OMultiValueChangeEvent<Integer, String>(OChangeType.REMOVE, 0, null, "value1"));
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                if (firedEvents.get(0).equals(event))
                    firedEvents.remove(0);
                else
                    Assert.fail();

            }
        });
        trackedList.clear();
        Assert.assertEquals(0, firedEvents.size());
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testClearTwo() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        trackedList.clear();
        Assert.assertTrue(doc.isDirty());
    }

    @Test
    public void testClearThree() {
        final ODocument doc = new ODocument();
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        ORecordInternal.unsetDirty(doc);
        Assert.assertFalse(doc.isDirty());
        final ORef<Boolean> changed = new ORef<Boolean>(false);
        trackedList.setInternalStatus(UNMARSHALLING);
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                changed.value = true;
            }
        });
        trackedList.clear();
        Assert.assertFalse(changed.value);
        Assert.assertFalse(doc.isDirty());
    }

    @Test
    public void testReturnOriginalStateOne() {
        final ODocument doc = new ODocument();
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        trackedList.add("value4");
        trackedList.add("value5");
        final List<String> original = new ArrayList<String>(trackedList);
        final List<OMultiValueChangeEvent<Integer, String>> firedEvents = new ArrayList<OMultiValueChangeEvent<Integer, String>>();
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                firedEvents.add(event);
            }
        });
        trackedList.add("value6");
        trackedList.add("value7");
        trackedList.set(2, "value10");
        trackedList.add(1, "value8");
        trackedList.add(1, "value8");
        trackedList.remove(3);
        trackedList.remove("value7");
        trackedList.add(0, "value9");
        trackedList.add(0, "value9");
        trackedList.add(0, "value9");
        trackedList.add(0, "value9");
        trackedList.remove("value9");
        trackedList.remove("value9");
        trackedList.add(4, "value11");
        Assert.assertEquals(original, trackedList.returnOriginalState(firedEvents));
    }

    @Test
    public void testReturnOriginalStateTwo() {
        final ODocument doc = new ODocument();
        final OTrackedList<String> trackedList = new OTrackedList<String>(doc);
        trackedList.add("value1");
        trackedList.add("value2");
        trackedList.add("value3");
        trackedList.add("value4");
        trackedList.add("value5");
        final List<String> original = new ArrayList<String>(trackedList);
        final List<OMultiValueChangeEvent<Integer, String>> firedEvents = new ArrayList<OMultiValueChangeEvent<Integer, String>>();
        trackedList.addChangeListener(new OMultiValueChangeListener<Integer, String>() {
            public void onAfterRecordChanged(final OMultiValueChangeEvent<Integer, String> event) {
                firedEvents.add(event);
            }
        });
        trackedList.add("value6");
        trackedList.add("value7");
        trackedList.set(2, "value10");
        trackedList.add(1, "value8");
        trackedList.remove(3);
        trackedList.clear();
        trackedList.remove("value7");
        trackedList.add(0, "value9");
        trackedList.add("value11");
        trackedList.add(0, "value12");
        trackedList.add("value12");
        Assert.assertEquals(original, trackedList.returnOriginalState(firedEvents));
    }

    /**
     * Test that {@link OTrackedList} is serialised correctly.
     */
    @Test
    public void testSerialization() throws Exception {
        class NotSerializableDocument extends ODocument {
            private static final long serialVersionUID = 1L;

            private void writeObject(ObjectOutputStream oos) throws IOException {
                throw new NotSerializableException();
            }
        }
        final OTrackedList<String> beforeSerialization = new OTrackedList<String>(new NotSerializableDocument());
        beforeSerialization.add("firstVal");
        beforeSerialization.add("secondVal");
        final OMemoryStream memoryStream = new OMemoryStream();
        ObjectOutputStream out = new ObjectOutputStream(memoryStream);
        out.writeObject(beforeSerialization);
        out.close();
        final ObjectInputStream input = new ObjectInputStream(new com.orientechnologies.orient.core.serialization.OMemoryInputStream(memoryStream.copy()));
        @SuppressWarnings("unchecked")
        final List<String> afterSerialization = ((List<String>) (input.readObject()));
        Assert.assertEquals(afterSerialization.size(), beforeSerialization.size());
        for (int i = 0; i < (afterSerialization.size()); i++) {
            Assert.assertEquals(afterSerialization.get(i), beforeSerialization.get(i));
        }
    }
}


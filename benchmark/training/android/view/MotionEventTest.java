package android.view;


import Build.VERSION;
import InputDevice.SOURCE_TOUCHSCREEN;
import MotionEvent.ACTION_DOWN;
import MotionEvent.ACTION_MOVE;
import MotionEvent.ACTION_UP;
import MotionEvent.AXIS_GENERIC_1;
import MotionEvent.AXIS_RTRIGGER;
import MotionEvent.CREATOR;
import MotionEvent.INVALID_POINTER_ID;
import MotionEvent.TOOL_TYPE_FINGER;
import MotionEvent.TOOL_TYPE_MOUSE;
import MotionEvent.TOOL_TYPE_UNKNOWN;
import Parcelable.PARCELABLE_WRITE_RETURN_VALUE;
import Subject.Factory;
import android.graphics.Matrix;
import android.os.Build.VERSION_CODES;
import android.os.Parcel;
import android.os.SystemClock;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import androidx.test.core.view.PointerCoordsBuilder;
import androidx.test.core.view.PointerPropertiesBuilder;
import androidx.test.ext.truth.view.MotionEventSubject;
import androidx.test.runner.AndroidJUnit4;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;

import static KeyEvent.META_SHIFT_ON;
import static MotionEvent.EDGE_TOP;
import static MotionEvent.TOOL_TYPE_MOUSE;


/**
 * Test {@link android.view.MotionEvent}.
 *
 * <p>Baselined from Android cts/tests/tests/view/src/android/view/cts/MotionEventTest.java
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class MotionEventTest {
    private MotionEvent motionEvent1;

    private MotionEvent motionEvent2;

    private MotionEvent motionEventDynamic;

    private long downTime;

    private long eventTime;

    private static final float X_3F = 3.0F;

    private static final float Y_4F = 4.0F;

    private static final int META_STATE = META_SHIFT_ON;

    private static final float PRESSURE_1F = 1.0F;

    private static final float SIZE_1F = 1.0F;

    private static final float X_PRECISION_3F = 3.0F;

    private static final float Y_PRECISION_4F = 4.0F;

    private static final int DEVICE_ID_1 = 1;

    private static final int EDGE_FLAGS = EDGE_TOP;

    private static final float TOLERANCE = 0.01F;

    @Test
    public void obtainBasic() {
        motionEvent1 = MotionEvent.obtain(downTime, eventTime, ACTION_DOWN, MotionEventTest.X_3F, MotionEventTest.Y_4F, MotionEventTest.META_STATE);
        MotionEventSubject.assertThat(motionEvent1).isNotNull();
        MotionEventSubject.assertThat(motionEvent1).hasDownTime(downTime);
        MotionEventSubject.assertThat(motionEvent1).hasEventTime(eventTime);
        MotionEventSubject.assertThat(motionEvent1).hasAction(ACTION_DOWN);
        MotionEventSubject.assertThat(motionEvent1).x().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_3F);
        MotionEventSubject.assertThat(motionEvent1).y().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_4F);
        MotionEventSubject.assertThat(motionEvent1).rawX().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_3F);
        MotionEventSubject.assertThat(motionEvent1).rawY().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_4F);
        MotionEventSubject.assertThat(motionEvent1).hasMetaState(MotionEventTest.META_STATE);
        MotionEventSubject.assertThat(motionEvent1).hasDeviceId(0);
        MotionEventSubject.assertThat(motionEvent1).hasEdgeFlags(0);
        MotionEventSubject.assertThat(motionEvent1).pressure().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.PRESSURE_1F);
        MotionEventSubject.assertThat(motionEvent1).size().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.SIZE_1F);
        MotionEventSubject.assertThat(motionEvent1).xPrecision().isWithin(MotionEventTest.TOLERANCE).of(1.0F);
        MotionEventSubject.assertThat(motionEvent1).yPrecision().isWithin(MotionEventTest.TOLERANCE).of(1.0F);
    }

    @Test
    public void testObtainFromMotionEvent() {
        motionEventDynamic = MotionEvent.obtain(motionEvent2);
        MotionEventSubject.assertThat(motionEventDynamic).isNotNull();
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).isEqualToWithinTolerance(motionEvent2, MotionEventTest.TOLERANCE);
    }

    @Test
    public void testObtainAllFields() {
        motionEventDynamic = MotionEvent.obtain(downTime, eventTime, ACTION_DOWN, MotionEventTest.X_3F, MotionEventTest.Y_4F, MotionEventTest.PRESSURE_1F, MotionEventTest.SIZE_1F, MotionEventTest.META_STATE, MotionEventTest.X_PRECISION_3F, MotionEventTest.Y_PRECISION_4F, MotionEventTest.DEVICE_ID_1, MotionEventTest.EDGE_FLAGS);
        MotionEventSubject.assertThat(motionEventDynamic).isNotNull();
        MotionEventSubject.assertThat(motionEventDynamic).hasButtonState(0);
        MotionEventSubject.assertThat(motionEventDynamic).hasDownTime(downTime);
        MotionEventSubject.assertThat(motionEventDynamic).hasEventTime(eventTime);
        MotionEventSubject.assertThat(motionEventDynamic).hasAction(ACTION_DOWN);
        MotionEventSubject.assertThat(motionEventDynamic).x().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_3F);
        MotionEventSubject.assertThat(motionEventDynamic).y().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_4F);
        MotionEventSubject.assertThat(motionEventDynamic).rawX().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_3F);
        MotionEventSubject.assertThat(motionEventDynamic).rawY().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_4F);
        MotionEventSubject.assertThat(motionEventDynamic).hasMetaState(MotionEventTest.META_STATE);
        MotionEventSubject.assertThat(motionEventDynamic).hasDeviceId(MotionEventTest.DEVICE_ID_1);
        MotionEventSubject.assertThat(motionEventDynamic).hasEdgeFlags(MotionEventTest.EDGE_FLAGS);
        MotionEventSubject.assertThat(motionEventDynamic).pressure().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.PRESSURE_1F);
        MotionEventSubject.assertThat(motionEventDynamic).size().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.SIZE_1F);
        MotionEventSubject.assertThat(motionEventDynamic).xPrecision().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_PRECISION_3F);
        MotionEventSubject.assertThat(motionEventDynamic).yPrecision().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_PRECISION_4F);
    }

    @Test
    public void actionButton() {
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, ACTION_DOWN, MotionEventTest.X_3F, MotionEventTest.Y_4F, MotionEventTest.META_STATE);
        if ((VERSION.SDK_INT) < (VERSION_CODES.M)) {
            try {
                MotionEventSubject.assertThat(event).hasActionButton(0);
                Assert.fail("IllegalStateException not thrown");
            } catch (IllegalStateException e) {
                // expected
            }
        } else {
            MotionEventSubject.assertThat(event).hasActionButton(0);
        }
    }

    @Test
    public void testObtainFromPropertyArrays() {
        PointerCoords coords0 = PointerCoordsBuilder.newBuilder().setCoords(MotionEventTest.X_3F, MotionEventTest.Y_4F).setPressure(MotionEventTest.PRESSURE_1F).setSize(MotionEventTest.SIZE_1F).setTool(1.2F, 1.4F).build();
        PointerCoords coords1 = PointerCoordsBuilder.newBuilder().setCoords(((MotionEventTest.X_3F) + 1.0F), ((MotionEventTest.Y_4F) - 2.0F)).setPressure(((MotionEventTest.PRESSURE_1F) + 0.2F)).setSize(((MotionEventTest.SIZE_1F) + 0.5F)).setTouch(2.2F, 0.6F).build();
        PointerProperties properties0 = PointerPropertiesBuilder.newBuilder().setId(0).setToolType(TOOL_TYPE_FINGER).build();
        PointerProperties properties1 = PointerPropertiesBuilder.newBuilder().setId(1).setToolType(TOOL_TYPE_FINGER).build();
        motionEventDynamic = MotionEvent.obtain(downTime, eventTime, ACTION_MOVE, 2, new PointerProperties[]{ properties0, properties1 }, new PointerCoords[]{ coords0, coords1 }, MotionEventTest.META_STATE, 0, MotionEventTest.X_PRECISION_3F, MotionEventTest.Y_PRECISION_4F, MotionEventTest.DEVICE_ID_1, MotionEventTest.EDGE_FLAGS, SOURCE_TOUCHSCREEN, 0);
        // We expect to have data for two pointers
        MotionEventSubject.assertThat(motionEventDynamic).hasPointerCount(2);
        MotionEventSubject.assertThat(motionEventDynamic).hasFlags(0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerId(0).isEqualTo(0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerId(1).isEqualTo(1);
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).pointerCoords(0).isEqualToWithinTolerance(coords0, MotionEventTest.TOLERANCE);
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).pointerCoords(1).isEqualToWithinTolerance(coords1, MotionEventTest.TOLERANCE);
        MotionEventSubject.assertThat(motionEventDynamic).pointerProperties(0).isEqualTo(properties0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerProperties(1).isEqualTo(properties1);
    }

    @Test
    public void testObtainNoHistory() {
        // Add two batch to one of our events
        motionEvent2.addBatch(((eventTime) + 10), ((MotionEventTest.X_3F) + 5.0F), ((MotionEventTest.Y_4F) + 5.0F), 0.5F, 0.5F, 0);
        motionEvent2.addBatch(((eventTime) + 20), ((MotionEventTest.X_3F) + 10.0F), ((MotionEventTest.Y_4F) + 15.0F), 2.0F, 3.0F, 0);
        // The newly added batch should be the "new" values of the event
        MotionEventSubject.assertThat(motionEvent2).x().isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.X_3F) + 10.0F));
        MotionEventSubject.assertThat(motionEvent2).y().isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.Y_4F) + 15.0F));
        MotionEventSubject.assertThat(motionEvent2).pressure().isWithin(MotionEventTest.TOLERANCE).of(2.0F);
        MotionEventSubject.assertThat(motionEvent2).size().isWithin(MotionEventTest.TOLERANCE).of(3.0F);
        MotionEventSubject.assertThat(motionEvent2).hasEventTime(((eventTime) + 20));
        // We should have history with 2 entries
        MotionEventSubject.assertThat(motionEvent2).hasHistorySize(2);
        // The previous data should be history at index 1
        MotionEventSubject.assertThat(motionEvent2).historicalX(1).isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.X_3F) + 5.0F));
        MotionEventSubject.assertThat(motionEvent2).historicalY(1).isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.Y_4F) + 5.0F));
        MotionEventSubject.assertThat(motionEvent2).historicalPressure(1).isWithin(MotionEventTest.TOLERANCE).of(0.5F);
        MotionEventSubject.assertThat(motionEvent2).historicalSize(1).isWithin(MotionEventTest.TOLERANCE).of(0.5F);
        MotionEventSubject.assertThat(motionEvent2).historicalEventTime(1).isEqualTo(((eventTime) + 10));
        // And the original data should be history at index 0
        MotionEventSubject.assertThat(motionEvent2).historicalX(0).isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_3F);
        MotionEventSubject.assertThat(motionEvent2).historicalY(0).isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_4F);
        MotionEventSubject.assertThat(motionEvent2).historicalPressure(0).isWithin(MotionEventTest.TOLERANCE).of(1.0F);
        MotionEventSubject.assertThat(motionEvent2).historicalSize(0).isWithin(MotionEventTest.TOLERANCE).of(1.0F);
        MotionEventSubject.assertThat(motionEvent2).historicalEventTime(0).isEqualTo(eventTime);
        motionEventDynamic = MotionEvent.obtainNoHistory(motionEvent2);
        // The newly obtained event should have the matching current content and no history
        MotionEventSubject.assertThat(motionEventDynamic).x().isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.X_3F) + 10.0F));
        MotionEventSubject.assertThat(motionEventDynamic).y().isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.Y_4F) + 15.0F));
        MotionEventSubject.assertThat(motionEventDynamic).pressure().isWithin(MotionEventTest.TOLERANCE).of(2.0F);
        MotionEventSubject.assertThat(motionEventDynamic).size().isWithin(MotionEventTest.TOLERANCE).of(3.0F);
        MotionEventSubject.assertThat(motionEventDynamic).hasHistorySize(0);
    }

    @Test
    public void testAccessAction() {
        MotionEventSubject.assertThat(motionEvent1).hasAction(ACTION_MOVE);
        motionEvent1.setAction(ACTION_UP);
        MotionEventSubject.assertThat(motionEvent1).hasAction(ACTION_UP);
    }

    @Test
    public void testDescribeContents() {
        // make sure this method never throw any exception.
        motionEvent2.describeContents();
    }

    @Test
    public void testAccessEdgeFlags() {
        MotionEventSubject.assertThat(motionEvent2).hasEdgeFlags(MotionEventTest.EDGE_FLAGS);
        motionEvent2.setEdgeFlags(10);
        MotionEventSubject.assertThat(motionEvent2).hasEdgeFlags(10);
    }

    @Test
    public void testWriteToParcel() {
        Parcel parcel = Parcel.obtain();
        motionEvent2.writeToParcel(parcel, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.setDataPosition(0);
        MotionEvent motionEvent = CREATOR.createFromParcel(parcel);
        MotionEventSubject.assertThat(motionEvent).rawY().isWithin(MotionEventTest.TOLERANCE).of(motionEvent2.getRawY());
        MotionEventSubject.assertThat(motionEvent).rawX().isWithin(MotionEventTest.TOLERANCE).of(motionEvent2.getRawX());
        MotionEventSubject.assertThat(motionEvent).y().isWithin(MotionEventTest.TOLERANCE).of(motionEvent2.getY());
        MotionEventSubject.assertThat(motionEvent).x().isWithin(MotionEventTest.TOLERANCE).of(motionEvent2.getX());
        MotionEventSubject.assertThat(motionEvent).hasAction(motionEvent2.getAction());
        MotionEventSubject.assertThat(motionEvent).hasDownTime(motionEvent2.getDownTime());
        MotionEventSubject.assertThat(motionEvent).hasEventTime(motionEvent2.getEventTime());
        MotionEventSubject.assertThat(motionEvent).hasEdgeFlags(motionEvent2.getEdgeFlags());
        MotionEventSubject.assertThat(motionEvent).hasDeviceId(motionEvent2.getDeviceId());
    }

    @Test
    public void testReadFromParcelWithInvalidPointerCountSize() {
        Parcel parcel = Parcel.obtain();
        motionEvent2.writeToParcel(parcel, PARCELABLE_WRITE_RETURN_VALUE);
        // Move to pointer id count.
        parcel.setDataPosition(4);
        parcel.writeInt(17);
        parcel.setDataPosition(0);
        try {
            CREATOR.createFromParcel(parcel);
            Assert.fail("deserialized invalid parcel");
        } catch (RuntimeException e) {
            // Expected.
        }
    }

    @Test
    public void testToString() {
        // make sure this method never throw exception.
        motionEvent2.toString();
    }

    @Test
    public void testOffsetLocation() {
        MotionEventSubject.assertThat(motionEvent2).x().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_3F);
        MotionEventSubject.assertThat(motionEvent2).y().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_4F);
        float offsetX = 1.0F;
        float offsetY = 1.0F;
        motionEvent2.offsetLocation(offsetX, offsetY);
        MotionEventSubject.assertThat(motionEvent2).x().isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.X_3F) + offsetX));
        MotionEventSubject.assertThat(motionEvent2).y().isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.Y_4F) + offsetY));
    }

    @Test
    public void testSetLocation() {
        MotionEventSubject.assertThat(motionEvent2).x().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_3F);
        MotionEventSubject.assertThat(motionEvent2).y().isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_4F);
        motionEvent2.setLocation(2.0F, 2.0F);
        MotionEventSubject.assertThat(motionEvent2).x().isWithin(MotionEventTest.TOLERANCE).of(2.0F);
        MotionEventSubject.assertThat(motionEvent2).y().isWithin(MotionEventTest.TOLERANCE).of(2.0F);
    }

    @Test
    public void testGetHistoricalData() {
        MotionEventSubject.assertThat(motionEvent2).hasHistorySize(0);
        motionEvent2.addBatch(((eventTime) + 10), ((MotionEventTest.X_3F) + 5.0F), ((MotionEventTest.Y_4F) + 5.0F), 0.5F, 0.5F, 0);
        // The newly added batch should be the "new" values of the event
        MotionEventSubject.assertThat(motionEvent2).x().isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.X_3F) + 5.0F));
        MotionEventSubject.assertThat(motionEvent2).y().isWithin(MotionEventTest.TOLERANCE).of(((MotionEventTest.Y_4F) + 5.0F));
        MotionEventSubject.assertThat(motionEvent2).pressure().isWithin(MotionEventTest.TOLERANCE).of(0.5F);
        MotionEventSubject.assertThat(motionEvent2).size().isWithin(MotionEventTest.TOLERANCE).of(0.5F);
        MotionEventSubject.assertThat(motionEvent2).hasEventTime(((eventTime) + 10));
        // We should have history with 1 entry
        MotionEventSubject.assertThat(motionEvent2).hasHistorySize(1);
        // And the previous / original data should be history at index 0
        MotionEventSubject.assertThat(motionEvent2).historicalEventTime(0).isEqualTo(eventTime);
        MotionEventSubject.assertThat(motionEvent2).historicalX(0).isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.X_3F);
        MotionEventSubject.assertThat(motionEvent2).historicalY(0).isWithin(MotionEventTest.TOLERANCE).of(MotionEventTest.Y_4F);
        MotionEventSubject.assertThat(motionEvent2).historicalPressure(0).isWithin(MotionEventTest.TOLERANCE).of(1.0F);
        MotionEventSubject.assertThat(motionEvent2).historicalSize(0).isWithin(MotionEventTest.TOLERANCE).of(1.0F);
    }

    @Test
    public void testGetCurrentDataWithTwoPointers() {
        PointerCoords coords0 = PointerCoordsBuilder.newBuilder().setCoords(10.0F, 20.0F).setPressure(1.2F).setSize(2.0F).setTool(1.2F, 1.4F).build();
        PointerCoords coords1 = PointerCoordsBuilder.newBuilder().setCoords(30.0F, 40.0F).setPressure(1.4F).setSize(3.0F).setTouch(2.2F, 0.6F).build();
        PointerProperties properties0 = PointerPropertiesBuilder.newBuilder().setId(0).setToolType(TOOL_TYPE_FINGER).build();
        PointerProperties properties1 = PointerPropertiesBuilder.newBuilder().setId(1).setToolType(TOOL_TYPE_FINGER).build();
        motionEventDynamic = MotionEvent.obtain(downTime, eventTime, ACTION_MOVE, 2, new PointerProperties[]{ properties0, properties1 }, new PointerCoords[]{ coords0, coords1 }, 0, 0, 1.0F, 1.0F, 0, 0, SOURCE_TOUCHSCREEN, 0);
        // We expect to have data for two pointers
        MotionEventSubject.assertThat(motionEventDynamic).pointerId(0).isEqualTo(0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerId(1).isEqualTo(1);
        MotionEventSubject.assertThat(motionEventDynamic).hasPointerCount(2);
        MotionEventSubject.assertThat(motionEventDynamic).hasFlags(0);
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).pointerCoords(0).isEqualToWithinTolerance(coords0, MotionEventTest.TOLERANCE);
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).pointerCoords(1).isEqualToWithinTolerance(coords1, MotionEventTest.TOLERANCE);
        MotionEventSubject.assertThat(motionEventDynamic).pointerProperties(0).isEqualTo(properties0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerProperties(1).isEqualTo(properties1);
    }

    @Test
    public void testGetHistoricalDataWithTwoPointers() {
        // PHASE 1 - construct the initial data for the event
        PointerCoords coordsInitial0 = PointerCoordsBuilder.newBuilder().setCoords(10.0F, 20.0F).setPressure(1.2F).setSize(2.0F).setTool(1.2F, 1.4F).setTouch(0.7F, 0.6F).setOrientation(2.0F).build();
        PointerCoords coordsInitial1 = PointerCoordsBuilder.newBuilder().setCoords(30.0F, 40.0F).setPressure(1.4F).setSize(3.0F).setTool(1.3F, 1.7F).setTouch(2.7F, 3.6F).setOrientation(1.0F).build();
        PointerProperties properties0 = PointerPropertiesBuilder.newBuilder().setId(0).setToolType(TOOL_TYPE_FINGER).build();
        PointerProperties properties1 = PointerPropertiesBuilder.newBuilder().setId(1).setToolType(TOOL_TYPE_FINGER).build();
        motionEventDynamic = MotionEvent.obtain(downTime, eventTime, ACTION_MOVE, 2, new PointerProperties[]{ properties0, properties1 }, new PointerCoords[]{ coordsInitial0, coordsInitial1 }, 0, 0, 1.0F, 1.0F, 0, 0, SOURCE_TOUCHSCREEN, 0);
        // We expect to have data for two pointers
        MotionEventSubject.assertThat(motionEventDynamic).hasPointerCount(2);
        MotionEventSubject.assertThat(motionEventDynamic).pointerId(0).isEqualTo(0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerId(1).isEqualTo(1);
        MotionEventSubject.assertThat(motionEventDynamic).hasFlags(0);
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).pointerCoords(0).isEqualToWithinTolerance(coordsInitial0, MotionEventTest.TOLERANCE);
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).pointerCoords(1).isEqualToWithinTolerance(coordsInitial1, MotionEventTest.TOLERANCE);
        MotionEventSubject.assertThat(motionEventDynamic).pointerProperties(0).isEqualTo(properties0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerProperties(1).isEqualTo(properties1);
        // PHASE 2 - add a new batch of data to our event
        PointerCoords coordsNext0 = PointerCoordsBuilder.newBuilder().setCoords(15.0F, 25.0F).setPressure(1.6F).setSize(2.2F).setTool(1.2F, 1.4F).setTouch(1.0F, 0.9F).setOrientation(2.2F).build();
        PointerCoords coordsNext1 = PointerCoordsBuilder.newBuilder().setCoords(35.0F, 45.0F).setPressure(1.8F).setSize(3.2F).setTool(1.2F, 1.4F).setTouch(0.7F, 0.6F).setOrientation(2.9F).build();
        motionEventDynamic.addBatch(((eventTime) + 10), new PointerCoords[]{ coordsNext0, coordsNext1 }, 0);
        // We still expect to have data for two pointers
        MotionEventSubject.assertThat(motionEventDynamic).hasPointerCount(2);
        MotionEventSubject.assertThat(motionEventDynamic).pointerId(0).isEqualTo(0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerId(1).isEqualTo(1);
        MotionEventSubject.assertThat(motionEventDynamic).hasFlags(0);
        // The newly added batch should be the "new" values of the event
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).pointerCoords(0).isEqualToWithinTolerance(coordsNext0, MotionEventTest.TOLERANCE);
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).pointerCoords(1).isEqualToWithinTolerance(coordsNext1, MotionEventTest.TOLERANCE);
        MotionEventSubject.assertThat(motionEventDynamic).pointerProperties(0).isEqualTo(properties0);
        MotionEventSubject.assertThat(motionEventDynamic).pointerProperties(1).isEqualTo(properties1);
        MotionEventSubject.assertThat(motionEventDynamic).hasEventTime(((eventTime) + 10));
        // We should have history with 1 entry
        MotionEventSubject.assertThat(motionEventDynamic).hasHistorySize(1);
        // And the previous / original data should be history at position 0
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).historicalPointerCoords(0, 0).isEqualToWithinTolerance(coordsInitial0, MotionEventTest.TOLERANCE);
        MotionEventTest.MotionEventEqualitySubject.assertThat(motionEventDynamic).historicalPointerCoords(1, 0).isEqualToWithinTolerance(coordsInitial1, MotionEventTest.TOLERANCE);
    }

    @Test
    public void testGetHistorySize() {
        long eventTime = SystemClock.uptimeMillis();
        float x = 10.0F;
        float y = 20.0F;
        float pressure = 1.0F;
        float size = 1.0F;
        motionEvent2.setAction(ACTION_DOWN);
        MotionEventSubject.assertThat(motionEvent2).hasHistorySize(0);
        motionEvent2.addBatch(eventTime, x, y, pressure, size, 0);
        MotionEventSubject.assertThat(motionEvent2).hasHistorySize(1);
    }

    @Test
    public void testRecycle() {
        motionEvent2.recycle();
        try {
            motionEvent2.recycle();
            Assert.fail("recycle() should throw an exception when the event has already been recycled.");
        } catch (RuntimeException ex) {
            // expected
        }
        motionEvent2 = null;// since it was recycled, don't try to recycle again in tear down

    }

    @Test
    public void testTransformShouldApplyMatrixToPointsAndPreserveRawPosition() {
        // Generate some points on a circle.
        // Each point 'i' is a point on a circle of radius ROTATION centered at (3,2) at an angle
        // of ARC * i degrees clockwise relative to the Y axis.
        // The geometrical representation is irrelevant to the test, it's just easy to generate
        // and check rotation.  We set the orientation to the same angle.
        // Coordinate system: down is increasing Y, right is increasing X.
        final float pi180 = ((float) ((Math.PI) / 180));
        final float radius = 10;
        final float arc = 36;
        final float rotation = arc * 2;
        final int pointerCount = 11;
        final int[] pointerIds = new int[pointerCount];
        final PointerCoords[] pointerCoords = new PointerCoords[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            final PointerCoords c = new PointerCoords();
            final float angle = ((float) ((i * arc) * pi180));
            pointerIds[i] = i;
            pointerCoords[i] = c;
            c.x = ((float) (((Math.sin(angle)) * radius) + 3));
            c.y = ((float) (((-(Math.cos(angle))) * radius) + 2));
            c.orientation = angle;
        }
        final MotionEvent event = MotionEvent.obtain(0, 0, ACTION_MOVE, pointerCount, pointerIds, pointerCoords, 0, 0, 0, 0, 0, 0, 0);
        final float originalRawX = 0 + 3;
        final float originalRawY = (-radius) + 2;
        // Check original raw X and Y assumption.
        MotionEventSubject.assertThat(event).rawX().isWithin(MotionEventTest.TOLERANCE).of(originalRawX);
        MotionEventSubject.assertThat(event).rawY().isWithin(MotionEventTest.TOLERANCE).of(originalRawY);
        // Now translate the motion event so the circle's origin is at (0,0).
        event.offsetLocation((-3), (-2));
        // Offsetting the location should preserve the raw X and Y of the first point.
        MotionEventSubject.assertThat(event).rawX().isWithin(MotionEventTest.TOLERANCE).of(originalRawX);
        MotionEventSubject.assertThat(event).rawY().isWithin(MotionEventTest.TOLERANCE).of(originalRawY);
        // Apply a rotation about the origin by ROTATION degrees clockwise.
        Matrix matrix = new Matrix();
        matrix.setRotate(rotation);
        event.transform(matrix);
        // Check the points.
        for (int i = 0; i < pointerCount; i++) {
            final PointerCoords c = pointerCoords[i];
            event.getPointerCoords(i, c);
            final float angle = ((float) (((i * arc) + rotation) * pi180));
            MotionEventSubject.assertThat(event).pointerCoords(i).x().isWithin(MotionEventTest.TOLERANCE).of(((float) ((Math.sin(angle)) * radius)));
            MotionEventSubject.assertThat(event).pointerCoords(i).y().isWithin(MotionEventTest.TOLERANCE).of(((-((float) (Math.cos(angle)))) * radius));
            MotionEventSubject.assertThat(Math.tan(c.orientation)).isWithin(0.1F).of(Math.tan(angle));
        }
        // Applying the transformation should preserve the raw X and Y of the first point.
        MotionEventSubject.assertThat(event).rawX().isWithin(MotionEventTest.TOLERANCE).of(originalRawX);
        MotionEventSubject.assertThat(event).rawY().isWithin(MotionEventTest.TOLERANCE).of(originalRawY);
    }

    @Test
    public void testPointerCoordsCopyConstructor() {
        PointerCoords coords = new PointerCoords();
        coords.x = 1;
        coords.y = 2;
        coords.pressure = 3;
        coords.size = 4;
        coords.touchMajor = 5;
        coords.touchMinor = 6;
        coords.toolMajor = 7;
        coords.toolMinor = 8;
        coords.orientation = 9;
        coords.setAxisValue(AXIS_GENERIC_1, 10);
        PointerCoords copy = new PointerCoords(coords);
        MotionEventSubject.assertThat(copy).x().isWithin(MotionEventTest.TOLERANCE).of(1.0F);
        MotionEventSubject.assertThat(copy).y().isWithin(MotionEventTest.TOLERANCE).of(2.0F);
        MotionEventSubject.assertThat(copy).pressure().isWithin(MotionEventTest.TOLERANCE).of(3.0F);
        MotionEventSubject.assertThat(copy).size().isWithin(MotionEventTest.TOLERANCE).of(4.0F);
        MotionEventSubject.assertThat(copy).touchMajor().isWithin(MotionEventTest.TOLERANCE).of(5.0F);
        MotionEventSubject.assertThat(copy).touchMinor().isWithin(MotionEventTest.TOLERANCE).of(6.0F);
        MotionEventSubject.assertThat(copy).toolMajor().isWithin(MotionEventTest.TOLERANCE).of(7.0F);
        MotionEventSubject.assertThat(copy).toolMinor().isWithin(MotionEventTest.TOLERANCE).of(8.0F);
        MotionEventSubject.assertThat(copy).orientation().isWithin(MotionEventTest.TOLERANCE).of(9.0F);
        MotionEventSubject.assertThat(copy).axisValue(AXIS_GENERIC_1).isWithin(MotionEventTest.TOLERANCE).of(10.0F);
    }

    @Test
    public void testPointerCoordsCopyFrom() {
        PointerCoords coords = new PointerCoords();
        coords.x = 1;
        coords.y = 2;
        coords.pressure = 3;
        coords.size = 4;
        coords.touchMajor = 5;
        coords.touchMinor = 6;
        coords.toolMajor = 7;
        coords.toolMinor = 8;
        coords.orientation = 9;
        coords.setAxisValue(AXIS_GENERIC_1, 10);
        PointerCoords copy = new PointerCoords();
        copy.copyFrom(coords);
        MotionEventSubject.assertThat(copy).x().isWithin(MotionEventTest.TOLERANCE).of(1.0F);
        MotionEventSubject.assertThat(copy).y().isWithin(MotionEventTest.TOLERANCE).of(2.0F);
        MotionEventSubject.assertThat(copy).pressure().isWithin(MotionEventTest.TOLERANCE).of(3.0F);
        MotionEventSubject.assertThat(copy).size().isWithin(MotionEventTest.TOLERANCE).of(4.0F);
        MotionEventSubject.assertThat(copy).touchMajor().isWithin(MotionEventTest.TOLERANCE).of(5.0F);
        MotionEventSubject.assertThat(copy).touchMinor().isWithin(MotionEventTest.TOLERANCE).of(6.0F);
        MotionEventSubject.assertThat(copy).toolMajor().isWithin(MotionEventTest.TOLERANCE).of(7.0F);
        MotionEventSubject.assertThat(copy).toolMinor().isWithin(MotionEventTest.TOLERANCE).of(8.0F);
        MotionEventSubject.assertThat(copy).orientation().isWithin(MotionEventTest.TOLERANCE).of(9.0F);
        MotionEventSubject.assertThat(copy).axisValue(AXIS_GENERIC_1).isWithin(MotionEventTest.TOLERANCE).of(10.0F);
    }

    @Test
    public void testPointerPropertiesDefaultConstructor() {
        PointerProperties properties = new PointerProperties();
        MotionEventSubject.assertThat(properties).hasId(INVALID_POINTER_ID);
        MotionEventSubject.assertThat(properties).hasToolType(TOOL_TYPE_UNKNOWN);
    }

    @Test
    public void testPointerPropertiesCopyConstructor() {
        PointerProperties properties = new PointerProperties();
        properties.id = 1;
        properties.toolType = TOOL_TYPE_MOUSE;
        PointerProperties copy = new PointerProperties(properties);
        MotionEventSubject.assertThat(copy).hasId(1);
        MotionEventSubject.assertThat(copy).hasToolType(TOOL_TYPE_MOUSE);
    }

    @Test
    public void testPointerPropertiesCopyFrom() {
        PointerProperties properties = new PointerProperties();
        properties.id = 1;
        properties.toolType = TOOL_TYPE_MOUSE;
        PointerProperties copy = new PointerProperties();
        copy.copyFrom(properties);
        MotionEventSubject.assertThat(copy).hasId(1);
        MotionEventSubject.assertThat(properties).hasToolType(TOOL_TYPE_MOUSE);
    }

    @Test
    public void testAxisFromToString() {
        MotionEventSubject.assertThat(MotionEvent.axisToString(AXIS_RTRIGGER)).isEqualTo("AXIS_RTRIGGER");
        MotionEventSubject.assertThat(MotionEvent.axisFromString("AXIS_RTRIGGER")).isEqualTo(AXIS_RTRIGGER);
    }

    private static class MotionEventEqualitySubject extends Subject<MotionEventTest.MotionEventEqualitySubject, MotionEvent> {
        private MotionEventEqualitySubject(FailureMetadata metadata, MotionEvent actual) {
            super(metadata, actual);
        }

        public static MotionEventTest.MotionEventEqualitySubject assertThat(MotionEvent event) {
            return Truth.assertAbout(motionEvents()).that(event);
        }

        public static Factory<MotionEventTest.MotionEventEqualitySubject, MotionEvent> motionEvents() {
            return MotionEventTest.MotionEventEqualitySubject::new;
        }

        public MotionEventTest.PointerCoordsEqualitySubject pointerCoords(int pointerIndex) {
            PointerCoords outPointerCoords = new PointerCoords();
            actual().getPointerCoords(pointerIndex, outPointerCoords);
            return check("getPointerCoords(%s)", pointerIndex).about(MotionEventTest.PointerCoordsEqualitySubject.pointerCoords()).that(outPointerCoords);
        }

        public MotionEventTest.PointerCoordsEqualitySubject historicalPointerCoords(int pointerIndex, int pos) {
            PointerCoords outPointerCoords = new PointerCoords();
            actual().getHistoricalPointerCoords(pointerIndex, pos, outPointerCoords);
            return check("getHistoricalPointerCoords(%s, %s)", pointerIndex, pos).about(MotionEventTest.PointerCoordsEqualitySubject.pointerCoords()).that(outPointerCoords);
        }

        /**
         * Asserts that the given MotionEvent matches the current subject.
         */
        public void isEqualToWithinTolerance(MotionEvent other, float tolerance) {
            check("getDownTime()").that(actual().getDownTime()).isEqualTo(other.getDownTime());
            check("getEventTime()").that(actual().getEventTime()).isEqualTo(other.getEventTime());
            check("action()").that(actual().getAction()).isEqualTo(other.getAction());
            check("buttonState()").that(actual().getButtonState()).isEqualTo(other.getButtonState());
            check("deviceId()").that(actual().getDeviceId()).isEqualTo(other.getDeviceId());
            check("getFlags()").that(actual().getFlags()).isEqualTo(other.getFlags());
            check("getEdgeFlags()").that(actual().getEdgeFlags()).isEqualTo(other.getEdgeFlags());
            check("getXPrecision()").that(actual().getXPrecision()).isEqualTo(other.getXPrecision());
            check("getYPrecision()").that(actual().getYPrecision()).isEqualTo(other.getYPrecision());
            check("getX()").that(actual().getX()).isWithin(tolerance).of(other.getX());
            check("getY()").that(actual().getY()).isWithin(tolerance).of(other.getY());
            check("getPressure()").that(actual().getPressure()).isWithin(tolerance).of(other.getPressure());
            check("getSize()").that(actual().getSize()).isWithin(tolerance).of(other.getSize());
            check("getTouchMajor()").that(actual().getTouchMajor()).isWithin(tolerance).of(other.getTouchMajor());
            check("getTouchMinor()").that(actual().getTouchMinor()).isWithin(tolerance).of(other.getTouchMinor());
            check("getToolMajor()").that(actual().getToolMajor()).isWithin(tolerance).of(other.getToolMajor());
            check("getToolMinor()").that(actual().getToolMinor()).isWithin(tolerance).of(other.getToolMinor());
            check("getOrientation()").that(actual().getOrientation()).isWithin(tolerance).of(other.getOrientation());
            check("getPointerCount()").that(actual().getPointerCount()).isEqualTo(other.getPointerCount());
            for (int i = 1; i < (actual().getPointerCount()); i++) {
                check("getX(%s)", i).that(actual().getX(i)).isWithin(tolerance).of(other.getX(i));
                check("getY(%s)", i).that(actual().getY(i)).isWithin(tolerance).of(other.getY(i));
                check("getPressure(%s)", i).that(actual().getPressure(i)).isWithin(tolerance).of(other.getPressure(i));
                check("getSize(%s)", i).that(actual().getSize(i)).isWithin(tolerance).of(other.getSize(i));
                check("getTouchMajor(%s)", i).that(actual().getTouchMajor(i)).isWithin(tolerance).of(other.getTouchMajor(i));
                check("getTouchMinor(%s)", i).that(actual().getTouchMinor(i)).isWithin(tolerance).of(other.getTouchMinor(i));
                check("getToolMajor(%s)", i).that(actual().getToolMajor(i)).isWithin(tolerance).of(other.getToolMajor(i));
                check("getToolMinor(%s)", i).that(actual().getToolMinor(i)).isWithin(tolerance).of(other.getToolMinor(i));
                check("getOrientation(%s)", i).that(actual().getOrientation(i)).isWithin(tolerance).of(other.getOrientation(i));
            }
            check("getHistorySize()").that(actual().getHistorySize()).isEqualTo(other.getHistorySize());
            for (int i = 0; i < (actual().getHistorySize()); i++) {
                check("getHistoricalX(%s)", i).that(actual().getX(i)).isWithin(tolerance).of(other.getX(i));
                check("getHistoricalY(%s)", i).that(actual().getHistoricalY(i)).isWithin(tolerance).of(other.getHistoricalY(i));
                check("getHistoricalPressure(%s)", i).that(actual().getHistoricalPressure(i)).isWithin(tolerance).of(other.getHistoricalPressure(i));
                check("getHistoricalSize(%s)", i).that(actual().getHistoricalSize(i)).isWithin(tolerance).of(other.getHistoricalSize(i));
                check("getHistoricalTouchMajor(%s)", i).that(actual().getHistoricalTouchMajor(i)).isWithin(tolerance).of(other.getHistoricalTouchMajor(i));
                check("getHistoricalTouchMinor(%s)", i).that(actual().getHistoricalTouchMinor(i)).isWithin(tolerance).of(other.getHistoricalTouchMinor(i));
                check("getHistoricalToolMajor(%s)", i).that(actual().getHistoricalToolMajor(i)).isWithin(tolerance).of(other.getHistoricalToolMajor(i));
                check("getHistoricalToolMinor(%s)", i).that(actual().getHistoricalToolMinor(i)).isWithin(tolerance).of(other.getHistoricalToolMinor(i));
                check("getHistoricalOrientation(%s)", i).that(actual().getHistoricalOrientation(i)).isWithin(tolerance).of(other.getHistoricalOrientation(i));
            }
        }
    }

    private static class PointerCoordsEqualitySubject extends Subject<MotionEventTest.PointerCoordsEqualitySubject, PointerCoords> {
        private PointerCoordsEqualitySubject(FailureMetadata metadata, PointerCoords actual) {
            super(metadata, actual);
        }

        public static MotionEventTest.PointerCoordsEqualitySubject assertThat(PointerCoords coords) {
            return Truth.assertAbout(pointerCoords()).that(coords);
        }

        public static Factory<MotionEventTest.PointerCoordsEqualitySubject, PointerCoords> pointerCoords() {
            return MotionEventTest.PointerCoordsEqualitySubject::new;
        }

        public void isEqualToWithinTolerance(PointerCoords other, float tolerance) {
            check("orientation").that(actual().orientation).isWithin(tolerance).of(other.orientation);
            check("pressure").that(actual().pressure).isWithin(tolerance).of(other.pressure);
            check("size").that(actual().size).isWithin(tolerance).of(other.size);
            check("toolMajor").that(actual().toolMajor).isWithin(tolerance).of(other.toolMajor);
            check("toolMinor").that(actual().toolMinor).isWithin(tolerance).of(other.toolMinor);
            check("touchMajor").that(actual().touchMajor).isWithin(tolerance).of(other.touchMajor);
            check("touchMinor").that(actual().touchMinor).isWithin(tolerance).of(other.touchMinor);
            check("x").that(actual().x).isWithin(tolerance).of(other.x);
            check("y").that(actual().y).isWithin(tolerance).of(other.y);
        }
    }
}


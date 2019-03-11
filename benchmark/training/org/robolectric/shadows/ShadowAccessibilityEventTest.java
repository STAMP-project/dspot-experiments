package org.robolectric.shadows;


import AccessibilityEvent.CREATOR;
import AccessibilityEvent.TYPE_ANNOUNCEMENT;
import ShadowAccessibilityRecord.NO_VIRTUAL_ID;
import android.app.Notification;
import android.os.Parcel;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowAccessibilityEventTest {
    private AccessibilityEvent event;

    @Test
    public void shouldRecordParcelables() {
        final Notification notification = new Notification();
        event.setParcelableData(notification);
        AccessibilityEvent anotherEvent = AccessibilityEvent.obtain(event);
        assertThat(anotherEvent.getParcelableData()).isInstanceOf(Notification.class);
        assertThat(anotherEvent.getParcelableData()).isEqualTo(notification);
        anotherEvent.recycle();
    }

    @Test
    public void shouldBeEqualToClonedEvent() {
        event.setEventType(TYPE_ANNOUNCEMENT);
        AccessibilityEvent newEvent = AccessibilityEvent.obtain(event);
        assertThat(event.getEventType()).isEqualTo(newEvent.getEventType());
        assertThat(event.isEnabled()).isEqualTo(newEvent.isEnabled());
        assertThat(event.getContentDescription()).isEqualTo(newEvent.getContentDescription());
        assertThat(event.getPackageName()).isEqualTo(newEvent.getPackageName());
        assertThat(event.getClassName()).isEqualTo(newEvent.getClassName());
        assertThat(event.getParcelableData()).isEqualTo(newEvent.getParcelableData());
        newEvent.recycle();
    }

    @Test
    public void shouldWriteAndReadFromParcelCorrectly() {
        Parcel p = Parcel.obtain();
        event.setContentDescription("test");
        event.writeToParcel(p, 0);
        p.setDataPosition(0);
        AccessibilityEvent anotherEvent = CREATOR.createFromParcel(p);
        assertThat(anotherEvent.getEventType()).isEqualTo(event.getEventType());
        assertThat(anotherEvent.isEnabled()).isEqualTo(event.isEnabled());
        assertThat(anotherEvent.getContentDescription()).isEqualTo(event.getContentDescription());
        assertThat(anotherEvent.getPackageName()).isEqualTo(event.getPackageName());
        assertThat(anotherEvent.getClassName()).isEqualTo(event.getClassName());
        assertThat(anotherEvent.getParcelableData()).isEqualTo(event.getParcelableData());
        anotherEvent.setContentDescription(null);
        anotherEvent.recycle();
    }

    @Test
    public void shouldHaveCurrentSourceId() {
        TextView rootView = new TextView(ApplicationProvider.getApplicationContext());
        event.setSource(rootView);
        assertThat(Shadows.shadowOf(event).getSourceRoot()).isEqualTo(rootView);
        assertThat(Shadows.shadowOf(event).getVirtualDescendantId()).isEqualTo(NO_VIRTUAL_ID);
        event.setSource(rootView, 1);
        assertThat(Shadows.shadowOf(event).getVirtualDescendantId()).isEqualTo(1);
    }

    @Test
    public void setSourceNode() {
        AccessibilityNodeInfo node = AccessibilityNodeInfo.obtain();
        Shadows.shadowOf(event).setSourceNode(node);
        assertThat(event.getSource()).isEqualTo(node);
        node.recycle();
    }

    @Test
    public void setWindowId() {
        int id = 2;
        Shadows.shadowOf(event).setWindowId(id);
        assertThat(event.getWindowId()).isEqualTo(id);
    }
}


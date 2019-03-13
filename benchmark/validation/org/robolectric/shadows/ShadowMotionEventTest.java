package org.robolectric.shadows;


import MotionEvent.ACTION_MOVE;
import android.view.MotionEvent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowMotionEventTest {
    private MotionEvent event;

    private ShadowMotionEvent shadowMotionEvent;

    @Test
    public void addingSecondPointerSetsCount() {
        assertThat(event.getX(0)).isEqualTo(5.0F);
        assertThat(event.getY(0)).isEqualTo(10.0F);
        assertThat(event.getPointerCount()).isEqualTo(1);
        shadowMotionEvent.setPointer2(20.0F, 30.0F);
        assertThat(event.getX(1)).isEqualTo(20.0F);
        assertThat(event.getY(1)).isEqualTo(30.0F);
        assertThat(event.getPointerCount()).isEqualTo(2);
    }

    @Test
    public void canSetPointerIdsByIndex() {
        shadowMotionEvent.setPointer2(20.0F, 30.0F);
        shadowMotionEvent.setPointerIds(2, 5);
        Assert.assertEquals(2, event.getPointerId(0));
        Assert.assertEquals(5, event.getPointerId(1));
    }

    @Test
    public void indexShowsUpInAction() {
        shadowMotionEvent.setPointerIndex(1);
        Assert.assertEquals(((1 << (MotionEvent.ACTION_POINTER_ID_SHIFT)) | (MotionEvent.ACTION_MOVE)), event.getAction());
    }

    @Test
    public void canGetActionIndex() {
        Assert.assertEquals(0, event.getActionIndex());
        shadowMotionEvent.setPointerIndex(1);
        Assert.assertEquals(1, event.getActionIndex());
    }

    @Test
    public void getActionMaskedStripsPointerIndexFromAction() {
        Assert.assertEquals(ACTION_MOVE, event.getActionMasked());
        shadowMotionEvent.setPointerIndex(1);
        Assert.assertEquals(ACTION_MOVE, event.getActionMasked());
    }

    @Test
    public void canFindPointerIndexFromId() {
        shadowMotionEvent.setPointer2(20.0F, 30.0F);
        shadowMotionEvent.setPointerIds(2, 1);
        Assert.assertEquals(0, event.findPointerIndex(2));
        Assert.assertEquals(1, event.findPointerIndex(1));
        Assert.assertEquals((-1), event.findPointerIndex(3));
    }
}


package org.robolectric.shadows;


import android.view.VelocityTracker;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class VelocityTrackerTest {
    VelocityTracker velocityTracker;

    @Test
    public void handlesXMovement() {
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(0, 0, 0));
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(20, 20, 0));
        velocityTracker.computeCurrentVelocity(1);
        // active pointer
        assertThat(velocityTracker.getXVelocity()).isEqualTo(1.0F);
        assertThat(velocityTracker.getXVelocity(0)).isEqualTo(1.0F);
        // inactive pointer
        assertThat(velocityTracker.getXVelocity(10)).isEqualTo(0.0F);
    }

    @Test
    public void handlesYMovement() {
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(0, 0, 0));
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(20, 0, 20));
        velocityTracker.computeCurrentVelocity(1);
        // active pointer
        assertThat(velocityTracker.getYVelocity()).isEqualTo(1.0F);
        assertThat(velocityTracker.getYVelocity(0)).isEqualTo(1.0F);
        // inactive pointer
        assertThat(velocityTracker.getYVelocity(10)).isEqualTo(0.0F);
    }

    @Test
    public void handlesXAndYMovement() {
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(0, 0, 0));
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(20, 20, 40));
        velocityTracker.computeCurrentVelocity(1);
        assertThat(velocityTracker.getXVelocity()).isEqualTo(1.0F);
        assertThat(velocityTracker.getYVelocity()).isEqualTo(2.0F);
    }

    @Test
    public void handlesWindowing_positive() {
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(0, 0, 0));
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(20, 10000, 10000));
        velocityTracker.computeCurrentVelocity(1, 10);
        assertThat(velocityTracker.getXVelocity()).isEqualTo(10.0F);
        assertThat(velocityTracker.getYVelocity()).isEqualTo(10.0F);
    }

    @Test
    public void handlesWindowing_negative() {
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(0, 0, 0));
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(20, (-10000), (-10000)));
        velocityTracker.computeCurrentVelocity(1, 10);
        assertThat(velocityTracker.getXVelocity()).isEqualTo((-10.0F));
        assertThat(velocityTracker.getYVelocity()).isEqualTo((-10.0F));
    }

    @Test
    public void handlesMultiplePointers() {
        // pointer 0 active
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(0, 0, 0));
        // pointer 1 active
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(20, 40, 40, 0, 0));
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(40, 80, 80, 20, 20));
        velocityTracker.computeCurrentVelocity(1);
        // active pointer
        assertThat(velocityTracker.getXVelocity()).isEqualTo(1.0F);
        assertThat(velocityTracker.getXVelocity(1)).isEqualTo(1.0F);
        // inactive pointer
        assertThat(velocityTracker.getXVelocity(0)).isEqualTo(2.0F);
    }

    @Test
    public void handlesClearing() {
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(0, 0, 0));
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(20, 20, 20));
        velocityTracker.computeCurrentVelocity(1);
        velocityTracker.clear();
        assertThat(velocityTracker.getXVelocity()).isEqualTo(0.0F);
        assertThat(velocityTracker.getYVelocity()).isEqualTo(0.0F);
        velocityTracker.computeCurrentVelocity(1);
        assertThat(velocityTracker.getXVelocity()).isEqualTo(0.0F);
        assertThat(velocityTracker.getYVelocity()).isEqualTo(0.0F);
    }

    @Test
    public void clearsOnDown() {
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(0, 0, 0));
        velocityTracker.addMovement(VelocityTrackerTest.doMotion(20, 20, 20));
        velocityTracker.computeCurrentVelocity(1);
        velocityTracker.addMovement(VelocityTrackerTest.doPointerDown(40, 40, 40));
        velocityTracker.computeCurrentVelocity(1);
        assertThat(velocityTracker.getXVelocity()).isEqualTo(0.0F);
        assertThat(velocityTracker.getYVelocity()).isEqualTo(0.0F);
    }
}


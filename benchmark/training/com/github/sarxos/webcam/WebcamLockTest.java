package com.github.sarxos.webcam;


import org.assertj.core.api.Assertions;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This test case is to cover {@link WebcamLock} class.
 *
 * @author Bartosz Firyn (sarxos)
 */
@RunWith(EasyMockRunner.class)
public class WebcamLockTest extends EasyMockSupport {
    Webcam webcam;

    @Test
    public void test_lock() {
        WebcamLock lock = new WebcamLock(webcam);
        lock.lock();
        Assertions.assertThat(lock.isLocked()).isTrue();
        lock.unlock();
        Assertions.assertThat(lock.isLocked()).isFalse();
    }

    @Test
    public void test_lock2() {
        WebcamLock first = new WebcamLock(webcam);
        WebcamLock second = new WebcamLock(webcam);
        first.lock();
        Assertions.assertThat(second.isLocked()).isTrue();
    }
}


/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  I2CBusImplTest.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package com.pi4j.io.i2c.impl;


import I2CFactory.DEFAULT_LOCKAQUIRE_TIMEOUT;
import I2CFactory.DEFAULT_LOCKAQUIRE_TIMEOUT_UNITS;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ I2CBusImpl.class })
public class I2CBusImplTest {
    private static int BUSNUMBER = 1;

    @SuppressWarnings("unused")
    private static int FILEDESCRIPTOR = 4711;

    private static String FILENAME = "/dev/i2c-" + (I2CBusImplTest.BUSNUMBER);

    private static int DEVICE_ADDRESS = 21;

    private static long DEFAULT_TIMEOUT = DEFAULT_LOCKAQUIRE_TIMEOUT_UNITS.toMillis(DEFAULT_LOCKAQUIRE_TIMEOUT);

    private static class TestableI2CBusImpl extends I2CBusImpl {
        protected TestableI2CBusImpl(final int busNumber, String fileName, final long lockAquireTimeout, final TimeUnit lockAquireTimeoutUnit) throws UnsupportedBusNumberException, IOException {
            super(busNumber, fileName, lockAquireTimeout, lockAquireTimeoutUnit);
        }

        @SuppressWarnings("unused")
        protected String getFilenameForBusnumber(int busNumber) throws UnsupportedBusNumberException {
            if (busNumber != (I2CBusImplTest.BUSNUMBER)) {
                throw new UnsupportedBusNumberException();
            }
            return I2CBusImplTest.FILENAME;
        }
    }

    private I2CBusImpl bus;

    @Test
    public void testBasics() throws Exception {
        byte[] buffer = new byte[3];
        final int busNo = bus.getBusNumber();
        Assert.assertEquals("Got wrong busnumber from I2CBusImpl-instance!", I2CBusImplTest.BUSNUMBER, busNo);
        bus.close();
        // test for IOExceptions on using a bus already closed
        try {
            bus.readByteDirect(null);
            Assert.fail(("calling 'readByteDirect(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
        try {
            bus.readByte(null, 0);
            Assert.fail(("calling 'readByte(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
        try {
            bus.readBytesDirect(null, 0, 0, buffer);
            Assert.fail(("calling 'readBytesDirect(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
        try {
            bus.readBytes(null, 0, 0, 0, buffer);
            Assert.fail(("calling 'readBytes(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
        try {
            bus.writeByteDirect(null, Byte.MIN_VALUE);
            Assert.fail(("calling 'writeByteDirect(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
        try {
            bus.writeByte(null, 0, Byte.MIN_VALUE);
            Assert.fail(("calling 'writeByte(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
        try {
            bus.writeBytesDirect(null, 0, 0, buffer);
            Assert.fail(("calling 'writeBytesDirect(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
        try {
            bus.writeBytes(null, 0, 0, 0, buffer);
            Assert.fail(("calling 'writeBytes(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
        try {
            bus.writeAndReadBytesDirect(null, 0, 0, buffer, 0, 0, buffer);
            Assert.fail(("calling 'writeAndReadBytesDirect(...)' on a closed bus should throw " + "an IOException but did not!"));
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testConcurrency() throws Exception {
        PowerMockito.whenNew(I2CDeviceImpl.class).withArguments(bus, I2CBusImplTest.DEVICE_ADDRESS).thenReturn(Mockito.mock(I2CDeviceImpl.class));
        I2CDevice device = bus.getDevice(I2CBusImplTest.DEVICE_ADDRESS);
        Assert.assertNotNull("'I2CBus.getDevice(...)' did not return an device-instance", device);
        Assert.assertTrue("'I2CBus.getDevice(...)' does not return an instance of I2CDeviceImpl!", (device instanceof I2CDeviceImpl));
        PowerMockito.verifyNew(I2CDeviceImpl.class, Mockito.times(1)).withArguments(bus, I2CBusImplTest.DEVICE_ADDRESS);
        Mockito.when(device.getAddress()).thenReturn(I2CBusImplTest.DEVICE_ADDRESS);
        I2CDeviceImpl deviceImpl = ((I2CDeviceImpl) (device));
        // test simple locking
        bus.open();
        long before1 = System.currentTimeMillis();
        boolean result1 = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        });
        long time1 = (System.currentTimeMillis()) - before1;
        Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "was not run!"), result1);
        Assert.assertTrue(("It seems that the bus was locked because running the Runnable " + "took more time than expected!"), (time1 < (I2CBusImplTest.DEFAULT_TIMEOUT)));
        // test second attempt
        long before2 = System.currentTimeMillis();
        boolean result2 = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        });
        long time2 = (System.currentTimeMillis()) - before2;
        Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "was not run!"), result2);
        Assert.assertTrue(("It seems that the bus was locked because running the Runnable " + "took more time than expected!"), (time2 < (I2CBusImplTest.DEFAULT_TIMEOUT)));
        // test lock-unlock and lock by another thread
        final boolean[] result3 = new boolean[]{ false };
        Thread testThread1 = new Thread() {
            public void run() {
                try {
                    result3[0] = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                // never mind
                            }
                            return true;
                        }
                    });
                } catch (Throwable e) {
                    // expected
                }
            }
        };
        testThread1.start();
        Thread.sleep(10);
        long before3 = System.currentTimeMillis();
        boolean result4 = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Thread.sleep(20);
                return true;
            }
        });
        long time3 = (System.currentTimeMillis()) - before3;
        Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "was not run!"), result4);
        Assert.assertTrue(((("It seems that the bus was not locked because running the Runnable " + "took less time than expected (") + time3) + "ms)!"), (time3 > 10));
        testThread1.join();
        Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "on a separate thread was not run!"), result3[0]);
        // test lock-unlock and lock by another thread - getting no lock in time
        final boolean[] result7 = new boolean[]{ false };
        Thread testThread3 = new Thread() {
            public void run() {
                try {
                    result7[0] = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                // never mind
                            }
                            return true;
                        }
                    });
                } catch (Throwable e) {
                    // expected
                }
            }
        };
        testThread3.start();
        Thread.sleep(10);
        long before5 = System.currentTimeMillis();
        boolean result8 = false;
        try {
            result8 = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Thread.sleep(20);
                    return true;
                }
            });
        } catch (Exception e) {
            // expected
        }
        long time5 = (System.currentTimeMillis()) - before5;
        Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "was run but shouldn't!"), (!result8));
        Assert.assertTrue(((("It seems that the bus was not locked because running the Runnable " + "took less time than expected (") + time5) + "ms)!"), (time5 > 10));
        testThread3.join();
        Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "on a separate thread was not run!"), result7[0]);
        // test lock-unlock and lock by another thread using random delays
        final Random rnd = new Random();
        System.out.println("Testing concurrency 0/50");
        for (int i = 1; i <= 50; ++i) {
            if ((i % 10) == 0) {
                System.out.println((("Testing concurrency " + i) + "/50"));
            }
            final boolean[] result5 = new boolean[]{ false };
            Thread testThread2 = new Thread() {
                public void run() {
                    try {
                        result5[0] = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                try {
                                    long tts = (rnd.nextInt(50)) + 20;
                                    Thread.sleep(tts);
                                } catch (InterruptedException e) {
                                    // never mind
                                }
                                return true;
                            }
                        });
                    } catch (Throwable e) {
                        // expected
                    }
                }
            };
            testThread2.start();
            Thread.sleep(10);
            long before4 = System.currentTimeMillis();
            boolean result6 = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Thread.sleep(20);
                    return true;
                }
            });
            long time4 = (System.currentTimeMillis()) - before4;
            Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "was not run!"), result6);
            Assert.assertTrue(((("It seems that the bus was not locked because running the Runnable " + "took less time than expected (") + time4) + "ms)!"), (time4 > 10));
            testThread2.join();
            Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "on a separate thread was not run!"), result5[0]);
        }
        // test custom-code using I2CDevice.read() which leads to call
        // 'runBusLockedDeviceAction' byte the custom I2CRunnable and the
        // I2CRunnable of the read/write-methods of I2CDeviceImpl
        final boolean[] results = new boolean[]{ false, false };
        long before11 = System.currentTimeMillis();
        results[0] = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    long before12 = System.currentTimeMillis();
                    results[1] = bus.runBusLockedDeviceAction(deviceImpl, new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return true;
                        }
                    });
                    long time12 = (System.currentTimeMillis()) - before12;
                    Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "was not run!"), results[1]);
                    Assert.assertTrue(("It seems that the bus was locked because running the Runnable " + "took more time than expected!"), (time12 < (I2CBusImplTest.DEFAULT_TIMEOUT)));
                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });
        long time11 = (System.currentTimeMillis()) - before11;
        Assert.assertTrue(("The Runnable given to 'I2CBus.runBusLockedDeviceAction(deviceImpl, ...)' " + "was not run!"), results[0]);
        Assert.assertTrue(("It seems that the bus was locked because running the Runnable " + "took more time than expected!"), (time11 < (I2CBusImplTest.DEFAULT_TIMEOUT)));
    }
}


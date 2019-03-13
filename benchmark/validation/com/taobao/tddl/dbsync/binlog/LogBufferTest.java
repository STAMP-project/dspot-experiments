package com.taobao.tddl.dbsync.binlog;


import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.TestCase;


public class LogBufferTest extends TestCase {
    public static final int LOOP = 10000;

    public void testSigned() {
        byte[] array = new byte[]{ 0, 0, 0, ((byte) (255)) };
        LogBuffer buffer = new LogBuffer(array, 0, array.length);
        System.out.println(buffer.getInt32(0));
        System.out.println(buffer.getUint32(0));
        System.out.println(buffer.getInt24(1));
        System.out.println(buffer.getUint24(1));
    }

    public void testBigInteger() {
        byte[] array = new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) };
        LogBuffer buffer = new LogBuffer(array, 0, array.length);
        long tt1 = 0;
        long l1 = 0;
        for (int i = 0; i < (LogBufferTest.LOOP); i++) {
            final long t1 = System.nanoTime();
            l1 = buffer.getLong64(0);
            tt1 += (System.nanoTime()) - t1;
        }
        System.out.print((tt1 / (LogBufferTest.LOOP)));
        System.out.print("ns >> ");
        System.out.println(l1);
        long tt2 = 0;
        BigInteger l2 = null;
        for (int i = 0; i < (LogBufferTest.LOOP); i++) {
            final long t2 = System.nanoTime();
            l2 = buffer.getUlong64(0);
            tt2 += (System.nanoTime()) - t2;
        }
        System.out.print((tt2 / (LogBufferTest.LOOP)));
        System.out.print("ns >> ");
        System.out.println(l2);
    }

    public static final byte[] array1 = new byte[]{ ((byte) (128)), 0, 0, 5, 27, 56, ((byte) (176)), 96, 0 };

    public static final byte[] array2 = new byte[]{ ((byte) (127)), ((byte) (255)), ((byte) (255)), ((byte) (251)), ((byte) (228)), ((byte) (199)), ((byte) (79)), ((byte) (160)), ((byte) (255)) };

    public static final byte[] array3 = new byte[]{ -128, 0, 6, 20, 113, 56, 6, 26, -123 };

    public static final byte[] array4 = new byte[]{ -128, 7, 0, 0, 0, 1, 0, 0, 3 };

    public static final byte[] array5 = new byte[]{ -128, 0, 0, 0, 0, 1, 1, -122, -96, -108 };

    public void testBigDecimal() throws InterruptedException {
        do {
            System.out.println("old extract decimal: ");
            long tt1 = 0;
            BigDecimal bd1 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t1 = System.nanoTime();
                bd1 = LogBufferTest.extractDecimal(LogBufferTest.array2, 19, 10);
                tt1 += (System.nanoTime()) - t1;
            }
            System.out.print((tt1 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd1);
            long tt2 = 0;
            BigDecimal bd2 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t2 = System.nanoTime();
                bd2 = LogBufferTest.extractDecimal(LogBufferTest.array1, 19, 10);
                tt2 += (System.nanoTime()) - t2;
            }
            System.out.print((tt2 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd2);
            long tt3 = 0;
            BigDecimal bd3 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t3 = System.nanoTime();
                bd3 = LogBufferTest.extractDecimal(LogBufferTest.array3, 18, 6);
                tt3 += (System.nanoTime()) - t3;
            }
            System.out.print((tt3 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd3);
            long tt4 = 0;
            BigDecimal bd4 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t4 = System.nanoTime();
                bd4 = LogBufferTest.extractDecimal(LogBufferTest.array4, 18, 6);
                tt4 += (System.nanoTime()) - t4;
            }
            System.out.print((tt4 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd4);
            long tt5 = 0;
            BigDecimal bd5 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t5 = System.nanoTime();
                bd5 = LogBufferTest.extractDecimal(LogBufferTest.array5, 18, 6);
                tt5 += (System.nanoTime()) - t5;
            }
            System.out.print((tt5 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd5);
        } while (false );
        do {
            System.out.println("new extract decimal: ");
            LogBuffer buffer1 = new LogBuffer(LogBufferTest.array2, 0, LogBufferTest.array2.length);
            LogBuffer buffer2 = new LogBuffer(LogBufferTest.array1, 0, LogBufferTest.array1.length);
            LogBuffer buffer3 = new LogBuffer(LogBufferTest.array3, 0, LogBufferTest.array3.length);
            LogBuffer buffer4 = new LogBuffer(LogBufferTest.array4, 0, LogBufferTest.array4.length);
            LogBuffer buffer5 = new LogBuffer(LogBufferTest.array5, 0, LogBufferTest.array5.length);
            long tt1 = 0;
            BigDecimal bd1 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t1 = System.nanoTime();
                bd1 = buffer1.getDecimal(0, 19, 10);
                tt1 += (System.nanoTime()) - t1;
            }
            System.out.print((tt1 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd1);
            long tt2 = 0;
            BigDecimal bd2 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t2 = System.nanoTime();
                bd2 = buffer2.getDecimal(0, 19, 10);
                tt2 += (System.nanoTime()) - t2;
            }
            System.out.print((tt2 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd2);
            long tt3 = 0;
            BigDecimal bd3 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t3 = System.nanoTime();
                bd3 = buffer3.getDecimal(0, 18, 6);
                tt3 += (System.nanoTime()) - t3;
            }
            System.out.print((tt3 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd3);
            long tt4 = 0;
            BigDecimal bd4 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t4 = System.nanoTime();
                bd4 = buffer4.getDecimal(0, 18, 6);
                tt4 += (System.nanoTime()) - t4;
            }
            System.out.print((tt4 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd4);
            long tt5 = 0;
            BigDecimal bd5 = null;
            for (int i = 0; i < (LogBufferTest.LOOP); i++) {
                final long t5 = System.nanoTime();
                bd5 = buffer5.getDecimal(0, 18, 6);
                tt5 += (System.nanoTime()) - t5;
            }
            System.out.print((tt5 / (LogBufferTest.LOOP)));
            System.out.print("ns >> ");
            System.out.println(bd5);
        } while (false );
    }
}


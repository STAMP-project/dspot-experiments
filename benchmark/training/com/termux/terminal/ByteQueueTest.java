package com.termux.terminal;


import junit.framework.TestCase;


public class ByteQueueTest extends TestCase {
    public void testCompleteWrites() throws Exception {
        ByteQueue q = new ByteQueue(10);
        TestCase.assertTrue(q.write(new byte[]{ 1, 2, 3 }, 0, 3));
        byte[] arr = new byte[10];
        TestCase.assertEquals(3, q.read(arr, true));
        ByteQueueTest.assertArrayEquals(new byte[]{ 1, 2, 3 }, new byte[]{ arr[0], arr[1], arr[2] });
        TestCase.assertTrue(q.write(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 0, 10));
        TestCase.assertEquals(10, q.read(arr, true));
        ByteQueueTest.assertArrayEquals(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, arr);
    }

    public void testQueueWraparound() throws Exception {
        ByteQueue q = new ByteQueue(10);
        byte[] origArray = new byte[]{ 1, 2, 3, 4, 5, 6 };
        byte[] readArray = new byte[origArray.length];
        for (int i = 0; i < 20; i++) {
            q.write(origArray, 0, origArray.length);
            TestCase.assertEquals(origArray.length, q.read(readArray, true));
            ByteQueueTest.assertArrayEquals(origArray, readArray);
        }
    }

    public void testWriteNotesClosing() throws Exception {
        ByteQueue q = new ByteQueue(10);
        q.close();
        TestCase.assertFalse(q.write(new byte[]{ 1, 2, 3 }, 0, 3));
    }

    public void testReadNonBlocking() throws Exception {
        ByteQueue q = new ByteQueue(10);
        TestCase.assertEquals(0, q.read(new byte[128], false));
    }
}


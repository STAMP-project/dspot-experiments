package io.mycat.route.function;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import org.junit.Test;


/**
 * ?????????
 *
 * @author Hash Zhang
 */
public class PartitionByHashModTest {
    String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Test
    public void test() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        PartitionByHashModTest.Task task1 = new PartitionByHashModTest.Task(countDownLatch, 63);
        PartitionByHashModTest.Task task2 = new PartitionByHashModTest.Task(countDownLatch, 64);
        task1.start();
        task2.start();
        countDownLatch.countDown();
        task1.join();
        task2.join();
    }

    private class Task extends Thread {
        CountDownLatch countDownLatch;

        int count;

        public Task(CountDownLatch countDownLatch, int count) {
            this.countDownLatch = countDownLatch;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PartitionByHashMod partitionByHashMod = new PartitionByHashMod();
            partitionByHashMod.setCount(count);
            Random random = new Random();
            StringBuffer sb = new StringBuffer();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                for (int j = 0; j < 32; j++) {
                    sb.append(allChar.charAt(random.nextInt(allChar.length())));
                }
                int result = partitionByHashMod.calculate(sb.toString());
                sb = new StringBuffer();
                Assert.assertTrue(((0 <= result) && (result < (count))));
            }
            long end = System.currentTimeMillis();
            System.out.println(((("Shard Count is " + (count)) + ", time elapsed: ") + (end - start)));
        }
    }
}


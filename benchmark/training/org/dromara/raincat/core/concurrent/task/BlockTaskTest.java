package org.dromara.raincat.core.concurrent.task;


import org.junit.Test;


/**
 *
 *
 * @author xiaoyu(Myth)
 */
public class BlockTaskTest {
    @Test
    public void isNotify() {
        BlockTask task1 = new BlockTask();
        task1.signal();
        System.out.println(task1.isNotify());
        BlockTask task2 = new BlockTask();
        System.out.println(task2.isNotify());
    }
}


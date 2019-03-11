package cn.myperf4j.base.test;


import cn.myperf4j.base.util.ChunkPool;
import org.junit.Test;


/**
 * Created by LinShunkang on 2018/10/17
 */
public class ChunkPoolTest {
    @Test
    public void test() {
        ChunkPool pool = ChunkPool.getInstance();
        test(pool, 16);
        test(pool, 1024);
        test(pool, (10 * 1024));
        test(pool, (100 * 1024));
        test(pool, (1024 * 1024));
        pool.returnChunk(new int[]{ 1 });
    }
}


package org.osmdroid.mtp;


import org.junit.Test;


/**
 * Created by alex on 9/13/16.
 */
public class TilePackagerTest {
    @Test
    public void runBasicTest() {
        runTest("fr_mapnick_12.zip");
    }

    @Test
    public void runBasicTestSql() {
        runTest("fr_mapnick_12.sql");
    }

    @Test
    public void runBasicTestGemf() {
        runTest("fr_mapnick_12.gemf");
    }
}


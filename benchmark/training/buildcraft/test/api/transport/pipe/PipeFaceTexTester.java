package buildcraft.test.api.transport.pipe;


import buildcraft.api.transport.pipe.PipeFaceTex;
import org.junit.Test;


public class PipeFaceTexTester {
    @Test
    public void testBasicHashCodes() {
        for (int i = 0; i < 1000; i++) {
            PipeFaceTexTester.assertEquals(PipeFaceTex.___testing_create_single(i), PipeFaceTex.get(i));
        }
        PipeFaceTexTester.assertEquals(PipeFaceTex.get(0), PipeFaceTex.get(new int[]{ 0 }, (-1)));
    }
}


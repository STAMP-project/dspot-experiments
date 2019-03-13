package org.nd4j.parameterserver.distributed.logic;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.parameterserver.distributed.logic.completion.FrameCompletionHandler;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class FrameCompletionHandlerTest {
    /**
     * This test emulates 2 frames being processed at the same time
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCompletion1() throws Exception {
        FrameCompletionHandler handler = new FrameCompletionHandler();
        long[] frames = new long[]{ 15L, 17L };
        long[] originators = new long[]{ 123L, 183L };
        for (Long originator : originators) {
            for (Long frame : frames) {
                for (int e = 1; e <= 512; e++) {
                    handler.addHook(originator, frame, ((long) (e)));
                }
            }
            for (Long frame : frames) {
                for (int e = 1; e <= 512; e++) {
                    handler.notifyFrame(originator, frame, ((long) (e)));
                }
            }
        }
        for (Long originator : originators) {
            for (Long frame : frames) {
                Assert.assertEquals(true, handler.isCompleted(originator, frame));
            }
        }
    }
}


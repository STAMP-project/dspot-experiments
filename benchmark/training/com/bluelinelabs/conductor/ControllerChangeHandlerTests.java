package com.bluelinelabs.conductor;


import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.util.TestController;
import org.junit.Assert;
import org.junit.Test;


public class ControllerChangeHandlerTests {
    @Test
    public void testSaveRestore() {
        HorizontalChangeHandler horizontalChangeHandler = new HorizontalChangeHandler();
        FadeChangeHandler fadeChangeHandler = new FadeChangeHandler(120, false);
        RouterTransaction transaction = RouterTransaction.with(new TestController()).pushChangeHandler(horizontalChangeHandler).popChangeHandler(fadeChangeHandler);
        RouterTransaction restoredTransaction = new RouterTransaction(transaction.saveInstanceState());
        ControllerChangeHandler restoredHorizontal = restoredTransaction.pushChangeHandler();
        ControllerChangeHandler restoredFade = restoredTransaction.popChangeHandler();
        Assert.assertEquals(horizontalChangeHandler.getClass(), restoredHorizontal.getClass());
        Assert.assertEquals(fadeChangeHandler.getClass(), restoredFade.getClass());
        HorizontalChangeHandler restoredHorizontalCast = ((HorizontalChangeHandler) (restoredHorizontal));
        FadeChangeHandler restoredFadeCast = ((FadeChangeHandler) (restoredFade));
        Assert.assertEquals(horizontalChangeHandler.getAnimationDuration(), restoredHorizontalCast.getAnimationDuration());
        Assert.assertEquals(horizontalChangeHandler.removesFromViewOnPush(), restoredHorizontalCast.removesFromViewOnPush());
        Assert.assertEquals(fadeChangeHandler.getAnimationDuration(), restoredFadeCast.getAnimationDuration());
        Assert.assertEquals(fadeChangeHandler.removesFromViewOnPush(), restoredFadeCast.removesFromViewOnPush());
    }
}


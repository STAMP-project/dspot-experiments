package com.bluelinelabs.conductor;


import android.os.Bundle;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.bluelinelabs.conductor.util.TestController;
import org.junit.Assert;
import org.junit.Test;


public class ControllerTransactionTests {
    @Test
    public void testRouterSaveRestore() {
        RouterTransaction transaction = RouterTransaction.with(new TestController()).pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new VerticalChangeHandler()).tag("Test Tag");
        Bundle bundle = transaction.saveInstanceState();
        RouterTransaction restoredTransaction = new RouterTransaction(bundle);
        Assert.assertEquals(transaction.controller.getClass(), restoredTransaction.controller.getClass());
        Assert.assertEquals(transaction.pushChangeHandler().getClass(), restoredTransaction.pushChangeHandler().getClass());
        Assert.assertEquals(transaction.popChangeHandler().getClass(), restoredTransaction.popChangeHandler().getClass());
        Assert.assertEquals(transaction.tag(), restoredTransaction.tag());
    }
}


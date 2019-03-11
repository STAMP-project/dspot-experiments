package com.uber.okbuck;


import com.uber.okbuck.example.common.Calc;
import com.uber.okbuck.example.common.CalcMonitor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void testAdd() throws Exception {
        CalcMonitor monitor = Mockito.mock(CalcMonitor.class);
        Calc calc = new Calc(monitor);
        int ret = calc.add(1, 2);
        Mockito.verify(monitor, Mockito.only()).addCalled(ArgumentMatchers.any(String.class));
        Assert.assertEquals(3, ret);
    }
}


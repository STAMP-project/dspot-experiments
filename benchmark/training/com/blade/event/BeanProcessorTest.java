package com.blade.event;


import com.blade.BaseTestCase;
import com.blade.Blade;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class BeanProcessorTest extends BaseTestCase {
    @Test
    public void testBeanProcessor() {
        Blade blade = Blade.me();
        BeanProcessor beanProcessor = Mockito.mock(BeanProcessor.class);
        beanProcessor.processor(blade);
        Mockito.verify(beanProcessor).processor(blade);
    }
}


/**
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.plugin.deltaspike;


import org.hotswap.agent.plugin.deltaspike.testBeans.WindowBean1;
import org.hotswap.agent.plugin.deltaspike.testBeansHotswap.WindowBean2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Deltaspike Plugin contexts, using OWB
 *
 * See maven setup for javaagent and autohotswap settings.
 *
 * @author Vladimir Dvorak
 */
public class DeltaspikePluginContextsTest extends HAAbstractUnitTest {
    @Test
    public void windowBeanTest() throws Exception {
        WindowBean1 windowBean = getBeanInstance(WindowBean1.class);
        Assert.assertEquals("WindowBean1.hello():ProxyHello1.hello()", windowBean.hello());
        swapClasses(WindowBean1.class, WindowBean2.class.getName());
        Assert.assertEquals("WindowBean2.hello():ProxyHello2.hello():ProxyHello1.hello()", windowBean.hello());
        // return configuration
        swapClasses(WindowBean1.class, WindowBean1.class.getName());
        Assert.assertEquals("WindowBean1.hello():ProxyHello1.hello()", windowBean.hello());
    }
}


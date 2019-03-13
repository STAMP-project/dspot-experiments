/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.pilight.internal;


import java.math.BigDecimal;
import junit.framework.Assert;
import org.junit.Test;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.DecimalType;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 *
 *
 * @author Jeroen Idserda
 * @since 1.7.0
 */
public class PilightGenericBindingProviderTest {
    private PilightGenericBindingProvider provider;

    private PilightBinding binding;

    private Item testItem;

    @Test
    public void testNumberItemScale() throws BindingConfigParseException {
        String bindingConfig = "kaku#weather,property=temperature";
        PilightBindingConfig config = provider.parseBindingConfig(testItem, bindingConfig);
        Assert.assertNotNull(config);
        DecimalType number0 = ((DecimalType) (binding.getState("23", config)));
        Assert.assertEquals(number0.toBigDecimal().compareTo(new BigDecimal("23")), 0);
    }
}


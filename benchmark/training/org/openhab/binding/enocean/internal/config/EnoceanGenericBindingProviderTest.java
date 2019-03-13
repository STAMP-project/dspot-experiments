/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.config;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.common.EEPId;
import org.openhab.core.items.GenericItem;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.model.item.binding.BindingConfigParseException;


public class EnoceanGenericBindingProviderTest {
    @Test
    public void testGetParameterAddress() throws BindingConfigParseException {
        EnoceanGenericBindingProvider provider = new EnoceanGenericBindingProvider();
        provider.processBindingConfiguration("enocean", new EnoceanGenericBindingProviderTest.TestItem("item"), "{id=00:8B:62:43, eep=F6:02:01, channel=B, parameter=I_PRESSED}");
        Assert.assertEquals(new org.opencean.core.address.EnoceanParameterAddress(EnoceanId.fromString("00:8B:62:43"), "B", "I_PRESSED"), provider.getParameterAddress("item"));
    }

    @Test
    public void testGetEEP() throws BindingConfigParseException {
        EnoceanGenericBindingProvider provider = new EnoceanGenericBindingProvider();
        provider.processBindingConfiguration("enocean", new EnoceanGenericBindingProviderTest.TestItem("item"), "{id=00:8B:62:43, eep=F6:02:01, channel=B, parameter=I_PRESSED}");
        Assert.assertEquals(new EEPId("F6:02:01"), provider.getEEP("item"));
    }

    class TestItem extends GenericItem {
        public TestItem(String name) {
            super(name);
        }

        public List<Class<? extends State>> getAcceptedDataTypes() {
            // TODO Auto-generated method stub
            return null;
        }

        public List<Class<? extends Command>> getAcceptedCommandTypes() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}


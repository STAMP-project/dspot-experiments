/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.config;


import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.enocean.internal.config.EnoceanGenericBindingProvider.EnoceanBindingConfig;
import org.openhab.model.item.binding.BindingConfigParseException;


public class BindingConfigParserTest {
    @Test
    public void testParseConfigFullFilled() throws BindingConfigParseException {
        BindingConfigParser<EnoceanBindingConfig> parser = new BindingConfigParser<EnoceanBindingConfig>();
        EnoceanBindingConfig config = new EnoceanGenericBindingProvider().new EnoceanBindingConfig();
        parser.parse("{id=00:8B:62:43, eep=F6:02:01, channel=B, parameter=I_PRESSED}", config);
        Assert.assertEquals("id", "00:8B:62:43", config.id);
        Assert.assertEquals("eep", "F6:02:01", config.eep);
        Assert.assertEquals("channel", "B", config.channel);
        Assert.assertEquals("parameter", "I_PRESSED", config.parameter);
    }

    @Test
    public void testParseConfigIdFilled() throws BindingConfigParseException {
        BindingConfigParser<EnoceanBindingConfig> parser = new BindingConfigParser<EnoceanBindingConfig>();
        EnoceanBindingConfig config = new EnoceanGenericBindingProvider().new EnoceanBindingConfig();
        parser.parse("{id=00:8B:62:43}", config);
        Assert.assertEquals("id", "00:8B:62:43", config.id);
        Assert.assertNull("eep", config.eep);
        Assert.assertNull("channel", config.channel);
        Assert.assertNull("parameter", config.parameter);
    }

    @Test
    public void testParseConfigIdAndParameterFilled() throws BindingConfigParseException {
        BindingConfigParser<EnoceanBindingConfig> parser = new BindingConfigParser<EnoceanBindingConfig>();
        EnoceanBindingConfig config = new EnoceanGenericBindingProvider().new EnoceanBindingConfig();
        parser.parse("{id=00:8B:62:43, parameter=I_PRESSED}", config);
        Assert.assertEquals("id", "00:8B:62:43", config.id);
        Assert.assertEquals("parameter", "I_PRESSED", config.parameter);
    }
}


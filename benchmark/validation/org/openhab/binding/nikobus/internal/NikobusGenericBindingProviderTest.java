/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nikobus.internal;


import Button.PressType.LONG;
import Button.PressType.SHORT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhab.binding.nikobus.internal.core.NikobusCommandReceiver;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 *
 *
 * @author Davy Vanherbergen
 * @since 1.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class NikobusGenericBindingProviderTest {
    private NikobusGenericBindingProvider provider = new NikobusGenericBindingProvider();

    @Mock
    private NikobusCommandReceiver receiver;

    @Mock
    private NikobusBinding binding;

    @Test
    public void canParseValidButtonConfig() throws BindingConfigParseException {
        // test parsing of regular buttons
        parseButton("btn-cfg1", "#NA0914D", SHORT, "#NA0914D");
        parseButton("btn-cfg2", "#N003334:SHORT", SHORT, "#N003334");
        parseButton("btn-cfg3", "#N003335:LONG", LONG, "#N003335");
        parseButton("btn-cfg4", "#N003333", SHORT, "#N003333");
    }

    @Test
    public void canParseValidButtonConfigWithRefresh() throws BindingConfigParseException {
        // test parsing of regular buttons
        parseButtonWithRefresh("btn-cfg1", "#NA0914D[1973-1,1824-2]", SHORT, "#NA0914D", new String[]{ "1973-1", "1824-2" });
        parseButtonWithRefresh("btn-cfg1", "#NA0914D[1973-1,1824-2,55DC-2,884E-1]", SHORT, "#NA0914D", new String[]{ "1973-1", "1824-2", "55DC-2", "884E-1" });
        parseButtonWithRefresh("btn-cfg1", "#NA0914D[1973-1]", SHORT, "#NA0914D", new String[]{ "1973-1" });
        parseButtonWithRefresh("btn-cfg2", "#N003334:SHORT[1233-1]", SHORT, "#N003334", new String[]{ "1233-1" });
        parseButtonWithRefresh("btn-cfg3", "#N003335:LONG[1973-1,1824-2]", LONG, "#N003335", new String[]{ "1973-1", "1824-2" });
        parseButtonWithRefresh("btn-cfg4", "#N003333[1973-1,1824-2]", SHORT, "#N003333", new String[]{ "1973-1", "1824-2" });
    }

    @Test
    public void canDetectInvalidButtonConfig() {
        parseInvalidButton("#NA0914");
        parseInvalidButton("#NA0914dd");
        parseInvalidButton("#NA09-14D");
        parseInvalidButton("#NA0914D::SHORT");
        parseInvalidButton("#NA0914DSHORT");
        parseInvalidButton("#MA0914D:SHORT");
        parseInvalidButton("NA0914D:SHORT");
        parseInvalidButton("#NA0*14D:SHORT");
        parseInvalidButton("#NA0*14D[123]");
        parseInvalidButton("#NA0*14D[1234-5");
        parseInvalidButton("#NA0*14D2");
        parseInvalidButton("#NA0*14D[123111]");
        parseInvalidButton("#NA0*14D[1231-35-2]");
        parseInvalidButton("#NA0*14D[1234-46,]");
        parseInvalidButton("#NA0*14D[1234-46,12]");
        parseInvalidButton("#NA0914D[1973-4,1824-2,55DC-2,884E-1]");
        parseInvalidButton("#NA0914D[1973-4]");
    }

    @Test
    public void canParseValidSwitchModuleChannelConfig() throws BindingConfigParseException {
        parseChannelConfig("ch-cfg-1", "ABCD:1", "ABCD", 1);
        parseChannelConfig("ch-cfg-2", "1234:12", "1234", 12);
        parseChannelConfig("ch-cfg-3", "A11D:3", "A11D", 3);
        parseChannelConfig("ch-cfg-4", "ABCF:5", "ABCF", 5);
        parseChannelConfig("ch-cfg-5", "ABFD:7", "ABFD", 7);
    }

    @Test
    public void canDetectInvalidSwitchModuleChannelConfig() {
        parseInvalidChannelConfig("ABC:1");
        parseInvalidChannelConfig("ABCDE:1");
        parseInvalidChannelConfig("ABCD:0");
        parseInvalidChannelConfig("ABCD:13");
        parseInvalidChannelConfig("ABCDEE");
        parseInvalidChannelConfig("ABCD");
        parseInvalidChannelConfig("ABCD-1");
    }
}


/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import Commands.CHIME;
import Commands.OFF;
import Commands.ON;
import RFXComLighting1Message.SubType.ARC;
import RFXComLighting1Message.SubType.X10;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @since 1.9.0
 */
public class RFXComLighting1MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("0710015242080780", ARC, 82, "B.8", ((byte) (8)), CHIME);
        testMessage("0710010047010070", ARC, 0, "G.1", ((byte) (7)), OFF);
        testMessage("071001014D090160", ARC, 1, "M.9", ((byte) (6)), ON);
        testMessage("0710010543080060", ARC, 5, "C.8", ((byte) (6)), OFF);
        testMessage("0710010B43080160", ARC, 11, "C.8", ((byte) (6)), ON);
        testMessage("0710000843010150", X10, 8, "C.1", ((byte) (5)), ON);
        testMessage("0710007F41010000", X10, 127, "A.1", ((byte) (0)), OFF);
        testMessage("0710009A41010170", X10, 154, "A.1", ((byte) (7)), ON);
    }
}


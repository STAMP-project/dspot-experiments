/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.stiebelheatpump.internal;


import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.stiebelheatpump.protocol.Request;


public class ConfigParserTest {
    private ConfigParser configParser = new ConfigParser();

    public ConfigParserTest() {
    }

    @Test
    public void CreateParserConfiguration() throws IOException, JAXBException, StiebelHeatPumpException {
        List<Request> configuration = getHeatPumpConfiguration();
        File config = new File("target/testconfig.xml");
        configParser.marshal(configuration, config);
        List<Request> configuration2 = configParser.unmarshal(config);
        Request firstRequest = configuration2.get(0);
        Assert.assertEquals("Version", firstRequest.getName());
        Assert.assertEquals(((byte) (253)), firstRequest.getRequestByte());
    }

    @Test
    public void LoadParserConfigurationFromResources() throws StiebelHeatPumpException {
        List<Request> configuration = configParser.parseConfig("2.06.xml");
        Request firstRequest = configuration.get(0);
        Assert.assertEquals("Version", firstRequest.getName());
        Assert.assertEquals(((byte) (253)), firstRequest.getRequestByte());
    }
}


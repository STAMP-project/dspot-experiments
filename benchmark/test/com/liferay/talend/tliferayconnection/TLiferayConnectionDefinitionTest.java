/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.talend.tliferayconnection;


import ConnectorTopology.INCOMING;
import ConnectorTopology.NONE;
import ExecutionEngine.DI;
import LiferayBaseComponentDefinition.RUNTIME_SOURCE_OR_SINK_CLASS_NAME;
import com.liferay.talend.connection.LiferayConnectionProperties;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.runtime.RuntimeInfo;


/**
 *
 *
 * @author Zolt?n Tak?cs
 */
public class TLiferayConnectionDefinitionTest {
    @Test
    public void testGetRuntimeInfoWrongTopology() {
        expectedException.expect(TalendRuntimeException.class);
        expectedException.expectMessage("WRONG_CONNECTOR:{component=tLiferayConnection}");
        _tLiferayConnectionDefinition.getRuntimeInfo(DI, null, INCOMING);
    }

    @Test
    public void testRuntimeInfo() {
        RuntimeInfo runtimeInfo = _tLiferayConnectionDefinition.getRuntimeInfo(DI, _tLiferayConnectionProperties, NONE);
        Assert.assertThat(runtimeInfo, CoreMatchers.instanceOf(JarRuntimeInfo.class));
        JarRuntimeInfo jarRuntimeInfo = ((JarRuntimeInfo) (runtimeInfo));
        Assert.assertNotNull(jarRuntimeInfo.getDepTxtPath());
        Assert.assertNotNull(jarRuntimeInfo.getJarUrl());
        Assert.assertEquals(RUNTIME_SOURCE_OR_SINK_CLASS_NAME, jarRuntimeInfo.getRuntimeClassName());
    }

    @Test
    public void testStartable() {
        Assert.assertTrue(_tLiferayConnectionDefinition.isStartable());
    }

    @Test
    public void testSupportedConnectorTopologies() {
        Set<ConnectorTopology> topologySet = _tLiferayConnectionDefinition.getSupportedConnectorTopologies();
        Assert.assertThat(topologySet, CoreMatchers.hasItems(NONE));
    }

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private TLiferayConnectionDefinition _tLiferayConnectionDefinition;

    private LiferayConnectionProperties _tLiferayConnectionProperties;
}


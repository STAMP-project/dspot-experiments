/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.persistence.mapdb;


import HSBType.GREEN;
import OnOffType.ON;
import PercentType.HUNDRED;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.State;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.persistence.mapdb.internal.StateTypeAdapter;


/**
 *
 *
 * @author Martin K?hl - Initial contribution
 */
public class StateTypeAdapterTest {
    Gson mapper = new GsonBuilder().registerTypeHierarchyAdapter(State.class, new StateTypeAdapter()).create();

    @Test
    public void readWriteRoundtripShouldRecreateTheWrittenState() {
        Assert.assertThat(roundtrip(ON), CoreMatchers.is(CoreMatchers.equalTo(ON)));
        Assert.assertThat(roundtrip(HUNDRED), CoreMatchers.is(CoreMatchers.equalTo(HUNDRED)));
        Assert.assertThat(roundtrip(GREEN), CoreMatchers.is(CoreMatchers.equalTo(GREEN)));
        Assert.assertThat(roundtrip(StringType.valueOf("test")), CoreMatchers.is(CoreMatchers.equalTo(StringType.valueOf("test"))));
    }
}


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


import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.library.items.ColorItem;
import org.eclipse.smarthome.core.library.items.DimmerItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.persistence.FilterCriteria;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.persistence.mapdb.internal.MapDbPersistenceService;


/**
 *
 *
 * @author Martin K?hl - Initial contribution
 */
public class MapDbPersistenceServiceOSGiTest extends JavaOSGiTest {
    private MapDbPersistenceService persistenceService;

    @Test
    public void storeShouldStoreTheItem() {
        String name = "switch1";
        String alias = "switch2";
        State state = OnOffType.ON;
        GenericItem item = new SwitchItem(name);
        item.setState(state);
        Assert.assertThat(persistenceService.getItemInfo(), CoreMatchers.not(CoreMatchers.hasItem(hasProperty("name", CoreMatchers.equalTo(name)))));
        persistenceService.store(item);
        Assert.assertThat(persistenceService.getItemInfo(), CoreMatchers.hasItem(hasProperty("name", CoreMatchers.equalTo(name))));
        persistenceService.store(item, alias);
        Assert.assertThat(persistenceService.getItemInfo(), CoreMatchers.hasItems(hasProperty("name", CoreMatchers.equalTo(name)), hasProperty("name", CoreMatchers.equalTo(alias))));
    }

    @Test
    public void queryShouldFindStoredItemsByName() {
        String name = "dimmer";
        State state = PercentType.HUNDRED;
        GenericItem item = new DimmerItem(name);
        item.setState(state);
        FilterCriteria filter = new FilterCriteria();
        filter.setItemName(name);
        Assert.assertThat(persistenceService.query(filter), CoreMatchers.is(emptyIterable()));
        persistenceService.store(item);
        Assert.assertThat(persistenceService.query(filter), contains(CoreMatchers.allOf(hasProperty("name", CoreMatchers.equalTo(name)), hasProperty("state", CoreMatchers.equalTo(state)))));
    }

    @Test
    public void queryShouldFindStoredItemsByAlias() {
        String name = "color";
        String alias = "alias";
        State state = HSBType.GREEN;
        GenericItem item = new ColorItem(name);
        item.setState(state);
        FilterCriteria filterByName = new FilterCriteria();
        filterByName.setItemName(name);
        FilterCriteria filterByAlias = new FilterCriteria();
        filterByAlias.setItemName(alias);
        Assert.assertThat(persistenceService.query(filterByName), CoreMatchers.is(emptyIterable()));
        Assert.assertThat(persistenceService.query(filterByAlias), CoreMatchers.is(emptyIterable()));
        persistenceService.store(item, alias);
        Assert.assertThat(persistenceService.query(filterByName), CoreMatchers.is(emptyIterable()));
        Assert.assertThat(persistenceService.query(filterByAlias), contains(CoreMatchers.allOf(hasProperty("name", CoreMatchers.equalTo(alias)), hasProperty("state", CoreMatchers.equalTo(state)))));
    }
}


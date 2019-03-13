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
package org.openhab.io.hueemulation.internal;


import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.storage.Storage;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.io.hueemulation.internal.dto.HueDataStore;
import org.openhab.io.hueemulation.internal.dto.HueDevice;
import org.openhab.io.hueemulation.internal.dto.HueStatePlug;


/**
 * Tests for {@link LightItems}.
 *
 * @author David Graeff - Initial contribution
 */
public class LightItemsTest {
    private Gson gson;

    private HueDataStore ds;

    private LightItems lightItems;

    @Mock
    private ItemRegistry itemRegistry;

    @Mock
    Storage<Integer> storage;

    @Test
    public void loadStorage() throws IOException {
        Map<String, Integer> itemUIDtoHueID = new TreeMap<>();
        itemUIDtoHueID.put("switch1", 12);
        Mockito.when(storage.getKeys()).thenReturn(itemUIDtoHueID.keySet());
        Mockito.when(storage.get(ArgumentMatchers.eq("switch1"))).thenReturn(itemUIDtoHueID.get("switch1"));
        lightItems.loadMappingFromFile(storage);
    }

    @Test
    public void addSwitchableByCategory() throws IOException {
        SwitchItem item = new SwitchItem("switch1");
        item.setCategory("Light");
        lightItems.added(item);
        HueDevice device = ds.lights.get(lightItems.itemUIDtoHueID.get("switch1"));
        Assert.assertThat(device.item, CoreMatchers.is(item));
        Assert.assertThat(device.state, CoreMatchers.is(CoreMatchers.instanceOf(HueStatePlug.class)));
    }

    @Test
    public void addSwitchableByTag() throws IOException {
        SwitchItem item = new SwitchItem("switch1");
        item.addTag("Switchable");
        lightItems.added(item);
        HueDevice device = ds.lights.get(lightItems.itemUIDtoHueID.get("switch1"));
        Assert.assertThat(device.item, CoreMatchers.is(item));
        Assert.assertThat(device.state, CoreMatchers.is(CoreMatchers.instanceOf(HueStatePlug.class)));
    }

    @Test
    public void addGroupSwitchableByTag() throws IOException {
        GroupItem item = new GroupItem("group1", new SwitchItem("switch1"));
        item.addTag("Switchable");
        lightItems.added(item);
        HueDevice device = ds.lights.get(lightItems.itemUIDtoHueID.get("group1"));
        Assert.assertThat(device.item, CoreMatchers.is(item));
        Assert.assertThat(device.state, CoreMatchers.is(CoreMatchers.instanceOf(HueStatePlug.class)));
    }

    @Test
    public void addGroupWithoutTypeByTag() throws IOException {
        GroupItem item = new GroupItem("group1", null);
        item.addTag("Switchable");
        lightItems.added(item);
        HueDevice device = ds.lights.get(lightItems.itemUIDtoHueID.get("group1"));
        Assert.assertThat(device.item, CoreMatchers.is(item));
        Assert.assertThat(device.state, CoreMatchers.is(CoreMatchers.instanceOf(HueStatePlug.class)));
        Assert.assertThat(ds.groups.get(lightItems.itemUIDtoHueID.get("group1")).groupItem, CoreMatchers.is(item));
    }

    @Test
    public void removeGroupWithoutTypeAndTag() throws IOException {
        String groupName = "group1";
        GroupItem item = new GroupItem(groupName, null);
        item.addTag("Switchable");
        lightItems.added(item);
        Integer hueId = lightItems.itemUIDtoHueID.get(groupName);
        lightItems.updated(item, new GroupItem(groupName, null));
        Assert.assertThat(lightItems.itemUIDtoHueID.get(groupName), CoreMatchers.nullValue());
        Assert.assertThat(ds.lights.get(hueId), CoreMatchers.nullValue());
        Assert.assertThat(ds.groups.get(hueId), CoreMatchers.nullValue());
    }

    @Test
    public void updateSwitchable() throws IOException {
        SwitchItem item = new SwitchItem("switch1");
        item.setLabel("labelOld");
        item.addTag("Switchable");
        lightItems.added(item);
        Integer hueID = lightItems.itemUIDtoHueID.get("switch1");
        HueDevice device = ds.lights.get(hueID);
        Assert.assertThat(device.item, CoreMatchers.is(item));
        Assert.assertThat(device.state, CoreMatchers.is(CoreMatchers.instanceOf(HueStatePlug.class)));
        Assert.assertThat(device.name, CoreMatchers.is("labelOld"));
        SwitchItem newitem = new SwitchItem("switch1");
        newitem.setLabel("labelNew");
        newitem.addTag("Switchable");
        lightItems.updated(item, newitem);
        device = ds.lights.get(hueID);
        Assert.assertThat(device.item, CoreMatchers.is(newitem));
        Assert.assertThat(device.state, CoreMatchers.is(CoreMatchers.instanceOf(HueStatePlug.class)));
        Assert.assertThat(device.name, CoreMatchers.is("labelNew"));
        // Update with an item that has no tags anymore -> should be removed
        SwitchItem newitemWithoutTag = new SwitchItem("switch1");
        newitemWithoutTag.setLabel("labelNew2");
        lightItems.updated(newitem, newitemWithoutTag);
        device = ds.lights.get(hueID);
        Assert.assertThat(device, CoreMatchers.nullValue());
        Assert.assertThat(lightItems.itemUIDtoHueID.get("switch1"), CoreMatchers.nullValue());
    }
}


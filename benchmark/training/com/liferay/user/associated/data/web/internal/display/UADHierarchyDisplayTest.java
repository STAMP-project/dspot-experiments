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
package com.liferay.user.associated.data.web.internal.display;


import QueryUtil.ALL_POS;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Drew Brokke
 */
public class UADHierarchyDisplayTest {
    @Test
    public void testCountAll() {
        Assert.assertEquals(11, _uadHierarchyDisplay.countAll(UADHierarchyDisplayTest._USER_ID));
        Assert.assertEquals(8, _uadHierarchyDisplay.countAll(UADHierarchyDisplayTest._USER_ID_OTHER));
    }

    @Test
    public void testFieldValueCount() throws Exception {
        List items = _uadHierarchyDisplay.search(DummyContainer.class, DummyService.DEFAULT_CONTAINER_ID, UADHierarchyDisplayTest._USER_ID, null, "", "createDate", "asc", ALL_POS, ALL_POS);
        for (Object item : items) {
            Map<String, Object> fieldValues = _uadHierarchyDisplay.getFieldValues(item, new String[]{ "count" });
            String uuid = ((String) (fieldValues.get("uuid")));
            Assert.assertNotNull(uuid);
            if (StringUtil.equals(uuid, _folderA.getUuid())) {
                Long count = ((Long) (fieldValues.get("count")));
                Assert.assertEquals(4, count.intValue());
            }
        }
    }

    @Test
    public void testSearch() throws Exception {
        for (DummyContainer dummyContainer : _dummyContainerService.getEntities()) {
            List items = _uadHierarchyDisplay.search(DummyContainer.class, dummyContainer.getId(), UADHierarchyDisplayTest._USER_ID, null, "", "name", "asc", ALL_POS, ALL_POS);
            Assert.assertEquals(((int) (_userFolderAndItemCountMap.get(dummyContainer.getId()))), items.size());
        }
    }

    private static final long _USER_ID = 100;

    private static final long _USER_ID_OTHER = 200;

    private final DummyService<DummyContainer> _dummyContainerService = new DummyService<>(200, DummyContainer::new);

    private final DummyService<DummyEntry> _dummyEntryService = new DummyService<>(100, DummyEntry::new);

    private DummyContainer _folderA;

    private UADHierarchyDisplay _uadHierarchyDisplay;

    private final Map<Long, Integer> _userFolderAndItemCountMap = new HashMap<>();
}


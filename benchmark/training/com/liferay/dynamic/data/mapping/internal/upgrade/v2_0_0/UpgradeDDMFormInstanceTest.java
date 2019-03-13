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
package com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_0;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Pedro Queiroz
 */
@RunWith(PowerMockRunner.class)
public class UpgradeDDMFormInstanceTest extends PowerMockito {
    @Test
    public void testGetNewActionIds1() throws Exception {
        mockResourceActionLocalService("RecordSet", this::createRecorSetResourceActionList, "FormInstance", this::createFormInstanceResourceActionList);
        // VIEW, ADD_RECORD
        long currentNewActionIds = 9;
        Assert.assertEquals(17, _upgradeDDMFormInstance.getNewActionIds("RecordSet", "FormInstance", 0, currentNewActionIds));
    }

    @Test
    public void testGetNewActionIds2() throws Exception {
        mockResourceActionLocalService("RecordSet", this::createRecorSetResourceActionList, "FormInstance", this::createFormInstanceResourceActionList);
        // VIEW, UPDATE, ADD_RECORD
        long currentNewActionIds = 25;
        Assert.assertEquals(25, _upgradeDDMFormInstance.getNewActionIds("RecordSet", "FormInstance", 0, currentNewActionIds));
    }

    private UpgradeDDMFormInstance _upgradeDDMFormInstance;
}


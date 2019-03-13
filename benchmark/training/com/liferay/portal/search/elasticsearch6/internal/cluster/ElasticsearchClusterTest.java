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
package com.liferay.portal.search.elasticsearch6.internal.cluster;


import com.liferay.portal.kernel.service.CompanyLocalService;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Artur Aquino
 */
@Ignore
public class ElasticsearchClusterTest {
    @Test
    public void testReplicaIndexNamesIncludeSystemCompanyId() {
        long[] companyIds = new long[]{ 42, 142857 };
        setUpCompanyLocalService(getCompanies(companyIds));
        String[] targetIndexNames = _replicasClusterContext.getTargetIndexNames();
        Arrays.sort(targetIndexNames);
        Assert.assertEquals("[cid-0, cid-142857, cid-42]", Arrays.toString(targetIndexNames));
    }

    @Mock
    private CompanyLocalService _companyLocalService;

    private ReplicasClusterContext _replicasClusterContext;
}


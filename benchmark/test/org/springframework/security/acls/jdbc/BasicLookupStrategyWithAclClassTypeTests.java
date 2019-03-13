/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.acls.jdbc;


import java.util.Arrays;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ObjectIdentity;


/**
 * Tests {@link BasicLookupStrategy} with Acl Class type id set to UUID.
 *
 * @author Paul Wheeler
 */
public class BasicLookupStrategyWithAclClassTypeTests extends AbstractBasicLookupStrategyTests {
    private static final BasicLookupStrategyTestsDbHelper DATABASE_HELPER = new BasicLookupStrategyTestsDbHelper(true);

    private BasicLookupStrategy uuidEnabledStrategy;

    @Test
    public void testReadObjectIdentityUsingUuidType() {
        ObjectIdentity oid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS_WITH_UUID, AbstractBasicLookupStrategyTests.OBJECT_IDENTITY_UUID);
        Map<ObjectIdentity, Acl> foundAcls = uuidEnabledStrategy.readAclsById(Arrays.asList(oid), Arrays.asList(AbstractBasicLookupStrategyTests.BEN_SID));
        Assert.assertEquals(1, foundAcls.size());
        Assert.assertNotNull(foundAcls.get(oid));
    }

    @Test
    public void testReadObjectIdentityUsingLongTypeWithConversionServiceEnabled() {
        ObjectIdentity oid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(100));
        Map<ObjectIdentity, Acl> foundAcls = uuidEnabledStrategy.readAclsById(Arrays.asList(oid), Arrays.asList(AbstractBasicLookupStrategyTests.BEN_SID));
        Assert.assertEquals(1, foundAcls.size());
        Assert.assertNotNull(foundAcls.get(oid));
    }

    @Test(expected = ConversionFailedException.class)
    public void testReadObjectIdentityUsingNonUuidInDatabase() {
        ObjectIdentity oid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS_WITH_UUID, AbstractBasicLookupStrategyTests.OBJECT_IDENTITY_LONG_AS_UUID);
        uuidEnabledStrategy.readAclsById(Arrays.asList(oid), Arrays.asList(AbstractBasicLookupStrategyTests.BEN_SID));
    }
}


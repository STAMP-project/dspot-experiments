/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.federatedstore;


import com.google.common.collect.Sets;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.user.User;


public class FederatedAccessNullEmptyTest {
    User user;

    @Test
    public void shouldInValidateWithExplicitlyNullCollectionAuth() throws Exception {
        final FederatedAccess access = new FederatedAccess.Builder().graphAuths(((Collection) (null))).build();
        Assert.assertFalse(access.isValidToExecute(user));
    }

    @Test
    public void shouldInValidateWithExplicitlyNullStringsAuth() throws Exception {
        final FederatedAccess access = new FederatedAccess.Builder().graphAuths(((String[]) (null))).build();
        Assert.assertFalse(access.isValidToExecute(user));
    }

    @Test
    public void shouldInValidateWithEmptyStringAuth() throws Exception {
        final FederatedAccess access = new FederatedAccess.Builder().graphAuths("").build();
        Assert.assertFalse(access.isValidToExecute(user));
    }

    @Test
    public void shouldInValidateWithEmptyStringsAuth() throws Exception {
        final FederatedAccess access = new FederatedAccess.Builder().graphAuths(new String[0]).build();
        Assert.assertFalse(access.isValidToExecute(user));
    }

    @Test
    public void shouldInValidateWithEmptyCollectionAuth() throws Exception {
        final FederatedAccess access = new FederatedAccess.Builder().graphAuths(Sets.newHashSet()).build();
        Assert.assertFalse(access.isValidToExecute(user));
    }

    @Test
    public void shouldInValidateWithUnSetAuth() throws Exception {
        final FederatedAccess access = new FederatedAccess.Builder().build();
        Assert.assertFalse(access.isValidToExecute(user));
    }
}


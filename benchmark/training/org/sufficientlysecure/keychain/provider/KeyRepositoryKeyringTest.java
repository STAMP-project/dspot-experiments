/**
 * Copyright (C) Art O Cathain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sufficientlysecure.keychain.provider;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.support.KeyringTestingHelper;


@RunWith(KeychainTestRunner.class)
public class KeyRepositoryKeyringTest {
    @Test
    public void testSavePublicKeyring() throws Exception {
        Assert.assertTrue(new KeyringTestingHelper(RuntimeEnvironment.application).addKeyring(Collections.singleton("/public-key-for-sample.blob")));
    }
}


/**
 * Copyright (C) 2014 Dominik Sch?rmann <dominik@dominikschuermann.de>
 * Copyright (C) 2014 Vincent Breitmoser <v.breitmoser@mugenguild.com>
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


import RuntimeEnvironment.application;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyRepository;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing;
import org.sufficientlysecure.keychain.ui.util.KeyFormattingUtils;


@RunWith(KeychainTestRunner.class)
public class KeyRepositoryTest {
    private UncachedKeyRing testKeyring;

    @Test
    public void testKeySelection() throws Exception {
        long expectedAuthSubKeyId = KeyFormattingUtils.convertKeyIdHexToKeyId("0xcf64ee600f6fec9c");
        long expectedEncryptSubKeyId = KeyFormattingUtils.convertKeyIdHexToKeyId("0xDA7207E385A44339");
        long expectedSignSubKeyId = KeyFormattingUtils.convertKeyIdHexToKeyId("0xB8ECA89E054028D5");
        KeyRepository keyRepository = KeyRepository.create(application);
        long masterKeyId = testKeyring.getMasterKeyId();
        long authSubKeyId = keyRepository.getEffectiveAuthenticationKeyId(masterKeyId);
        long signSubKeyId = keyRepository.getSecretSignId(masterKeyId);
        List<Long> publicEncryptionIds = keyRepository.getPublicEncryptionIds(masterKeyId);
        Assert.assertEquals(expectedAuthSubKeyId, authSubKeyId);
        Assert.assertEquals(expectedSignSubKeyId, signSubKeyId);
        Assert.assertEquals(1, publicEncryptionIds.size());
        Assert.assertEquals(expectedEncryptSubKeyId, ((long) (publicEncryptionIds.get(0))));
    }
}


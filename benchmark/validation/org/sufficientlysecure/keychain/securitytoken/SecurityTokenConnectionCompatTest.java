/**
 * Copyright (C) 2018 Sch?rmann & Breitmoser GbR
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
package org.sufficientlysecure.keychain.securitytoken;


import java.util.List;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;


@RunWith(KeychainTestRunner.class)
@Ignore("Only for reference right now")
public class SecurityTokenConnectionCompatTest {
    private byte[] encryptedSessionKey;

    private OpenPgpCommandApduFactory openPgpCommandApduFactory;

    /* we have a report of breaking compatibility on some earlier version.
    this test checks what was sent in that version to what we send now.
    // see https://github.com/open-keychain/open-keychain/issues/2049
    // see https://github.com/open-keychain/open-keychain/commit/ee8cd3862f65de580ed949bc838628610e22cd98
     */
    @Test
    public void testPrePostEquals() throws Exception {
        List<String> preApdus = decryptPre_ee8cd38();
        List<String> postApdus = decryptNow();
        Assert.assertEquals(preApdus, postApdus);
    }
}


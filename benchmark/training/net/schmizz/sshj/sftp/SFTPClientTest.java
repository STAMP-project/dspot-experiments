/**
 * Copyright (C)2009 - SSHJ Contributors
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
package net.schmizz.sshj.sftp;


import org.junit.Test;
import org.mockito.Mockito;


public class SFTPClientTest {
    private final SFTPEngine sftpEngine = Mockito.mock(SFTPEngine.class);

    @Test
    public void doesNotTryToCreateDirectoryTwiceWhenPathHasTrailingSeparator() throws Exception {
        SFTPClient client = new SFTPClient(sftpEngine);
        client.mkdirs("/folder/directory/");
        Mockito.verify(sftpEngine, Mockito.times(1)).makeDir("/folder/directory");
    }
}


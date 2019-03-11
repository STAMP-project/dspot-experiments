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


import FileMode.Type.DIRECTORY;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class FileModeTest {
    @Test
    public void shouldDetectDirectoryWithLinuxMask() {
        FileMode fileMode = new FileMode(16877);
        Assert.assertThat(fileMode.toString(), IsEqual.equalTo("[mask=40755]"));
        Assert.assertThat(fileMode.getType(), IsEqual.equalTo(DIRECTORY));
    }

    @Test
    public void shouldDetectDirectoryWithAixUnixMask() {
        FileMode fileMode = new FileMode(82413);
        Assert.assertThat(fileMode.toString(), IsEqual.equalTo("[mask=240755]"));
        Assert.assertThat(fileMode.getType(), IsEqual.equalTo(DIRECTORY));
    }
}


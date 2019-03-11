/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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
package org.springframework.security.acls.domain;


import BasePermission.ADMINISTRATION;
import BasePermission.CREATE;
import BasePermission.READ;
import BasePermission.WRITE;
import org.junit.Test;
import org.springframework.security.acls.model.Permission;


/**
 * Tests classes associated with Permission.
 *
 * @author Ben Alex
 */
public class PermissionTests {
    private DefaultPermissionFactory permissionFactory;

    @Test
    public void basePermissionTest() {
        Permission p = permissionFactory.buildFromName("WRITE");
        assertThat(p).isNotNull();
    }

    @Test
    public void expectedIntegerValues() {
        assertThat(READ.getMask()).isEqualTo(1);
        assertThat(ADMINISTRATION.getMask()).isEqualTo(16);
        assertThat(new CumulativePermission().set(READ).set(WRITE).set(CREATE).getMask()).isEqualTo(7);
        assertThat(new CumulativePermission().set(READ).set(ADMINISTRATION).getMask()).isEqualTo(17);
    }

    @Test
    public void fromInteger() {
        Permission permission = permissionFactory.buildFromMask(7);
        permission = permissionFactory.buildFromMask(4);
    }

    @Test
    public void stringConversion() {
        permissionFactory.registerPublicPermissions(SpecialPermission.class);
        assertThat(READ.toString()).isEqualTo("BasePermission[...............................R=1]");
        assertThat(ADMINISTRATION.toString()).isEqualTo("BasePermission[...........................A....=16]");
        assertThat(new CumulativePermission().set(READ).toString()).isEqualTo("CumulativePermission[...............................R=1]");
        assertThat(new CumulativePermission().set(SpecialPermission.ENTER).set(ADMINISTRATION).toString()).isEqualTo("CumulativePermission[..........................EA....=48]");
        assertThat(new CumulativePermission().set(ADMINISTRATION).set(READ).toString()).isEqualTo("CumulativePermission[...........................A...R=17]");
        assertThat(new CumulativePermission().set(ADMINISTRATION).set(READ).clear(ADMINISTRATION).toString()).isEqualTo("CumulativePermission[...............................R=1]");
        assertThat(new CumulativePermission().set(ADMINISTRATION).set(READ).clear(ADMINISTRATION).clear(READ).toString()).isEqualTo("CumulativePermission[................................=0]");
    }
}


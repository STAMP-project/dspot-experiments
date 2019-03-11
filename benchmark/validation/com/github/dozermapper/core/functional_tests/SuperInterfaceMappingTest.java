/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import java.io.Serializable;
import org.junit.Test;


public class SuperInterfaceMappingTest extends AbstractFunctionalTest {
    @Test
    public void testInterfaces() {
        // test data
        String name = "name1";
        String socialNr = "socialNr1";
        SuperInterfaceMappingTest.Taxer source = new SuperInterfaceMappingTest.TaxerDef(name, socialNr);
        // convert
        SuperInterfaceMappingTest.TaxerDto target = SuperInterfaceMappingTest.TaxerDto.class.cast(mapper.map(source, SuperInterfaceMappingTest.TaxerDto.class));
        assertTarget(target);
    }

    public interface Member {
        String getName();
    }

    public interface Taxer extends SuperInterfaceMappingTest.Member {
        String getSocialNr();
    }

    public interface EntityRich {}

    public interface EntityAdminObject extends SuperInterfaceMappingTest.AdminObject , SuperInterfaceMappingTest.EntityRich {}

    public interface AdminObject extends SuperInterfaceMappingTest.HasDateCreation , SuperInterfaceMappingTest.HasDateModified {}

    public interface HasDateCreation {}

    public interface HasDateModified {}

    public static class EntityDao implements SuperInterfaceMappingTest.EntityRich {}

    public static class EntityAdminDao extends SuperInterfaceMappingTest.EntityDao implements SuperInterfaceMappingTest.EntityAdminObject {}

    public static class EntityBase extends SuperInterfaceMappingTest.EntityAdminDao {}

    public static class MemberDef extends SuperInterfaceMappingTest.EntityBase implements SuperInterfaceMappingTest.Member {
        private String name;

        public MemberDef(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class TaxerDef extends SuperInterfaceMappingTest.MemberDef implements SuperInterfaceMappingTest.Taxer {
        private String socialNr;

        public TaxerDef(String name, String socialNr) {
            super(name);
            this.socialNr = socialNr;
        }

        public String getSocialNr() {
            return this.socialNr;
        }
    }

    public interface HasId extends SuperInterfaceMappingTest.HasIdRead {}

    public interface HasIdRead {}

    public static class EntityIdSimple implements SuperInterfaceMappingTest.HasId , Serializable {}

    public static class EntityAdmin extends SuperInterfaceMappingTest.EntityIdSimple {}

    public static class MemberDto extends SuperInterfaceMappingTest.EntityAdmin {
        private String name;

        public String getName() {
            return name;
        }
    }

    public static class TaxerDto extends SuperInterfaceMappingTest.MemberDto {
        private String socialNr;

        public String getSocialNr() {
            return this.socialNr;
        }
    }
}


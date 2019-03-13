/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.disassembly.types;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests all public methods of the TypeManager class. Each test is performed for the kernel32 and
 * the notepad module, respectively.
 *
 *  The tests basically verify two things: a) whether the output from the type manager is correct,
 * and b) whether the database tables have been updated correctly.
 *
 *  Each tests has to cleanup the types/members that were created, otherwise other tests might fail
 * or depend on the order of execution.
 *
 * @author jannewger@google.com (Jan Newger)
 */
@RunWith(JUnit4.class)
public class TypeManagerExpensiveTests extends ExpensiveBaseTest {
    private static String TypeName = "test_type";

    private static String UpdatedTypeName = "updated_test_type";

    private static String CompoundTypeName = "test_compound_type";

    private static String MemberName = "test_member";

    private static String MemberName1 = "test_member_1";

    private static String MemberName2 = "test_member_2";

    private static String UpdatedMemberName = "updated_test_member";

    @Test
    public void testAddMember() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        testAddMember(getKernel32Module());
        testAddMember(getNotepadModule());
    }

    @Test
    public void testCreateType() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        testCreateType(getKernel32Module());
        testCreateType(getNotepadModule());
    }

    @Test
    public void testCreateTypeSubstitution() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        testCreateTypeSubstitution(getKernel32Module());
        testCreateTypeSubstitution(getNotepadModule());
    }

    @Test
    public void testDeleteFirstMember() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        testDeleteFirstMember(getKernel32Module());
        testDeleteFirstMember(getNotepadModule());
    }

    @Test
    public void testDeleteMember() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        testDeleteMember(getKernel32Module());
        testDeleteMember(getNotepadModule());
    }

    @Test
    public void testDeleteType() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        testDeleteType(getKernel32Module());
        testDeleteType(getNotepadModule());
    }

    @Test
    public void testUpdateMember() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        testUpdateMember(getKernel32Module());
        testUpdateMember(getNotepadModule());
    }

    @Test
    public void testUpdateType() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        testUpdateType(getKernel32Module());
        testUpdateType(getNotepadModule());
    }
}


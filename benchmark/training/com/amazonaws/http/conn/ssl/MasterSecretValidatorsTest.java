/**
 * Copyright 2015-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http.conn.ssl;


import com.amazonaws.http.conn.ssl.MasterSecretValidators.NoOpMasterSecretValidator;
import com.amazonaws.http.conn.ssl.privileged.PrivilegedMasterSecretValidator;
import org.junit.Test;


public class MasterSecretValidatorsTest {
    @Test
    public void java6() {
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(PrivilegedMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 6, 0, 90));
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(NoOpMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 6, 0, 91));
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(NoOpMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 6, 0, 92));
    }

    @Test
    public void java7() {
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(PrivilegedMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 7, 0, 50));
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(NoOpMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 7, 0, 51));
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(NoOpMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 7, 0, 52));
    }

    @Test
    public void java8() {
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(PrivilegedMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 8, 0, 30));
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(NoOpMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 8, 0, 31));
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(NoOpMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 8, 0, 32));
    }

    @Test
    public void java9() {
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(NoOpMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 9, 0, 0));
    }

    @Test
    public void unknownJavaVersion() {
        MasterSecretValidatorsTest.assertMasterSecretValidatorImplForJavaVersion(NoOpMasterSecretValidator.class, MasterSecretValidatorsTest.jv(1, 5, 0, 0));
    }
}


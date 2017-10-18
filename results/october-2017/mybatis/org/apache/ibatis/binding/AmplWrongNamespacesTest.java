/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.binding;


public class AmplWrongNamespacesTest {
    @org.junit.Test(expected = java.lang.RuntimeException.class)
    public void shouldFailForWrongNamespace() throws java.lang.Exception {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.addMapper(org.apache.ibatis.binding.WrongNamespaceMapper.class);
    }

    @org.junit.Test(expected = java.lang.RuntimeException.class)
    public void shouldFailForMissingNamespace() throws java.lang.Exception {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.addMapper(org.apache.ibatis.binding.MissingNamespaceMapper.class);
    }

    @org.junit.Test(timeout = 10000)
    public void shouldFailForWrongNamespace_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.addMapper(org.apache.ibatis.binding.WrongNamespaceMapper.class);
            org.junit.Assert.fail("shouldFailForWrongNamespace should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void shouldFailForMissingNamespace_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.addMapper(org.apache.ibatis.binding.MissingNamespaceMapper.class);
            org.junit.Assert.fail("shouldFailForMissingNamespace should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.binding.WrongNamespacesTest#shouldFailForMissingNamespace */
    @org.junit.Test(timeout = 10000)
    public void shouldFailForMissingNamespace_sd100_failAssert86() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            boolean __DSPOT_useGeneratedKeys_51 = true;
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.addMapper(org.apache.ibatis.binding.MissingNamespaceMapper.class);
            // StatementAdd: add invocation of a method
            configuration.setUseGeneratedKeys(__DSPOT_useGeneratedKeys_51);
            org.junit.Assert.fail("shouldFailForMissingNamespace_sd100 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.binding.WrongNamespacesTest#shouldFailForMissingNamespace */
    @org.junit.Test(timeout = 10000)
    public void shouldFailForMissingNamespace_sd84_failAssert73() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_databaseId_35 = "QM-k,I]-r8//GGUV@1wl";
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.addMapper(org.apache.ibatis.binding.MissingNamespaceMapper.class);
            // StatementAdd: add invocation of a method
            configuration.setDatabaseId(__DSPOT_databaseId_35);
            org.junit.Assert.fail("shouldFailForMissingNamespace_sd84 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.binding.WrongNamespacesTest#shouldFailForWrongNamespace */
    @org.junit.Test(timeout = 10000)
    public void shouldFailForWrongNamespace_sd34372_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String __DSPOT_referencedNamespace_16822 = "#5_g!:09c<7;I=PfKpvJ";
            java.lang.String __DSPOT_namespace_16821 = "o4u!VU-Uk:_Szgn=Iy@;";
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.addMapper(org.apache.ibatis.binding.WrongNamespaceMapper.class);
            // StatementAdd: add invocation of a method
            configuration.addCacheRef(__DSPOT_namespace_16821, __DSPOT_referencedNamespace_16822);
            org.junit.Assert.fail("shouldFailForWrongNamespace_sd34372 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }

    /* amplification of org.apache.ibatis.binding.WrongNamespacesTest#shouldFailForWrongNamespace */
    @org.junit.Test(timeout = 10000)
    public void shouldFailForWrongNamespace_sd34445_failAssert66() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.addMapper(org.apache.ibatis.binding.WrongNamespaceMapper.class);
            // StatementAdd: add invocation of a method
            configuration.isUseActualParamName();
            org.junit.Assert.fail("shouldFailForWrongNamespace_sd34445 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder.BuilderException eee) {
        }
    }
}


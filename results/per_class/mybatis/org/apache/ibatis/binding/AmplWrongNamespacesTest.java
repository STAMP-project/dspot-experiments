

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
    public void shouldFailForMissingNamespace_add1_failAssert0() throws java.lang.Exception {
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.addMapper(org.apache.ibatis.binding.MissingNamespaceMapper.class);
            configuration.addMapper(org.apache.ibatis.binding.MissingNamespaceMapper.class);
            org.junit.Assert.fail("shouldFailForMissingNamespace_add1 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void shouldFailForWrongNamespace_add10_failAssert0() throws java.lang.Exception {
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.addMapper(org.apache.ibatis.binding.WrongNamespaceMapper.class);
            configuration.addMapper(org.apache.ibatis.binding.WrongNamespaceMapper.class);
            org.junit.Assert.fail("shouldFailForWrongNamespace_add10 should have thrown BuilderException");
        } catch (org.apache.ibatis.builder eee) {
        }
    }
}


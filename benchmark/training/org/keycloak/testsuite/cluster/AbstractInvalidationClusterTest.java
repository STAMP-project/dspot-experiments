package org.keycloak.testsuite.cluster;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author tkyjovsk
 * @param <T>
 * 		entity representation
 * @param <TR>
 * 		entity resource
 */
public abstract class AbstractInvalidationClusterTest<T, TR> extends AbstractClusterTest {
    @Test
    public void crudWithoutFailover() {
        crud(false);
    }

    @Test
    public void crudWithFailover() {
        crud(true);
    }

    protected List<String> excludedComparisonFields = new ArrayList<>();
}


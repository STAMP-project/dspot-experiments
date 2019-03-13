package org.robolectric.res;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ResourceTableTest {
    private PackageResourceTable resourceTable;

    @Test
    public void getPackageName_shouldReturnPackageNameOfItsResources() {
        resourceTable.addResource(43620761, "type", "name");
        assertThat(resourceTable.getPackageName()).isEqualTo("myPackage");
    }

    @Test
    public void getPackageIdentifier_shouldReturnPackageIdentiferOfItsResources() {
        resourceTable.addResource(43620761, "type", "name");
        assertThat(resourceTable.getPackageIdentifier()).isEqualTo(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addResource_shouldPreventMixedPackageIdentifiers() {
        resourceTable.addResource(43620761, "type", "name");
        resourceTable.addResource(60397977, "type", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldForbidIdClashes() {
        resourceTable.addResource(42502280, "type", "name");
        resourceTable.addResource(43620761, "type", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldForbidDuplicateNames() {
        resourceTable.addResource(43620761, "type", "name");
        resourceTable.addResource(43620761, "type", "anotherName");
    }
}


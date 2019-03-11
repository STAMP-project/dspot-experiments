/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.mixedmode;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.hibernate.jpamodelgen.test.util.WithMappingFiles;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class MixedConfigurationTest extends CompilationTest {
    @Test
    @WithClasses({ Car.class, Vehicle.class })
    @WithMappingFiles("car.xml")
    public void testDefaultAccessTypeApplied() {
        TestUtil.assertMetamodelClassGeneratedFor(Vehicle.class);
        TestUtil.assertMetamodelClassGeneratedFor(Car.class);
        TestUtil.assertAbsenceOfFieldInMetamodelFor(Car.class, "horsePower", "'horsePower' should not appear in metamodel since it does have no field.");
    }

    @Test
    @WithClasses({ Truck.class, Vehicle.class })
    @WithMappingFiles("truck.xml")
    public void testExplicitXmlConfiguredAccessTypeApplied() {
        TestUtil.assertMetamodelClassGeneratedFor(Vehicle.class);
        TestUtil.assertMetamodelClassGeneratedFor(Truck.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(Truck.class, "horsePower", "Property 'horsePower' has explicit access type and should be in metamodel");
        TestUtil.assertAttributeTypeInMetaModelFor(Truck.class, "horsePower", Integer.class, "Wrong meta model type");
    }

    @Test
    @WithClasses({ Car.class, Vehicle.class, RentalCar.class, RentalCompany.class })
    @WithMappingFiles({ "car.xml", "rentalcar.xml" })
    public void testMixedConfiguration() {
        TestUtil.assertMetamodelClassGeneratedFor(RentalCar.class);
        TestUtil.assertMetamodelClassGeneratedFor(RentalCompany.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(RentalCar.class, "company", "Property 'company' should be included due to xml configuration");
        TestUtil.assertAttributeTypeInMetaModelFor(RentalCar.class, "company", RentalCompany.class, "Wrong meta model type");
        TestUtil.assertPresenceOfFieldInMetamodelFor(RentalCar.class, "insurance", "Property 'insurance' should be included since it is an embeddable");
        TestUtil.assertAttributeTypeInMetaModelFor(RentalCar.class, "insurance", Insurance.class, "Wrong meta model type");
    }

    @Test
    @WithClasses({ Coordinates.class, ZeroCoordinates.class, Location.class })
    @WithMappingFiles("coordinates.xml")
    public void testAccessTypeForXmlConfiguredEmbeddables() {
        TestUtil.assertMetamodelClassGeneratedFor(Coordinates.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(Coordinates.class, "longitude", "field exists and should be in metamodel");
        TestUtil.assertPresenceOfFieldInMetamodelFor(Coordinates.class, "latitude", "field exists and should be in metamodel");
        TestUtil.assertMetamodelClassGeneratedFor(ZeroCoordinates.class);
        TestUtil.assertAbsenceOfFieldInMetamodelFor(ZeroCoordinates.class, "longitude", "Field access should be used, but ZeroCoordinates does not define fields");
        TestUtil.assertAbsenceOfFieldInMetamodelFor(ZeroCoordinates.class, "latitude", "Field access should be used, but ZeroCoordinates does not define fields");
    }
}


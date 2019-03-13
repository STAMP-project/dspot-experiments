/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.reflection;


import EnumType.STRING;
import FetchType.LAZY;
import GenerationType.SEQUENCE;
import InheritanceType.JOINED;
import TemporalType.DATE;
import TemporalType.TIMESTAMP;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.hibernate.annotations.Columns;
import org.hibernate.cfg.annotations.reflection.JPAOverriddenAnnotationReader;
import org.hibernate.cfg.annotations.reflection.XMLContext;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.boot.BootstrapContextImpl;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class JPAOverriddenAnnotationReaderTest extends BaseUnitTestCase {
    @Test
    public void testMappedSuperclassAnnotations() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/metadata-complete.xml");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(Organization.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertTrue(reader.isAnnotationPresent(MappedSuperclass.class));
    }

    @Test
    public void testEntityRelatedAnnotations() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/orm.xml");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(Administration.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Entity.class));
        Assert.assertEquals("Default value in xml entity should not override @Entity.name", "JavaAdministration", reader.getAnnotation(Entity.class).name());
        Assert.assertNotNull(reader.getAnnotation(Table.class));
        Assert.assertEquals("@Table not overridden", "tbl_admin", reader.getAnnotation(Table.class).name());
        Assert.assertEquals("Default schema not overridden", "myschema", reader.getAnnotation(Table.class).schema());
        Assert.assertEquals("Proper @Table.uniqueConstraints", 2, reader.getAnnotation(Table.class).uniqueConstraints()[0].columnNames().length);
        String columnName = reader.getAnnotation(Table.class).uniqueConstraints()[0].columnNames()[0];
        Assert.assertTrue("Proper @Table.uniqueConstraints", (("firstname".equals(columnName)) || ("lastname".equals(columnName))));
        Assert.assertNull("Both Java and XML used", reader.getAnnotation(SecondaryTable.class));
        Assert.assertNotNull("XML does not work", reader.getAnnotation(SecondaryTables.class));
        SecondaryTable[] tables = reader.getAnnotation(SecondaryTables.class).value();
        Assert.assertEquals(1, tables.length);
        Assert.assertEquals("admin2", tables[0].name());
        Assert.assertEquals("unique constraints ignored", 1, tables[0].uniqueConstraints().length);
        Assert.assertEquals("pk join column ignored", 1, tables[0].pkJoinColumns().length);
        Assert.assertEquals("pk join column ignored", "admin_id", tables[0].pkJoinColumns()[0].name());
        Assert.assertNotNull("Sequence Overriding not working", reader.getAnnotation(SequenceGenerator.class));
        Assert.assertEquals("wrong sequence name", "seqhilo", reader.getAnnotation(SequenceGenerator.class).sequenceName());
        Assert.assertEquals("default fails", 50, reader.getAnnotation(SequenceGenerator.class).allocationSize());
        Assert.assertNotNull("TableOverriding not working", reader.getAnnotation(TableGenerator.class));
        Assert.assertEquals("wrong tble name", "tablehilo", reader.getAnnotation(TableGenerator.class).table());
        Assert.assertEquals("no schema overriding", "myschema", reader.getAnnotation(TableGenerator.class).schema());
        reader = new JPAOverriddenAnnotationReader(Match.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Table.class));
        Assert.assertEquals("Java annotation not taken into account", "matchtable", reader.getAnnotation(Table.class).name());
        Assert.assertEquals("Java annotation not taken into account", "matchschema", reader.getAnnotation(Table.class).schema());
        Assert.assertEquals("Overriding not taken into account", "mycatalog", reader.getAnnotation(Table.class).catalog());
        Assert.assertNotNull("SecondaryTable swallowed", reader.getAnnotation(SecondaryTables.class));
        Assert.assertEquals("Default schema not taken into account", "myschema", reader.getAnnotation(SecondaryTables.class).value()[0].schema());
        Assert.assertNotNull(reader.getAnnotation(Inheritance.class));
        Assert.assertEquals("inheritance strategy not overriden", JOINED, reader.getAnnotation(Inheritance.class).strategy());
        Assert.assertNotNull("NamedQuery not overriden", reader.getAnnotation(NamedQueries.class));
        Assert.assertEquals("No deduplication", 3, reader.getAnnotation(NamedQueries.class).value().length);
        Assert.assertEquals("deduplication kept the Java version", 1, reader.getAnnotation(NamedQueries.class).value()[1].hints().length);
        Assert.assertEquals("org.hibernate.timeout", reader.getAnnotation(NamedQueries.class).value()[1].hints()[0].name());
        Assert.assertNotNull("NamedNativeQuery not overriden", reader.getAnnotation(NamedNativeQueries.class));
        Assert.assertEquals("No deduplication", 3, reader.getAnnotation(NamedNativeQueries.class).value().length);
        Assert.assertEquals("deduplication kept the Java version", 1, reader.getAnnotation(NamedNativeQueries.class).value()[1].hints().length);
        Assert.assertEquals("org.hibernate.timeout", reader.getAnnotation(NamedNativeQueries.class).value()[1].hints()[0].name());
        Assert.assertNotNull(reader.getAnnotation(SqlResultSetMappings.class));
        Assert.assertEquals("competitor1Point", reader.getAnnotation(SqlResultSetMappings.class).value()[0].columns()[0].name());
        Assert.assertEquals("competitor1Point", reader.getAnnotation(SqlResultSetMappings.class).value()[0].entities()[0].fields()[0].column());
        Assert.assertNotNull(reader.getAnnotation(ExcludeSuperclassListeners.class));
        Assert.assertNotNull(reader.getAnnotation(ExcludeDefaultListeners.class));
        reader = new JPAOverriddenAnnotationReader(Competition.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(MappedSuperclass.class));
        reader = new JPAOverriddenAnnotationReader(TennisMatch.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNull("Mutualize PKJC into PKJCs", reader.getAnnotation(PrimaryKeyJoinColumn.class));
        Assert.assertNotNull(reader.getAnnotation(PrimaryKeyJoinColumns.class));
        Assert.assertEquals("PrimaryKeyJoinColumn overrden", "id", reader.getAnnotation(PrimaryKeyJoinColumns.class).value()[0].name());
        Assert.assertNotNull(reader.getAnnotation(AttributeOverrides.class));
        Assert.assertEquals("Wrong deduplication", 3, reader.getAnnotation(AttributeOverrides.class).value().length);
        Assert.assertEquals("Wrong priority (XML vs java annotations)", "fld_net", reader.getAnnotation(AttributeOverrides.class).value()[0].column().name());
        Assert.assertEquals("Column mapping", 2, reader.getAnnotation(AttributeOverrides.class).value()[1].column().scale());
        Assert.assertEquals("Column mapping", true, reader.getAnnotation(AttributeOverrides.class).value()[1].column().unique());
        Assert.assertNotNull(reader.getAnnotation(AssociationOverrides.class));
        Assert.assertEquals("no XML processing", 1, reader.getAnnotation(AssociationOverrides.class).value().length);
        Assert.assertEquals("wrong xml processing", "id", reader.getAnnotation(AssociationOverrides.class).value()[0].joinColumns()[0].referencedColumnName());
        reader = new JPAOverriddenAnnotationReader(SocialSecurityPhysicalAccount.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(IdClass.class));
        Assert.assertEquals("id-class not used", SocialSecurityNumber.class, reader.getAnnotation(IdClass.class).value());
        Assert.assertEquals("discriminator-value not used", "Physical", reader.getAnnotation(DiscriminatorValue.class).value());
        Assert.assertNotNull("discriminator-column not used", reader.getAnnotation(DiscriminatorColumn.class));
        Assert.assertEquals("discriminator-column.name default value broken", "DTYPE", reader.getAnnotation(DiscriminatorColumn.class).name());
        Assert.assertEquals("discriminator-column.length broken", 34, reader.getAnnotation(DiscriminatorColumn.class).length());
    }

    @Test
    public void testEntityRelatedAnnotationsMetadataComplete() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/metadata-complete.xml");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(Administration.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Entity.class));
        Assert.assertEquals("Metadata complete should ignore java annotations", "", reader.getAnnotation(Entity.class).name());
        Assert.assertNotNull(reader.getAnnotation(Table.class));
        Assert.assertEquals("@Table should not be used", "", reader.getAnnotation(Table.class).name());
        Assert.assertEquals("Default schema not overriden", "myschema", reader.getAnnotation(Table.class).schema());
        reader = new JPAOverriddenAnnotationReader(Match.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Table.class));
        Assert.assertEquals("@Table should not be used", "", reader.getAnnotation(Table.class).name());
        Assert.assertEquals("Overriding not taken into account", "myschema", reader.getAnnotation(Table.class).schema());
        Assert.assertEquals("Overriding not taken into account", "mycatalog", reader.getAnnotation(Table.class).catalog());
        Assert.assertNull("Ignore Java annotation", reader.getAnnotation(SecondaryTable.class));
        Assert.assertNull("Ignore Java annotation", reader.getAnnotation(SecondaryTables.class));
        Assert.assertNull("Ignore Java annotation", reader.getAnnotation(Inheritance.class));
        Assert.assertNull(reader.getAnnotation(NamedQueries.class));
        Assert.assertNull(reader.getAnnotation(NamedNativeQueries.class));
        reader = new JPAOverriddenAnnotationReader(TennisMatch.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNull(reader.getAnnotation(PrimaryKeyJoinColumn.class));
        Assert.assertNull(reader.getAnnotation(PrimaryKeyJoinColumns.class));
        reader = new JPAOverriddenAnnotationReader(Competition.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNull(reader.getAnnotation(MappedSuperclass.class));
        reader = new JPAOverriddenAnnotationReader(SocialSecurityMoralAccount.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNull(reader.getAnnotation(IdClass.class));
        Assert.assertNull(reader.getAnnotation(DiscriminatorValue.class));
        Assert.assertNull(reader.getAnnotation(DiscriminatorColumn.class));
        Assert.assertNull(reader.getAnnotation(SequenceGenerator.class));
        Assert.assertNull(reader.getAnnotation(TableGenerator.class));
    }

    @Test
    public void testIdRelatedAnnotations() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/orm.xml");
        Method method = Administration.class.getDeclaredMethod("getId");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(method, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNull(reader.getAnnotation(Id.class));
        Assert.assertNull(reader.getAnnotation(Column.class));
        Field field = Administration.class.getDeclaredField("id");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Id.class));
        Assert.assertNotNull(reader.getAnnotation(GeneratedValue.class));
        Assert.assertEquals(SEQUENCE, reader.getAnnotation(GeneratedValue.class).strategy());
        Assert.assertEquals("generator", reader.getAnnotation(GeneratedValue.class).generator());
        Assert.assertNotNull(reader.getAnnotation(SequenceGenerator.class));
        Assert.assertEquals("seq", reader.getAnnotation(SequenceGenerator.class).sequenceName());
        Assert.assertNotNull(reader.getAnnotation(Columns.class));
        Assert.assertEquals(1, reader.getAnnotation(Columns.class).columns().length);
        Assert.assertEquals("fld_id", reader.getAnnotation(Columns.class).columns()[0].name());
        Assert.assertNotNull(reader.getAnnotation(Temporal.class));
        Assert.assertEquals(DATE, reader.getAnnotation(Temporal.class).value());
        context = buildContext("org/hibernate/test/annotations/reflection/metadata-complete.xml");
        method = Administration.class.getDeclaredMethod("getId");
        reader = new JPAOverriddenAnnotationReader(method, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull("Default access type when not defined in metadata complete should be property", reader.getAnnotation(Id.class));
        field = Administration.class.getDeclaredField("id");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNull("Default access type when not defined in metadata complete should be property", reader.getAnnotation(Id.class));
        method = BusTrip.class.getDeclaredMethod("getId");
        reader = new JPAOverriddenAnnotationReader(method, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNull(reader.getAnnotation(EmbeddedId.class));
        field = BusTrip.class.getDeclaredField("id");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(EmbeddedId.class));
        Assert.assertNotNull(reader.getAnnotation(AttributeOverrides.class));
        Assert.assertEquals(1, reader.getAnnotation(AttributeOverrides.class).value().length);
    }

    @Test
    public void testBasicRelatedAnnotations() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/metadata-complete.xml");
        Field field = BusTrip.class.getDeclaredField("status");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Enumerated.class));
        Assert.assertEquals(STRING, reader.getAnnotation(Enumerated.class).value());
        Assert.assertEquals(false, reader.getAnnotation(Basic.class).optional());
        field = BusTrip.class.getDeclaredField("serial");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Lob.class));
        Assert.assertEquals("serialbytes", reader.getAnnotation(Columns.class).columns()[0].name());
        field = BusTrip.class.getDeclaredField("terminusTime");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Temporal.class));
        Assert.assertEquals(TIMESTAMP, reader.getAnnotation(Temporal.class).value());
        Assert.assertEquals(LAZY, reader.getAnnotation(Basic.class).fetch());
        field = BusTripPk.class.getDeclaredField("busDriver");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.isAnnotationPresent(Basic.class));
    }

    @Test
    public void testVersionRelatedAnnotations() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/orm.xml");
        Method method = Administration.class.getDeclaredMethod("getVersion");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(method, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Version.class));
        Field field = Match.class.getDeclaredField("version");
        Assert.assertNotNull(reader.getAnnotation(Version.class));
    }

    @Test
    public void testTransientAndEmbeddedRelatedAnnotations() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/orm.xml");
        Field field = Administration.class.getDeclaredField("transientField");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Transient.class));
        Assert.assertNull(reader.getAnnotation(Basic.class));
        field = Match.class.getDeclaredField("playerASSN");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(Embedded.class));
    }

    @Test
    public void testAssociationRelatedAnnotations() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/orm.xml");
        Field field = Administration.class.getDeclaredField("defaultBusTrip");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(OneToOne.class));
        Assert.assertNull(reader.getAnnotation(JoinColumns.class));
        Assert.assertNotNull(reader.getAnnotation(PrimaryKeyJoinColumns.class));
        Assert.assertEquals("pk", reader.getAnnotation(PrimaryKeyJoinColumns.class).value()[0].name());
        Assert.assertEquals(5, reader.getAnnotation(OneToOne.class).cascade().length);
        Assert.assertEquals(LAZY, reader.getAnnotation(OneToOne.class).fetch());
        Assert.assertEquals("test", reader.getAnnotation(OneToOne.class).mappedBy());
        context = buildContext("org/hibernate/test/annotations/reflection/metadata-complete.xml");
        field = BusTrip.class.getDeclaredField("players");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(OneToMany.class));
        Assert.assertNotNull(reader.getAnnotation(JoinColumns.class));
        Assert.assertEquals(2, reader.getAnnotation(JoinColumns.class).value().length);
        Assert.assertEquals("driver", reader.getAnnotation(JoinColumns.class).value()[0].name());
        Assert.assertNotNull(reader.getAnnotation(MapKey.class));
        Assert.assertEquals("name", reader.getAnnotation(MapKey.class).name());
        field = BusTrip.class.getDeclaredField("roads");
        reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(ManyToMany.class));
        Assert.assertNotNull(reader.getAnnotation(JoinTable.class));
        Assert.assertEquals("bus_road", reader.getAnnotation(JoinTable.class).name());
        Assert.assertEquals(2, reader.getAnnotation(JoinTable.class).joinColumns().length);
        Assert.assertEquals(1, reader.getAnnotation(JoinTable.class).inverseJoinColumns().length);
        Assert.assertEquals(2, reader.getAnnotation(JoinTable.class).uniqueConstraints()[0].columnNames().length);
        Assert.assertNotNull(reader.getAnnotation(OrderBy.class));
        Assert.assertEquals("maxSpeed", reader.getAnnotation(OrderBy.class).value());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11924")
    public void testElementCollectionConverter() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/orm.xml");
        Field field = Company.class.getDeclaredField("organizations");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(field, context, BootstrapContextImpl.INSTANCE);
        Assert.assertNotNull(reader.getAnnotation(ElementCollection.class));
        Assert.assertNotNull(reader.getAnnotation(Converts.class));
        Assert.assertNotNull(reader.getAnnotation(Converts.class).value());
        Assert.assertTrue(((reader.getAnnotation(Converts.class).value().length) == 1));
        Assert.assertEquals(OrganizationConverter.class, reader.getAnnotation(Converts.class).value()[0].converter());
    }

    @Test
    public void testEntityListeners() throws Exception {
        XMLContext context = buildContext("org/hibernate/test/annotations/reflection/orm.xml");
        Method method = Administration.class.getDeclaredMethod("calculate");
        JPAOverriddenAnnotationReader reader = new JPAOverriddenAnnotationReader(method, context, BootstrapContextImpl.INSTANCE);
        Assert.assertTrue(reader.isAnnotationPresent(PrePersist.class));
        reader = new JPAOverriddenAnnotationReader(Administration.class, context, BootstrapContextImpl.INSTANCE);
        Assert.assertTrue(reader.isAnnotationPresent(EntityListeners.class));
        Assert.assertEquals(1, reader.getAnnotation(EntityListeners.class).value().length);
        Assert.assertEquals(LogListener.class, reader.getAnnotation(EntityListeners.class).value()[0]);
        method = LogListener.class.getDeclaredMethod("noLog", Object.class);
        reader = new JPAOverriddenAnnotationReader(method, context, BootstrapContextImpl.INSTANCE);
        Assert.assertTrue(reader.isAnnotationPresent(PostLoad.class));
        method = LogListener.class.getDeclaredMethod("log", Object.class);
        reader = new JPAOverriddenAnnotationReader(method, context, BootstrapContextImpl.INSTANCE);
        Assert.assertTrue(reader.isAnnotationPresent(PrePersist.class));
        Assert.assertFalse(reader.isAnnotationPresent(PostPersist.class));
        Assert.assertEquals(1, context.getDefaultEntityListeners().size());
        Assert.assertEquals(OtherLogListener.class.getName(), context.getDefaultEntityListeners().get(0));
    }
}


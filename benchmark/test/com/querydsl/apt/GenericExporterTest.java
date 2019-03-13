package com.querydsl.apt;


import Keywords.JPA;
import com.google.common.collect.ForwardingSet;
import com.querydsl.apt.domain.AbstractEntityTest;
import com.querydsl.apt.domain.CustomCollection;
import com.querydsl.apt.domain.Generic2Test;
import com.querydsl.apt.hibernate.HibernateAnnotationProcessor;
import com.querydsl.codegen.GenericExporter;
import com.querydsl.core.domain.A;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class GenericExporterTest extends AbstractProcessorTest {
    private static final String PACKAGE_PATH = "src/test/java/com/querydsl/apt/domain/";

    private static final List<String> CLASSES = AbstractProcessorTest.getFiles(GenericExporterTest.PACKAGE_PATH);

    @Test
    public void execute() throws IOException {
        // via APT
        process(QuerydslAnnotationProcessor.class, GenericExporterTest.CLASSES, "QuerydslAnnotationProcessor");
        // via GenericExporter
        GenericExporter exporter = new GenericExporter();
        exporter.setTargetFolder(new File("target/GenericExporterTest"));
        exporter.export(AbstractEntityTest.class.getPackage(), A.class.getPackage());
        List<String> expected = new ArrayList<String>();
        // delegates are not supported
        expected.add("QDelegateTest_SimpleUser.java");
        expected.add("QDelegateTest_SimpleUser2.java");
        expected.add("QDelegateTest_User.java");
        expected.add("QDelegate2Test_Entity.java");
        expected.add("QExampleEntity.java");
        expected.add("QQueryProjectionTest_DTOWithProjection.java");
        expected.add("QQueryProjectionTest_EntityWithProjection.java");
        expected.add("QEmbeddable3Test_EmbeddableClass.java");
        expected.add("QQueryEmbedded4Test_User.java");
        execute(expected, "GenericExporterTest", "QuerydslAnnotationProcessor");
    }

    @Test
    public void execute2() throws IOException {
        // via APT
        process(HibernateAnnotationProcessor.class, GenericExporterTest.CLASSES, "HibernateAnnotationProcessor");
        // via GenericExporter
        GenericExporter exporter = new GenericExporter();
        exporter.setKeywords(JPA);
        exporter.setEntityAnnotation(Entity.class);
        exporter.setEmbeddableAnnotation(Embeddable.class);
        exporter.setEmbeddedAnnotation(Embedded.class);
        exporter.setSupertypeAnnotation(MappedSuperclass.class);
        exporter.setSkipAnnotation(Transient.class);
        exporter.setTargetFolder(new File("target/GenericExporterTest2"));
        exporter.addStopClass(ForwardingSet.class);
        exporter.setStrictMode(true);
        exporter.setPropertyHandling(PropertyHandling.JPA);
        exporter.export(AbstractEntityTest.class.getPackage(), A.class.getPackage());
        List<String> expected = new ArrayList<String>();
        // GenericExporter doesn't include field/method selection
        expected.add("QCustomCollection_MyCustomCollection2.java");
        expected.add("QTemporalTest_MyEntity.java");
        expected.add("QTemporal2Test_Cheque.java");
        expected.add("QQueryProjectionTest_DTOWithProjection.java");
        expected.add("QQueryProjectionTest_EntityWithProjection.java");
        expected.add("QEmbeddable3Test_EmbeddableClass.java");
        expected.add("QGeneric4Test_HidaBez.java");
        expected.add("QGeneric4Test_HidaBezGruppe.java");
        expected.add("QInterfaceType2Test_UserImpl.java");
        expected.add("QOrderTest_Order.java");
        expected.add("QManagedEmailTest_ManagedEmails.java");
        expected.add("QGeneric12Test_ChannelRole.java");
        expected.add("QManyToManyTest_Person.java");
        expected.add("QOneToOneTest_Person.java");
        expected.add("QGeneric16Test_HidaBez.java");
        expected.add("QGeneric16Test_HidaBezGruppe.java");
        execute(expected, "GenericExporterTest2", "HibernateAnnotationProcessor");
    }

    @Test
    public void execute3() {
        GenericExporter exporter = new GenericExporter();
        exporter.setKeywords(JPA);
        exporter.setEntityAnnotation(Entity.class);
        exporter.setEmbeddableAnnotation(Embeddable.class);
        exporter.setEmbeddedAnnotation(Embedded.class);
        exporter.setSupertypeAnnotation(MappedSuperclass.class);
        exporter.setSkipAnnotation(Transient.class);
        exporter.setTargetFolder(new File("target/GenericExporterTest3"));
        exporter.setPropertyHandling(PropertyHandling.JPA);
        // exporter.addStopClass(ForwardingSet.class);
        exporter.export(CustomCollection.MyCustomCollection.class, CustomCollection.MyCustomCollection2.class, CustomCollection.MyEntity.class);
    }

    @Test
    public void execute4() throws IOException {
        GenericExporter exporter = new GenericExporter();
        exporter.setKeywords(JPA);
        exporter.setEntityAnnotation(Entity.class);
        exporter.setEmbeddableAnnotation(Embeddable.class);
        exporter.setEmbeddedAnnotation(Embedded.class);
        exporter.setSupertypeAnnotation(MappedSuperclass.class);
        exporter.setSkipAnnotation(Transient.class);
        exporter.setTargetFolder(new File("target/GenericExporterTest4"));
        exporter.setPropertyHandling(PropertyHandling.JPA);
        exporter.addStopClass(ForwardingSet.class);
        exporter.export(Generic2Test.class.getClasses());
    }
}


package io.github.classgraph.issues.issue318;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;


/**
 * The Class Issue314.
 */
public class Issue318 {
    /**
     * The Interface MyAnn.
     */
    @Repeatable(Issue318.MyAnnRepeating.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @interface MyAnn {}

    /**
     * The Interface MyAnnRepeating.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @interface MyAnnRepeating {
        /**
         * Value.
         *
         * @return the my ann[]
         */
        Issue318.MyAnn[] value();
    }

    // /**
    // * The Interface MyAnnRepeating.
    // */
    // @Retention(RetentionPolicy.RUNTIME)
    // @Target({ ElementType.TYPE })
    // @interface MyAnnRepeating2 {
    // /**
    // * Value.
    // *
    // * @return the my ann[]
    // */
    // MyAnn[] value();
    // }
    /**
     * The Class With0MyAnn.
     */
    class With0MyAnn {}

    /**
     * The Class With1MyAnn.
     */
    @Issue318.MyAnn
    class With1MyAnn {}

    /**
     * The Class With2MyAnn.
     */
    @Issue318.MyAnn
    @Issue318.MyAnn
    class With2MyAnn {}

    /**
     * The Class With3MyAnn.
     */
    @Issue318.MyAnn
    @Issue318.MyAnn
    @Issue318.MyAnn
    class With3MyAnn {}

    /**
     * Issue 318.
     */
    @Test
    public void issue318() {
        try (final ScanResult scanResult = // 
        // .verbose() //
        new ClassGraph().whitelistPackages(Issue318.class.getPackage().getName()).enableAnnotationInfo().enableClassInfo().ignoreClassVisibility().scan()) {
            assertThat(scanResult.getClassesWithAnnotation(Issue318.MyAnn.class.getName()).getNames()).containsOnly(Issue318.With1MyAnn.class.getName(), Issue318.With2MyAnn.class.getName(), Issue318.With3MyAnn.class.getName());
            assertThat(scanResult.getClassInfo(Issue318.With3MyAnn.class.getName()).getAnnotationInfoRepeatable(Issue318.MyAnn.class.getName()).size()).isEqualTo(3);
        }
    }
}


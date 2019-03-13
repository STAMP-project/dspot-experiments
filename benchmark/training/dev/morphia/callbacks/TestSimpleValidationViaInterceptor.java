package dev.morphia.callbacks;


import com.mongodb.DBObject;
import dev.morphia.AbstractEntityInterceptor;
import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PrePersist;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class TestSimpleValidationViaInterceptor extends TestBase {
    static {
        MappedField.addInterestingAnnotation(TestSimpleValidationViaInterceptor.NonNull.class);
    }

    @Test
    public void testGlobalEntityInterceptorWorksAfterEntityCallback() {
        getMorphia().getMapper().addInterceptor(new TestSimpleValidationViaInterceptor.NonNullValidation());
        getMorphia().map(TestSimpleValidationViaInterceptor.E.class);
        getMorphia().map(TestSimpleValidationViaInterceptor.E2.class);
        getDs().save(new TestSimpleValidationViaInterceptor.E());
        try {
            getDs().save(new TestSimpleValidationViaInterceptor.E2());
            Assert.fail();
        } catch (TestSimpleValidationViaInterceptor.NonNullValidation.NonNullValidationException e) {
            // expected
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    public @interface NonNull {}

    static class E {
        @Id
        private final ObjectId id = new ObjectId();

        @TestSimpleValidationViaInterceptor.NonNull
        private Date lastModified;

        @PrePersist
        void entityCallback() {
            lastModified = new Date();
        }
    }

    static class E2 {
        @Id
        private final ObjectId id = new ObjectId();

        @TestSimpleValidationViaInterceptor.NonNull
        private String mustFailValidation;
    }

    public static class NonNullValidation extends AbstractEntityInterceptor {
        @Override
        public void prePersist(final Object ent, final DBObject dbObj, final Mapper mapper) {
            final MappedClass mc = mapper.getMappedClass(ent);
            final List<MappedField> fieldsToTest = mc.getFieldsAnnotatedWith(TestSimpleValidationViaInterceptor.NonNull.class);
            for (final MappedField mf : fieldsToTest) {
                if ((mf.getFieldValue(ent)) == null) {
                    throw new TestSimpleValidationViaInterceptor.NonNullValidation.NonNullValidationException(mf);
                }
            }
        }

        static class NonNullValidationException extends RuntimeException {
            NonNullValidationException(final MappedField mf) {
                super(("NonNull field is null " + (mf.getFullName())));
            }
        }
    }
}


package fr.sciam.rcast;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Stereotype;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Stereotype
@Dependent
public @interface RegisterRcast {
    String appName() default "";
}

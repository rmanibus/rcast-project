package fr.sciam.rcast;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Stereotype;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Stereotype
@Dependent
public @interface RegisterRcast {
    String appName() default "";
}

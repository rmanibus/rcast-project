package fr.sciam.rcast;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Qualifier
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Rcast {
}

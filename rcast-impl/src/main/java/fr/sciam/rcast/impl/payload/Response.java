package fr.sciam.rcast.impl.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class Response implements Serializable {
    Object result;
    Throwable exception;
}

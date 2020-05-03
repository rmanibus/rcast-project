package fr.sciam.rcast;

public interface RcastProvider {
    <T> T getInstance(Class<T> clazz, String appName);
}

package fr.sciam.rcast.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class Boot {
    @Inject
    HazelcastInstance instance;

    void onStart(@Observes StartupEvent ev) {
        instance.getConfig();
    }

    void onStop(@Observes ShutdownEvent ev) {
        instance.shutdown();
    }
}

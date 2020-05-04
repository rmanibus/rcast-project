package fr.sciam.rcast.ext;

import com.hazelcast.core.HazelcastInstance;
import fr.sciam.rcast.Rcast;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class RcastExtTest {
    @Inject
    HazelcastInstance instance;

    @Inject
    @Rcast
    FakeService service;

    @Test
    public void testVirtualInstanceGeneration(){
        service.method1("aa");
        instance.getConfig();
    }
}

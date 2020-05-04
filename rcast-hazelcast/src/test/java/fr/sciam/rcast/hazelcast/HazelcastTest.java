package fr.sciam.rcast.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class HazelcastTest {
    @Inject
    HazelcastInstance instance;

    @Test
    public void test(){
        instance.getMap("test");
    }
}

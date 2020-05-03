package fr.sciam.rcast.impl.emitter;

import fr.sciam.rcast.RcastProvider;
import fr.sciam.rcast.impl.receiver.Receiver;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
public class HzcRcastProviderTest {

    @Inject
    RcastProvider provider;
    @Inject
    Receiver receiver;

    @BeforeEach
    public void setup(){
    }
    @Test
    public void callServiceThruHazelcast(){
        FakeService service = provider.getInstance(FakeService.class, "the-app");
        assertThat(service.method1("the arg")).isEqualTo("the arg");
        assertThatThrownBy(() -> service.throwingMethod()).isInstanceOf(FakeException.class);
    }
}

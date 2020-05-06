package fr.sciam.rcast.ext;

import fr.sciam.rcast.Rcast;
import fr.sciam.rcast.impl.RcastException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@QuarkusTest
public class RcastExtTest {

    @Inject
    @Rcast
    FakeService service;

    @Inject
    @Rcast
    InnexistingService innexistingService;

    @Inject
    @Rcast
    InnexistingApp innexistingApp;

    @Test
    public void testVirtualInstanceGeneration() {
        assertThat(service.method1("the arg")).isEqualTo("the arg");
        assertThatThrownBy(innexistingService::method1).isInstanceOf(RcastException.class).hasMessageContaining("no bean found for");
        assertThatThrownBy(innexistingApp::method1).isInstanceOf(RcastException.class).hasMessageContaining("Request timeout");

    }
}

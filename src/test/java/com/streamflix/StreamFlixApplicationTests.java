package com.streamflix;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies that the whole Spring application context can start up
 * without errors (all beans, controllers, and services wire together).
 */
@SpringBootTest
class StreamFlixApplicationTests {

    @Test
    void contextLoads() {
        // If the Spring context fails to start, this test fails automatically.
    }

}

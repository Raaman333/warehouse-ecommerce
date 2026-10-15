package com.example.warehouse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.data.mongodb.uri=mongodb://localhost:27017/test_db",
        "app.jwt.secret=dGVzdF9zZWNyZXRfa2V5X2ZvcjEyM19sb2NhbF90ZXN0aW5nX29ubHk=",
})
class WarehouseApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context starts successfully with test properties
    }
}

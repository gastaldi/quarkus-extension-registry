package io.quarkus.extensions.catalog.app;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class RegistryResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/registry")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

}
package org.acme.s3;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import java.io.File;

import org.acme.objectstorage.s3.S3URLTempDTO;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

@QuarkusTest
@QuarkusTestResource(S3ContainerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
public class S3IntegrationTest {

    private static final String BASE_PATH = "/v1/s3";

    private static final String PATH_HELLO_WORLD_FILE = "src/test/java/org/acme/s3/files/hello-world.txt";
    private static final String PATH_TEST_FILE = "src/test/java/org/acme/s3/files/test-file.txt";

    private static S3URLTempDTO response;

    @Order(1)
    @Test
    public void uploadFile__DeveSalvarArquivoS3() {
        given()
                .when()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("arquivo", new File(PATH_HELLO_WORLD_FILE))
                .multiPart("nomeArquivo", "hello-world.txt")
                .post(BASE_PATH + "/upload")
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    @Order(2)
    public void downloadFile__DeveBaixarArquivoDoS3() {
        given()
                .when()
                .pathParam("chaveObjeto", "hello-world.txt")
                .get(BASE_PATH + "/{chaveObjeto}")
                .then()
                .statusCode(OK.getStatusCode())
                .body(CoreMatchers.containsString("HELLO WORLD"));
    }

    @Test
    @Order(3)
    public void downloadFile__DeveCriarUrlAssinada() {
        response = given()
                .when()
                .pathParam("chaveObjeto", "hello-world.txt")
                .post(BASE_PATH + "/sign-url/{chaveObjeto}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(S3URLTempDTO.class);
    }

    @Test
    @Order(4)
    public void downloadFile__DeveFazerUploadSobrescrevendoOArquivoPelaUrlAssinada() {
        given()
                .when()
                .multiPart(new File(PATH_TEST_FILE))
                .put(response.getUrlAssinadaPUT())
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    @Order(5)
    public void downloadFile__DeveBaixarArquivoSobrescritoPelaUrlAssinada() {
        given()
                .when()
                .get(response.getUrlAssinadaGET())
                .then()
                .statusCode(OK.getStatusCode())
                .body(CoreMatchers.containsString("TEST FILE"));
    }

    @Test
    @Order(6)
    public void deleteFile__DeveDeletarOArquivo() {
        given()
                .when()
                .pathParam("chaveObjeto", "hello-world.txt")
                .delete(BASE_PATH + "/{chaveObjeto}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

}

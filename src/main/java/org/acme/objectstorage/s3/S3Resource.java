package org.acme.objectstorage.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Path("/v1/s3")
public class S3Resource {

    private final Logger log = LoggerFactory.getLogger(S3Resource.class);

    @Inject
    private S3Service service;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response enviaObjeto(@MultipartForm final MultiparteArquivo multiparteArquivo) throws IOException {
        this.log.debug("Requisição para enviar objeto. Nome do Arquivo: {}", multiparteArquivo.getNomeArquivo());

        this.service.enviaObjeto(S3TipoInstanciaCliente.TEST_BUCKET, multiparteArquivo);

        return Response.ok().status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{chaveObjeto:(?:[^/]+/){0,}[^/]+}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response baixaObjeto(@PathParam("chaveObjeto") final String chaveObjeto) {
        this.log.debug("Requisição para baixar objeto. Chave do Objeto: {}", chaveObjeto);

        final ResponseBytes<GetObjectResponse> responseBytes = this.service.baixaObjeto(S3TipoInstanciaCliente.TEST_BUCKET, chaveObjeto);

        final Response.ResponseBuilder responseBuilder = Response.ok(responseBytes.asByteArray());
        responseBuilder.header("Content-Disposition", "attachment;filename=" + chaveObjeto);
        responseBuilder.header("Content-Type", responseBytes.response().contentType());

        return responseBuilder.build();
    }

    @DELETE
    @Path("/{chaveObjeto:(?:[^/]+/){0,}[^/]+}")
    public Response deletaObjeto(@PathParam("chaveObjeto") final String chaveObjeto) {
        this.log.debug("Requisição para deletar objeto. Chave do Objeto: {}", chaveObjeto);

        this.service.deletaObjeto(S3TipoInstanciaCliente.TEST_BUCKET, chaveObjeto);

        return Response.noContent().build();
    }

    @POST
    @Path("/sign-url/{chaveObjeto:(?:[^/]+/){0,}[^/]+}")
    public S3URLTempDTO generateTempURL(
            @PathParam("chaveObjeto") final String chaveObjeto) throws MalformedURLException, URISyntaxException {
        this.log.debug("Requisição para gerar url assinada. Chave do Objeto: {}", chaveObjeto);

        return this.service.geraURLAssinadaTemporaria(S3TipoInstanciaCliente.TEST_BUCKET, chaveObjeto);
    }

}

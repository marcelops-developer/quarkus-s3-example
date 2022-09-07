package org.acme.objectstorage.s3;

import org.acme.objectstorage.s3.MultiparteArquivo;
import org.acme.objectstorage.s3.S3InstanciaFactory;
import org.acme.objectstorage.s3.S3ObjectoFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ApplicationScoped
public class S3Service {

    @Inject
    private S3ObjectoFactory objectFactory;

    @Inject
    private S3InstanciaFactory s3InstanciaFactory;

    public void enviaObjeto(
            final S3TipoInstanciaCliente instanciaEnum, final MultiparteArquivo multiparteArquivo
    ) throws IOException {

        final S3InstanciaCliente instancia = this.criaInstancia(instanciaEnum);

        instancia
                .getS3Cliente()
                .putObject(
                        this.objectFactory.constroiObjetoPUT(multiparteArquivo, instancia.getNomeBalde()),
                        RequestBody.fromFile(multiparteArquivo.getArquivo())
                );
    }

    public ResponseBytes<GetObjectResponse> baixaObjeto(final S3TipoInstanciaCliente instanciaEnum, final String chaveObjeto) {
        final S3InstanciaCliente instancia = this.criaInstancia(instanciaEnum);

        return instancia
                .getS3Cliente()
                .getObjectAsBytes(
                        this.objectFactory.constroiObjetoGET(chaveObjeto, instancia.getNomeBalde())
                );
    }

    public void deletaObjeto(final S3TipoInstanciaCliente instanciaEnum, final String chaveObjeto) {
        final S3InstanciaCliente instancia = this.criaInstancia(instanciaEnum);

        instancia
                .getS3Cliente()
                .deleteObject(
                        this.objectFactory.constroiObjetoDELETE(chaveObjeto, instancia.getNomeBalde())
                );
    }

    public S3URLTempDTO geraURLAssinadaTemporaria(
            final S3TipoInstanciaCliente instanciaEnum, final String nomeArquivo
    ) throws URISyntaxException, MalformedURLException {
        
        final S3InstanciaCliente instancia = this.criaInstancia(instanciaEnum);

        final PutObjectPresignRequest putObjectPresignRequest =
                this.objectFactory.constroiURLAssinadaPUT(
                        nomeArquivo, instancia.getNomeBalde(), instancia.getTempoExpiracaoSegundos()
                );
        final GetObjectPresignRequest getObjectPresignRequest =
                this.objectFactory.constroiURLAssinadaGET(
                        nomeArquivo, instancia.getNomeBalde(), instancia.getTempoExpiracaoSegundos()
                );

        final URL putUrl = instancia.getS3PreAssinante()
                .presignPutObject(putObjectPresignRequest)
                .url();

        final URL getUrl = instancia.getS3PreAssinante()
                .presignGetObject(getObjectPresignRequest)
                .url();

        final URL unsignedUrl = this.controiURLNaoAssinada(getUrl);

        return S3URLTempDTO
                .builder()
                .urlAssinadaPUT(putUrl.toString())
                .urlAssinadaGET(getUrl.toString())
                .chaveObjeto(nomeArquivo)
                .urlNaoAssinada(unsignedUrl.toString())
                .build();
    }

    private S3InstanciaCliente criaInstancia(final S3TipoInstanciaCliente instancia) {
        return this.s3InstanciaFactory.criaInstancia(instancia);
    }

    private URL controiURLNaoAssinada(final URL urlAssinadaGET) throws URISyntaxException, MalformedURLException {
        return new URI(
                urlAssinadaGET.toURI().getScheme(),
                urlAssinadaGET.toURI().getAuthority(),
                urlAssinadaGET.toURI().getPath(),
                null,
                null
        ).toURL();
    }

}

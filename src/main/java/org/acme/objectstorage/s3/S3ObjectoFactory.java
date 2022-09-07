package org.acme.objectstorage.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;

import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ApplicationScoped
public class S3ObjectoFactory {

    public PutObjectRequest constroiObjetoPUT(final MultiparteArquivo multiparteArquivo, final String nomeBalde) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(multiparteArquivo.getArquivo());

        final String mimeType = URLConnection.guessContentTypeFromStream(fileInputStream);

        return PutObjectRequest.builder()
                .bucket(nomeBalde)
                .key(multiparteArquivo.getNomeArquivo())
                .contentType(mimeType)
                .build();
    }

    public GetObjectRequest constroiObjetoGET(final String chaveObjeto, final String nomeBalde) {
        return GetObjectRequest.builder()
                .bucket(nomeBalde)
                .key(chaveObjeto)
                .build();
    }

    public DeleteObjectRequest constroiObjetoDELETE(final String chaveObjeto, final String nomeBalde) {
        return DeleteObjectRequest.builder()
                .bucket(nomeBalde)
                .key(chaveObjeto)
                .build();
    }

    public PutObjectPresignRequest constroiURLAssinadaPUT(
            final String chaveObjeto, final String nomeBalde, final long tempoExpiracaoSegundos
    ) {
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .key(chaveObjeto)
                .bucket(nomeBalde)
//                .acl(ObjectCannedACL.PUBLIC_READ_WRITE)
                .build();

        return PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(tempoExpiracaoSegundos))
                .putObjectRequest(putObjectRequest)
                .build();
    }

    public GetObjectPresignRequest constroiURLAssinadaGET(
            final String chaveObjeto, final String nomeBalde, final long tempoExpiracaoSegundos
    ) {
        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .key(chaveObjeto)
                .bucket(nomeBalde)
                .build();

        return GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(tempoExpiracaoSegundos))
                .getObjectRequest(getObjectRequest)
                .build();
    }

}

package org.acme.objectstorage.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class S3InstanciaCliente {
    private S3Client s3Cliente;
    private S3Presigner s3PreAssinante;
    private String nomeBalde;
    private int tempoExpiracaoSegundos;
}

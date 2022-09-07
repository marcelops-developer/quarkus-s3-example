package org.acme.objectstorage.s3;

public interface S3InstanciaFactory {
    S3InstanciaCliente criaInstancia(S3TipoInstanciaCliente s3TipoInstanciaCliente);
}

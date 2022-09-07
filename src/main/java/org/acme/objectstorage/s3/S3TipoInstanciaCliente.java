package org.acme.objectstorage.s3;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum S3TipoInstanciaCliente {
    TEST_BUCKET("test-bucket");

    private final String propertyId;
}

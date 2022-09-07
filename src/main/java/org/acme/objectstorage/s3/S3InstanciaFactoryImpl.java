package org.acme.objectstorage.s3;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.enterprise.inject.spi.CDI;
import org.eclipse.microprofile.config.ConfigProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@ApplicationScoped
public class S3InstanciaFactoryImpl implements S3InstanciaFactory {

    private static final String SUFIXO_INSTANCIA_CLIENTE = "-client";
    private static final String SUFIXO_INSTANCIA_CLIENTE_PRE_ASSINANTE = "-presigner";

    private static final String PREFIXO_PROPRIEDADE_S3 = "s3.";
    private static final String SUFIXO_PROPRIEDADE_NOME_BALDE = ".bucket-name";
    private static final String SUFIXO_PROPRIEDADE_TEMPO_EXPIRACAO = ".expiration-time-in-seconds";

    @Override
    public S3InstanciaCliente criaInstancia(final S3TipoInstanciaCliente s3TipoInstanciaCliente) {
        final Instance<S3Client> s3Client = CDI
                .current()
                .select(S3Client.class, NamedLiteral.of(s3TipoInstanciaCliente.getPropertyId() + SUFIXO_INSTANCIA_CLIENTE));

        final Instance<S3Presigner> s3Presigner = CDI
                .current()
                .select(
                        S3Presigner.class, NamedLiteral.of(s3TipoInstanciaCliente.getPropertyId() + SUFIXO_INSTANCIA_CLIENTE_PRE_ASSINANTE)
                );

        final String nomeBalde = ConfigProvider.getConfig()
                .getValue(
                        PREFIXO_PROPRIEDADE_S3 + s3TipoInstanciaCliente.getPropertyId() + SUFIXO_PROPRIEDADE_NOME_BALDE,
                        String.class
                );

        final Integer tempoExpiracaoSegundos = ConfigProvider.getConfig()
                .getValue(
                        PREFIXO_PROPRIEDADE_S3 + s3TipoInstanciaCliente.getPropertyId() + SUFIXO_PROPRIEDADE_TEMPO_EXPIRACAO,
                        Integer.class
                );

        return S3InstanciaCliente.builder()
                .s3Cliente(s3Client.get())
                .s3PreAssinante(s3Presigner.get())
                .nomeBalde(nomeBalde)
                .tempoExpiracaoSegundos(tempoExpiracaoSegundos)
                .build();
    }

}

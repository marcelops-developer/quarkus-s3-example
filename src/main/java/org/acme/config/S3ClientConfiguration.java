package org.acme.config;

import java.net.URI;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Dependent
public class S3ClientConfiguration {

    @ConfigProperty(name = "s3.region")
    private String region;

    @ConfigProperty(name = "s3.endpoint-override")
    private String endpointOverride;

    @ConfigProperty(name = "s3.test-bucket.credentials.access-key-id")
    private String accessKeyId;

    @ConfigProperty(name = "s3.test-bucket.credentials.secret-access-key")
    private String secretAccessKey;

    @Produces
    @ApplicationScoped
    @Named("test-bucket-client")
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(this.region))
                .endpointOverride(URI.create(this.endpointOverride))
                .credentialsProvider(this.criaCredenciais())
                .serviceConfiguration(this.criaConfiguracao())
                .build();
    }

    @Produces
    @ApplicationScoped
    @Named("test-bucket-presigner")
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(this.region))
                .endpointOverride(URI.create(this.endpointOverride))
                .credentialsProvider(this.criaCredenciais())
                .serviceConfiguration(this.criaConfiguracao())
                .build();
    }

    private AwsCredentialsProvider criaCredenciais() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(this.accessKeyId, this.secretAccessKey)
        );
    }

    private S3Configuration criaConfiguracao() {
        return S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .checksumValidationEnabled(false)
                .build();
    }

}

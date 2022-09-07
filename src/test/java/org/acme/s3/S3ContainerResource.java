package org.acme.s3;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.junit.Rule;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3ContainerResource implements QuarkusTestResourceLifecycleManager {

    DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:0.11.5");

    @Rule
    public LocalStackContainer localstack = new LocalStackContainer(this.localstackImage)
            .withServices(LocalStackContainer.Service.S3);

    @Override
    public Map<String, String> start() {
        this.localstack.start();
        final StaticCredentialsProvider staticCredentials = StaticCredentialsProvider
                .create(AwsBasicCredentials.create(this.localstack.getAccessKey(), this.localstack.getSecretKey()));

        final S3Client s3 = S3Client
                .builder()
                .endpointOverride(this.localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(staticCredentials)
                .region(Region.US_EAST_1)
                .build();

        final String bucketName = "quarkus.s3.bucket-test";
        final String tempoExpiracaoSegundos = "120";

        s3.createBucket(t -> t.bucket(bucketName));

        return Map.ofEntries(
                Map.entry("s3.endpoint-override", this.localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString()),
                Map.entry("s3.region", this.localstack.getRegion()),
                Map.entry("s3.test-bucket.credentials.access-key-id", this.localstack.getAccessKey()),
                Map.entry("s3.test-bucket.credentials.secret-access-key", this.localstack.getSecretKey()),
                Map.entry("s3.test-bucket.bucket-name", bucketName),
                Map.entry("s3.test-bucket.expiration-time-in-seconds", tempoExpiracaoSegundos)
        );
    }

    @Override
    public void stop() {
        this.localstack.stop();
    }

}

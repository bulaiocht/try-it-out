import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.test.LocalStackStarter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@ContextConfiguration(classes = LocalStackStarter.class)
class LocalStackTest {

    @Container
    private final LocalStackContainer localstack = new LocalStackContainer()
            .withServices(LocalStackContainer.Service.S3);

    @Test
    void testPutToS3() {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(localstack.getEndpointConfiguration(LocalStackContainer.Service.S3))
                .withCredentials(localstack.getDefaultCredentialsProvider())
                .build();

        String bucketName = "foo";
        s3.createBucket(bucketName);
        String key = "bar";
        String content = "baz";
        s3.putObject(bucketName, key, content);
        ObjectListing objectListing = s3.listObjects(bucketName);
        var summaries = objectListing.getObjectSummaries();
        Assertions.assertThat(summaries).isNotEmpty();
        S3ObjectSummary objectSummary = summaries.stream().findFirst().orElse(null);
        Assertions.assertThat(objectSummary).isNotNull();
        Assertions.assertThat(objectSummary.getKey()).isEqualTo(key);
    }
}

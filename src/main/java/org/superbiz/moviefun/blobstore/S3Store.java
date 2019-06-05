package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private String photoBucket;
    private AmazonS3Client client;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.client = s3Client;
        this.photoBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.contentType);
        client.putObject(this.photoBucket, blob.name, blob.inputStream, metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            S3Object object = client.getObject(this.photoBucket, name);
            if (object != null) {
                ObjectMetadata meta = object.getObjectMetadata();
                Blob found = new Blob(name, object.getObjectContent(), meta.getContentType());
                System.out.println(meta.getContentLength());
                return Optional.of(found);
            }
            return Optional.empty();
        } catch (AmazonS3Exception e) {
            System.out.println("Not Found");
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {

    }
}

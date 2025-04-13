package com.bwwd.BestWorldWideDeals.Repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import java.io.File;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class S3Dao {

    private static Logger logger = LoggerFactory.getLogger(S3Dao.class);
    private static final String ACCESS_KEY  = "S3_ACCESS_KEY";
    private static final String SECRET_KEY  = "S3_SECRET_KEY";
    private static final String BUCKET_NAME = "bwwd-listing-images";
    public static boolean writeImageToS3(String folder,
                                      String imageName,
                                      String filePath){
        logger.info(String.format("Received request to write file => %s into folder => %s into S3.",imageName, folder));
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                System.getenv(ACCESS_KEY),
                System.getenv(SECRET_KEY)
        );
        String key = String.format("%s/%s",folder,imageName);
        S3AsyncClient client = S3AsyncClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();
        try {
            // Put the object into the bucket
            CompletableFuture<PutObjectResponse> future = client.putObject(objectRequest,
                    AsyncRequestBody.fromFile(new File(filePath))
            );
            future.whenComplete((resp, err) -> {
                try {
                    if (resp != null) {
                        logger.info(String.format("Successfully wrote file => %s into folder => %s into S3.", imageName, folder));
                    } else {
                        logger.error(String.format("Failed to write file => %s into folder => %s into S3.",imageName, folder));
                        logger.error(err.getMessage());
                    }
                } finally {
                    // Only close the client when you are completely done with it
                    client.close();
                }
            });
            future.join();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteImageFromS3(String folder, String imageName){
        logger.info(String.format("Received request to delete files => %s from folder => %s in S3.",
                                        imageName, folder));
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                ACCESS_KEY,
                SECRET_KEY
        );
        S3AsyncClient client = S3AsyncClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        try {
            DeleteObjectRequest deleteOb = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(String.format("%s/%s",folder,imageName))
                    .build();
            client.deleteObject(deleteOb);
            CompletableFuture<DeleteObjectResponse> future = client.deleteObject(deleteOb);
            future.whenComplete((resp, err) -> {
                try {
                    if (resp != null) {
                        logger.info(String.format("Successfully deleted files => %s from folder => %s in S3.", imageName, folder));
                    } else {
                        logger.error(String.format("Failed to delete files => %s from folder => %s in S3.", imageName, folder));
                        logger.error(err.getMessage());
                    }
                } finally {
                    // Only close the client when you are completely done with it
                    client.close();
                }
            });
            future.join();
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }
//    public static void main(String[] args) {
//       //boolean b =  deleteImagesFromS3("test", "image3");
//       boolean b =writeImageToS3("test", "image2", "/home/hossein/Downloads/heart.png");
//         System.out.println(b);
//    }
}

package com.bwwd.BestWorldWideDeals.Repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import java.io.File;
import java.time.Duration;

public class S3Dao {
    private static Logger logger = LoggerFactory.getLogger(S3Dao.class);
    private static final String ACCESS_KEY  = "AKIA5N4IHKZKILFQOVT4";
    private static final String SECRET_KEY  = "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";
    private static final String BUCKET_NAME = "bwwd-listing-images";
    public static boolean writeImageToS3(String folder,
                                      String imageName,
                                      String filePath){
        logger.info(String.format("Received request to write file => %s into folder => %s into S3.",imageName, folder));
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                ACCESS_KEY,
                SECRET_KEY
        );
        String key = String.format("%s/%s",folder,imageName);
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();
            s3Client.putObject(putOb, RequestBody.fromFile(new File(filePath)));
            S3Waiter waiter = s3Client.waiter();
            HeadObjectRequest requestWait = HeadObjectRequest.builder().bucket(BUCKET_NAME).key(key).build();
            WaiterOverrideConfiguration waiterOverrideConfiguration = WaiterOverrideConfiguration.builder()
                    .waitTimeout(Duration.ofSeconds(5L)).build();
            WaiterResponse<HeadObjectResponse> waiterResponse = waiter.waitUntilObjectExists(requestWait,
                                                                            waiterOverrideConfiguration);
            if(waiterResponse.matched().response().isPresent()) {
                logger.info(String.format("Successfully wrote file => %s into folder => %s into S3.", imageName, folder));
                return true;
            }else{
                logger.error(String.format("Failed to write file => %s into folder => %s into S3 in less than 5 " +
                        "seconds.",imageName, folder));
                return false;
            }
        } catch (S3Exception e) {
            logger.error(String.format("Failed to write file => %s into folder => %s into S3.",imageName, folder));
            logger.error(e.getMessage());
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
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        try {
            DeleteObjectRequest deleteOb = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(String.format("%s/%s",folder,imageName))
                    .build();
            s3Client.deleteObject(deleteOb);
            S3Waiter waiter = s3Client.waiter();
            HeadObjectRequest requestWait = HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(String.format("%s/%s",folder,imageName))
                    .build();
            WaiterOverrideConfiguration waiterOverrideConfiguration = WaiterOverrideConfiguration.builder()
                    .waitTimeout(Duration.ofSeconds(5L)).build();
            WaiterResponse<HeadObjectResponse> waiterResponse = waiter.waitUntilObjectNotExists(requestWait,
                    waiterOverrideConfiguration);
            if(!waiterResponse.matched().response().isPresent()) {
                logger.info(String.format("Successfully deleted files => %s from folder => %s in S3.",
                        imageName, folder));
                return true;
            }else{
                logger.error(String.format("Failed to delete file => %s from folder => %s into S3 in less than 5 " +
                        "seconds.",imageName, folder));
                return false;
            }
        } catch (S3Exception e) {
            logger.error(String.format("Failed to delete files => %s from folder => %s in S3.", imageName, folder));
            logger.error(e.getMessage());
            return false;
        }
    }
//    public static void main(String[] args) {
//       //boolean b =  deleteImagesFromS3("test", "image3");
//       boolean b =writeImageToS3("test", "image2", "/home/hossein/Downloads/heart.png");
//         System.out.println(b);
//    }
}

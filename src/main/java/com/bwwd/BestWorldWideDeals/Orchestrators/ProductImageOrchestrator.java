package com.bwwd.BestWorldWideDeals.Orchestrators;

import com.bwwd.BestWorldWideDeals.Repositories.ProductImageRepository;
import com.bwwd.BestWorldWideDeals.Repositories.S3Dao;
import com.bwwd.BestWorldWideDeals.Models.ProductImage;
import com.bwwd.BestWorldWideDeals.Utils.Constants;
import com.bwwd.BestWorldWideDeals.Utils.SystemStorageUtil;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Repository
public class ProductImageOrchestrator {

    private static Logger logger = LoggerFactory.getLogger(ProductImageOrchestrator.class);

    @Autowired
    private ProductImageRepository productImageDao;

    public ResponseEntity<Map<String,Boolean>> saveImages(Long pId, List<MultipartFile> images){
       logger.info(String.format("Received request to save Images for pId: %s",pId));
       Map<String, Boolean> results = new HashMap<>();
       for(MultipartFile multipartFile: images){
           logger.info(String.format("Saving Image -> %s",multipartFile.getOriginalFilename()));
           try {
               String filePath = SystemStorageUtil.save(String.valueOf(pId), multipartFile);
               boolean s3Result = S3Dao.writeImageToS3(String.valueOf(pId), multipartFile.getOriginalFilename(), filePath);
               ProductImage productImage = new ProductImage(pId, multipartFile.getName());
               String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                       Constants.IMAGES_S3_BUCKET_NAME, Region.US_EAST_2,
                                String.format("%s/%s",String.valueOf(pId), multipartFile.getOriginalFilename()));
               productImageDao.save(new ProductImage(pId, url));
               if (s3Result) {
                   results.put(multipartFile.getOriginalFilename(), true);
               }else{
                   logger.error(String.format("FAILED to Save Image -> %s into S3.",multipartFile.getOriginalFilename()));
                   results.put(multipartFile.getOriginalFilename(), false);
               }
           }catch (Exception e){
               logger.error(String.format("FAILED to Save Image -> %s into S3.",multipartFile.getOriginalFilename()));
               e.printStackTrace();
              results.put(multipartFile.getName(), false);
           }
       }
       SystemStorageUtil.deleteAllInDirectory(Constants.SYSTEM_IMAGES_ROOT_PATH.resolve(String.valueOf(pId)));
       return ResponseEntity.status(HttpStatus.OK).body(results);
    }

    public  List<ProductImage> getImages(Long pId){
        return productImageDao.getImagesByPID(pId);
    }

    public ResponseEntity<Map<Number,Boolean>> deleteImages(List<Long> imageIds){
        logger.info(String.format("Received request to delete Images for following images :  %s",
                imageIds.stream().map(d->String.valueOf(d)).collect(Collectors.joining(","))));
        Map<Number, Boolean> results = new HashMap<>();
        List<ProductImage> images = productImageDao.getImagesByIds(imageIds);
        for(ProductImage p: images){
            int l = p.imageLink.split("/").length;
            String object = p.imageLink.split("/")[l-1];
            String folder = p.imageLink.split("/")[l-2];
            logger.info(String.format("Deleting Image -> %s",object));
            try {
                productImageDao.deleteById(p.id);
                Boolean s3Result = S3Dao.deleteImageFromS3(folder, object);
                if (s3Result) {
                    results.put(p.id, true);
                }else{
                    logger.error(String.format("FAILED to Delete Image -> %s from S3.",object));
                    results.put(p.id, false);
                }
            }catch (Exception e){
                logger.error(String.format("FAILED to Delete Image -> %s.",object));
                e.printStackTrace();
                results.put(p.id, false);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

}

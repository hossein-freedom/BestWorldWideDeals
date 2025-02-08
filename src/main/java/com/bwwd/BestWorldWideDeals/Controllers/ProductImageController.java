package com.bwwd.BestWorldWideDeals.Controllers;

import com.bwwd.BestWorldWideDeals.Models.IdsApiInput;
import com.bwwd.BestWorldWideDeals.Models.ProductImage;
import com.bwwd.BestWorldWideDeals.Orchestrators.ProductImageOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class ProductImageController {

    @Autowired
    private ProductImageOrchestrator productImageRepository;

    @RequestMapping(
            value ="/api/uploadimages/{pid}",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<Map<String, Boolean>> uploadImages(
            @PathVariable("pid") Long pId,
            @RequestPart List<MultipartFile> images) {
        return productImageRepository.saveImages(pId, images);
    }

    @RequestMapping(
            value ="/api/deleteimages",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    ResponseEntity<Map<Number, Boolean>> deleteImages(@RequestBody IdsApiInput ids) {
        return productImageRepository.deleteImages(ids.ids);
    }


    @RequestMapping(
            value ="/api/getimagesforproduct/{pid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    List<ProductImage> getImagesForProduct(
            @PathVariable("pid") Long pId) {
        return productImageRepository.getImages(pId);
    }

}

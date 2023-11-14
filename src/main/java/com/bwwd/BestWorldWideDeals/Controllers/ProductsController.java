package com.bwwd.BestWorldWideDeals.Controllers;

import com.bwwd.BestWorldWideDeals.Models.Filter;
import com.bwwd.BestWorldWideDeals.Models.Product;
import com.bwwd.BestWorldWideDeals.Models.SearchCriteria;
import com.bwwd.BestWorldWideDeals.Orchestrators.ProductOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class ProductsController {

    @Autowired
    private ProductOrchestrator productRepository;

    @RequestMapping(
            value ="/allproducts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    List<Product> getAllProducts() {    return productRepository.getAllProducts();   }

    @RequestMapping(
            value ="/saveproducts",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void saveProducts(@RequestBody List<Product> products) { productRepository.saveAllProducts(products); }

    @RequestMapping(
            value ="/saveproduct",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    ResponseEntity<Map<String,Number>> saveProduct(@RequestBody Product product) {
        Number pId = productRepository.saveProduct(product);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("productId", pId));
    }

    @RequestMapping(
            value ="/deleteproduct/{id}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void deleteProduct(@PathVariable("id") Long id) {
        productRepository.deleteProduct(id);
    }

    @RequestMapping(
            value ="/deleteproducts",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void deleteProducts(@RequestBody List<Long> ids) {
        productRepository.deleteProducts(ids);
    }


    @RequestMapping(
            value ="/updateproduct",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void updateProduct(@RequestBody Product product) {
        productRepository.updateProduct(product);
    }

    @RequestMapping(
            value ="/getproduct/{pid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    ResponseEntity<Product> getProduct(@PathVariable("pid") Long pId) {
        Product product = productRepository.getProductById(pId);
        if(Objects.nonNull(product)){
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @RequestMapping(
            value ="/getproductsbyfilter",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    List<Product> getProductsByFilter(@RequestBody SearchCriteria searchCriteria) {
        return productRepository.getProductsByFilter(searchCriteria);
    }
    @RequestMapping(
            value ="/getproductscountbyfilter",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    Long getProductsCountByFilter(@RequestBody List<Filter> filters) {
        return productRepository.getProductsCountByFilter(filters);
    }
}

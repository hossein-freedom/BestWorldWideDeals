package com.bwwd.BestWorldWideDeals.Controllers;

import com.bwwd.BestWorldWideDeals.Models.*;
import com.bwwd.BestWorldWideDeals.Orchestrators.ProductOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
public class ProductsController {

    @Autowired
    private ProductOrchestrator productOrchestrator;

    @RequestMapping(
            value ="/api/allproducts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    List<Product> getAllProducts() {  return productOrchestrator.getAllProducts();   }

    @RequestMapping(
            value ="/api/saveproducts",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void saveProducts(@RequestBody List<Product> products) { productOrchestrator.saveAllProducts(products); }

    @RequestMapping(
            value ="/api/saveproduct",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    ResponseEntity<Map<String,Number>> saveProduct(@RequestBody Product product) {
        Number pId = productOrchestrator.saveProduct(product);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("productId", pId));
    }

    @RequestMapping(
            value ="/api/deleteproduct/{id}",
            method = RequestMethod.DELETE
    )

    @ResponseBody
    ResponseEntity<Map<String,String>> deleteProduct(@PathVariable("id") Long id) {
        productOrchestrator.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("result","Product was succeessfully deleted."));
    }

    @RequestMapping(
            value ="/api/deleteproducts",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void deleteProducts(@RequestBody List<Long> ids) {
        productOrchestrator.deleteProducts(ids);
    }


    @RequestMapping(
            value ="/api/updateproduct",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void updateProduct(@RequestBody Product product) {
        productOrchestrator.updateProduct(product);
    }

    @RequestMapping(
            value ="/api/getproduct/{pid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    ResponseEntity<Product> getProduct(@PathVariable("pid") Long pId) {
        Product product = productOrchestrator.getProductById(pId);
        if(Objects.nonNull(product)){
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @RequestMapping(
            value ="/api/getproducts",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    ProductSearchResult getProducts(@RequestBody SearchCriteria searchCriteria) {
        return productOrchestrator.getProductsByFilter(searchCriteria);
    }
    @RequestMapping(
            value ="/api/getproductscountbyfilter",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    Long getProductsCountByFilter(@RequestBody SearchCriteria searchCriteria) {
        return productOrchestrator.getProductsCountByFilter(searchCriteria);
    }

    @RequestMapping(
            value ="/api/getproductcategories",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    List<String> getProductCategories() {
        return productOrchestrator.getProductCategories();
    }

    @RequestMapping(
            value ="/api/getproductsubcategories/{category}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    List<String> getProductSubCategories(@PathVariable("category") String category) {
        return productOrchestrator.getProductSubCategories(category);
    }

    @RequestMapping(
            value ="/api/getallproductsubcategories",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Map<String, List<String>> getAllProductSubCategories() {
        return productOrchestrator.getAllProductSubCategories();
    }

    @RequestMapping(
            value ="/api/getallproductsources",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    List<String> getAllProductSources() {
        return productOrchestrator.getAllProductSources();
    }

    @RequestMapping(
            value ="/api/login/{username}/{password}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Map<String, String> login(@PathVariable("username") String userName,
                              @PathVariable("password") String password) {
        return productOrchestrator.login(userName, password);
    }

    @RequestMapping(
            value ="/api/getallproductsourcesbyfilter",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    List<Source> getAllProductSourceByFilter(@RequestBody SearchCriteria searchCriteria){
        return productOrchestrator.getSourcesByFilter(searchCriteria);
    }

    @RequestMapping(
            value ="/api/getcategorysubcategorybyFilter",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    Map<String, Set<String>> getCategorySubcategoryByFilter(@RequestBody SearchCriteria searchCriteria){
        return productOrchestrator.getCategorySubcategoryByFilter(searchCriteria);
    }

}

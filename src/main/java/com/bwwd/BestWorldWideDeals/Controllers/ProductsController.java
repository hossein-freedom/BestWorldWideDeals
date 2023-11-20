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
    private ProductOrchestrator productOrchestrator;

    @RequestMapping(
            value ="/allproducts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    List<Product> getAllProducts() {    return productOrchestrator.getAllProducts();   }

    @RequestMapping(
            value ="/saveproducts",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void saveProducts(@RequestBody List<Product> products) { productOrchestrator.saveAllProducts(products); }

    @RequestMapping(
            value ="/saveproduct",
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
            value ="/deleteproduct/{id}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void deleteProduct(@PathVariable("id") Long id) {
        productOrchestrator.deleteProduct(id);
    }

    @RequestMapping(
            value ="/deleteproducts",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void deleteProducts(@RequestBody List<Long> ids) {
        productOrchestrator.deleteProducts(ids);
    }


    @RequestMapping(
            value ="/updateproduct",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    void updateProduct(@RequestBody Product product) {
        productOrchestrator.updateProduct(product);
    }

    @RequestMapping(
            value ="/getproduct/{pid}",
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
            value ="/getproductsbyfilter",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    List<Product> getProductsByFilter(@RequestBody SearchCriteria searchCriteria) {
        return productOrchestrator.getProductsByFilter(searchCriteria);
    }
    @RequestMapping(
            value ="/getproductscountbyfilter",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    Long getProductsCountByFilter(@RequestBody List<Filter> filters) {
        return productOrchestrator.getProductsCountByFilter(filters);
    }

    @RequestMapping(
            value ="/getproductcategories",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    List<String> getProductCategories() {
        return productOrchestrator.getProductCategories();
    }

    @RequestMapping(
            value ="/getproductsubcategories/{category}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    @ResponseBody
    List<String> getProductSubCategories(@PathVariable("category") String category) {
        return productOrchestrator.getProductSubCategories(category);
    }
}

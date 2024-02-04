package com.bwwd.BestWorldWideDeals.Orchestrators;

import com.bwwd.BestWorldWideDeals.Models.*;
import com.bwwd.BestWorldWideDeals.Repositories.ProductRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.*;
import java.util.stream.Collectors;

// This interface will be automatically populated by JPA

@NoArgsConstructor
@Repository
@Slf4j
public class ProductOrchestrator {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageOrchestrator productImageOrchestrator;


    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public void saveAllProducts(List<Product> products){
        productRepository.saveAll(products);
    }

    public Number saveProduct(Product product){
        Product savedProduct = productRepository.save(product);
        return savedProduct.id;
    }

    public void deleteProduct(Long id) {
        List<Long> imageIds = productImageOrchestrator.getImages(id).stream().map( p -> p.id)
                        .collect(Collectors.toList());
        productImageOrchestrator.deleteImages(imageIds);
        productRepository.deleteById(id);
    }

    public void deleteProducts(List<Long> ids){
        ids.forEach(id -> deleteProduct(id));
    }

    public void updateProduct(Product product){
        productRepository.updateProduct(product.id,
                product.source, product.bannerCode, product.isActive, product.category, product.subCategory,
                product.sellerWebsite, product.affiliateLink, product.email, product.price, product.isOnSale,
                product.salePrice, product.title, product.description, product.endDate);
    }

    public Product getProductById(Long pId){
        Optional<Product> product = productRepository.findById(pId);
        return product.isPresent() ? product.get() : null;

    }

    public ProductSearchResult getProductsByFilter(SearchCriteria searchCriteria){
        return productRepository.findAllProducts(searchCriteria);
    }

    public Long getProductsCountByFilter(SearchCriteria searchCriteria){
        return productRepository.findAllProductsCount(searchCriteria);
    }

    public List<String> getProductCategories(){
        return productRepository.selectProductCategories();
    }

    public List<String> getProductSubCategories(String category){
        return productRepository.selectProductSubCategories(category);
    }

    public List<String> getAllProductSources(){
        return productRepository.getAllProductSources();
    }

    public Map<String, List<String>> getAllProductSubCategories(){
        List<Object[]> result = productRepository.selectAllProductSubCategories();
        Map<String, List<String>> output = new HashMap<>();
        for(Object[] entry: result){
            if(output.containsKey((String)entry[0])){
                output.get((String)entry[0]).add((String)entry[1]);
            }else{
                output.put((String)entry[0], new ArrayList<>());
                output.get((String)entry[0]).add((String)entry[1]);
            }
        }
        return output;
    }

    public Map<String, List<String>> getCategorySubcategoryByFilter(SearchCriteria searchCriteria) {
        return productRepository.getCategorySubcategoryByFilter(searchCriteria);
    }

    public Map<String, String> login(String userName, String password) {
        List<String> result = productRepository.login(userName, password);
        if(result.size() > 0){
            return Map.of("Status", "Successful");
        }else{
            return Map.of("Status", "Failed");
        }
    }

    public List<Source> getSourcesByFilter(SearchCriteria searchCriteria){
        return productRepository.getSourcesByFilter(searchCriteria);
    }
}

package com.bwwd.BestWorldWideDeals.Orchestrators;

import com.bwwd.BestWorldWideDeals.Models.Filter;
import com.bwwd.BestWorldWideDeals.Models.SearchCriteria;
import com.bwwd.BestWorldWideDeals.Repositories.ProductRepository;
import com.bwwd.BestWorldWideDeals.Models.Product;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// This interface will be automatically populated by JPA

@NoArgsConstructor
@Repository
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

    public void deleteProduct(Long id){
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

    public List<Product> getProductsByFilter(SearchCriteria searchCriteria){
        return productRepository.findAllProducts(searchCriteria);
    }

    public Long getProductsCountByFilter(List<Filter> filters){
        return productRepository.findAllProductsCount(filters);
    }

    public List<String> getProductCategories(){
        return productRepository.selectProductCategories();
    }

    public List<String> getProductSubCategories(String category){
        return productRepository.selectProductSubCategories(category);
    }

}

package com.bwwd.BestWorldWideDeals.Repositories;

import com.bwwd.BestWorldWideDeals.Models.Filter;
import com.bwwd.BestWorldWideDeals.Models.Product;
import com.bwwd.BestWorldWideDeals.Models.SearchCriteria;
import com.bwwd.BestWorldWideDeals.Models.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

// This interface will be automatically populated by JPA
@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product,Long >{

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product  SET source = :source,bannercode = :bannercode," +
            "isactive = :isactive,category = :category,subcategory = :subcategory,sellerwebsite = :sellerwebsite," +
            "affiliatelink = :affiliatelink,email = :email,price = :price,isonsale = :isonsale,saleprice = :saleprice," +
            "title = :title,description = :description,enddate=:enddate  WHERE p_id = :pId")
    int updateProduct(@Param("pId") Number pId, @Param("source") Source source, @Param("bannercode") String bannercode,
                      @Param("isactive") Boolean isactive, @Param("category") String category,
                      @Param("subcategory") String subCategory, @Param("sellerwebsite") String sellerwebsite,
                      @Param("affiliatelink") String affiliatelink, @Param("email") String email,
                      @Param("price") Number price, @Param("isonsale") Boolean isonsale,
                      @Param("saleprice") Number saleprice, @Param("title") String title,
                      @Param("description") String description, @Param("enddate") Number enddate);

    @Modifying(clearAutomatically = true)
    @Query(value = "Select Distinct category From product_details", nativeQuery = true)
    List<String> selectProductCategories();

    @Modifying(clearAutomatically = true)
    @Query(value = "Select Distinct subcategory From product_details WHERE category = :category", nativeQuery = true)
    List<String> selectProductSubCategories(@Param("category") String category);

    List<Product> findAllProducts(SearchCriteria searchCriteria);

    Long findAllProductsCount(List<Filter> filters);

    @Modifying(clearAutomatically = true)
    @Query(value = "Select Distinct category, subcategory From product_details", nativeQuery = true)
    List<Object[]> selectAllProductSubCategories();

    @Modifying(clearAutomatically = true)
    @Query(value = "Select Distinct source From product_details", nativeQuery = true)
    List<String> getAllProductSources();
}

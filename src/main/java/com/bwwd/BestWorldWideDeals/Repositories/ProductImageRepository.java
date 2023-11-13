package com.bwwd.BestWorldWideDeals.Repositories;

import com.bwwd.BestWorldWideDeals.Models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query(value="SELECT id,p_id,imageLink FROM product_images WHERE p_id = :pId",nativeQuery = true)
    public List<ProductImage> getImagesByPID(@Param("pId") Long pId);

    @Query(value="SELECT id,p_id,imageLink FROM product_images WHERE id in (:ids)",nativeQuery = true)
    public List<ProductImage> getImagesByIds(@Param("ids") List<Long> ids);

}

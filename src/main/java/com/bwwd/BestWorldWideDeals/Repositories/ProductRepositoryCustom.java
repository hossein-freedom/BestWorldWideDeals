package com.bwwd.BestWorldWideDeals.Repositories;

import com.bwwd.BestWorldWideDeals.Models.Product;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface ProductRepositoryCustom{
    List<Product> findAllProducts(String title);

}
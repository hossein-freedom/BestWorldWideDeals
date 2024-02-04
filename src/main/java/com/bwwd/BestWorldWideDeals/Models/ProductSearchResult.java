package com.bwwd.BestWorldWideDeals.Models;

import java.util.List;

public class ProductSearchResult {

    public ProductSearchResult() {
    }

    public List<Product> products;

    public Long totalResultNumber;

    public Integer curPageNumber;

    public Integer curPageSize;

    public List<Product> getProducts() {
        return products;
    }

    public Long getTotalResultNumber() {
        return totalResultNumber;
    }

    public Integer getCurPageNumber() {
        return curPageNumber;
    }

    public Integer getCurPageSize() {
        return curPageSize;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setTotalResultNumber(Long totalResultNumber) {
        this.totalResultNumber = totalResultNumber;
    }

    public void setCurPageNumber(Integer curPageNumber) {
        this.curPageNumber = curPageNumber;
    }

    public void setCurPageSize(Integer curPageSize) {
        this.curPageSize = curPageSize;
    }
}

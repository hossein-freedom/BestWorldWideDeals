package com.bwwd.BestWorldWideDeals.Models;

public class Page {
    public Integer pageSize;

    public Integer pageNumber;

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }
}

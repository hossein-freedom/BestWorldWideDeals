package com.bwwd.BestWorldWideDeals.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Component
@Table(name = "product_details", schema = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "p_id", updatable = false, nullable = false)
    public Long id;

    @Column(name = "source")
    public Source source;

    @Column(name = "isactive")
    public Boolean isActive;

    @Column(name = "bannercode")
    public String bannerCode;

    @Column(name = "category")
    public String category;

    @Column(name = "subcategory")
    public String subCategory;

    @Column(name = "sellerwebsite")
    public String sellerWebsite;

    @Column(name = "affiliatelink")
    public String affiliateLink;

    @Column(name = "email")
    public String email;

    @Column(name = "price")
    public Double price;

    @Column(name = "isonsale")
    public Boolean isonsale;

    @Column(name = "saleprice")
    public Double saleprice;

    @Column(name = "title")
    public String title;

    @Column(name = "description")
    public String description;

    @Column(name = "enddate")
    public Long endDate;

    public Product(Long id, Source source, Boolean isActive, String bannerCode, String category, String subCategory,
                   String sellerWebsite, String affiliateLink, String email, Double price, Boolean isonsale,
                   Double saleprice, String title, String description, Long endDate) {
        this.id = id;
        this.source = source;
        this.isActive = isActive;
        this.bannerCode = bannerCode;
        this.category = category;
        this.subCategory = subCategory;
        this.sellerWebsite = sellerWebsite;
        this.affiliateLink = affiliateLink;
        this.email = email;
        this.price = price;
        this.isonsale = isonsale;
        this.saleprice = saleprice;
        this.title = title;
        this.description = description;
        this.endDate = endDate;
    }
}

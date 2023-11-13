package com.bwwd.BestWorldWideDeals.Models;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Component
@Table(name = "product_images", schema = "products")
@Getter
@Setter
@Data
@NoArgsConstructor
public class ProductImage
{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;

    @Column(name = "p_id")
    public Long  p_id;

    @Column(name = "imagelink")
    public String  imageLink;

    public ProductImage(Long id, Long p_id, String imageLink) {
        this.id = id;
        this.p_id = p_id;
        this.imageLink = imageLink;
    }

    public ProductImage( Long p_id, String imageLink) {
        this.id = null;
        this.p_id = p_id;
        this.imageLink = imageLink;
    }
}

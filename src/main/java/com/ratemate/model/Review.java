```java
package com.ratemate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reviewerName;
    private int rating;

    @Column(length = 1500)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public Review() {}

    public Review(String reviewerName, int rating, String comment, Product product) {
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.product = product;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getFormattedDate() {
        return createdAt.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        );
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
```

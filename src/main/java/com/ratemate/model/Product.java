package com.ratemate.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private double price;
    @Column(length = 1000) private String description;
    @Column(length = 1000) private String imageUrl;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Product() {}
    public Product(String name, String category, double price, String description, String imageUrl) {
        this.name = name; this.category = category; this.price = price;
        this.description = description; this.imageUrl = imageUrl;
    }
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
    public int getReviewCount() { return reviews == null ? 0 : reviews.size(); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public List<Review> getReviews() { return reviews; }
}

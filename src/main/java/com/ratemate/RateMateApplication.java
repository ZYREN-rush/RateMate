package com.ratemate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.ratemate.model.Product;
import com.ratemate.repository.ProductRepository;

@SpringBootApplication
public class RateMateApplication {
    public static void main(String[] args) {
        SpringApplication.run(RateMateApplication.class, args);
    }

    @Bean
    CommandLineRunner seedProducts(ProductRepository products) {
        return args -> {
            if (products.count() == 0) {
                products.save(new Product("SoundCore Wireless Headphones", "Electronics", 2499.00,
                    "Immersive sound, comfortable ear cushions and long battery life.",
                    "https://images.unsplash.com/photo-150577ytu?auto=format&fit=crop&w=900&q=85"));
                products.save(new Product("Everyday Smart Watch", "Electronics", 3999.00,
                    "Track your activity, notifications and daily goals in one place.",
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=85"));
                products.save(new Product("Minimal Everyday Backpack", "Lifestyle", 1799.00,
                    "A lightweight, practical backpack for college and everyday travel.",
                    "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=85"));
                products.save(new Product("Insulated Travel Bottle", "Lifestyle", 699.00,
                    "A reusable bottle designed to keep your drink at the right temperature.",
                    "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=85"));
            }
        };
    }
}

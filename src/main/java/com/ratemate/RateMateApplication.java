
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

            addProductIfMissing(products, "SoundCore Wireless Headphones",
                "Electronics", 2499.00,
                "Immersive sound, comfortable ear cushions and long battery life.",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=85");

            addProductIfMissing(products, "Everyday Smart Watch",
                "Electronics", 3999.00,
                "Track your activity, notifications and daily goals in one place.",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=85");

            addProductIfMissing(products, "Minimal Everyday Backpack",
                "Lifestyle", 1799.00,
                "A lightweight, practical backpack for college and everyday travel.",
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=85");

            addProductIfMissing(products, "Insulated Travel Bottle",
                "Lifestyle", 699.00,
                "A reusable bottle designed to keep your drink at the right temperature.",
                "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=85");

            addProductIfMissing(products, "Wireless Earbuds",
                "Electronics", 1499.00,
                "Compact wireless earbuds with a charging case for music and calls.",
                "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?auto=format&fit=crop&w=900&q=85");

            addProductIfMissing(products, "Portable Bluetooth Speaker",
                "Electronics", 1999.00,
                "A compact portable speaker for music at home and on the go.",
                "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=85");

            addProductIfMissing(products, "Everyday Sneakers",
                "Lifestyle", 2299.00,
                "Versatile everyday sneakers designed for comfort and casual outfits.",
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=85");

            addProductIfMissing(products, "Everyday Wallet",
                "Lifestyle", 799.00,
                "A minimalist wallet for carrying your everyday essentials.",
                "https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=900&q=85");
        };
    }

    private void addProductIfMissing(
            ProductRepository products,
            String name,
            String category,
            double price,
            String description,
            String imageUrl) {

        boolean exists = products.findAll().stream()
                .anyMatch(product -> product.getName().equalsIgnoreCase(name));

        if (!exists) {
            products.save(new Product(
                    name, category, price, description, imageUrl));
        }
    }
}

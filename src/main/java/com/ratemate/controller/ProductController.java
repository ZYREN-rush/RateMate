
package com.ratemate.controller;

import com.ratemate.model.Product;
import com.ratemate.model.Review;
import com.ratemate.repository.ProductRepository;
import com.ratemate.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private final ProductRepository products;
    private final ReviewRepository reviews;

    public ProductController(ProductRepository products, ReviewRepository reviews) {
        this.products = products;
        this.reviews = reviews;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String category,
                       Model model) {
        var all = products.findAll();

        if (q != null && !q.isBlank()) {
            String term = q.toLowerCase();
            all = all.stream()
                    .filter(p -> p.getName().toLowerCase().contains(term)
                            || p.getCategory().toLowerCase().contains(term))
                    .toList();
        }

        if (category != null && !category.isBlank()
                && !category.equalsIgnoreCase("All")) {
            all = all.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        model.addAttribute("products", all);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("category", category == null ? "All" : category);
        model.addAttribute("totalProducts", products.count());
        model.addAttribute("totalReviews", reviews.count());

        return "index";
    }

    @GetMapping("/products/{id}")
    public String product(@PathVariable Long id, Model model,
                          RedirectAttributes redirect) {
        var product = products.findById(id);

        if (product.isEmpty()) {
            redirect.addFlashAttribute("error", "That product could not be found.");
            return "redirect:/";
        }

        model.addAttribute("product", product.get());
        model.addAttribute("review", new Review());
        return "product";
    }

    @PostMapping("/products/{id}/reviews")
    public String addReview(@PathVariable Long id,
                            @RequestParam String reviewerName,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            RedirectAttributes redirect) {
        var product = products.findById(id);

        if (product.isEmpty()) {
            return "redirect:/";
        }

        if (reviewerName == null || reviewerName.isBlank()
                || reviewerName.length() > 60
                || rating < 1 || rating > 5
                || comment == null || comment.isBlank()
                || comment.length() > 1500) {
            redirect.addFlashAttribute("error",
                    "Please enter a name, choose 1–5 stars, and write a review (max 1500 characters).");
            return "redirect:/products/" + id;
        }

        reviews.save(new Review(
                reviewerName.trim(),
                rating,
                comment.trim(),
                product.get()
        ));

        redirect.addFlashAttribute("success",
                "Thanks! Your review has been submitted.");

        return "redirect:/products/" + id;
    }

    @PostMapping("/products/{productId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long productId,
                               @PathVariable Long reviewId,
                               @RequestParam String reviewerName,
                               RedirectAttributes redirect) {
        var review = reviews.findById(reviewId);

        if (review.isPresent()
                && review.get().getProduct().getId().equals(productId)
                && review.get().getReviewerName().equals(reviewerName.trim())) {
            reviews.delete(review.get());
            redirect.addFlashAttribute("success", "Your review has been deleted.");
        } else {
            redirect.addFlashAttribute("error",
                    "Review could not be deleted. Check the reviewer name.");
        }

        return "redirect:/products/" + productId;
    }
}

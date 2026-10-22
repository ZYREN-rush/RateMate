java
package com.ratemate.controller;

import com.ratemate.model.Product;
import com.ratemate.model.Review;
import com.ratemate.repository.ProductRepository;
import com.ratemate.repository.ReviewRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Controller
public class ProductController {

    private final ProductRepository products;
    private final ReviewRepository reviews;

    public ProductController(ProductRepository products,
                             ReviewRepository reviews) {
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
                    .filter(p -> p.getCategory()
                            .equalsIgnoreCase(category))
                    .toList();
        }

        model.addAttribute("products", all);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("category",
                category == null ? "All" : category);
        model.addAttribute("totalProducts", products.count());
        model.addAttribute("totalReviews", reviews.count());

        return "index";
    }

    @GetMapping("/products/{id}")
    public String product(@PathVariable Long id,
                          Model model,
                          HttpServletRequest request,
                          RedirectAttributes redirect) {

        var productResult = products.findById(id);

        if (productResult.isEmpty()) {
            redirect.addFlashAttribute(
                    "error", "That product could not be found.");
            return "redirect:/";
        }

        Product product = productResult.get();
        Set<Long> manageableReviewIds = new HashSet<>();

        for (Review review : product.getReviews()) {
            String token = getReviewToken(request, review.getId());

            if (token != null && review.getEditToken() != null
                    && tokensMatch(token, review.getEditToken())) {
                manageableReviewIds.add(review.getId());
            }
        }

        model.addAttribute("product", product);
        model.addAttribute("review", new Review());
        model.addAttribute("manageableReviewIds", manageableReviewIds);

        return "product";
    }

    @PostMapping("/products/{id}/reviews")
    public String addReview(@PathVariable Long id,
                            @RequestParam String reviewerName,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            HttpServletRequest request,
                            HttpServletResponse response,
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
                    "Enter a name, choose 1–5 stars, and write a review (max 1500 characters).");

            return "redirect:/products/" + id;
        }

        Review review = new Review(
                reviewerName.trim(),
                rating,
                comment.trim(),
                product.get()
        );

        String token = UUID.randomUUID().toString();
        review.setEditToken(token);

        reviews.save(review);

        setReviewCookie(response, request, id, review.getId(), token);

        redirect.addFlashAttribute(
                "success", "Thanks! Your review has been submitted.");

        return "redirect:/products/" + id;
    }

    @PostMapping("/products/{productId}/reviews/{reviewId}/edit")
    public String editReview(@PathVariable Long productId,
                             @PathVariable Long reviewId,
                             @RequestParam String reviewerName,
                             @RequestParam int rating,
                             @RequestParam String comment,
                             HttpServletRequest request,
                             RedirectAttributes redirect) {

        var reviewResult = reviews.findById(reviewId);

        if (reviewResult.isEmpty()
                || !reviewResult.get().getProduct().getId()
                        .equals(productId)) {
            redirect.addFlashAttribute("error",
                    "Review not found.");
            return "redirect:/products/" + productId;
        }

        Review review = reviewResult.get();
        String token = getReviewToken(request, reviewId);

        if (!isAuthorized(review, token)) {
            redirect.addFlashAttribute("error",
                    "You cannot edit this review from this browser.");
            return "redirect:/products/" + productId;
        }

        if (reviewerName == null || reviewerName.isBlank()
                || reviewerName.length() > 60
                || rating < 1 || rating > 5
                || comment == null || comment.isBlank()
                || comment.length() > 1500) {

            redirect.addFlashAttribute("error",
                    "Check your name, rating, and review text.");
            return "redirect:/products/" + productId;
        }

        review.setReviewerName(reviewerName.trim());
        review.setRating(rating);
        review.setComment(comment.trim());

        reviews.save(review);

        redirect.addFlashAttribute(
                "success", "Your review has been updated.");

        return "redirect:/products/" + productId;
    }

    @PostMapping("/products/{productId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long productId,
                               @PathVariable Long reviewId,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               RedirectAttributes redirect) {

        var reviewResult = reviews.findById(reviewId);

        if (reviewResult.isEmpty()
                || !reviewResult.get().getProduct().getId()
                        .equals(productId)) {
            redirect.addFlashAttribute("error",
                    "Review not found.");
            return "redirect:/products/" + productId;
        }

        Review review = reviewResult.get();
        String token = getReviewToken(request, reviewId);

        if (!isAuthorized(review, token)) {
            redirect.addFlashAttribute("error",
                    "You cannot delete this review from this browser.");
            return "redirect:/products/" + productId;
        }

        reviews.delete(review);

        expireReviewCookie(response, request, productId, reviewId);

        redirect.addFlashAttribute(
                "success", "Your review has been deleted.");

        return "redirect:/products/" + productId;
    }

    private boolean isAuthorized(Review review, String token) {
        return token != null
                && review.getEditToken() != null
                && tokensMatch(token, review.getEditToken());
    }

    private boolean tokensMatch(String first, String second) {
        return MessageDigest.isEqual(
                first.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                second.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    private String cookieName(Long reviewId) {
        return "review_token_" + reviewId;
    }

    private String getReviewToken(HttpServletRequest request,
                                  Long reviewId) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        String expectedName = cookieName(reviewId);

        for (Cookie cookie : cookies) {
            if (expectedName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private void setReviewCookie(HttpServletResponse response,
                                 HttpServletRequest request,
                                 Long productId,
                                 Long reviewId,
                                 String token) {

        String header = cookieName(reviewId) + "=" + token
                + "; Path=/products/" + productId
                + "; Max-Age=31536000"
                + "; HttpOnly; SameSite=Lax";

        if (isHttps(request)) {
            header += "; Secure";
        }

        response.addHeader("Set-Cookie", header);
    }

    private void expireReviewCookie(HttpServletResponse response,
                                    HttpServletRequest request,
                                    Long productId,
                                    Long reviewId) {

        String header = cookieName(reviewId) + "="
                + "; Path=/products/" + productId
                + "; Max-Age=0"
                + "; HttpOnly; SameSite=Lax";

        if (isHttps(request)) {
            header += "; Secure";
        }

        response.addHeader("Set-Cookie", header);
    }

    private boolean isHttps(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");

        return request.isSecure()
                || (forwardedProto != null
                && forwardedProto.equalsIgnoreCase("https"));
    }
}


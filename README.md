# RateMate — Product Rating & Review System

## Requirements
- Java 17 or newer
- IntelliJ IDEA (or another Java IDE)
- Internet connection for Maven dependencies and remote product images

## Run in IntelliJ
1. Extract the ZIP.
2. IntelliJ: File → Open → select the extracted `RateMate` folder (the folder containing `pom.xml`).
3. Trust/open the project and wait for Maven to finish downloading dependencies.
4. Open `src/main/java/com/ratemate/RateMateApplication.java`.
5. Click the green Run arrow next to `main`.
6. Wait until the console says the application has started on port 8080.
7. Open http://localhost:8080 in your browser.

## What works
- Product catalogue with seeded sample products
- Search by product name/category
- Category filters
- Product details
- 1–5 star ratings
- Review submission and review list
- Average rating and review count calculated from saved reviews
- H2 file database at `./data/ratematedb`, so reviews persist between restarts

## Tech stack
Java 17, Spring Boot, Spring MVC, Thymeleaf, Spring Data JPA, H2 Database, HTML, CSS.

## Important notes
- This starter uses H2 rather than MySQL to minimize setup risk for a next-day presentation. You can explain it as a relational database-backed prototype; do not claim MySQL is used.
- Product images use external URLs and need internet access.
- Sample products are inserted only when the database is empty.
- This is a classroom prototype, not a production-ready public deployment. It has no login or verified-purchase checks.

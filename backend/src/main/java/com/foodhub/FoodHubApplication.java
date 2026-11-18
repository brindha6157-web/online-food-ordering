package com.foodhub;

import com.foodhub.entity.Category;
import com.foodhub.entity.MenuItem;
import com.foodhub.entity.User;
import com.foodhub.repository.CategoryRepository;
import com.foodhub.repository.MenuRepository;
import com.foodhub.repository.UserRepository;
import com.foodhub.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

/**
 * FoodHubApplication
 * Spring Boot Main Application Class.
 * Demonstrates: Spring Boot Autoconfiguration, CommandLineRunner initialisation.
 */
@SpringBootApplication
public class FoodHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodHubApplication.class, args);
        System.out.println("==========================================================");
        System.out.println(" FoodHub Backend Application Started Successfully!       ");
        System.out.println(" Server: http://localhost:8080                            ");
        System.out.println(" Customer Dashboard: http://localhost:8080/dashboard.html ");
        System.out.println(" Admin Dashboard:    http://localhost:8080/admin.html     ");
        System.out.println("==========================================================");
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                          CategoryRepository categoryRepository,
                                          MenuRepository menuRepository,
                                          UserService userService) {
        return args -> {
            // 1. Seed Administrator if not present
            if (!userRepository.existsByEmail("admin@foodhub.com")) {
                User admin = new User(
                        "System Administrator",
                        "admin@foodhub.com",
                        userService.hashPassword("Admin@123"),
                        "9876543210",
                        "FoodHub HQ, Chennai",
                        User.Role.ADMIN
                );
                userRepository.save(admin);
                System.out.println("[DB Seed] Default Admin created: admin@foodhub.com / Admin@123");
            }

            // 2. Seed Default Categories if not present
            if (categoryRepository.count() == 0) {
                Category pizza = categoryRepository.save(new Category("Pizza", "Authentic wood-fired and pan-crust pizzas", "/images/categories/pizza.jpg"));
                Category burger = categoryRepository.save(new Category("Burger", "Juicy grilled gourmet burgers packed with fresh veggies", "/images/categories/burger.jpg"));
                Category biryani = categoryRepository.save(new Category("Biryani", "Traditional aromatic dum biryani with saffron", "/images/categories/biryani.jpg"));
                Category drinks = categoryRepository.save(new Category("Drinks", "Refreshing chilled beverages and mocktails", "/images/categories/drinks.jpg"));
                Category desserts = categoryRepository.save(new Category("Desserts", "Gourmet pastries, brownies, and ice creams", "/images/categories/desserts.jpg"));

                System.out.println("[DB Seed] 5 Standard Categories created.");

                // 3. Seed Menu Items
                menuRepository.saveAll(List.of(
                        new MenuItem("Margherita Supreme", "Fresh mozzarella, vine-ripened tomatoes, sweet basil", new BigDecimal("299.00"), pizza, "/images/menu/margherita.jpg", true),
                        new MenuItem("Paneer Tikka Pizza", "Spiced tandoori paneer, crunchy bell peppers, onions", new BigDecimal("349.00"), pizza, "/images/menu/paneer-pizza.jpg", true),
                        new MenuItem("Chicken Feast Pizza", "Smoked BBQ chicken chunks, chicken sausage, jalapeños", new BigDecimal("399.00"), pizza, "/images/menu/chicken-pizza.jpg", true),
                        new MenuItem("Veggie Paradise", "Golden corn, black olives, crisp bell peppers, mushrooms", new BigDecimal("329.00"), pizza, "/images/menu/veggie-pizza.jpg", true),

                        new MenuItem("Classic Veg Crispy Burger", "Crunchy spiced potato patty with creamy mayo and lettuce", new BigDecimal("149.00"), burger, "/images/menu/veg-burger.jpg", true),
                        new MenuItem("Grilled Chicken Burger", "Tender flame-grilled chicken breast with chipotle sauce", new BigDecimal("219.00"), burger, "/images/menu/chicken-burger.jpg", true),
                        new MenuItem("Double Cheese Delight Burger", "Loaded double cheese slices melted over crispy vegetable patty", new BigDecimal("189.00"), burger, "/images/menu/cheese-burger.jpg", true),
                        new MenuItem("Spicy Paneer Crunch Burger", "Crispy cottage cheese patty in hot peri-peri glaze", new BigDecimal("199.00"), burger, "/images/menu/paneer-burger.jpg", true),

                        new MenuItem("Hyderabadi Chicken Dum Biryani", "Slow-cooked aromatic basmati rice layered with chicken", new BigDecimal("280.00"), biryani, "/images/menu/chicken-biryani.jpg", true),
                        new MenuItem("Mutton Royal Dum Biryani", "Tender bone-in mutton pieces cooked in Awadhi dum style", new BigDecimal("360.00"), biryani, "/images/menu/mutton-biryani.jpg", true),
                        new MenuItem("Special Veg Biryani", "Garden-fresh vegetables and paneer cubes with herbs", new BigDecimal("220.00"), biryani, "/images/menu/veg-biryani.jpg", true),
                        new MenuItem("Egg Biryani Feast", "Fragrant biryani topped with roasted golden boiled eggs", new BigDecimal("210.00"), biryani, "/images/menu/egg-biryani.jpg", true),

                        new MenuItem("Fresh Mint Mojito", "Zesty fresh lime muddled with mint leaves and soda", new BigDecimal("99.00"), drinks, "/images/menu/mojito.jpg", true),
                        new MenuItem("Mango Lassi", "Traditional chilled yogurt shake blended with Alphonso mango", new BigDecimal("89.00"), drinks, "/images/menu/mango-lassi.jpg", true),
                        new MenuItem("Iced Cold Coffee", "Rich espresso blended with chilled milk and vanilla ice cream", new BigDecimal("119.00"), drinks, "/images/menu/cold-coffee.jpg", true),

                        new MenuItem("Sizzling Chocolate Brownie", "Warm walnut brownie with hot Belgian fudge and ice cream", new BigDecimal("159.00"), desserts, "/images/menu/brownie.jpg", true),
                        new MenuItem("Gulab Jamun with Ice Cream", "Warm khoya dumplings soaked in rose syrup with vanilla gelato", new BigDecimal("109.00"), desserts, "/images/menu/gulab-jamun.jpg", true),
                        new MenuItem("Royal Kulfi Falooda", "Creamy malai kulfi topped with basil seeds and rose vermicelli", new BigDecimal("139.00"), desserts, "/images/menu/kulfi.jpg", true)
                ));

                System.out.println("[DB Seed] 18 Menu Items created.");
            }
        };
    }
}

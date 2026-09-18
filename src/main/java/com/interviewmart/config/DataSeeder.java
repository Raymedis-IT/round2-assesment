package com.interviewmart.config;

import com.interviewmart.entity.Product;
import com.interviewmart.entity.Role;
import com.interviewmart.entity.User;
import com.interviewmart.repository.ProductRepository;
import com.interviewmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@interviewmart.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .build());

        User jane = userRepository.save(User.builder()
                .name("Jane Shopper")
                .email("jane@interviewmart.com")
                .password(passwordEncoder.encode("User@123"))
                .role(Role.USER)
                .build());

        productRepository.save(Product.builder()
                .name("Mechanical Keyboard")
                .description("Tactile switches, full size")
                .price(new BigDecimal("89.99"))
                .category("electronics")
                .stockQuantity(12)
                .createdBy(jane)
                .build());

        productRepository.save(Product.builder()
                .name("Ceramic Mug")
                .description("350ml matte finish")
                .price(new BigDecimal("12.50"))
                .category("home")
                .stockQuantity(40)
                .createdBy(jane)
                .build());

        productRepository.save(Product.builder()
                .name("USB-C Hub")
                .description("7-in-1 adapter")
                .price(new BigDecimal("34.00"))
                .category("electronics")
                .stockQuantity(8)
                .createdBy(admin)
                .build());
    }
}

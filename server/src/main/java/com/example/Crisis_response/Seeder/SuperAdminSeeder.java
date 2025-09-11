package com.example.Crisis_response.Seeder;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.Crisis_response.Entity.User.UserAuthEntity;
import com.example.Crisis_response.Entity.User.UserEntity;
import com.example.Crisis_response.Entity.User.UserRollEntity;

import com.example.Crisis_response.Repository.UserAuthRepository;
import com.example.Crisis_response.Repository.UserRepository;
import com.example.Crisis_response.Repository.UserRollRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
@Order(2)
public class SuperAdminSeeder implements ApplicationRunner {

    public static final String SUPERADMIN_NAME = "Super Admin";
    public static final String SUPERADMIN_PHONE = "9999999999";
    public static final String SUPERADMIN_EMAIL = "superadmin@Crisis.com";
    public static final String SUPERADMIN_PASSWORD = "Admin@12345";

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserRollRepository userRollRepository;
    private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public SuperAdminSeeder(
            UserRepository userRepository,
            UserAuthRepository userAuthRepository,
            UserRollRepository userRollRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
        this.userRollRepository = userRollRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        boolean superAdminExists = userAuthRepository.findByEmail(SUPERADMIN_EMAIL).isPresent();

        if (!superAdminExists) {
            UserRollEntity adminRole = userRollRepository.findByRoll("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ADMIN role must exist before seeding superadmin"));

            // Insert super admin user with self-auditing using a native insert to avoid transient self-reference
            Number nextIdNum = (Number) entityManager
                    .createNativeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM users")
                    .getSingleResult();
            Long newId = nextIdNum.longValue();

            entityManager.createNativeQuery(
                    "INSERT INTO users (id, name, phone_number, created_at, updated_at, whatsapp_number, instagram_handle, website_url, is_active, phone_verified, user_roll_id, created_by, updated_by, is_deleted) " +
                    "VALUES (?1, ?2, ?3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, NULL, TRUE, TRUE, ?4, ?1, ?1, FALSE)")
                    .setParameter(1, newId)
                    .setParameter(2, SUPERADMIN_NAME)
                    .setParameter(3, SUPERADMIN_PHONE)
                    .setParameter(4, adminRole.getId())
                    .executeUpdate();

            UserEntity superAdmin = userRepository.findById(newId)
                    .orElseThrow(() -> new IllegalStateException("Failed to read back seeded superadmin user"));

            UserAuthEntity auth = new UserAuthEntity();
            auth.setEmail(SUPERADMIN_EMAIL);
            auth.setPassword(passwordEncoder.encode(SUPERADMIN_PASSWORD));
            auth.setUser(superAdmin);
            auth.setCreatedBy(superAdmin);
            auth.setUpdatedBy(superAdmin);
            userAuthRepository.save(auth);
        }

        // Always ensure IDENTITY next value is max(id)+1 so new users do not collide
        Number maxIdNum = (Number) entityManager
                .createNativeQuery("SELECT COALESCE(MAX(id), 0) FROM users")
                .getSingleResult();
        long nextIdentity = maxIdNum.longValue() + 1L;
        // H2 syntax to restart identity
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH " + nextIdentity)
                .executeUpdate();
    }
}



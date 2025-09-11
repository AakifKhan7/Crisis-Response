package com.example.Crisis_response.Seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.Crisis_response.Entity.User.UserEntity;
import com.example.Crisis_response.Repository.UserRollRepository;
import com.example.Crisis_response.Repository.UserAuthRepository;

@Component
@Order(3)
public class RoleSeederFixCreatedBy implements ApplicationRunner {

    private final UserRollRepository userRollRepository;
    private final UserAuthRepository userAuthRepository;

    @Autowired
    public RoleSeederFixCreatedBy(UserRollRepository userRollRepository,
                                    UserAuthRepository userAuthRepository) {
        this.userRollRepository = userRollRepository;
        this.userAuthRepository = userAuthRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // Ensure superadmin exists (auth implies user exists)
        UserEntity superAdmin = userAuthRepository.findByEmail(SuperAdminSeeder.SUPERADMIN_EMAIL)
                .map(a -> a.getUser())
                .orElse(null);
        if (superAdmin == null) {
            return;
        }

        userRollRepository.findByRoll("ADMIN").ifPresent(role -> {
            boolean needsUpdate = (role.getCreatedBy() == null) || (role.getUpdatedBy() == null);
            if (needsUpdate) {
                role.setCreatedBy(superAdmin);
                role.setUpdatedBy(superAdmin);
                userRollRepository.save(role);
            }
        });

        userRollRepository.findByRoll("USER").ifPresent(role -> {
            boolean needsUpdate = (role.getCreatedBy() == null) || (role.getUpdatedBy() == null);
            if (needsUpdate) {
                role.setCreatedBy(superAdmin);
                role.setUpdatedBy(superAdmin);
                userRollRepository.save(role);
            }
        });
    }
}



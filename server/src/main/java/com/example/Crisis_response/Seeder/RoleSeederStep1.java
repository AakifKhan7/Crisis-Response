package com.example.Crisis_response.Seeder;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.Crisis_response.Entity.User.UserRollEntity;
import com.example.Crisis_response.Repository.UserRollRepository;

@Component
@Order(1)
public class RoleSeederStep1 implements ApplicationRunner {

    private final UserRollRepository userRollRepository;

    @Autowired
    public RoleSeederStep1(UserRollRepository userRollRepository) {
        this.userRollRepository = userRollRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        userRollRepository.findByRoll("ADMIN").orElseGet(() -> {
            UserRollEntity admin = new UserRollEntity();
            admin.setRoll("ADMIN");
            admin.setActive(true);
            // createdBy / updatedBy are intentionally left null in step 1
            return userRollRepository.save(admin);
        });

        userRollRepository.findByRoll("USER").orElseGet(() -> {
            UserRollEntity user = new UserRollEntity();
            user.setRoll("USER");
            user.setActive(true);
            // createdBy / updatedBy are intentionally left null in step 1
            return userRollRepository.save(user);
        });
    }
}



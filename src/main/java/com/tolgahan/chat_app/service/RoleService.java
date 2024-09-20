package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.model.Role;
import com.tolgahan.chat_app.repository.RoleRepository;
import com.tolgahan.chat_app.service.interfaces.IRoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void initRoles() {
        if (!roleRepository.existsByName("ADMIN")) {
            roleRepository.save(new Role("ADMIN"));
        }

        if (!roleRepository.existsByName("USER")) {
            roleRepository.save(new Role("USER"));
        }
    }
}

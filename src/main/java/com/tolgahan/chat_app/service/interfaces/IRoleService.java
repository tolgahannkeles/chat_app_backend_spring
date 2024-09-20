package com.tolgahan.chat_app.service.interfaces;

import jakarta.annotation.PostConstruct;

public interface IRoleService {
    @PostConstruct
    void initRoles();
}

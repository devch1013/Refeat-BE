package com.audrey.refeat.domain.admin.controller;

import com.audrey.refeat.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @GetMapping(path = "/821hejij1n3br7ef739hwee/admin/user", produces="text/html; charset=utf8")
    public String user() {
        return adminService.userList();
    }

    @GetMapping(path = "/821hejij1n3br7ef739hwee/admin/project", produces="text/html; charset=utf8")
    public String project() {
        return adminService.projectList();
    }
}

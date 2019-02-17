package com.ozden.coiner.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @GetMapping(value = "current-user")
    public String getCurrentUserName(HttpServletRequest request) {
        return request.getUserPrincipal().getName();
    }
}

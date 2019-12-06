package com.yang.gateway.controller;

import com.yang.gateway.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/user")
public class UseController {

//    @Autowired
//    private UserService userService;

//    @PostMapping(value = "")
//    public Mono<User> saveUser(User user) {
//        return userService.save(user);
//    }

    @PostMapping(value = "/{username}")
    public Mono<User> getUser(@PathVariable("username") String username) {
        return Mono.just(new User("100",username,"100"));

    }
}

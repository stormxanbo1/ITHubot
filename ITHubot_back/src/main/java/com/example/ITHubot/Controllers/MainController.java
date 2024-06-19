package com.example.ITHubot.Controllers;



import com.example.ITHubot.Dal.DataAccessLayer;
import com.example.ITHubot.Dto.SignupRequest;
import com.example.ITHubot.Models.*;
import com.example.ITHubot.Security.JwtCore;
import com.example.ITHubot.Service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/admin")

public class MainController {

    private final DataAccessLayer dataAccessLayer;
    private final UserDetailsServiceImpl userService;

    @Autowired
    public MainController(UserDetailsServiceImpl userService, DataAccessLayer dataAccessLayer) {
        this.userService = userService;
        this.dataAccessLayer = dataAccessLayer;
    }
    @GetMapping("/get/users")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity getUsers(){
        return ResponseEntity.ok(dataAccessLayer.getUsers());
    }


}

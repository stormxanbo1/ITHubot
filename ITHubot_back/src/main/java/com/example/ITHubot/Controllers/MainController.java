package com.example.ITHubot.Controllers;



import com.example.ITHubot.Dal.DataAccessLayer;
import com.example.ITHubot.Dto.ResultRequest;
import com.example.ITHubot.Dto.SignupRequest;
import com.example.ITHubot.Models.*;
import com.example.ITHubot.Security.JwtCore;
import com.example.ITHubot.Service.TestEvaluationService;
import com.example.ITHubot.Service.UserDetailsServiceImpl;
import com.example.ITHubot.Service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/main")

public class MainController {
    @Autowired
    private TestEvaluationService testEvaluationService;
    @Autowired
    private UserScoreService userScoreService;
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

//////////////////////////////////
@PostMapping("/create/result")
public ResponseEntity<?> createResult(@RequestBody ResultRequest resultRequest) {
    // Получаем пользователя
    User user = dataAccessLayer.getUserById(resultRequest.getUserId());
    if (user == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // Получаем тест
    Test test = dataAccessLayer.getTestById(resultRequest.getTestId());
    if (test == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Test not found");
    }

    // Вычисляем баллы пользователя
    int score = testEvaluationService.calculateScore(test, resultRequest.getUserAnswers());

    // Создаем результат
    Result result = new Result();
    result.setUser(user);
    result.setTest(test);
    result.setScore(score);
    result.setCompletedAt(new Date());

    // Сохраняем результат
    dataAccessLayer.createResult(result);

    // Обновляем UserScore для пользователя
    UserScore userScore = dataAccessLayer.getUserScoreByUserId(user.getUserId());
    if (userScore == null) {
        // Если UserScore не существует, создаем новый
        userScore = new UserScore();
        userScore.setUser(user);
        userScore.setTotalScore(score);
        dataAccessLayer.createUserScore(userScore);
    } else {
        // Если UserScore существует, обновляем его
        userScore.setTotalScore(userScore.getTotalScore() + score);
        dataAccessLayer.updateUserScore(userScore.getUserScoreId(), userScore);
    }

    return ResponseEntity.ok("Result created successfully");
}


}

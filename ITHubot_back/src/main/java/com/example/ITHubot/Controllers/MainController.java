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
import java.util.List;
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


    return ResponseEntity.ok("Result created successfully");
}
    @GetMapping("/get/userScores")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getUserScores(){
        return ResponseEntity.ok(dataAccessLayer.getUserScores());
    }

    @GetMapping("get/question/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getQuestionById(@PathVariable("id") long id) {
        dataAccessLayer.getQuestionById(id);
        return ResponseEntity.ok("User updated!");
    }


    @GetMapping("get/answer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getAnswerById(@PathVariable("id") long id) {

        return ResponseEntity.ok(dataAccessLayer.getAnswerById(id));
    }
    /////////////////////////////////////////////////////////

    @GetMapping("/questions/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getQuestionsByTestId(@PathVariable("id") Long id) {
        List<Question> questions = dataAccessLayer.getQuestionsByTestID(id);
        if (questions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No questions found for the given test");
        }
        return ResponseEntity.ok(questions);
    }
    @GetMapping("get/question/answer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAnswerByQuestionId(@PathVariable("id") Long id) {
        List<Answer> answers = dataAccessLayer.getAnswersByQuestionId(id);
        if (answers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No questions found for the given test");
        }
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/score/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserScore> getUserScoreByUserId(@PathVariable("id") Long id) {
        UserScore userScore = dataAccessLayer.getUserScoreByUserId(id);
        if (userScore == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userScore, HttpStatus.OK);
    }
    @GetMapping("get/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getUserById(@PathVariable("id") long id) {

        return ResponseEntity.ok((dataAccessLayer.getUserById(id)));
    }

    @GetMapping("/get/answer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getAnswer(){
        return ResponseEntity.ok(dataAccessLayer.getAnswer());
    }


    @GetMapping("/get/test")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getTest(){
        return ResponseEntity.ok(dataAccessLayer.getTest());
    }
    @GetMapping("/get/results")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getResults(){
        return ResponseEntity.ok(dataAccessLayer.getResults());
    }

    @GetMapping("get/test/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getTestById(@PathVariable("id") long id) {

        return ResponseEntity.ok(dataAccessLayer.getTestById(id));
    }


}

//UserScore userScore = dataAccessLayer.getUserScoreByUserId(user.getUserId());
//    if (userScore == null) {
//// Если UserScore не существует, создаем новый
//userScore = new UserScore();
//        userScore.setUser(user);
//        userScore.setTotalScore(score);
//        dataAccessLayer.createUserScore(userScore);
//    } else {
//            // Если UserScore существует, обновляем его
//            userScore.setTotalScore(userScore.getTotalScore() + score);
//        dataAccessLayer.updateUserScore(userScore.getUserScoreId(), userScore);
//        }

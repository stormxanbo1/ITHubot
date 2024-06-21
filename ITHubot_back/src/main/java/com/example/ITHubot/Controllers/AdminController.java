package com.example.ITHubot.Controllers;


import com.example.ITHubot.Dal.DataAccessLayer;
import com.example.ITHubot.Dto.SignupRequest;
import com.example.ITHubot.Models.*;
import com.example.ITHubot.Security.JwtCore;
import com.example.ITHubot.Service.TestEvaluationService;
import com.example.ITHubot.Service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/admin")
public class AdminController {
    private final DataAccessLayer dataAccessLayer;
    private final UserDetailsServiceImpl userService;
 /*   private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    public void someMethod() {
        logger.debug("Debug message");
//        logger.info("Info message");
        logger.warn("Warning message");
        logger.error("Error message");
    }*/
    @Autowired
    public AdminController(UserDetailsServiceImpl userService, DataAccessLayer dataAccessLayer) {
        this.userService = userService;
        this.dataAccessLayer = dataAccessLayer;
    }
    @Autowired
    private JwtCore jwtCore;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestEvaluationService testEvaluationService;
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createAdmin(@RequestBody SignupRequest signupRequest) {
//        signupRequest.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        signupRequest.setRoles(Set.of("ROLE_ADMIN"));
        String serviceResult = userService.newUser(signupRequest);
        if (Objects.equals(serviceResult, "Выберите другое имя")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResult);
        }
        if (Objects.equals(serviceResult, "Выберите другую почту")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResult);
        }
        return ResponseEntity.ok("Администратор успешно создан.");
    }


//    @GetMapping("/get/users")
//    public ResponseEntity getUsers(){
//        return ResponseEntity.ok(dataAccessLayer.getUsers());
//    }

    @PostMapping("/create/user")
    public ResponseEntity createUser(@RequestBody User user){
        dataAccessLayer.createUser(user);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity deleteUserById(@PathVariable("id") long id){
        dataAccessLayer.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/user/{id}")
    public ResponseEntity updateUserById(@PathVariable("id") long id, @RequestBody User newUser) {
        dataAccessLayer.updateUser(id, newUser);
        return ResponseEntity.ok("User updated!");
    }
    @GetMapping("get/user/{id}")
    public ResponseEntity getUserById(@PathVariable("id") long id) {

        return ResponseEntity.ok((dataAccessLayer.getUserById(id)));
    }

    @GetMapping("/get/answer")
    public ResponseEntity getAnswer(){
        return ResponseEntity.ok(dataAccessLayer.getAnswer());
    }

    @PostMapping("/create/answer")
    public ResponseEntity createAnswer(@RequestBody Answer answer){
        dataAccessLayer.createAnswer(answer);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/answer/{id}")
    public ResponseEntity deleteAnswerById(@PathVariable("id") long id){
        dataAccessLayer.deleteAnswerById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/answer/{id}")
    public ResponseEntity updateAnswerById(@PathVariable("id") long id, @RequestBody Answer newAnswer) {
        dataAccessLayer.updateAnswer(id, newAnswer);
        return ResponseEntity.ok("User updated!");
    }
    @GetMapping("/get/question")
    public ResponseEntity getQuestion(){
        return ResponseEntity.ok(dataAccessLayer.getQuestion());
    }

    @PostMapping("/create/question")
    public ResponseEntity createQuestion(@RequestBody Question question){
        dataAccessLayer.createQuestion(question);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/question/{id}")
    public ResponseEntity deleteQuestionById(@PathVariable("id") long id){
        dataAccessLayer.deleteQuestionById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/question/{id}")
    public ResponseEntity updateQuestionById(@PathVariable("id") long id, @RequestBody Question newQuestion) {
        dataAccessLayer.updateQuestion(id, newQuestion);
        return ResponseEntity.ok("User updated!");
    }
    @GetMapping("/get/result")
    public ResponseEntity getResult(){
        return ResponseEntity.ok(dataAccessLayer.getAnswer());
    }

    @PostMapping("/create/result")
    public ResponseEntity createResult(@RequestBody Result result){
        dataAccessLayer.createResult(result);
        return ResponseEntity.ok("Result added successfully!");
    }
    @DeleteMapping("/delete/result/{id}")
    public ResponseEntity deleteResultById(@PathVariable("id") long id){
        dataAccessLayer.deleteResultById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/result/{id}")
    public ResponseEntity updateResultById(@PathVariable("id") long id, @RequestBody Result newResult) {
        dataAccessLayer.updateResult(id, newResult);
        return ResponseEntity.ok("User updated!");
    }
    @GetMapping("/get/test")
    public ResponseEntity getTest(){
        return ResponseEntity.ok(dataAccessLayer.getTest());
    }

    @PostMapping("/create/test")
    public ResponseEntity createTest(@RequestBody Test Test){
        dataAccessLayer.createTest(Test);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/test/{id}")
    public ResponseEntity deleteTestById(@PathVariable("id") long id){
        dataAccessLayer.deleteTestById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/test/{id}")
    public ResponseEntity updateTestById(@PathVariable("id") long id, @RequestBody Test newTest) {
        dataAccessLayer.updateTest(id, newTest);
        return ResponseEntity.ok("User updated!");
    }
    @GetMapping("/get/score")
    public ResponseEntity getUserScore(){
        return ResponseEntity.ok(dataAccessLayer.getAnswer());
    }

    @PostMapping("/create/userscore")
    public ResponseEntity createUserScore(@RequestBody UserScore userScore ){
        dataAccessLayer.createUserScore(userScore);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/userscore/{id}")
    public ResponseEntity deleteUserScoreById(@PathVariable("id") long id){
        dataAccessLayer.deleteUserScoreById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/userscore/{id}")
    public ResponseEntity updateUserScoreById(@PathVariable("id") long id, @RequestBody UserScore newUserScore) {
        dataAccessLayer.updateUserScore(id, newUserScore);
        return ResponseEntity.ok("User updated!");
    }

    @GetMapping("update/result/{id}")
    public ResponseEntity getResultById(@PathVariable("id") long id) {
        dataAccessLayer.getResultById(id);
        return ResponseEntity.ok("result updated!");
    }
    @GetMapping("/get/results")
    public ResponseEntity getResults(){
        return ResponseEntity.ok(dataAccessLayer.getResults());
    }

    @GetMapping("get/test/{id}")
    public ResponseEntity getTestById(@PathVariable("id") long id) {

        return ResponseEntity.ok(dataAccessLayer.getTestById(id));
    }
    @GetMapping("update/userScore/{id}")
    public ResponseEntity getUserScoreById(@PathVariable("id") long id) {
        dataAccessLayer.getUserScoreById(id);
        return ResponseEntity.ok("result updated!");
    }

    @GetMapping("/get/userScores")
    public ResponseEntity getUserScores(){
        return ResponseEntity.ok(dataAccessLayer.getUserScores());
    }

    @GetMapping("get/question/{id}")
    public ResponseEntity getQuestionById(@PathVariable("id") long id) {
        dataAccessLayer.getQuestionById(id);
        return ResponseEntity.ok("User updated!");
    }


    @GetMapping("get/answer/{id}")
    public ResponseEntity getAnswerById(@PathVariable("id") long id) {

        return ResponseEntity.ok(dataAccessLayer.getAnswerById(id));
    }
    /////////////////////////////////////////////////////////

    @GetMapping("/questions/{id}")
    public ResponseEntity<?> getQuestionsByTestId(@PathVariable("id") Long id) {
        List<Question> questions = dataAccessLayer.getQuestionsByTestID(id);
        if (questions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No questions found for the given test");
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("get/question/answer/{id}")
    public ResponseEntity<?> getAnswerByQuestionId(@PathVariable("id") Long id) {
        List<Answer> answers = dataAccessLayer.getAnswersByQuestionId(id);
        if (answers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No questions found for the given test");
        }
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/score/{id}")
    public ResponseEntity<UserScore> getUserScoreByUserId(@PathVariable("id") Long id) {
        UserScore userScore = dataAccessLayer.getUserScoreByUserId(id);
        if (userScore == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userScore, HttpStatus.OK);
    }

}
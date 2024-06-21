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

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/admin")
public class AdminController {
    private final DataAccessLayer dataAccessLayer;
    private final UserDetailsServiceImpl userService;
    private final JwtCore jwtCore;
    private final PasswordEncoder passwordEncoder;

    private final TestEvaluationService testEvaluationService;
    @Autowired
    public AdminController(UserDetailsServiceImpl userService, DataAccessLayer dataAccessLayer, JwtCore jwtCore, PasswordEncoder passwordEncoder, TestEvaluationService testEvaluationService) {
        this.userService = userService;
        this.dataAccessLayer = dataAccessLayer;
        this.jwtCore = jwtCore;
        this.passwordEncoder = passwordEncoder;
        this.testEvaluationService = testEvaluationService;
    }

//    @PostMapping("/create")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> createAdmin(@RequestBody SignupRequest signupRequest) {
////        signupRequest.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
//        signupRequest.setRoles(Set.of("ROLE_ADMIN"));
//        String serviceResult = userService.newUser(signupRequest);
//        if (Objects.equals(serviceResult, "Выберите другое имя")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResult);
//        }
//        if (Objects.equals(serviceResult, "Выберите другую почту")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResult);
//        }
//        return ResponseEntity.ok("Администратор успешно создан.");
//    }


//    @GetMapping("/get/users")
//    public ResponseEntity getUsers(){
//        return ResponseEntity.ok(dataAccessLayer.getUsers());
//    }

    @PostMapping("/create/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createUser(@RequestBody User user){
        dataAccessLayer.createUser(user);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteUserById(@PathVariable("id") long id){
        dataAccessLayer.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateUserById(@PathVariable("id") long id, @RequestBody User newUser) {
        dataAccessLayer.updateUser(id, newUser);
        return ResponseEntity.ok("User updated!");
    }

    @PostMapping("/create/answer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createAnswer(@RequestBody Answer answer){
        dataAccessLayer.createAnswer(answer);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/answer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteAnswerById(@PathVariable("id") long id){
        dataAccessLayer.deleteAnswerById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/answer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateAnswerById(@PathVariable("id") long id, @RequestBody Answer newAnswer) {
        dataAccessLayer.updateAnswer(id, newAnswer);
        return ResponseEntity.ok("User updated!");
    }
    @GetMapping("/get/question")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getQuestion(){
        return ResponseEntity.ok(dataAccessLayer.getQuestion());
    }

    @PostMapping("/create/question")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createQuestion(@RequestBody Question question){
        dataAccessLayer.createQuestion(question);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/question/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteQuestionById(@PathVariable("id") long id){
        dataAccessLayer.deleteQuestionById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/question/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateQuestionById(@PathVariable("id") long id, @RequestBody Question newQuestion) {
        dataAccessLayer.updateQuestion(id, newQuestion);
        return ResponseEntity.ok("User updated!");
    }
    @GetMapping("/get/result")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getResult(){
        return ResponseEntity.ok(dataAccessLayer.getAnswer());
    }

    @PostMapping("/create/result")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createResult(@RequestBody Result result){
        dataAccessLayer.createResult(result);
        return ResponseEntity.ok("Result added successfully!");
    }
    @DeleteMapping("/delete/result/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteResultById(@PathVariable("id") long id){
        dataAccessLayer.deleteResultById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/result/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateResultById(@PathVariable("id") long id, @RequestBody Result newResult) {
        dataAccessLayer.updateResult(id, newResult);
        return ResponseEntity.ok("User updated!");
    }

    @PostMapping("/create/test")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createTest(@RequestBody Test test){
        dataAccessLayer.createTest(test);
        return ResponseEntity.ok("Test added successfully!");
    }
    @DeleteMapping("/delete/test/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteTestById(@PathVariable("id") long id){
        dataAccessLayer.deleteTestById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/test/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateTestById(@PathVariable("id") long id, @RequestBody Test newTest) {
        dataAccessLayer.updateTest(id, newTest);
        return ResponseEntity.ok("User updated!");
    }
    @GetMapping("/get/score")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getUserScore(){
        return ResponseEntity.ok(dataAccessLayer.getAnswer());
    }

    @PostMapping("/create/userscore")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity createUserScore(@RequestBody UserScore userScore ){
        dataAccessLayer.createUserScore(userScore);
        return ResponseEntity.ok("User added successfully!");
    }
    @DeleteMapping("/delete/userscore/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity deleteUserScoreById(@PathVariable("id") long id){
        dataAccessLayer.deleteUserScoreById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
    @PostMapping("update/userscore/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity updateUserScoreById(@PathVariable("id") long id, @RequestBody UserScore newUserScore) {
        dataAccessLayer.updateUserScore(id, newUserScore);
        return ResponseEntity.ok("User updated!");
    }

    @GetMapping("update/result/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getResultById(@PathVariable("id") long id) {
        dataAccessLayer.getResultById(id);
        return ResponseEntity.ok("result updated!");
    }

    @GetMapping("update/userScore/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getUserScoreById(@PathVariable("id") long id) {
        dataAccessLayer.getUserScoreById(id);
        return ResponseEntity.ok("result updated!");
    }




        


}
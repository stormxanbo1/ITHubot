package com.example.ITHubot.Dal;


import com.example.ITHubot.Models.*;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
@Getter
public class DataAccessLayer {
    private final SessionFactory sessionFactory;

    @Autowired
    public DataAccessLayer(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    Session session = null;


    public void createUser(User user){
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(user);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void deleteUserById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        User user = session.get(User.class, id);
        session.remove(id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void updateUser(Long id, User updatedUser){
        session = sessionFactory.openSession();
        session.beginTransaction();
        User user = session.get(User.class, id);
        user.setUsername(updatedUser.getUsername());
        session.merge(user);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public User getUserById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        User user = session.get(User.class, id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
        return user;
    }
    public List<User> getUsers(){
        session = sessionFactory.openSession();
        session.getTransaction().begin();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);
        List<User> resultList = session.createQuery(query).getResultList();
        return resultList;
    }
    public String newUserToDatabase(User user) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        String name = user.getUsername();

        Query query = session
                .createQuery("FROM User where username = :username")
                .setParameter("username", name);
        User userFrom = (User) query.uniqueResult();

        if (userFrom != null) {
            return "Выберите другое имя";
        }
        session.persist(user);
        session.getTransaction().commit();
        session.close();
        return "Pabeda)";
    }
    public User getUserFromDatabaseByUsername(String name) {
        session = sessionFactory.openSession();
        session.getTransaction().begin();
        Query query = session
                .createQuery("FROM User where username = :username")
                .setParameter("username", name);
        User userFrom = (User) query.uniqueResult();
        if (userFrom == null) {
            return null;
        }
        return userFrom;
    }
    public void createQuestion(Question question){
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(question);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void deleteQuestionById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Question question = session.get(Question.class, id);
        session.remove(id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void updateQuestion(Long id, Question updatedQuestion){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Question question = session.get(Question.class, id);
        question.setUpdatedAt(updatedQuestion.getUpdatedAt());
        question.setContent(updatedQuestion.getContent());
        question.setCreatedAt(updatedQuestion.getCreatedAt());

        session.merge(question);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public Question getQuestionById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Question question = session.get(Question.class, id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
        return question;
    }
    public List<Question> getQuestion(){
        session = sessionFactory.openSession();
        session.getTransaction().begin();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Question> query = builder.createQuery(Question.class);
        Root<Question> root = query.from(Question.class);
        query.select(root);
        List<Question> resultList = session.createQuery(query).getResultList();
        return resultList;
    }
    public void createAnswer(Answer answer){
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(answer);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void deleteAnswerById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Answer answer = session.get(Answer.class, id);
        session.remove(id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void updateAnswer(Long id, Answer updatedAnswer){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Answer answer = session.get(Answer.class, id);
        answer.setContent(updatedAnswer.getContent());
        answer.setCreatedAt(updatedAnswer.getCreatedAt());
        answer.setUpdatedAt(updatedAnswer.getUpdatedAt());
        answer.setIsCorrect(updatedAnswer.getIsCorrect());
        session.merge(answer);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public Answer getAnswerById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Answer answer = session.get(Answer.class, id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
        return answer;
    }
    public List<Answer> getAnswer(){
        session = sessionFactory.openSession();
        session.getTransaction().begin();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Answer> query = builder.createQuery(Answer.class);
        Root<Answer> root = query.from(Answer.class);
        query.select(root);
        List<Answer> resultList = session.createQuery(query).getResultList();
        return resultList;
    }
    public void createResult(Result result){
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(result);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void deleteResultById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Result result = session.get(Result.class, id);
        session.remove(id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void updateResult(Long id, Result updatedResult){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Result result = session.get(Result.class, id);
        result.setCompletedAt(updatedResult.getCompletedAt());
        result.setScore(updatedResult.getScore());
        session.merge(result);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public Result getResultById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Result result = session.get(Result.class, id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
        return result;
    }
    public List<Result> getResults(){
        session = sessionFactory.openSession();
        session.getTransaction().begin();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Result> query = builder.createQuery(Result.class);
        Root<Result> root = query.from(Result.class);
        query.select(root);
        List<Result> resultList = session.createQuery(query).getResultList();
        return resultList;
    }
    public void createTest(Test test){
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(test);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void deleteTestById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Test test = session.get(Test.class, id);
        session.remove(id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void updateTest(Long id, Test updatedTest){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Test test = session.get(Test.class, id);
        test.setTitle(updatedTest.getTitle());
        test.setDescription(updatedTest.getDescription());
        test.setUpdatedAt(updatedTest.getUpdatedAt());
        test.setCreatedAt(updatedTest.getCreatedAt());
        session.merge(test);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    @Transactional
    public Test getTestById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Test test = session.get(Test.class, id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
        return test;
    }
    public List<Test> getTest(){
        session = sessionFactory.openSession();
        session.getTransaction().begin();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Test> query = builder.createQuery(Test.class);
        Root<Test> root = query.from(Test.class);
        query.select(root);
        List<Test> resultList = session.createQuery(query).getResultList();
        return resultList;
    }
    public void createUserScore(UserScore userScore) {
        session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            // Merge the user entity to attach it to the current session
            User user = userScore.getUser();
            if (user != null && user.getUserId() != null) {
                user = session.merge(user);
                userScore.setUser(user);
            }

            // Persist the UserScore entity
            session.persist(userScore);

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public void deleteUserScoreById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        UserScore userScore = session.get(UserScore.class, id);
        session.remove(id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public void updateUserScore(Long id, UserScore updatedUserScore){
        session = sessionFactory.openSession();
        session.beginTransaction();
        UserScore userScore = session.get(UserScore.class, id);
        userScore.setTotalScore(updatedUserScore.getTotalScore());
        session.merge(userScore);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
    }
    public UserScore getUserScoreById(Long id){
        session = sessionFactory.openSession();
        session.beginTransaction();
        UserScore userScore = session.get(UserScore.class, id);
        session.getTransaction().commit();
        if (session != null) {
            session.close();
        }
        return userScore;
    }
    public List<UserScore> getUserScores(){
        session = sessionFactory.openSession();
        session.getTransaction().begin();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<UserScore> query = builder.createQuery(UserScore.class);
        Root<UserScore> root = query.from(UserScore.class);
        query.select(root);
        List<UserScore> resultList = session.createQuery(query).getResultList();
        return resultList;
    }

    //////////////////////////////////////////////////////////////
    @Transactional
    public List<Question> getQuestionsByTest(Test test) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Question> query = builder.createQuery(Question.class);
        Root<Question> root = query.from(Question.class);
        query.select(root).where(builder.equal(root.get("test"), test));
        List<Question> questions = session.createQuery(query).getResultList();
        session.getTransaction().commit();
        session.close();
        return questions;
    }

    @Transactional
    public Long getCorrectAnswerIdByQuestion(Question question) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("SELECT a.answerId FROM Answer a WHERE a.question = :question AND a.isCorrect = true")
                .setParameter("question", question);
        Long correctAnswerId = (Long) query.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return correctAnswerId;
    }
    @Transactional
    public List<Question> getQuestionsByTestID(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Question> query = builder.createQuery(Question.class);
        Root<Question> root = query.from(Question.class);
        query.select(root).where(builder.equal(root.get("test").get("testId"), id));
        List<Question> questions = session.createQuery(query).getResultList();
        session.getTransaction().commit();
        session.close();
        return questions;
    }
    @Transactional
    public List<Answer> getAnswersByQuestionId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создание запроса с использованием Criteria API
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Answer> query = builder.createQuery(Answer.class);
            Root<Answer> root = query.from(Answer.class);

            // Условие для выбора ответов по ID вопроса
            query.select(root).where(builder.equal(root.get("question").get("questionId"), id));

            // Выполнение запроса и получение результатов
            List<Answer> answers = session.createQuery(query).getResultList();

            session.getTransaction().commit();
            return answers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public UserScore getUserScoreByUserId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UserScore> query = builder.createQuery(UserScore.class);
            Root<UserScore> root = query.from(UserScore.class);

            // Условие для выбора UserScore по userId
            query.select(root).where(builder.equal(root.get("user").get("userId"), id));

            UserScore userScore = session.createQuery(query).uniqueResult();

            session.getTransaction().commit();
            return userScore;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}

//public List<Answer> getAnswerByQuestionID(Long id) {
//    Session session = sessionFactory.openSession();
//    session.beginTransaction();
//    CriteriaBuilder builder = session.getCriteriaBuilder();
//    CriteriaQuery<Answer> query = builder.createQuery(Answer.class);
//    Root<Answer> root = query.from(Answer.class);
//    query.select(root).where(builder.equal(root.get("question").get("questionId"), id));
//    List<Answer> answers = session.createQuery(query).getResultList();
//    session.getTransaction().commit();
//    session.close();
//    return answers;
//}
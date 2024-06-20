package com.example.ITHubot.Service;

import com.example.ITHubot.Models.User;
import com.example.ITHubot.Models.UserScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserScoreService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateUserScore(User user, int newScore) {
        user = entityManager.merge(user);
        UserScore userScore = entityManager.find(UserScore.class, user.getUserId());
        if (userScore == null) {
            userScore = new UserScore();
            userScore.setUser(user);
            userScore.setTotalScore(newScore);
            entityManager.persist(userScore);
        } else {
            userScore.setTotalScore(userScore.getTotalScore() + newScore);
            entityManager.merge(userScore);
        }
    }
}

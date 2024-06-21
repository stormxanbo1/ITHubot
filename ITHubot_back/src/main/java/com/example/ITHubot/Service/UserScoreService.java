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
        // Merge user if necessary to attach to the current persistence context
        user = entityManager.merge(user);

        // Find existing UserScore or create a new one if not found
        UserScore userScore = entityManager.find(UserScore.class, user.getUserId());
        if (userScore == null) {
            userScore = new UserScore();
            userScore.setUser(user);
            userScore.setTotalScore(newScore);
            entityManager.persist(userScore);
        } else {
            int currentScore = userScore.getTotalScore() != null ? userScore.getTotalScore() : 0;
            userScore.setTotalScore(currentScore + newScore);
            // No need to merge userScore again, since it's already managed
        }
    }
}

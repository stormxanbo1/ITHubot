package com.example.ITHubot.Service;

import com.example.ITHubot.Dal.DataAccessLayer;

import com.example.ITHubot.Models.Question;
import com.example.ITHubot.Models.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestEvaluationService {

    @Autowired
    private DataAccessLayer dataAccessLayer;

    public int calculateScore(Test test, Map<Long, Long> userAnswers) {
        int score = 0;
        List<Question> questions = dataAccessLayer.getQuestionsByTest(test);

        for (Question question : questions) {
            Long correctAnswerId = dataAccessLayer.getCorrectAnswerIdByQuestion(question);
            Long userAnswerId = userAnswers.get(question.getQuestionId());

            if (correctAnswerId != null && correctAnswerId.equals(userAnswerId)) {
                score++;
            }
        }
        return score;
    }
}

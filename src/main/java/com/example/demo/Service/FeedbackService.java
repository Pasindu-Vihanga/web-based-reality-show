package com.example.demo.Service;

import com.example.demo.DAO.FeedbackDAO;
import com.example.demo.Entity.Feedback;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackDAO feedbackDAO;

    public FeedbackService(FeedbackDAO feedbackDAO) {
        this.feedbackDAO = feedbackDAO;
    }

    public void saveFeedback(Feedback feedback) {
        if (validateFeedback(feedback)) {
            feedbackDAO.save(feedback);
        } else {
            throw new IllegalArgumentException("Invalid feedback data");
        }
    }

    public int updateFeedback(Feedback feedback) {
        if (validateFeedback(feedback)) {
            return feedbackDAO.update(feedback);
        }
        return 0;
    }

    public int deleteFeedback(Long feedbackId) {
        return feedbackDAO.delete(feedbackId);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackDAO.findAll();
    }

    public List<Feedback> findByUserId(String userId) {
        return feedbackDAO.findByUserId(userId);
    }

    public Optional<Feedback> findById(Long feedbackId) {
        return feedbackDAO.findById(feedbackId);
    }

    public boolean validateFeedback(Feedback feedback) {
        return feedback != null &&
                feedback.getUser() != null &&
                feedback.getUser().getUserId() != null &&
                feedback.getMessage() != null &&
                !feedback.getMessage().trim().isEmpty();
    }
}

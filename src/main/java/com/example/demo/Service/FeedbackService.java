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

    /** ================== SAVE FEEDBACK ================== */
    public void saveFeedback(Feedback feedback) {
        if (validateFeedback(feedback)) {
            feedbackDAO.save(feedback);
        } else {
            throw new IllegalArgumentException("Invalid feedback data");
        }
    }

    /** ================== UPDATE FEEDBACK ================== */
    public int updateFeedback(Feedback feedback) {
        if (validateFeedback(feedback)) {
            return feedbackDAO.update(feedback);
        }
        return 0;
    }

    /** ================== DELETE FEEDBACK ================== */
    public int deleteFeedback(Long feedbackId) {
        return feedbackDAO.delete(feedbackId);
    }

    /** ================== GET ALL FEEDBACK (ADMIN) ================== */
    public List<Feedback> getAllFeedback() {
        return feedbackDAO.findAll();
    }

    /** ================== GET FEEDBACK BY USER ================== */
    public List<Feedback> findByUserId(String userId) {
        return feedbackDAO.findByUserId(userId);
    }

    /** ================== GET FEEDBACK BY ID ================== */
    public Optional<Feedback> findById(Long feedbackId) {
        return feedbackDAO.findById(feedbackId);
    }

    /** ================== VALIDATION ================== */
    public boolean validateFeedback(Feedback feedback) {
        if (feedback == null) return false;
        if (feedback.getUser() == null || feedback.getUser().getUserId() == null) return false;
        if (feedback.getMessage() == null || feedback.getMessage().trim().isEmpty()) return false;
        return true;
    }
}

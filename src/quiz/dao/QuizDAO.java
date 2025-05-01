package quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import quiz.model.QuizOption;
import quiz.util.DBUtil;

public class QuizDAO {
    
    // Create a quiz option
    public int createQuizOption(QuizOption option) throws SQLException {
        String sql = "INSERT INTO quiz (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, option.getQuestionId());
            pstmt.setString(2, option.getOptionText());
            pstmt.setBoolean(3, option.isCorrect());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating quiz option failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    option.setQuizId(generatedKeys.getInt(1));
                    return option.getQuizId();
                } else {
                    throw new SQLException("Creating quiz option failed, no ID obtained.");
                }
            }
        }
    }
    
    // Create a quiz option with the connection provided (for transactions)
    public int createQuizOption(QuizOption option, Connection conn) throws SQLException {
        String sql = "INSERT INTO quiz (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, option.getQuestionId());
            pstmt.setString(2, option.getOptionText());
            pstmt.setBoolean(3, option.isCorrect());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating quiz option failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    option.setQuizId(generatedKeys.getInt(1));
                    return option.getQuizId();
                } else {
                    throw new SQLException("Creating quiz option failed, no ID obtained.");
                }
            }
        }
    }
    
    // Get option by ID
    public QuizOption getOptionById(int quizId) throws SQLException {
        String sql = "SELECT * FROM quiz WHERE quiz_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quizId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    QuizOption option = new QuizOption();
                    option.setQuizId(rs.getInt("quiz_id"));
                    option.setQuestionId(rs.getInt("question_id"));
                    option.setOptionText(rs.getString("option_text"));
                    option.setCorrect(rs.getBoolean("is_correct"));
                    return option;
                }
            }
        }
        
        return null;
    }
    
    // Get all options for a question
    public List<QuizOption> getOptionsByQuestionId(int questionId) throws SQLException {
        List<QuizOption> options = new ArrayList<>();
        String sql = "SELECT * FROM quiz WHERE question_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    QuizOption option = new QuizOption();
                    option.setQuizId(rs.getInt("quiz_id"));
                    option.setQuestionId(rs.getInt("question_id"));
                    option.setOptionText(rs.getString("option_text"));
                    option.setCorrect(rs.getBoolean("is_correct"));
                    options.add(option);
                }
            }
        }
        
        return options;
    }
    
    // Update quiz option
    public boolean updateOption(QuizOption option) throws SQLException {
        String sql = "UPDATE quiz SET option_text = ?, is_correct = ? WHERE quiz_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, option.getOptionText());
            pstmt.setBoolean(2, option.isCorrect());
            pstmt.setInt(3, option.getQuizId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Delete a quiz option
    public boolean deleteOption(int quizId) throws SQLException {
        String sql = "DELETE FROM quiz WHERE quiz_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quizId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Delete all options for a question
    public boolean deleteOptionsByQuestionId(int questionId) throws SQLException {
        String sql = "DELETE FROM quiz WHERE question_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Delete all options for a question with the connection provided (for transactions)
    public boolean deleteOptionsByQuestionId(int questionId, Connection conn) throws SQLException {
        String sql = "DELETE FROM quiz WHERE question_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
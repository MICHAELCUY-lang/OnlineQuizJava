package quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import quiz.model.Question;
import quiz.model.QuizOption;
import quiz.util.DBUtil;

public class QuestionDAO {
    
    private QuizDAO quizDAO = new QuizDAO();
    
    // Create a new question
    public int createQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions (subject_id, question_text, correct_answer, created_by) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, question.getSubjectId());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getCorrectAnswer());
            pstmt.setInt(4, question.getCreatedBy());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating question failed, no rows affected.");
            }
            
            generatedKeys = pstmt.getGeneratedKeys();
            
            if (generatedKeys.next()) {
                int questionId = generatedKeys.getInt(1);
                question.setQuestionId(questionId);
                
                // Add options for this question
                if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                    for (QuizOption option : question.getOptions()) {
                        option.setQuestionId(questionId);
                        quizDAO.createQuizOption(option, conn);
                    }
                }
                
                conn.commit(); // Commit transaction
                return questionId;
            } else {
                conn.rollback(); // Rollback in case of failure
                throw new SQLException("Creating question failed, no ID obtained.");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of exception
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    DBUtil.closeConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Get question by ID with its options
    public Question getQuestionById(int questionId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question();
                    question.setQuestionId(rs.getInt("question_id"));
                    question.setSubjectId(rs.getInt("subject_id"));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setCreatedBy(rs.getInt("created_by"));
                    question.setCreatedDate(rs.getTimestamp("created_date"));
                    
                    // Get options for this question
                    List<QuizOption> options = quizDAO.getOptionsByQuestionId(question.getQuestionId());
                    question.setOptions(options);
                    
                    questions.add(question);
                }
            }
        }
        
        return questions;
    }
}.executeQuery()) {
                if (rs.next()) {
                    Question question = new Question();
                    question.setQuestionId(rs.getInt("question_id"));
                    question.setSubjectId(rs.getInt("subject_id"));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setCreatedBy(rs.getInt("created_by"));
                    question.setCreatedDate(rs.getTimestamp("created_date"));
                    
                    // Get options for this question
                    List<QuizOption> options = quizDAO.getOptionsByQuestionId(questionId);
                    question.setOptions(options);
                    
                    return question;
                }
            }
        }
        
        return null;
    }
    
    // Get all questions with their options
    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Question question = new Question();
                question.setQuestionId(rs.getInt("question_id"));
                question.setSubjectId(rs.getInt("subject_id"));
                question.setQuestionText(rs.getString("question_text"));
                question.setCorrectAnswer(rs.getString("correct_answer"));
                question.setCreatedBy(rs.getInt("created_by"));
                question.setCreatedDate(rs.getTimestamp("created_date"));
                
                // Get options for this question
                List<QuizOption> options = quizDAO.getOptionsByQuestionId(question.getQuestionId());
                question.setOptions(options);
                
                questions.add(question);
            }
        }
        
        return questions;
    }
    
    // Get questions by subject ID with their options
    public List<Question> getQuestionsBySubject(int subjectId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE subject_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question();
                    question.setQuestionId(rs.getInt("question_id"));
                    question.setSubjectId(rs.getInt("subject_id"));
                    question.setQuestionText(rs.getString("question_text"));
                    question.setCorrectAnswer(rs.getString("correct_answer"));
                    question.setCreatedBy(rs.getInt("created_by"));
                    question.setCreatedDate(rs.getTimestamp("created_date"));
                    
                    // Get options for this question
                    List<QuizOption> options = quizDAO.getOptionsByQuestionId(question.getQuestionId());
                    question.setOptions(options);
                    
                    questions.add(question);
                }
            }
        }
        
        return questions;
    }
    
    // Update question
    public boolean updateQuestion(Question question) throws SQLException {
        String sql = "UPDATE questions SET subject_id = ?, question_text = ?, correct_answer = ? WHERE question_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, question.getSubjectId());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getCorrectAnswer());
            pstmt.setInt(4, question.getQuestionId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Delete existing options
                quizDAO.deleteOptionsByQuestionId(question.getQuestionId(), conn);
                
                // Add updated options
                if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                    for (QuizOption option : question.getOptions()) {
                        option.setQuestionId(question.getQuestionId());
                        quizDAO.createQuizOption(option, conn);
                    }
                }
                
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback(); // Rollback in case of failure
                return false;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of exception
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    DBUtil.closeConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Delete question and its options
    public boolean deleteQuestion(int questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Delete options first (due to foreign key constraints)
            quizDAO.deleteOptionsByQuestionId(questionId, conn);
            
            // Delete the question
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, questionId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback(); // Rollback in case of failure
                return false;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of exception
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    DBUtil.closeConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Get random questions for a quiz by subject
    public List<Question> getRandomQuestionsBySubject(int subjectId, int count) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE subject_id = ? ORDER BY RAND() LIMIT ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setInt(2, count);
            
            try (ResultSet rs = pstmt
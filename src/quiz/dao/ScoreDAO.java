package quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import quiz.model.Score;
import quiz.util.DBUtil;

public class ScoreDAO {
    
    // Create a new quiz history
    public int createQuizHistory(int userId) throws SQLException {
        String sql = "INSERT INTO quiz_history (user_id) VALUES (?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, userId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating quiz history failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating quiz history failed, no ID obtained.");
                }
            }
        }
    }
    
    // Update quiz history end time
    public boolean updateQuizHistoryEndTime(int historyId) throws SQLException {
        String sql = "UPDATE quiz_history SET end_time = CURRENT_TIMESTAMP WHERE history_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, historyId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Create a new score record
    public int createScore(Score score) throws SQLException {
        String sql = "INSERT INTO scores (user_id, history_id, total_score, subject_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, score.getUserId());
            pstmt.setInt(2, score.getHistoryId());
            pstmt.setInt(3, score.getTotalScore());
            pstmt.setInt(4, score.getSubjectId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating score failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    score.setScoreId(generatedKeys.getInt(1));
                    return score.getScoreId();
                } else {
                    throw new SQLException("Creating score failed, no ID obtained.");
                }
            }
        }
    }
    
    // Record a quiz answer
    public int recordQuizAnswer(int historyId, int questionId, int selectedOptionId, boolean isCorrect) throws SQLException {
        String sql = "INSERT INTO quiz_answers (history_id, question_id, selected_option_id, is_correct) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, historyId);
            pstmt.setInt(2, questionId);
            pstmt.setInt(3, selectedOptionId);
            pstmt.setBoolean(4, isCorrect);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Recording quiz answer failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Recording quiz answer failed, no ID obtained.");
                }
            }
        }
    }
    
    // Get all scores for a user
    public List<Score> getScoresByUserId(int userId) throws SQLException {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT s.*, u.username, sb.subject_name " +
                     "FROM scores s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "JOIN subjects sb ON s.subject_id = sb.subject_id " +
                     "WHERE s.user_id = ? " +
                     "ORDER BY s.date_taken DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Score score = new Score();
                    score.setScoreId(rs.getInt("score_id"));
                    score.setUserId(rs.getInt("user_id"));
                    score.setHistoryId(rs.getInt("history_id"));
                    score.setTotalScore(rs.getInt("total_score"));
                    score.setDateTaken(rs.getTimestamp("date_taken"));
                    score.setSubjectId(rs.getInt("subject_id"));
                    score.setUsername(rs.getString("username"));
                    score.setSubjectName(rs.getString("subject_name"));
                    scores.add(score);
                }
            }
        }
        
        return scores;
    }
    
    // Get all scores for a subject
    public List<Score> getScoresBySubject(int subjectId) throws SQLException {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT s.*, u.username, sb.subject_name " +
                     "FROM scores s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "JOIN subjects sb ON s.subject_id = sb.subject_id " +
                     "WHERE s.subject_id = ? " +
                     "ORDER BY s.date_taken DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Score score = new Score();
                    score.setScoreId(rs.getInt("score_id"));
                    score.setUserId(rs.getInt("user_id"));
                    score.setHistoryId(rs.getInt("history_id"));
                    score.setTotalScore(rs.getInt("total_score"));
                    score.setDateTaken(rs.getTimestamp("date_taken"));
                    score.setSubjectId(rs.getInt("subject_id"));
                    score.setUsername(rs.getString("username"));
                    score.setSubjectName(rs.getString("subject_name"));
                    scores.add(score);
                }
            }
        }
        
        return scores;
    }
    
    // Get all scores
    public List<Score> getAllScores() throws SQLException {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT s.*, u.username, sb.subject_name " +
                     "FROM scores s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "JOIN subjects sb ON s.subject_id = sb.subject_id " +
                     "ORDER BY s.date_taken DESC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Score score = new Score();
                score.setScoreId(rs.getInt("score_id"));
                score.setUserId(rs.getInt("user_id"));
                score.setHistoryId(rs.getInt("history_id"));
                score.setTotalScore(rs.getInt("total_score"));
                score.setDateTaken(rs.getTimestamp("date_taken"));
                score.setSubjectId(rs.getInt("subject_id"));
                score.setUsername(rs.getString("username"));
                score.setSubjectName(rs.getString("subject_name"));
                scores.add(score);
            }
        }
        
        return scores;
    }
    
    // Get score by ID
    public Score getScoreById(int scoreId) throws SQLException {
        String sql = "SELECT s.*, u.username, sb.subject_name " +
                     "FROM scores s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "JOIN subjects sb ON s.subject_id = sb.subject_id " +
                     "WHERE s.score_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, scoreId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Score score = new Score();
                    score.setScoreId(rs.getInt("score_id"));
                    score.setUserId(rs.getInt("user_id"));
                    score.setHistoryId(rs.getInt("history_id"));
                    score.setTotalScore(rs.getInt("total_score"));
                    score.setDateTaken(rs.getTimestamp("date_taken"));
                    score.setSubjectId(rs.getInt("subject_id"));
                    score.setUsername(rs.getString("username"));
                    score.setSubjectName(rs.getString("subject_name"));
                    return score;
                }
            }
        }
        
        return null;
    }
    
    // Get chart data by user
    public List<Object[]> getChartDataByUser(int userId) throws SQLException {
        List<Object[]> chartData = new ArrayList<>();
        String sql = "SELECT sb.subject_name, AVG(s.total_score) as avg_score " +
                     "FROM scores s " +
                     "JOIN subjects sb ON s.subject_id = sb.subject_id " +
                     "WHERE s.user_id = ? " +
                     "GROUP BY sb.subject_id, sb.subject_name";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] data = new Object[2];
                    data[0] = rs.getString("subject_name");
                    data[1] = rs.getDouble("avg_score");
                    chartData.add(data);
                }
            }
        }
        
        return chartData;
    }
    
    // Get chart data by subject
    public List<Object[]> getChartDataBySubject(int subjectId) throws SQLException {
        List<Object[]> chartData = new ArrayList<>();
        String sql = "SELECT u.username, AVG(s.total_score) as avg_score " +
                     "FROM scores s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.subject_id = ? " +
                     "GROUP BY u.user_id, u.username";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] data = new Object[2];
                    data[0] = rs.getString("username");
                    data[1] = rs.getDouble("avg_score");
                    chartData.add(data);
                }
            }
        }
        
        return chartData;
    }
}
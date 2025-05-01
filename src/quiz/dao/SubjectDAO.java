package quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import quiz.model.Subject;
import quiz.util.DBUtil;

public class SubjectDAO {
    
    // Create a new subject
    public int createSubject(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (subject_name, description) VALUES (?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, subject.getSubjectName());
            pstmt.setString(2, subject.getDescription());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating subject failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    subject.setSubjectId(generatedKeys.getInt(1));
                    return subject.getSubjectId();
                } else {
                    throw new SQLException("Creating subject failed, no ID obtained.");
                }
            }
        }
    }
    
    // Get subject by ID
    public Subject getSubjectById(int subjectId) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE subject_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Subject subject = new Subject();
                    subject.setSubjectId(rs.getInt("subject_id"));
                    subject.setSubjectName(rs.getString("subject_name"));
                    subject.setDescription(rs.getString("description"));
                    return subject;
                }
            }
        }
        
        return null;
    }
    
    // Get all subjects
    public List<Subject> getAllSubjects() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Subject subject = new Subject();
                subject.setSubjectId(rs.getInt("subject_id"));
                subject.setSubjectName(rs.getString("subject_name"));
                subject.setDescription(rs.getString("description"));
                subjects.add(subject);
            }
        }
        
        return subjects;
    }
    
    // Update subject
    public boolean updateSubject(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET subject_name = ?, description = ? WHERE subject_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, subject.getSubjectName());
            pstmt.setString(2, subject.getDescription());
            pstmt.setInt(3, subject.getSubjectId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Delete subject
    public boolean deleteSubject(int subjectId) throws SQLException {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
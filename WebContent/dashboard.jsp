<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="quiz.model.Subject" %>
<%@ page import="quiz.model.Score" %>
<%@ page import="quiz.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard - Online Quiz System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="dashboard-container">
        <header>
            <h1>Online Quiz System</h1>
            <div class="user-info">
                <p>Welcome, ${sessionScope.username}!</p>
                <a href="logout" class="btn-logout">Logout</a>
            </div>
        </header>
        
        <% if(request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        
        <div class="dashboard-content">
            <div class="dashboard-section">
                <h2>Select a Subject to Start Quiz</h2>
                <div class="subject-list">
                    <% 
                    List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
                    if(subjects != null && !subjects.isEmpty()) {
                        for(Subject subject : subjects) {
                    %>
                        <div class="subject-card">
                            <h3><%= subject.getSubjectName() %></h3>
                            <p><%= subject.getDescription() %></p>
                            <a href="quiz?subjectId=<%= subject.getSubjectId() %>" class="btn-start-quiz">Start Quiz</a>
                        </div>
                    <% 
                        }
                    } else {
                    %>
                        <p>No subjects available. Please contact your teacher.</p>
                    <% } %>
                </div>
            </div>
            
            <div class="dashboard-section">
                <h2>Your Recent Scores</h2>
                <div class="score-list">
                    <% 
                    List<Score> userScores = (List<Score>) request.getAttribute("userScores");
                    if(userScores != null && !userScores.isEmpty()) {
                    %>
                        <table class="score-table">
                            <thead>
                                <tr>
                                    <th>Subject</th>
                                    <th>Score</th>
                                    <th>Date</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                for(Score score : userScores) {
                                %>
                                <tr>
                                    <td><%= score.getSubjectName() %></td>
                                    <td><%= score.getTotalScore() %></td>
                                    <td><%= score.getDateTaken() %></td>
                                    <td><a href="score?scoreId=<%= score.getScoreId() %>" class="btn-view">View Details</a></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                        <a href="score?action=viewAll" class="btn-view-all">View All Scores</a>
                    <% } else { %>
                        <p>You haven't taken any quizzes yet.</p>
                    <% } %>
                </div>
            </div>
            
            <% 
            // Additional section for teachers only
            User user = (User) session.getAttribute("user");
            if(user != null && user.isTeacher()) {
                List<Score> allScores = (List<Score>) request.getAttribute("allScores");
            %>
            <div class="dashboard-section teacher-section">
                <h2>Teacher Dashboard</h2>
                
                <div class="admin-actions">
                    <h3>Admin Actions</h3>
                    <ul>
                        <li><a href="question-management" class="btn-admin">Manage Questions</a></li>
                        <li><a href="subject-management" class="btn-admin">Manage Subjects</a></li>
                        <li><a href="user-management" class="btn-admin">Manage Users</a></li>
                    </ul>
                </div>
                
                <div class="all-scores">
                    <h3>Recent Student Scores</h3>
                    <% if(allScores != null && !allScores.isEmpty()) { %>
                        <table class="score-table">
                            <thead>
                                <tr>
                                    <th>Student</th>
                                    <th>Subject</th>
                                    <th>Score</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                for(Score score : allScores) {
                                %>
                                <tr>
                                    <td><%= score.getUsername() %></td>
                                    <td><%= score.getSubjectName() %></td>
                                    <td><%= score.getTotalScore() %></td>
                                    <td><%= score.getDateTaken() %></td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    <% } else { %>
                        <p>No student scores available.</p>
                    <% } %>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html>
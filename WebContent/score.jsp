<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="quiz.model.Score" %>
<%@ page import="quiz.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Scores - Online Quiz System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="scores-container">
        <header>
            <h1>${requestScope.title != null ? requestScope.title : 'Quiz Scores'}</h1>
            <div class="user-info">
                <p>User: ${sessionScope.username}</p>
                <a href="dashboard" class="btn-dashboard">Back to Dashboard</a>
                <a href="logout" class="btn-logout">Logout</a>
            </div>
        </header>
        
        <% if(request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        
        <div class="scores-content">
            <% 
            List<Score> scores = (List<Score>) request.getAttribute("scores");
            if(scores != null && !scores.isEmpty()) {
            %>
            <div class="score-table-container">
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
                        for(Score score : scores) {
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
            </div>
            <% } else { %>
            <div class="no-scores">
                <p>No scores available.</p>
            </div>
            <% } %>
            
            <% 
            // Additional section for teachers only
            User user = (User) session.getAttribute("user");
            if(user != null && user.isTeacher()) {
                List<Score> allScores = (List<Score>) request.getAttribute("allScores");
                if(allScores != null && !allScores.isEmpty()) {
            %>
            <div class="teacher-scores-section">
                <h2>All Student Scores</h2>
                <div class="score-table-container">
                    <table class="score-table">
                        <thead>
                            <tr>
                                <th>Student</th>
                                <th>Subject</th>
                                <th>Score</th>
                                <th>Date</th>
                                <th>Action</th>
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
                                <td><a href="score?scoreId=<%= score.getScoreId() %>" class="btn-view">View Details</a></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
            <% 
                }
            } 
            %>
        </div>
    </div>
</body>
</html>
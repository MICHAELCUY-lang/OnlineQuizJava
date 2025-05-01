<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="quiz.model.Score" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quiz Results - Online Quiz System</title>
    <link rel="stylesheet" href="css/style.css">
    <script src="js/chart.js"></script>
</head>
<body>
    <div class="result-container">
        <header>
            <h1>Quiz Results</h1>
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
        
        <div class="result-content">
            <% 
            Score score = (Score) request.getAttribute("score");
            if(score != null) {
            %>
            <div class="result-summary">
                <h2>Your Quiz Summary</h2>
                <div class="summary-details">
                    <div class="summary-item">
                        <span class="label">Subject:</span>
                        <span class="value"><%= score.getSubjectName() %></span>
                    </div>
                    <div class="summary-item">
                        <span class="label">Score:</span>
                        <span class="value"><%= score.getTotalScore() %></span>
                    </div>
                    <div class="summary-item">
                        <span class="label">Date Taken:</span>
                        <span class="value"><%= score.getDateTaken() %></span>
                    </div>
                </div>
            </div>
            
            <div class="charts-section">
                <div class="chart-container">
                    <h3>Your Performance by Subject</h3>
                    <div id="userPerformanceChart" class="chart"></div>
                </div>
                
                <div class="chart-container">
                    <h3>Subject Performance Comparison</h3>
                    <div id="subjectComparisonChart" class="chart"></div>
                </div>
            </div>
            
            <div class="action-buttons">
                <a href="quiz?subjectId=<%= score.getSubjectId() %>" class="btn-retry">Try Again</a>
                <a href="score?action=viewAll" class="btn-view-all">View All Your Scores</a>
            </div>
            
            <% 
            // Prepare data for charts
            List<Object[]> userChartData = (List<Object[]>) request.getAttribute("userChartData");
            List<Object[]> subjectChartData = (List<Object[]>) request.getAttribute("subjectChartData");
            
            // Convert data to JSON for JavaScript charts
            StringBuilder userDataJson = new StringBuilder("[");
            if(userChartData != null && !userChartData.isEmpty()) {
                for(int i = 0; i < userChartData.size(); i++) {
                    Object[] data = userChartData.get(i);
                    userDataJson.append("{\"label\":\"").append(data[0]).append("\",\"value\":").append(data[1]).append("}");
                    if(i < userChartData.size() - 1) {
                        userDataJson.append(",");
                    }
                }
            }
            userDataJson.append("]");
            
            StringBuilder subjectDataJson = new StringBuilder("[");
            if(subjectChartData != null && !subjectChartData.isEmpty()) {
                for(int i = 0; i < subjectChartData.size(); i++) {
                    Object[] data = subjectChartData.get(i);
                    subjectDataJson.append("{\"label\":\"").append(data[0]).append("\",\"value\":").append(data[1]).append("}");
                    if(i < subjectChartData.size() - 1) {
                        subjectDataJson.append(",");
                    }
                }
            }
            subjectDataJson.append("]");
            %>
            
            <script>
                // Initialize charts when DOM is ready
                document.addEventListener('DOMContentLoaded', function() {
                    // User performance chart (pie chart)
                    const userData = <%= userDataJson.toString() %>;
                    createPieChart('userPerformanceChart', userData, 'Your Performance by Subject');
                    
                    // Subject comparison chart (bar chart)
                    const subjectData = <%= subjectDataJson.toString() %>;
                    createBarChart('subjectComparisonChart', subjectData, 'Subject Performance Comparison');
                });
            </script>
            
            <% } else { %>
            <div class="no-result">
                <p>No result data available.</p>
                <a href="dashboard" class="btn-primary">Return to Dashboard</a>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html>
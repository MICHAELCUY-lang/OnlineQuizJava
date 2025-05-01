<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="quiz.model.Question" %>
<%@ page import="quiz.model.QuizOption" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quiz - Online Quiz System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="quiz-container">
        <header>
            <h1>${sessionScope.subjectName} Quiz</h1>
            <div class="quiz-info">
                <p>Subject: ${sessionScope.subjectName}</p>
                <p>User: ${sessionScope.username}</p>
            </div>
        </header>
        
        <% if(request.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        
        <div class="quiz-content">
            <%
            List<Question> questions = (List<Question>) session.getAttribute("quizQuestions");
            Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
            
            if(questions != null && currentIndex != null && currentIndex < questions.size()) {
                Question currentQuestion = questions.get(currentIndex);
            %>
            <div class="question-card">
                <div class="question-header">
                    <span class="question-number">Question <%= currentIndex + 1 %> of <%= questions.size() %></span>
                </div>
                
                <div class="question-text">
                    <h3><%= currentQuestion.getQuestionText() %></h3>
                </div>
                
                <form action="quiz" method="post" class="options-form">
                    <div class="options-list">
                        <% 
                        List<QuizOption> options = currentQuestion.getOptions();
                        if(options != null && !options.isEmpty()) {
                            for(QuizOption option : options) {
                        %>
                        <div class="option">
                            <input type="radio" name="optionId" id="option<%= option.getQuizId() %>" 
                                   value="<%= option.getQuizId() %>" required>
                            <label for="option<%= option.getQuizId() %>"><%= option.getOptionText() %></label>
                        </div>
                        <% 
                            }
                        } else {
                        %>
                        <p>No options available for this question.</p>
                        <% } %>
                    </div>
                    
                    <div class="form-actions">
                        <input type="submit" value="Next" class="btn-primary">
                    </div>
                </form>
            </div>
            <% } else { %>
            <div class="quiz-complete">
                <h2>Quiz Complete!</h2>
                <p>All questions have been answered.</p>
                <a href="dashboard" class="btn-primary">Return to Dashboard</a>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html>
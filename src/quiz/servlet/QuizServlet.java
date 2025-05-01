package quiz.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import quiz.dao.QuestionDAO;
import quiz.dao.QuizDAO;
import quiz.dao.ScoreDAO;
import quiz.dao.SubjectDAO;
import quiz.model.Question;
import quiz.model.QuizOption;
import quiz.model.Score;
import quiz.model.Subject;
import quiz.model.User;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO = new QuestionDAO();
    private SubjectDAO subjectDAO = new SubjectDAO();
    private ScoreDAO scoreDAO = new ScoreDAO();
    private QuizDAO quizDAO = new QuizDAO();
    
    public QuizServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // Redirect to login if not logged in
            response.sendRedirect("login");
            return;
        }
        
        // Get subject ID from request
        String subjectIdParam = request.getParameter("subjectId");
        
        // If subjectId is not provided, redirect to dashboard to select a subject
        if (subjectIdParam == null || subjectIdParam.trim().isEmpty()) {
            response.sendRedirect("dashboard");
            return;
        }
        
        try {
            int subjectId = Integer.parseInt(subjectIdParam);
            
            // Get subject details
            Subject subject = subjectDAO.getSubjectById(subjectId);
            if (subject == null) {
                response.sendRedirect("dashboard");
                return;
            }
            
            // Get random questions for the subject (limit to 10 questions for the quiz)
            List<Question> questions = questionDAO.getRandomQuestionsBySubject(subjectId, 10);
            
            // If no questions available, redirect back with a message
            if (questions == null || questions.isEmpty()) {
                request.setAttribute("errorMessage", "No questions available for this subject.");
                request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
                return;
            }
            
            // Shuffle the order of questions
            Collections.shuffle(questions);
            
            // Store quiz data in session
            session.setAttribute("quizQuestions", questions);
            session.setAttribute("currentQuestionIndex", 0);
            session.setAttribute("subjectId", subjectId);
            session.setAttribute("subjectName", subject.getSubjectName());
            
            // Create a new quiz history record
            User user = (User) session.getAttribute("user");
            int historyId = scoreDAO.createQuizHistory(user.getUserId());
            session.setAttribute("historyId", historyId);
            
            // Initialize score tracking
            session.setAttribute("correctAnswers", 0);
            
            // Forward to the quiz page with the first question
            request.getRequestDispatcher("/quiz.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while starting the quiz: " + e.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // Redirect to login if not logged in
            response.sendRedirect("login");
            return;
        }
        
        try {
            // Get quiz data from session
            @SuppressWarnings("unchecked")
            List<Question> questions = (List<Question>) session.getAttribute("quizQuestions");
            Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
            Integer correctAnswers = (Integer) session.getAttribute("correctAnswers");
            Integer historyId = (Integer) session.getAttribute("historyId");
            Integer subjectId = (Integer) session.getAttribute("subjectId");
            
            // Check if quiz is initialized
            if (questions == null || currentIndex == null || correctAnswers == null || historyId == null) {
                response.sendRedirect("dashboard");
                return;
            }
            
            // Get the current question
            Question currentQuestion = questions.get(currentIndex);
            
            // Process user's answer
            String selectedOptionIdParam = request.getParameter("optionId");
            
            if (selectedOptionIdParam != null && !selectedOptionIdParam.trim().isEmpty()) {
                int selectedOptionId = Integer.parseInt(selectedOptionIdParam);
                
                // Check if the selected option is correct
                QuizOption selectedOption = quizDAO.getOptionById(selectedOptionId);
                boolean isCorrect = selectedOption != null && selectedOption.isCorrect();
                
                // Record the answer
                scoreDAO.recordQuizAnswer(historyId, currentQuestion.getQuestionId(), selectedOptionId, isCorrect);
                
                // Update score if correct
                if (isCorrect) {
                    correctAnswers++;
                    session.setAttribute("correctAnswers", correctAnswers);
                }
            }
            
            // Move to the next question
            currentIndex++;
            session.setAttribute("currentQuestionIndex", currentIndex);
            
            // Check if quiz is complete
            if (currentIndex >= questions.size()) {
                // Update end time for the quiz
                scoreDAO.updateQuizHistoryEndTime(historyId);
                
                // Calculate final score
                int totalQuestions = questions.size();
                int finalScore = correctAnswers;
                
                // Save score to database
                User user = (User) session.getAttribute("user");
                Score score = new Score();
                score.setUserId(user.getUserId());
                score.setHistoryId(historyId);
                score.setTotalScore(finalScore);
                score.setSubjectId(subjectId);
                
                int scoreId = scoreDAO.createScore(score);
                
                // Redirect to results page
                response.sendRedirect("score?scoreId=" + scoreId);
            } else {
                // Continue with the next question
                request.getRequestDispatcher("/quiz.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing the quiz: " + e.getMessage());
            request.getRequestDispatcher("/quiz.jsp").forward(request, response);
        }
    }
}
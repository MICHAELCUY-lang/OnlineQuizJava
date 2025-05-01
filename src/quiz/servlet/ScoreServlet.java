package quiz.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import quiz.dao.ScoreDAO;
import quiz.model.Score;
import quiz.model.User;

@WebServlet("/score")
public class ScoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ScoreDAO scoreDAO = new ScoreDAO();
    
    public ScoreServlet() {
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
        
        try {
            User user = (User) session.getAttribute("user");
            
            // Handle different types of score requests
            String action = request.getParameter("action");
            
            if (action != null && action.equals("viewAll")) {
                // View all scores for the current user
                List<Score> userScores = scoreDAO.getScoresByUserId(user.getUserId());
                request.setAttribute("scores", userScores);
                request.setAttribute("title", "All Your Scores");
                
                // For teachers, optionally show all scores
                if (user.isTeacher() && request.getParameter("showAll") != null) {
                    List<Score> allScores = scoreDAO.getAllScores();
                    request.setAttribute("allScores", allScores);
                }
                
                request.getRequestDispatcher("/score.jsp").forward(request, response);
                return;
            } else if (action != null && action.equals("viewBySubject")) {
                // View scores by subject
                String subjectIdParam = request.getParameter("subjectId");
                if (subjectIdParam != null && !subjectIdParam.trim().isEmpty()) {
                    int subjectId = Integer.parseInt(subjectIdParam);
                    List<Score> subjectScores = scoreDAO.getScoresBySubject(subjectId);
                    request.setAttribute("scores", subjectScores);
                    request.setAttribute("title", "Scores for " + subjectScores.get(0).getSubjectName());
                    request.getRequestDispatcher("/score.jsp").forward(request, response);
                    return;
                }
            } else {
                // View a specific score
                String scoreIdParam = request.getParameter("scoreId");
                if (scoreIdParam != null && !scoreIdParam.trim().isEmpty()) {
                    int scoreId = Integer.parseInt(scoreIdParam);
                    Score score = scoreDAO.getScoreById(scoreId);
                    
                    if (score != null) {
                        request.setAttribute("score", score);
                        
                        // Get chart data for this user and subject
                        List<Object[]> userChartData = scoreDAO.getChartDataByUser(user.getUserId());
                        List<Object[]> subjectChartData = scoreDAO.getChartDataBySubject(score.getSubjectId());
                        
                        request.setAttribute("userChartData", userChartData);
                        request.setAttribute("subjectChartData", subjectChartData);
                        
                        request.getRequestDispatcher("/result.jsp").forward(request, response);
                        return;
                    }
                }
            }
            
            // Default: redirect to dashboard if no valid action
            response.sendRedirect("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while retrieving scores: " + e.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
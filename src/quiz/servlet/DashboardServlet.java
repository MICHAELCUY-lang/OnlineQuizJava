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
import quiz.dao.SubjectDAO;
import quiz.model.Score;
import quiz.model.Subject;
import quiz.model.User;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SubjectDAO subjectDAO = new SubjectDAO();
    private ScoreDAO scoreDAO = new ScoreDAO();
    
    public DashboardServlet() {
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
            // Get current user from session
            User user = (User) session.getAttribute("user");
            
            // Get all subjects
            List<Subject> subjects = subjectDAO.getAllSubjects();
            request.setAttribute("subjects", subjects);
            
            // Get user's recent scores
            List<Score> userScores = scoreDAO.getScoresByUserId(user.getUserId());
            request.setAttribute("userScores", userScores);
            
            // For teachers, get all scores for analysis
            if (user.isTeacher()) {
                List<Score> allScores = scoreDAO.getAllScores();
                request.setAttribute("allScores", allScores);
            }
            
            // Forward to dashboard page
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading the dashboard: " + e.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
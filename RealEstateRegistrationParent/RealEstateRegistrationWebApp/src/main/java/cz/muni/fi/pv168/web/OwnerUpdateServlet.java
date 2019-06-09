package cz.muni.fi.pv168.web;

import com.sun.deploy.net.HttpResponse;
import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.managers.OwnerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(OwnerUpdateServlet.URL_MAPPING + "/*")
public class OwnerUpdateServlet extends HttpServlet {
    private static final String LIST_JSP = "/ownerEdit.jsp";
    public static final String URL_MAPPING = "/ownerEdit";

    private final static Logger log = LoggerFactory.getLogger(OwnerUpdateServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showOwnerData(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //support non-ASCII characters in form
        request.setCharacterEncoding("utf-8");
        //action specified by pathInfo
        String action = request.getPathInfo();
        log.debug("POST ... {}", action);

        switch (action) {
            case "/update":
                Owner owner = getOwnerManager().retrieveOwnerById(Long.parseLong(request.getParameter("id")));
                String nameUpdate = request.getParameter("name");
                String idCardOrCorpNumberUpdate = request.getParameter("idCardOrCorpNumber");
                String isCorpUpdate = request.getParameter("isCorp");
                owner.setName(nameUpdate);
                owner.setIdCardOrCorpNumber(idCardOrCorpNumberUpdate);
                getOwnerManager().updateOwner(owner);
                //redirect-after-POST protects from multiple submission
                log.debug("Redirecting after POST");
                response.sendRedirect(request.getContextPath()+OwnerServlet.URL_MAPPING);
                return;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private OwnerManager getOwnerManager() {
        return (OwnerManager) getServletContext().getAttribute("OwnerManager");
    }

    private void showOwnerData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.debug("Showing table of owners");
            Owner o = getOwnerManager().retrieveOwnerById(Long.parseLong(request.getParameter("id")));
            request.setAttribute("owner", o);
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception ex) {
            log.error("Cannot show owners", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}

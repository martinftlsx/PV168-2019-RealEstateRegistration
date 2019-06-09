package cz.muni.fi.pv168.web;

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

@WebServlet(OwnerServlet.URL_MAPPING + "/*")
public class OwnerServlet extends HttpServlet {
    private static final String LIST_JSP = "/ownersList.jsp";
    public static final String URL_MAPPING = "/owners";

    private final static Logger log = LoggerFactory.getLogger(OwnerServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET ...");
        showOwnersList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //support non-ASCII characters in form
        request.setCharacterEncoding("utf-8");
        //action specified by pathInfo
        String action = request.getPathInfo();
        log.debug("POST ... {}", action);

        switch (action) {
            case "/add":
                //getting POST parameters from form
                String name = request.getParameter("name");
                String idCardOrCorpNumber = request.getParameter("idCardOrCorpNumber");
                String isCorp = request.getParameter("isCorp");
                //form data validity check
                if (name == null || name.isEmpty() || idCardOrCorpNumber == null || idCardOrCorpNumber.isEmpty()
                        || isCorp == null || isCorp.isEmpty() || (!isCorp.equals("Yes") && !isCorp.equals("No"))) {
                    request.setAttribute("Error", "All values must be filled");
                    log.debug("Form data invalid");
                    showOwnersList(request, response);
                    return;
                }
                //form data processing - storing to database
                try {
                    Owner owner = new Owner();
                    owner.setName(name);
                    owner.setIdCardOrCorpNumber(idCardOrCorpNumber);
                    if (isCorp.equals("Yes")) owner.setCorp(Boolean.TRUE);
                    else owner.setCorp(Boolean.FALSE);
                    getOwnerManager().createOwner(owner);
                    //redirect-after-POST protects from multiple submission
                    log.debug("Redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception ex) {
                    log.error("Cannot add owner", ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Owner owner = new Owner();
                    owner.setId(Long.valueOf(request.getParameter("id")));
                    getOwnerManager().deleteOwner(owner);
                    log.debug("Redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception ex) {
                    log.error("Cannot delete owner", ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
            case "/update":
                log.debug("Redirecting after POST");
                response.sendRedirect(request.getContextPath()+OwnerUpdateServlet.URL_MAPPING+"/ownerEdit?id="+request.getParameter("id"));
                break;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private OwnerManager getOwnerManager() {
        return (OwnerManager) getServletContext().getAttribute("OwnerManager");
    }

    private void showOwnersList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.debug("Showing table of owners");
            request.setAttribute("owners", getOwnerManager().retrieveAllOwners());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception ex) {
            log.error("Cannot show owners", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}

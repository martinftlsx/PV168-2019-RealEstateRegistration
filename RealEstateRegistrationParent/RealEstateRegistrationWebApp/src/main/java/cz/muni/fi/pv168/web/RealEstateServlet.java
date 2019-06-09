package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.entities.RealEstate;
import cz.muni.fi.pv168.managers.RealEstateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(RealEstateServlet.URL_MAPPING + "/*")
public class RealEstateServlet extends HttpServlet {
    private static final String LIST_JSP = "/realEstatesList.jsp";
    public static final String URL_MAPPING = "/realEstates";

    private final static Logger log = LoggerFactory.getLogger(RealEstateServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET ...");
        showRealEstatesList(request, response);
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
                String cadastralArea = request.getParameter("cadastralArea");
                String parcelNumber = request.getParameter("parcelNumber");
                String areaInMetersSquared = request.getParameter("areaInMetersSquared");
                //form data validity check
                if (cadastralArea == null || cadastralArea.isEmpty() || parcelNumber == null || parcelNumber.isEmpty()
                        || areaInMetersSquared == null || areaInMetersSquared.isEmpty()) {
                    request.setAttribute("Error", "All values must be filled");
                    log.debug("Form data invalid");
                    showRealEstatesList(request, response);
                    return;
                }
                try {
                    Double.parseDouble(areaInMetersSquared);
                } catch (NumberFormatException ex) {
                    request.setAttribute("Error", "Wrong number format");
                    log.debug("Parsing area failed");
                    showRealEstatesList(request, response);
                    return;
                }
                //form data processing - storing to database
                try {
                    RealEstate realEstate = new RealEstate();
                    realEstate.setCadastralArea(cadastralArea);
                    realEstate.setParcelNumber(parcelNumber);
                    realEstate.setAreaInMetersSquared(Double.parseDouble(areaInMetersSquared));
                    getRealEstateManager().createRealEstate(realEstate);
                    //redirect-after-POST protects from multiple submission
                    log.debug("Redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception ex) {
                    log.error("Cannot add real estate", ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
            case "/delete":
                try {
                    RealEstate realEstate = new RealEstate();
                    realEstate.setId(Long.valueOf(request.getParameter("id")));
                    getRealEstateManager().deleteRealEstate(realEstate);
                    log.debug("Redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception ex) {
                    log.error("Cannot delete real estate", ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private RealEstateManager getRealEstateManager() {
        return (RealEstateManager) getServletContext().getAttribute("RealEstateManager");
    }

    private void showRealEstatesList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.debug("Showing table of real estates");
            request.setAttribute("realEstates", getRealEstateManager().retrieveAllRealEstates());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception ex) {
            log.error("Cannot show real estates", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

}

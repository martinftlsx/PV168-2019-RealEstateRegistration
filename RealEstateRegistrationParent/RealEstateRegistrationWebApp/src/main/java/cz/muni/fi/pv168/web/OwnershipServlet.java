package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.entities.Ownership;
import cz.muni.fi.pv168.entities.RealEstate;
import cz.muni.fi.pv168.managers.OwnerManager;
import cz.muni.fi.pv168.managers.OwnershipManager;
import cz.muni.fi.pv168.managers.RealEstateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(OwnershipServlet.URL_MAPPING + "/*")
public class OwnershipServlet extends HttpServlet {
    private static final String LIST_JSP = "/ownershipList.jsp";
    public static final String URL_MAPPING = "/ownerships";

    private final static Logger log = LoggerFactory.getLogger(OwnershipServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET ...");
        showOwnershipsList(request, response);
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
                String realEstate = request.getParameter("realEstate");
                String owner = request.getParameter("owner");
                String share = request.getParameter("share");
                //form data validity check
                if (realEstate == null || realEstate.isEmpty() || owner == null || owner.isEmpty()
                        || share == null || share.isEmpty()) {
                    request.setAttribute("Error", "All values must be filled");
                    log.debug("Form data invalid");
                    showOwnershipsList(request, response);
                    return;
                }
                String[] numDenum = share.split("/");
                if (numDenum.length != 2) {
                    request.setAttribute("Error", "Share format is invalid. Should be x/y.");
                    log.debug("Form data invalid");
                    showOwnershipsList(request, response);
                    return;
                }
                //form data processing - storing to database
                try {
                    Ownership ownership = new Ownership();
                    Owner o = new Owner();
                    RealEstate r = new RealEstate();
                    o.setId(Long.parseLong(owner));
                    r.setId(Long.parseLong(realEstate));
                    ownership.setOwner(o);
                    ownership.setRealEstate(r);
                    Integer numerator = Integer.parseInt(numDenum[0]);
                    Integer denumerator = Integer.parseInt(numDenum[1]);
                    ownership.setShareNumerator(numerator);
                    ownership.setShareDenominator(denumerator);
                    ownership.setOwnershipCreated(ZonedDateTime.now());
                    getOwnershipManager().createOwnership(ownership);
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
                    Ownership ownership = new Ownership();
                    ownership.setId(Long.valueOf(request.getParameter("id")));
                    getOwnershipManager().deleteOwnership(ownership);
                    log.debug("Redirecting after POST");
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception ex) {
                    log.debug("Redirecting after POST");
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private OwnershipManager getOwnershipManager() {
        return (OwnershipManager) getServletContext().getAttribute("OwnershipManager");
    }

    private OwnerManager getOwnerManager() {
        return (OwnerManager) getServletContext().getAttribute("OwnerManager");
    }

    private RealEstateManager getRealEstateManager() {
        return (RealEstateManager) getServletContext().getAttribute("RealEstateManager");
    }

    private void showOwnershipsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.debug("Showing table of ownerships");
            List<Ownership> ownerships = getOwnershipManager().retrieveAllOwnerships();
            List<OwnershipModel> ownershipModels = new ArrayList<OwnershipModel>();
            for (Ownership o : ownerships) {
                OwnershipModel ownershipModel = new OwnershipModel();
                ownershipModel.setOwnershipCreated(o.getOwnershipCreated().toString());
                if (o.getOwnershipRemoved() != null) ownershipModel.setOwnershipRemoved(o.getOwnershipRemoved().toString());
                else ownershipModel.setOwnershipRemoved("-");
                ownershipModel.setOwnerName(o.getOwner().getName());
                ownershipModel.setOwnerIdCardOrCorpNumber(o.getOwner().getIdCardOrCorpNumber());
                ownershipModel.setCadastralArea(o.getRealEstate().getCadastralArea());
                ownershipModel.setPareclNumber(o.getRealEstate().getParcelNumber());
                ownershipModel.setShare(o.getShareNumerator().toString() + "/" + o.getShareDenominator().toString());
                ownershipModel.setId(o.getId());
                ownershipModels.add(ownershipModel);
            }
            request.setAttribute("ownershipModels", ownershipModels);
            request.setAttribute("owners", getOwnerManager().retrieveAllOwners());
            request.setAttribute("realEstates", getRealEstateManager().retrieveAllRealEstates());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception ex) {
            log.error("Cannot show ownerships", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}

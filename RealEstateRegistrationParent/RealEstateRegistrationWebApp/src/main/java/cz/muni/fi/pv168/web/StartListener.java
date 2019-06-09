package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.Main;
import cz.muni.fi.pv168.managers.OwnerManagerImpl;
import cz.muni.fi.pv168.managers.OwnershipManagerImpl;
import cz.muni.fi.pv168.managers.RealEstateManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.IOException;

@WebListener
public class StartListener implements ServletContextListener {

    private final static Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("Webova aplikace inicializovana");
        ServletContext servletContext = servletContextEvent.getServletContext();
        try {
            DataSource dataSource = Main.createDataSource();
            servletContext.setAttribute("OwnerManager", new OwnerManagerImpl(dataSource));
            servletContext.setAttribute("RealEstateManager", new RealEstateManagerImpl(dataSource));
            servletContext.setAttribute("OwnershipManager", new OwnershipManagerImpl(dataSource));
            log.info("Managery vytvorene a ulozene do atributov servletContextu");
        } catch (IOException ex) {
            log.error("Nepodarilo sa vytvorit databazu", ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("Aplikacia konci.");
    }
}

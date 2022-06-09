package com.revature.ers.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.daos.UsersDAO;
import com.revature.ers.services.UsersServices;
import com.revature.ers.servlets.AuthServlet;
import com.revature.ers.servlets.UserServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;


/* Need this ContextLoaderListener for our dependency injection upon deployment. */
public class ContextLoaderListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("\nInitializing Ers web application");
        /* ObjectMapper provides functionality for reading and writing JSON, either to and from basic POJOs (Plain Old Java Objects) */
        ObjectMapper mapper = new ObjectMapper();
        /* Dependency injection. */
        UserServlet userServlet = new UserServlet(mapper, new UsersServices(new UsersDAO()));
        AuthServlet authServlet = new AuthServlet(mapper, new UsersServices(new UsersDAO()));
        /* Need ServletContext class to map whatever servlet to url path. */
        ServletContext context = sce.getServletContext();
        context.addServlet("UserServlet", userServlet).addMapping("/users/*");
        context.addServlet("AuthServlet", authServlet).addMapping("/auth/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("\nShutting down Ers web application");
    }
}
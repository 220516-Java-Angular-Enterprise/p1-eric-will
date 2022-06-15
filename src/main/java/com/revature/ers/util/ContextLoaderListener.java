package com.revature.ers.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.daos.ReimbursementsDAO;
import com.revature.ers.daos.UsersDAO;
import com.revature.ers.services.ReimbursementsServices;
import com.revature.ers.services.TokenServices;
import com.revature.ers.services.UsersServices;
import com.revature.ers.servlets.AuthServlet;
import com.revature.ers.servlets.ReimbursementServlet;
import com.revature.ers.servlets.UserServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/* Need this ContextLoaderListener for our dependency injection upon deployment. */
public class ContextLoaderListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("\nInitializing Ers web application");
        /* ObjectMapper provides functionality for reading and writing JSON, either to and from basic POJOs (Plain Old Java Objects) */
        ObjectMapper mapper = new ObjectMapper();
        /* Dependency injection. */
        UserServlet userServlet = new UserServlet(mapper, new UsersServices(new UsersDAO()), new TokenServices(new JwtConfig()));
        AuthServlet authServlet = new AuthServlet(mapper, new UsersServices(new UsersDAO()), new TokenServices(new JwtConfig()));
        ReimbursementServlet reimbServlet = new ReimbursementServlet(mapper, new ReimbursementsServices(new ReimbursementsDAO()), new TokenServices(new JwtConfig()));
        /* Need ServletContext class to map whatever servlet to url path. */
        ServletContext context = sce.getServletContext();
        context.addServlet("UserServlet", userServlet).addMapping("/users/*");
        context.addServlet("AuthServlet", authServlet).addMapping("/auth/*");
        context.addServlet("ReimbServlet", reimbServlet).addMapping("/reimb/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("\nShutting down Ers web application");
    }
}
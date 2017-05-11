package com.dms.planb.action.account.login;

import java.sql.SQLException;

import org.boxfox.dms.util.AdminManager;
import org.boxfox.dms.util.Guardian;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.log.Log;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path = "/account/login/admin", method = {HttpMethod.POST})
public class LoginAdminRequest implements Handler<RoutingContext> {
    private AdminManager adminManager;

    public LoginAdminRequest() {
        adminManager = new AdminManager();
    }

    @Override
    public void handle(RoutingContext context) {

        String id = context.request().getParam("id");
        String password = context.request().getParam("password");
        String remember = context.request().getParam("remember");
        remember = (remember == null) ? "false" : "true";
        
        if(!Guardian.checkParameters(id, password)) {
        	context.response().setStatusCode(400).end();
        	context.response().close();
        	return;
        }
        
        try {
            boolean check = adminManager.login(id, password);
            if (check) {
            	adminManager.registerSession(context, Boolean.valueOf(remember), id);
            	
            	context.response().setStatusCode(201).end();
                context.response().close();
            } else {
                context.response().setStatusCode(400).end();
                context.response().close();
            }
        } catch (SQLException e) {
            context.response().setStatusCode(500).end();
            context.response().close();

            Log.l("SQLException");
        }
        Log.l("Login Request (", id, ", ", context.request().remoteAddress(), ") status : " + context.response().getStatusCode());
    }
}

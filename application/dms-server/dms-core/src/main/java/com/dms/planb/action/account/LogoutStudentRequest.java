package com.dms.planb.action.account;

import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.actions.support.PrecedingWork;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path = "/account/logout/student", method = {HttpMethod.POST})
public class LogoutStudentRequest implements Handler<RoutingContext> {
    private UserManager userManager;

    public LogoutStudentRequest() {
        userManager = new UserManager();
    }

    @Override
    public void handle(RoutingContext context) {
        context = PrecedingWork.putHeaders(context);
        
        userManager.removeCookie(context);
        
        context.response().setStatusCode(201).end();
        context.response().close();
    }
}

package com.dms.planb.action.account;

import org.boxfox.dms.util.Guardian;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;


import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path="/account/idcheck/student", method={ HttpMethod.POST })
public class StudentIdCheck implements Handler<RoutingContext> {
	private UserManager userManager;
	
	public StudentIdCheck(){
		userManager = new UserManager();
	}

	@Override
	public void handle(RoutingContext ctx) {

		String id = ctx.request().getParam("id");
		
		if(!Guardian.checkParameters(id)) {
        	ctx.response().setStatusCode(400).end();
        	ctx.response().close();
        	return;
        }

		if (userManager.checkIdExists(id)) {
			ctx.response().setStatusCode(409).end();
			ctx.response().close();
		} else {
			ctx.response().setStatusCode(201).end();
			ctx.response().close();
		}
	}
}

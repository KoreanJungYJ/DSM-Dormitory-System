package com.dms.planb.action.merit;

import java.sql.SQLException;

import org.boxfox.dms.util.Guardian;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.database.DataBase;
import org.boxfox.dms.utilities.log.Log;


import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path="/apply/merit", method={HttpMethod.POST})
public class ApplyMerit implements Handler<RoutingContext> {
	private UserManager userManager;
	
	public ApplyMerit() {
		userManager = new UserManager();
	}
	
	@Override
	public void handle(RoutingContext ctx) {

		DataBase database = DataBase.getInstance();
		
		String id = userManager.getIdFromSession(ctx);
        String uid = null;
        
        try {
            if (id != null) {
                uid = userManager.getUid(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
		String content = ctx.request().getParam("content");
		
		if(!Guardian.checkParameters(id, uid, content)) {
            ctx.response().setStatusCode(400).end();
            ctx.response().close();
        	return;
        }
		
		try {
			if(ctx.request().params().contains("target")) {
				String target = ctx.request().getParam("target");
				
				database.executeUpdate("INSERT INTO merit_apply(uid, target, content) VALUES('", uid, "', '", target, "', '", content, "')");
			} else {
				database.executeUpdate("INSERT INTO merit_apply(uid, content) VALUES('", uid, "', '", content, "')");
			}
			
			ctx.response().setStatusCode(201).end();
			ctx.response().close();
		} catch(SQLException e) {
			ctx.response().setStatusCode(500).end();
			ctx.response().close();
			
			Log.l("SQLException");
		}
	}
}

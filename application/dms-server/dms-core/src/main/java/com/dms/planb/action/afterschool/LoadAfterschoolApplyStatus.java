package com.dms.planb.action.afterschool;

import java.sql.SQLException;

import org.boxfox.dms.util.Guardian;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.database.DataBase;
import org.boxfox.dms.utilities.database.SafeResultSet;
import org.boxfox.dms.utilities.json.EasyJsonArray;
import org.boxfox.dms.utilities.json.EasyJsonObject;
import org.boxfox.dms.utilities.log.Log;


import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path="/apply/afterschool", method={HttpMethod.GET})
public class LoadAfterschoolApplyStatus implements Handler<RoutingContext> {
	private UserManager userManager;
	
	public LoadAfterschoolApplyStatus() {
		userManager = new UserManager();
	}
	
	@Override
	public void handle(RoutingContext ctx) {

		DataBase database = DataBase.getInstance();
		SafeResultSet resultSet;
		EasyJsonObject responseObject = new EasyJsonObject();
		EasyJsonObject tempObject = new EasyJsonObject();
		EasyJsonArray tempArray = new EasyJsonArray();
		
		String id = userManager.getIdFromSession(ctx);
        String uid = null;
        
        try {
            if (id != null) {
                uid = userManager.getUid(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if(!Guardian.checkParameters(id, uid)) {
        	ctx.response().setStatusCode(400).end();
        	ctx.response().close();
        	return;
        }
		
		try {
			resultSet = database.executeQuery("SELECT no FROM afterschool_apply WHERE uid='", uid, "'");
			
			if(resultSet.next()) {
				do {
					tempObject = new EasyJsonObject();
					
					int no = resultSet.getInt("no");
					SafeResultSet tempResultSet = database.executeQuery("SELECT on_monday, on_tuesday, on_wednesday, on_saturday FROM afterschool_list WHERE no=", no);
					
					tempObject.put("no", resultSet.getInt("no"));
					tempObject.put("on_monday", tempResultSet.getBoolean("on_monday"));
					tempObject.put("on_tuesday", tempResultSet.getBoolean("on_tuesday"));
					tempObject.put("on_wednesday", tempResultSet.getBoolean("on_wednesday"));
					tempObject.put("on_saturday", tempResultSet.getBoolean("on_saturday"));
					
					tempArray.add(tempObject);
				} while(resultSet.next());
				
				responseObject.put("result", tempArray);
				
				ctx.response().setStatusCode(200);
				ctx.response().end(responseObject.toString());
				ctx.response().close();
			} else {
				ctx.response().setStatusCode(204).end();
				ctx.response().close();
			}
		} catch(SQLException e) {
			ctx.response().setStatusCode(500).end();
			ctx.response().close();
			
			Log.l("SQLException");
		}
	}
}

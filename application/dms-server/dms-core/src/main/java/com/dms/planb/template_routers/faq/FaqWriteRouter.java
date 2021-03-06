package com.dms.planb.template_routers.faq;

import java.io.IOException;

import org.boxfox.dms.util.AdminManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.log.Log;

import com.dms.boxfox.templates.DmsTemplate;

import freemarker.template.TemplateException;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

@RouteRegistration(path="/post/faq/write", method={HttpMethod.GET})
public class FaqWriteRouter implements Handler<RoutingContext> {
	private AdminManager adminManager;
	
	public FaqWriteRouter() {
		adminManager = new AdminManager();
	}
	
	public void handle(RoutingContext ctx) {
		if (!AdminManager.isAdmin(ctx)) {
			return;
		}
		boolean isLogin = adminManager.isLogined(ctx);
		if(isLogin) {
			DmsTemplate templates = new DmsTemplate("editor");
			try {
				templates.put("category", "faq");
				templates.put("type", "write");
				templates.put("content", "");
				
				ctx.response().setStatusCode(200);
				ctx.response().end(templates.process());
				ctx.response().close();
			} catch(IOException e) {
				Log.l("IOException");
			} catch(TemplateException e) {
				Log.l("TemplateException");
			}
		} else {
			ctx.response().setStatusCode(200);
            ctx.response().putHeader("Content-type", "text/html; utf-8");
            ctx.response().end("<script>window.location.href='/'</script>");
            ctx.response().close();
		}
	}
}

package com.dms.planb.template_routers.recruit;

import com.dms.boxfox.templates.DmsTemplate;
import freemarker.template.TemplateException;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.boxfox.dms.util.UserManager;
import org.boxfox.dms.utilities.actions.RouteRegistration;
import org.boxfox.dms.utilities.database.DataBase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by boxfox on 2017-05-29.
 */
@RouteRegistration(path = "/recruit/apply", method = {HttpMethod.GET})
public class RecruitApplyRouter implements Handler<RoutingContext> {
    private UserManager userManager;

    public RecruitApplyRouter() {
        userManager = new UserManager();
    }

    public void handle(RoutingContext context) {
        int code = 400;
        if (userManager.isLogined(context) && !isApply(context)) {
            String language = context.request().getParam("language");
            String project = context.request().getParam("project");
            String content = context.request().getParam("content");
            String area = context.request().getParam("area");
            try {
                String uid = userManager.getUid(userManager.getIdFromSession(context));
                DataBase.getInstance().executeUpdate("insert into recruit values('", uid, "', '", language, "', '", project, "', '", content, "', '", area, "')");
                code = 200;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        context.response().setStatusCode(code).end("<script>window.location.href=document.referrer;</script>");
        context.response().close();
    }

    private boolean isApply(RoutingContext ctx) {
        boolean result = false;
        try {
            if (DataBase.getInstance().executeQuery("select count(*) from recruit where uid='", userManager.getUid(userManager.getIdFromSession(ctx)), "'").nextAndReturn().getInt(1) > 0) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}

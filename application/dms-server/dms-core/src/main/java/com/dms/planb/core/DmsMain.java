package com.dms.planb.core;

/**
 * @author KimSeongrae : Boxfoxs, JoMingyu : PlanB (city7310@naver.com)
 * 
 * -- Daedeok Software Meister High School --
 * Dormitory Management System(DMS) Project
 * 
 * Android Application HTTP Server based Vert.x Framework.
 * 
 * -- Developers' Blog --
 * @see http://boxfoxs.tistory.com/
 * @see http://city7310.blog.me/
 * 
 * -- Work --
 * @since 2017.01.10
 * 
 * @see https://github.com/rlatjdfo112/DSM-Dormitory-System.git
 * @see https://app.asana.com/0/238698310665123/board
 * 
 * -- Libraries -- 
 * @see pom.xml
 */

import org.boxfox.dms.utilities.config.SecureConfig;
import org.boxfox.dms.utilities.database.DataBase;

import com.dms.parser.dataio.post.PostChangeDetector;
import com.dms.parser.dataio.post.PostUpdateListener;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.boxfox.dms.utilities.log.Log;
import org.boxfox.dms.utilities.log.LogErrorOutputStream;

import java.sql.SQLException;
import java.util.Calendar;

class DmsMain {
	private static Vertx vertx;
	private static VertxOptions options;

	private static void initialize() {
		Log.setPath("log");
		/*
		 * Initializing method when server started.
		 */

		/**
		 * @see pom.xml : vert.x and netty syntax
		 */
		vertx = Vertx.vertx();
		/*
		 * Get vert.x's instance from Vertx(extends Measured) class
		 */

		options = new VertxOptions();
		System.setErr(new LogErrorOutputStream(System.err));

		PostChangeDetector.getInstance().start();
		PostChangeDetector.getInstance().setOnCategoryUpdateListener(new PostUpdateListener() {
			@Override
			public void update(int currentCategory) {
				Calendar currentTime = Calendar.getInstance();
				int dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK);
				int hour = currentTime.get(Calendar.HOUR_OF_DAY);
				Log.l("Post Update", "Day of week : "+dayOfWeek+"  hour : "+hour);
				if (dayOfWeek == Calendar.MONDAY&& hour<5) {
					try {
						DataBase.getInstance().executeUpdate("delete from goingout_apply");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (hour >= 0 && hour <= 8) {
					try {
						DataBase.getInstance().executeUpdate("delete from extension_apply");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void main(String[] args) {
		if(SecureConfig.get("DB_PW")==null||SecureConfig.get("AES")==null){
			System.err.println("Key Config is not defined!");
			return;
		}
		initialize();
		/*
		 * Branch off initialize() method
		 */

		/**
		 * (non-Javadoc)
		 * 
		 * @see http://vertx.io/docs/apidocs/io/vertx/core/VertxOptions.html
		 */
		options.setMaxEventLoopExecuteTime(2100000000);
		/*
		 * Sets the value of max event loop execute time, in ns.
		 */

		/**
		 * (non-Javadoc)
		 * 
		 * @see http://vertx.io/docs/apidocs/io/vertx/core/Vertx.html
		 * @see com.dms.planb.core .DmsVerticle
		 */
		vertx.deployVerticle(new DmsVerticle());
		/*
		 * Deploy a verticle instance that you have created yourself. Branch off
		 * DmsVerticle class
		 */
	}
}

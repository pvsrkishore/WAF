package common.helpers;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.MathTool;

import com.beust.jcommander.internal.Lists;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import common.utility.Utility;

/**
 * @author sabarinath.s
 * Date: 19-May-2015	
 * Time: 12:59:37 am 
 */

public class HtmlHelper {



	public  static void generateVelocityTemplate(ClientRequest request, ClientResponse response, File file){

		try{
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();

		VelocityContext context = new VelocityContext();
		context.put("request", request);
		context.put("response", response);
		System.out.println(Utility.prettyPrintJson(response.getEntityInputStream()));
		response.getEntityInputStream().reset();
		context.put("requestHeaders", Lists.newArrayList(request.getHeaders().entrySet()));
		context.put("responseHeaders", Lists.newArrayList(response.getHeaders()!=null?response.getHeaders().entrySet():new ArrayList()));
		context.put("utility", new Utility());
		context.put("dateTool", new DateTool());
		context.put("mathTool", new MathTool());
		context.put("displayTool", new DisplayTool());
		
		if(response!=null)
			context.put("headerSize", request.getHeaders().size()>response.getHeaders().size()?request.getHeaders().size():response.getHeaders().size());
		else
			context.put("headerSize", request.getHeaders().size());

		FileWriter fw = new FileWriter(file);
		Template template = ve.getTemplate("apireport.vm");
		template.merge(context, fw );
		fw.flush();
		fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


}

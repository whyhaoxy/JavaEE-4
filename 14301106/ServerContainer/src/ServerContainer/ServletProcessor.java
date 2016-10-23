package ServerContainer;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ServletProcessor {

	/*
	 * public static void main(String args[]){ try { processServletRequest(null,
	 * null); } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */

	public void processServletRequest(MyServletRequest req, MyServletResponse res) throws Exception {
		String uri = req.getURI();
		// 解析 web.xml , 根据uri得到servlet路径
		String servletName = null;
		if(uri.indexOf(".jsp") != -1){
			servletName = Mapping.getJspMap().get(uri);
		}else if(uri.indexOf(".ico") != 0){
			return;//do nothing
		}
		else{
			servletName = Mapping.getServletMap().get(uri);
		}
		System.out.println("Processing servlet: " + servletName);
		// 加载servlet类
		Servlet servlet = loadServlet(servletName);
		// 将request和response交给Servlet处理
		callService(servlet, req, res);
	}

	private Servlet loadServlet(String servletName) throws MalformedURLException {
		String servletURL = "../" + servletName.replace('.', '/');

		File file = new File(servletURL);
		// URL url = new URL("file://Servlet/LoginServlet");
		URL url = file.toURL();
		URLClassLoader loader = new URLClassLoader(new URL[] { url }, Thread.currentThread().getContextClassLoader());
		Servlet servlet = null;

		try {
			@SuppressWarnings("unchecked")
			Class<Servlet> servletClass = (Class<Servlet>) loader.loadClass(servletName);
			servlet = (Servlet) servletClass.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return servlet;
	}

	private void callService(Servlet servlet, ServletRequest request, ServletResponse response) {
		try {
			servlet.service(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
package ServerContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JspParser {

	public static void Jsp2Servlet() {
		String root = System.getProperty("user.dir") + "/src";
		File file = new File(root + "/Jsp");

		if (file.isDirectory()) {
			String files[] = file.list();

			for (String f : files) {
				if (f.indexOf(".jsp") != -1) {// 判断是不是jsp文件
					// default folder to save the servlet file after translating
					final String translateRoot = root + "/Servlet";
					// get substring jsp uri before "."
					String jspName = f.substring(0, f.indexOf("."));
					// open file stream
					File servletFile = new File(translateRoot + "/" + jspName + ".java");
					File JspFile = new File(root + "/Jsp/" + f);
					try {
						PrintStream ps = new PrintStream(servletFile);
						FileReader fr = new FileReader(JspFile);
						/*FileInputStream fir = new FileInputStream(JspFile);
						InputStreamReader isr = new InputStreamReader(fir,"utf-8");*/
						BufferedReader br = new BufferedReader(fr);
						
						StringBuffer sb = new StringBuffer();
						String line = null;
						while ((line = br.readLine()) != null)
							sb.append(line);

						String str = sb.toString();
						
						ps.print("package Servlet;" );
						
						String importInfo = null;
						if(str.indexOf("import") != -1){
							importInfo = str.substring(str.indexOf("import"));
							String import_regex = "\".*?;";
							Matcher import_matcher = Pattern.compile(import_regex).matcher(importInfo);
							if(import_matcher.find()){
								importInfo = import_matcher.group();
								importInfo = importInfo.substring(importInfo.indexOf("\"") + 1, importInfo.lastIndexOf(";"));
								String imp[] = importInfo.split(",");
								for(String i : imp){
									ps.print("import " + i + ";");
								}
							}
						}
						ps.print("import java.io.IOException;" + "import java.io.PrintWriter;"
								+ "import javax.servlet.Servlet;" + "import javax.servlet.ServletConfig;"
								+ "import javax.servlet.ServletException;" + "import javax.servlet.ServletRequest;"
								+ "import javax.servlet.ServletResponse;" + "import ServerContainer.MyServletResponse;"
								+ "public class ");
						
						//打印类名
						ps.print(jspName);
						
						ps.print(" implements Servlet {"// 待修改
								// + "@Override"
								+ "public void destroy() {"
								// + "// TODO Auto-generated method stub"
								+ "}"
								// + "@Override"
								+ "public ServletConfig getServletConfig() {"
								// + "// TODO Auto-generated method stub"
								+ "return null;" + "}"
								// + "@Override"
								+ "public String getServletInfo() {"
								// + "// TODO Auto-generated method stub"
								+ "return null;" + "}"
								// + "@Override"
								+ "public void init(ServletConfig arg0) throws ServletException {"
								// + "// TODO Auto-generated method stub"
								+ "}"
								// + "@Override"
								+ "public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {"
								+ "MyServletResponse res = (MyServletResponse) response;"
								+ "PrintWriter out = res.getWriter();");
								//+ "OutputStream out = (OutputStream)res.getOutputStream();");
						//ps.print("out.println(\"<meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\">\");");
						String HTML_REGEX = "<html>.*?</html>";
						Pattern html_pattern = Pattern.compile(HTML_REGEX);
						Matcher html_matcher = html_pattern.matcher(str);

						if (html_matcher.find()) {
							String htmlStr = html_matcher.group();
							int startHtml = 0;
							int endHtml = htmlStr.length();

							String JSP_REGEX = "<%.*?%>";
							Pattern jsp_pattern = Pattern.compile(JSP_REGEX);
							Matcher jsp_matcher = jsp_pattern.matcher(htmlStr);

							int startJsp = startHtml;
							int endJsp = endHtml;
							while (jsp_matcher.find()) {
								String jspStr = jsp_matcher.group();
								startJsp = jsp_matcher.start();
								String pre = htmlStr.substring(startHtml, startJsp);
								ps.print("out.println(\"" + pre + "\");");
								endJsp = jsp_matcher.end();
								String regex = "<%[ ]*=";
								Matcher matcher = Pattern.compile(regex).matcher(jspStr);
								String jsp = null;
								if (matcher.find()) {
									jsp = jspStr.substring(jspStr.indexOf("=") + 1, jspStr.length() - 2);
									ps.print("out.println(" + jsp + ");");
								} else {
									jsp = htmlStr.substring(startJsp + 2, endJsp - 2);
									ps.print(jsp);
								}
								startHtml = endJsp;
							}
							String left = htmlStr.substring(startHtml, endHtml);
							ps.print("out.println(\"" + left + "\");");
							
							ps.print("out.close();");
						}

						ps.print("   }" + "}");

						ps.flush();
						ps.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}

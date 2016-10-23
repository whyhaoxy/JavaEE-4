package ServerContainer;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Mapping {
	private static Map<String, String> servletMap = new HashMap<String, String>();
	private static Map<String, String> jspMap = new HashMap<String, String>();
	//解析web.xml,获取映射关系
	public static void mapServlet(){
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File("web.xml"));
			// get root element ---<web-app></web-app>
			Element node = doc.getRootElement();
			System.out.println(node.getName());
			String uri = null;
			String servletName = null;
			String servletClass = null;
			Iterator<Element> it = node.elementIterator("servlet-mapping");

			while (it.hasNext()) {
				Element e = it.next();
				Iterator<Element> ite = e.elementIterator("url-pattern");
				
				Iterator<Element> ite1 = e.elementIterator("servlet-name");
				if (ite.hasNext()&&ite1.hasNext()) {
					uri = ite.next().getText();
					servletName = ite1.next().getText();
					
					Iterator<Element> it1 = node.elementIterator("servlet");
					while(ite1.hasNext()){
						Element e1 = it1.next();
						Iterator<Element> iter = e1.elementIterator("servlet-name");
						Iterator<Element> iter1 = e1.elementIterator("servlet-class");

						if (iter.hasNext() && ite1.hasNext() && ite.next().getText().equals(servletName)) {
							servletClass = iter1.next().getText();
							System.out.println(servletClass);
							servletMap.put(uri, servletClass);
							break;
						}
					}
				}else{
					System.out.println("Something missing in the web.xml");
					break;
				}
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//搜索程序包里的jsp代码，添加映射关系
	public  static void  mapJsp(){
		String root = System.getProperty("user.dir") + "/src/Jsp";
		File file = new File(root);
		if(file.isDirectory()){
			String files[] = file.list();
			
			for(String f : files){
				if(f.indexOf(".jsp")!=-1){
					jspMap.put("/" + f, "Servlet." + f.substring(0, f.indexOf(".")));
				}
			}
		}
	}
	public static Map<String, String> getJspMap(){
		return jspMap;
	}
	
	public static Map<String, String> getServletMap(){
		return servletMap;
	}
	
}

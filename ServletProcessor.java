package ServerContainer;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ServletProcessor {
	
	public static Map<String, String> servletMapping = null;
	public static Map<String, String> servlet = null;
	
	public static void processServletRequest(MyServletRequest req, 
			MyServletResponse res) throws Exception{
		String uri = req.getURI();
		//解析 web.xml , 根据uri得到servlet路径
		if(servletMapping==null){
			parseWebXML();
			
		}
		String servletName = getServerName(uri);
		if(servletName==null){
			System.out.println("Servlet: " + servletName+" is not found!!!");
			throw new NullPointerException("404");
		}
		System.out.println("Processing servlet: " + servletName);
		//加载servlet类
		Servlet servlet = loadServlet(servletName);
		//将request和response交给Servlet处理
		callService(servlet, req, res);
	}
	
	private static Servlet loadServlet(String servletName) throws MalformedURLException {
		//String servletURL = "../" + servletName.replace('.', '/');
		String servletURL = System.getProperty("user.dir")+File.separator+"bin";
		File file = new File(servletURL);
		//URL url = new URL("file://Servlet/LoginServlet");
		URL url = file.toURL();
		URLClassLoader loader = new URLClassLoader(
				new URL[] { url }, Thread.currentThread().getContextClassLoader());
		Servlet servlet = null;
		
		try {
			@SuppressWarnings("unchecked")
			Class<Servlet> servletClass = (Class<Servlet>) loader
					.loadClass(servletName);
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
	
	private static void callService(Servlet servlet, ServletRequest request,
			ServletResponse response) {
		try {
			servlet.service(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String getServerName(String uri)
		throws Exception{
	/*	String servletName = null;
		//parse web.xml
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new File("web.xml"));
		//get root element ---<web-app></web-app>
		Element node = doc.getRootElement();
		//List<Attribute> list = node.attributes();
		String servlet = null;
		Iterator<Element> servletMappings = node.elementIterator("servlet-mapping");
		
		while(servletMappings.hasNext()){
			Element e = servletMappings.next();
			Iterator<Element> urlPatterns = e.elementIterator("url-pattern");
			Iterator<Element> servletNames = e.elementIterator("servlet-name");
			if(urlPatterns.hasNext()){
				Element el = urlPatterns.next();
				if(el.getText().equals(uri)){
					servlet = servletNames.next().getText();
					break;
				}
			}
		}
		
		Iterator<Element> servlets = node.elementIterator("servlet");
		
		while(servlets.hasNext()){
			Element e = servlets.next();
			Iterator<Element> servletNames = e.elementIterator("servlet-name");
			Iterator<Element> servletClasses = e.elementIterator("servlet-class");
			
			if(servletNames.hasNext()&&servletNames.next().getText().equals(servlet)){
				servletName = servletClasses.next().getText();
				break;
			}
			
		}
		return servletName;*/
		return servlet.get(servletMapping.get(uri));
	}
	
	static void parseWebXML() throws DocumentException{
	
		servlet = new HashMap<>();
		servletMapping = new HashMap<>();
		//parse web.xml
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new File("web.xml"));
		//get root element ---<web-app></web-app>
		Element node = doc.getRootElement();
		
		
		Iterator<Element> servletMappings = node.elementIterator("servlet-mapping");
		while(servletMappings.hasNext()){
			Element e = servletMappings.next();
			Iterator<Element> urlPatterns = e.elementIterator("url-pattern");
			Iterator<Element> servletNames = e.elementIterator("servlet-name");
			if(urlPatterns.hasNext()&&servletNames.hasNext()){
				servletMapping.put(urlPatterns.next().getText(), servletNames.next().getText());
			}
		}
		
		
		Iterator<Element> servlets = node.elementIterator("servlet");
		while(servlets.hasNext()){
			Element e = servlets.next();
			Iterator<Element> servletNames = e.elementIterator("servlet-name");
			Iterator<Element> servletClasses = e.elementIterator("servlet-class");
			if(servletNames.hasNext()&&servletClasses.hasNext()){
				servlet.put(servletNames.next().getText(), servletClasses.next().getText());
			}
			
		}
	}
	
}

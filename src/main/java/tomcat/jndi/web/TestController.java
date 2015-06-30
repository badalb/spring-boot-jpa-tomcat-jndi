package tomcat.jndi.web;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

	@Autowired
	private DataSource dataSource;

	@RequestMapping("/factoryBean")
	@ResponseBody
	public String factoryBean() {
		return "DataSource retrieved from JNDI using JndiObjectFactoryBean: " + dataSource;
	}

	@RequestMapping("/direct")
	@ResponseBody
	public String direct() throws NamingException {
		return "DataSource retrieved directly from JNDI: " +
				new InitialContext().lookup("java:comp/env/jdbc/test");
	}

}

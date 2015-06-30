package tomcat.jndi;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.deploy.ContextResource;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class TomcatJndiApplication {
	
	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(TomcatJndiApplication.class, args);
	}


	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory() {
	    return new TomcatEmbeddedServletContainerFactory() {

	        @Override
	        protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(
	                Tomcat tomcat) {
	            tomcat.enableNaming();
	            TomcatEmbeddedServletContainer container = 
	                    super.getTomcatEmbeddedServletContainer(tomcat);
	            for (Container child: container.getTomcat().getHost().findChildren()) {
	                if (child instanceof Context) {
	                    ClassLoader contextClassLoader = 
	                            ((Context)child).getLoader().getClassLoader();
	                    Thread.currentThread().setContextClassLoader(contextClassLoader);
	                    break;
	                }
	            }
	            return container;
	        }

	        @Override
	        protected void postProcessContext(Context context) {
	            ContextResource resource = new ContextResource();
	            resource.setName("jdbc/test");
	            resource.setType(DataSource.class.getName());
	            resource.setProperty("driverClassName", env.getProperty("mls.datasource.driverClassName"));
	            resource.setProperty("url", env.getProperty("mls.datasource.url"));
	            resource.setProperty("password",env.getProperty("mls.datasource.password"));
	            resource.setProperty("username", env.getProperty("mls.datasource.username"));

	            context.getNamingResources().addResource(resource);
	        }
	    };
	}
	
	@Bean(destroyMethod="")
	public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName("java:comp/env/jdbc/test");
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(false);
		bean.afterPropertiesSet();
		return (DataSource)bean.getObject();
	}
}

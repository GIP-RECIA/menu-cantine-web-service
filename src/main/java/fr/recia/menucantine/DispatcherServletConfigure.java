package fr.recia.menucantine;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/* cette classe est utile juste pour changer la conf par défaut du bean 
 * DispatcherServlet pour ajouter des logs devrait être enlever en prod
 * ou seulement le @Configure
 */
@Configuration
public class DispatcherServletConfigure implements BeanPostProcessor {
	
	
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DispatcherServlet) {
        	DispatcherServlet ds = (DispatcherServlet) bean;
            ds.setEnableLoggingRequestDetails(true);
            ds.setDispatchTraceRequest(true);
        }
        return bean;
   }

   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
       return bean;
   }
   
  
}

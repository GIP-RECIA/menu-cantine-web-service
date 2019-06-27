/**
 * Copyright (C) 2019 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2019 Pierre Legay <pierre.legay@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.menucantine;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/* cette classe est utile juste pour changer la conf par défaut du bean 
 * DispatcherServlet pour ajouter des logs devrait être enlever en prod
 * ou seulement le @Configure
 */
// @Configuration
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

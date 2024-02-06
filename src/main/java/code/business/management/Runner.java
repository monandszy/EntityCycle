package code.business.management;

import code.infrastructure.configuration.ApplicationConfiguration;
import code.infrastructure.configuration.HibernateUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Runner {
   public static void main(String[] args) {
      try {
         Class.forName("org.postgresql.Driver");
         //on classpath
      } catch(ClassNotFoundException e) {
         // not on classpath
         throw new RuntimeException(e);
      }
      ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
      context.getBean(HibernateUtil.class);
      HibernateUtil.getSession();
   }
}
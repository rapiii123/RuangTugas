package ruangtugas.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ruangtugas.HibernateUtil;

@Configuration
@ComponentScan(basePackages = "ruangtugas")
public class SpringConfig {

    @Bean(destroyMethod = "close")
    public SessionFactory sessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

}

package config;

import dao.IDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("primary")
public class PrimaryDaoConfig {
    // @Primary on a configuration bean keeps component classes reusable in other strategies.
    @Bean
    @Primary
    public IDao defaultDao(@Qualifier("dao") IDao production) {
        return production;
    }
}

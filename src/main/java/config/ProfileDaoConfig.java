package config;

import dao.IDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Configuration
@Profile("profiles")
public class ProfileDaoConfig {
    @Bean
    @Primary
    @Profile("prod")
    public IDao selectedProd(@Qualifier("daoProd") IDao prod) {
        return prod;
    }

    @Bean
    @Primary
    @Profile("dev")
    public IDao selectedDev(@Qualifier("dao2") IDao dev) {
        return dev;
    }
}

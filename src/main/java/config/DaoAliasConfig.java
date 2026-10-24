package config;
import dao.IDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Configuration @Profile("alias")
public class DaoAliasConfig {
    @Bean @Primary
    public IDao daoAlias(@Qualifier("daoApi") IDao api) { return api; }
}

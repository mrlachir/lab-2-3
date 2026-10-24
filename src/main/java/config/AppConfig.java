package config;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"dao", "metier"})
@PropertySources({
    @PropertySource("classpath:app.properties"),
    @PropertySource(value = "file:${dao.config:./dao-override.properties}", ignoreResourceNotFound = true)
})
@Import({PrimaryDaoConfig.class, DaoAliasConfig.class, DynamicDaoConfig.class, ProfileDaoConfig.class})
public class AppConfig { }

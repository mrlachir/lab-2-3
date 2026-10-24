package config;

import dao.IDao;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.util.Map;

@Configuration
@Profile("dynamic")
public class DynamicDaoConfig {
    @Bean
    @Primary
    public IDao dynamicDao(Map<String, IDao> candidates, Environment environment) {
        // Explicit order: -Ddao.target, DAO_TARGET, Spring property sources, default.
        String target = System.getProperty("dao.target");
        if (target == null) target = environment.getProperty("DAO_TARGET");
        if (target == null) target = environment.getProperty("dao.target", "dao2");
        if (target.isBlank()) throw new IllegalArgumentException("Cible DAO vide");
        IDao selected = candidates.get(target);
        if (selected == null) throw new IllegalArgumentException("Cible DAO inconnue: " + target);
        System.out.println("[CONFIG] dao.target=" + target);
        return selected;
    }
}

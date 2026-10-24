package config;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class ContextFactory {
    private ContextFactory() { }
    public static AnnotationConfigApplicationContext open(String strategy, String environment) {
        if (!java.util.Set.of("primary","alias","dynamic","profiles","multiple","ambiguous").contains(strategy))
            throw new IllegalArgumentException("Strategie inconnue: " + strategy);
        if ("profiles".equals(strategy) && !java.util.Set.of("dev","prod").contains(environment))
            throw new IllegalArgumentException("Profil attendu: dev ou prod");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // Activate before refresh, unlike the sample's late setActiveProfiles.
        context.getEnvironment().setActiveProfiles("profiles".equals(strategy)
            ? new String[]{"profiles", environment} : new String[]{strategy});
        try { context.register(AppConfig.class); context.refresh(); return context; }
        catch (RuntimeException ex) { context.close(); throw ex; }
    }
}

import config.ContextFactory;
import metier.IMetier;
import metier.MetierMultiple;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        if (args.length > 2)
            throw new IllegalArgumentException("Usage: Main [dynamic|primary|alias|profiles|multiple|ambiguous] [dev|prod]");
        String strategy = args.length == 0 ? "dynamic" : args[0];
        String profile = args.length == 2 ? args[1] : "prod";
        System.out.println("STRATEGIE : " + strategy);
        try (AnnotationConfigApplicationContext context = ContextFactory.open(strategy, profile)) {
            IMetier metier = context.getBean("multiple".equals(strategy) ? "metierMultiple" : "metierImpl", IMetier.class);
            if (metier instanceof MetierMultiple)
                System.out.println("DAO disponibles : " + ((MetierMultiple) metier).getDaoNames());
            System.out.println("Resultat final : " + metier.calcul());
            System.out.println("DEMO TERMINEE");
        } catch (RuntimeException ex) {
            if ("ambiguous".equals(strategy)) {
                for (Throwable cause = ex; cause != null; cause = cause.getCause())
                    if (cause instanceof NoUniqueBeanDefinitionException) {
                        System.out.println("AMBIGUITE ATTENDUE : " + cause.getMessage());
                        return;
                    }
            }
            throw ex;
        }
    }
}

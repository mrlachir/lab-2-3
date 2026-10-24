package dao;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
@Component("daoProd") @Profile("prod")
public class DaoImplProd implements IDao {
    @Override public double getValue() {
        System.out.println("[DAO] Profil prod (simulation)");
        return 100.0;
    }
}

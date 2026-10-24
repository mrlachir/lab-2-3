package dao;
import org.springframework.stereotype.Component;

@Component("daoFile") 
public class DaoFile implements IDao {
    @Override public double getValue() {
        System.out.println("[DAO] Fichier local (simulation)");
        return 180.0;
    }
}

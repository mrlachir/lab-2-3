package dao;
import org.springframework.stereotype.Component;

@Component("daoApi") 
public class DaoApi implements IDao {
    @Override public double getValue() {
        System.out.println("[DAO] API REST (simulation)");
        return 220.0;
    }
}

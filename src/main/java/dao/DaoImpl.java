package dao;
import org.springframework.stereotype.Component;

@Component("dao") 
public class DaoImpl implements IDao {
    @Override public double getValue() {
        System.out.println("[DAO] Base de donnees prod (simulation)");
        return 100.0;
    }
}

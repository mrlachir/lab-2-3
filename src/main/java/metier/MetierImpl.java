package metier;
import dao.IDao;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
@org.springframework.context.annotation.Profile("!multiple")
public class MetierImpl implements IMetier {
    private final IDao dao;
    public MetierImpl(IDao source) { this.dao = Objects.requireNonNull(source); }
    @PostConstruct public void init() {
        System.out.println("[TRACE] Implementation DAO injectee : " + dao.getClass().getSimpleName());
    }
    @Override public double calcul() { return dao.getValue() * 2.0; }
}

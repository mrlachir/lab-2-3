package metier;
import dao.IDao;
import java.util.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service @Profile("multiple")
public class MetierMultiple implements IMetier {
    private final List<IDao> daos;
    private final Map<String, IDao> daoMap;
    public MetierMultiple(List<IDao> daos, Map<String, IDao> daoMap) {
        this.daos = List.copyOf(daos); this.daoMap = Map.copyOf(daoMap);
    }
    @Override public double calcul() { return daos.stream().mapToDouble(IDao::getValue).sum(); }
    public Set<String> getDaoNames() { return new TreeSet<>(daoMap.keySet()); }
    public int getDaoCount() { return daos.size(); }
}

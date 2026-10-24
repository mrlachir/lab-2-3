package test;
import config.ContextFactory;
import metier.*;
import dao.*;
import org.junit.*;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

public class OcpSelectionTest {
    private String previousTarget, previousConfig;
    @Before public void before() {
        previousTarget = System.getProperty("dao.target");
        previousConfig = System.getProperty("dao.config");
        System.clearProperty("dao.target");
        // Isolate tests from a user's local external configuration.
        System.setProperty("dao.config", "./target/nonexistent-test-override.properties");
    }
    @After public void after() {
        restore("dao.target", previousTarget); restore("dao.config", previousConfig);
    }
    private void restore(String key, String value) {
        if (value == null) System.clearProperty(key); else System.setProperty(key,value);
    }
    private double result(String strategy, String profile) {
        try (AnnotationConfigApplicationContext c = ContextFactory.open(strategy,profile)) {
            return c.getBean("metierImpl",IMetier.class).calcul();
        }
    }
    @Test public void primarySelectsProduction() { assertEquals(200,result("primary","prod"),0); }
    @Test public void aliasSelectsApi() { assertEquals(440,result("alias","prod"),0); }
    @Test public void dynamicSwitchesAllFourImplementations() {
        String[] names={"dao","dao2","daoFile","daoApi"};
        double[] expected={200,300,360,440};
        for (int i=0;i<names.length;i++) {
            System.setProperty("dao.target",names[i]);
            assertEquals(expected[i],result("dynamic","prod"),0);
        }
    }
    @Test public void externalFileSelectsWithoutBusinessChanges() throws Exception {
        Path file=Files.createTempFile("dao-selection-", ".properties");
        try {
            Files.write(file,"dao.target=daoFile\n".getBytes(StandardCharsets.UTF_8));
            System.setProperty("dao.config",file.toAbsolutePath().toString().replace('\\','/'));
            // With no process DAO_TARGET, file overrides the packaged default.
            if (System.getenv("DAO_TARGET")==null) assertEquals(360,result("dynamic","prod"),0);
            System.setProperty("dao.target","daoApi");
            assertEquals(440,result("dynamic","prod"),0);
        } finally { Files.deleteIfExists(file); }
    }
    @Test public void prodAndDevProfilesActivatedBeforeRefresh() {
        assertEquals(200,result("profiles","prod"),0);
        assertEquals(300,result("profiles","dev"),0);
    }
    @Test public void multipleInjectsFourSourcesWithoutAliasDuplication() {
        try (AnnotationConfigApplicationContext c=ContextFactory.open("multiple","prod")) {
            MetierMultiple m=c.getBean(MetierMultiple.class);
            assertEquals(4,m.getDaoCount()); assertEquals(4,m.getDaoNames().size());
            assertEquals(650,m.calcul(),0);
        }
    }
    @Test public void ambiguityIsDemonstratedWithoutPrimarySelection() {
        RuntimeException error=assertThrows(RuntimeException.class,()->ContextFactory.open("ambiguous","prod"));
        boolean found=false;
        for (Throwable c=error;c!=null;c=c.getCause()) if(c instanceof NoUniqueBeanDefinitionException) found=true;
        assertTrue(found);
    }
    @Test public void unknownTargetFailsClearly() {
        System.setProperty("dao.target","missing");
        RuntimeException error=assertThrows(RuntimeException.class,()->ContextFactory.open("dynamic","prod"));
        boolean found=false;
        for(Throwable c=error;c!=null;c=c.getCause()) if(c.getMessage()!=null&&c.getMessage().contains("Cible DAO inconnue")) found=true;
        assertTrue(found);
    }
    @Test public void invalidStrategyAndProfileRejected() {
        assertThrows(IllegalArgumentException.class,()->ContextFactory.open("bad","prod"));
        assertThrows(IllegalArgumentException.class,()->ContextFactory.open("profiles","bad"));
    }
    @Test public void businessLogicOnlyDependsOnContract() {
        assertEquals(25,new MetierImpl(()->12.5).calcul(),0);
        assertThrows(NullPointerException.class,()->new MetierImpl(null));
    }
}

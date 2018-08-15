package walmart;

import jetty.JettyRunner;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

/**
 * Created by aawork on 8/12/18
 */

public class Application extends JettyRunner {

    public static void main(final String[] arguments) {
        final Application runner = new Application();
        runner.startJetty(arguments);
    }

    @Override
    protected void setAppContext(final WebAppContext webAppContext) {
        webAppContext.setWar(new File("src/main/webapp").getAbsolutePath());
    }

}

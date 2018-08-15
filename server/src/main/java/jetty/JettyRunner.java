package jetty;

/**
 * Created by aawork on 8/12/18
 */

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class JettyRunner {

    private Map<String, Object> startParameters;

    public static void main(String[] args) {
        new JettyRunner().startJetty(args);
    }

    protected void startJetty(String[] args) {
        try {
            initBootLogger();
            startParameters = new HashMap<>();
            startParameters.put("args", args);
            int port = resolvePort(args);
            String contextPath = resolveContext(args);
            startJetty(port, contextPath);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initBootLogger() {
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.WARN);
    }

    private String resolveContext(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("CONTEXT")) {
                String[] values = arg.split("=", 2);
                if (values.length == 2) {
                    return values[1];
                }
            }
        }
        return getDefaultContextPath();
    }

    private int resolvePort(String[] args) {
        // Try command line args JETTY_PORT=xxx
        for (String argument : args) {
            String[] keyValue = argument.split("=", 2);
            if (keyValue.length > 1 && "JETTY_PORT".equals(keyValue[0])) {
                return Integer.parseInt(keyValue[1]);
            }
        }
        // Try System properties
        String systemProperty = System.getenv("JETTY_PORT");
        if (systemProperty != null) {
            return Integer.parseInt(systemProperty);
        }

        // Return default value
        return getDefaultPort();
    }

    private void startJetty(int port, String contextPath) throws Throwable {

        final String delimiter = "==================================================";

        System.out.printf("\n%s\nStarting JETTY on port:%d context:%s\n%s\n\n", delimiter, port, contextPath, delimiter);

        startParameters.put("port", port);
        startParameters.put("contextPath", contextPath);

        int numberOfThreads = numberOfJettyThreads();
        startParameters.put("numberOfThreads", numberOfThreads);

        int idleTimeout = 60000;
        startParameters.put("idleTimeout", idleTimeout);

        int acceptQueueSize = 10000;
        startParameters.put("acceptQueueSize", acceptQueueSize);

        int queueSize = acceptQueueSize;
        startParameters.put("jodQueueSize", queueSize);
        BlockingArrayQueue<Runnable> jobQueue = new BlockingArrayQueue<>(acceptQueueSize, BlockingArrayQueue.DEFAULT_GROWTH, acceptQueueSize);

        QueuedThreadPool threadPool = new QueuedThreadPool(numberOfThreads, numberOfThreads, idleTimeout, jobQueue);
        threadPool.setName("Jetty");

        Server server = new Server(threadPool);

        ScheduledExecutorScheduler sharedScheduler = new ScheduledExecutorScheduler("Jetty Internal 1", false);
        sharedScheduler.start();
        server.addBean(sharedScheduler);

        ScheduledExecutorScheduler sharedScheduler2 = new ScheduledExecutorScheduler("Jetty Internal 2", false);
        sharedScheduler2.start();

        HttpConfiguration configuration = new HttpConfiguration();
        configuration.setSendXPoweredBy(false);
        configuration.setSendServerVersion(false);

        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(configuration);

        ServerConnector connector = new ServerConnector(server, null, sharedScheduler2, null, -1, -1, httpConnectionFactory);
        connector.setPort(port);
        connector.setAcceptQueueSize(acceptQueueSize);

        server.setConnectors(new Connector[]{connector});

//        URL webDir = this.getClass().getResource("/web");

//        System.out.println(webDir);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(contextPath);
        webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        webapp.setAttribute("startParameters", startParameters);
//        if (webDir != null) {
//            webapp.setResourceBase(webDir.toExternalForm());
//        }
        
        setAppContext(webapp);

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setIncludedMethods("GET", "POST", "PUT", "DELETE");
        gzipHandler.setIncludedMimeTypes("text/html", "text/plain", "text/css", "text/javascript", "application/javascript", "application/json");
        gzipHandler.setHandler(webapp);

        server.setHandler(gzipHandler);
        server.setRequestLog(new Slf4jRequestLog());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown...");
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        server.start();

        if (webapp.getUnavailableException() != null) {
            System.err.println("Application failed to start");
            System.err.println("Reason " + webapp.getUnavailableException().getMessage());
            throw webapp.getUnavailableException();
        }

        server.join();
    }

    protected void setAppContext(WebAppContext webapp) {
        ProtectionDomain protectionDomain = Server.class.getProtectionDomain();
        webapp.setWar(protectionDomain.getCodeSource().getLocation().toExternalForm());
    }

    protected int getDefaultPort() {
        return 8080;
    }

    protected String getDefaultContextPath() {
        return "/";
    }

    public static int numberOfJettyThreads() {
        return Runtime.getRuntime().availableProcessors() * 8;
    }
}
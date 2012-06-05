package ru.aptu.warehouse.tester;

import org.xml.sax.SAXException;
import ru.aptu.warehouse.tester.core.Launch;
import ru.aptu.warehouse.tester.core.execution.Loader;
import ru.aptu.warehouse.tester.core.execution.Scenario;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.core.report.Report;
import ru.aptu.warehouse.tester.services.*;
import ru.aptu.warehouse.tester.utils.executionPlan.PlanBuilder;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;
import ru.aptu.warehouse.tester.utils.executionPlan.readers.PlanReader;
import ru.aptu.warehouse.tester.utils.executionPlan.validators.LaunchValidator;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 7:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Launcher {
    private String planPath;
    private String loggerPropertyFile;
    private Launch launch;
    private LaunchLogger logger;

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        try {
            launcher.readArguments(args);
            Launch launch = PlanReader.read(launcher.planPath);
            new LaunchValidator(launch).validate();

            LoggerProperty loggerProperty = launcher.readLoggerProperties();
            DbService dbService = new DbService(loggerProperty);
            launch = new PlanBuilder(launch, dbService).build();

            LaunchLogger launchLogger = new LaunchLogger(dbService, launch, loggerProperty.get("db.logger.log.file"));
            launcher.setLogger(launchLogger);

            launch.setExecutorsLogger(launchLogger);
            launcher.setLaunch(launch);
            launcher.run();

            LaunchReporter reporter = new LaunchReporter(dbService, launch);
            launcher.processReports(reporter);
            
        } catch (TestPlanException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error: Cannot connect to data base for logger: "+e.getMessage());
        }
    }


    public Launch getLaunch() {
        return launch;
    }

    public void setLaunch(Launch launch) {
        this.launch = launch;
    }

    public LaunchLogger getLogger() {
        return logger;
    }

    public void setLogger(LaunchLogger logger) {
        this.logger = logger;
    }
    
    public void processReports(Reporter reporter) {
        for (Report reportInfo: launch.getReports()) {
            reporter.makeReport(reportInfo);
        }
    }

    public void run() {
        Scenario scenario = launch.getScenario();
        if (scenario.getLoader() != null) {
            runWithLoader();
        } else {
            runOnlyTests();
        }
    }
    
    private void runWithLoader() {
        Scenario scenario = launch.getScenario();
        boolean firstIteration = true;
        for (List<String> args: scenario.getLoader().getArgs()) {
            logger.setCurrentTest(0);
            Loader loader = scenario.getLoader();
            if (
                    (firstIteration &&
                            loader.getInitProperties().isNeedCreate()
                    ) ||
                            (loader.getInitProperties().isNeedCreate() &&
                                    loader.getInitProperties().isIterative()
                            )
                    ) {
                firstIteration = false;
                loader.getExecutor().clear();
                loader.getExecutor().init();
            }
            scenario.getLoader().getExecutor().load(args);
            runTestExecutors(1);
        }
    }
    
    private void runTestExecutors(int startIndex) {
        Scenario scenario = launch.getScenario();
        if (scenario.getTestsArgs().size() > 0) {
            for (int i = startIndex; i< scenario.getTests().size(); ++i) {
                logger.setCurrentTest(i);
                Test test = scenario.getTests().get(i);
                if (test.isIterative()) {
                    for (List<String> args: scenario.getTestsArgs()) {
                        test.getRunner().run(args);
                    }
                } else {
                    test.getRunner().run(null);
                }
                
            }    
        } else {
            for (int i = startIndex; i< scenario.getTests().size(); ++i) {
                logger.setCurrentTest(i);
                scenario.getTests().get(i).getRunner().run(null);
            }
        }
    }
    
    private void runOnlyTests() {
        runTestExecutors(0);
    }
    
    public void readArguments(String[] args) throws TestPlanException {
        for (String arg: args) {
            if (arg.startsWith("-plan=")) {
                planPath = arg.substring("-plan=".length());
            }
            if (arg.startsWith("-loggerProperty=")) {
                loggerPropertyFile = arg.substring("-loggerProperty=".length());
            }
        }
        if (planPath == null) {
            throw  new TestPlanException("Error: No argument for plan file.\nUsage: Launcher -plan=plan_file");
        }
    }

    public LoggerProperty readLoggerProperties() throws TestPlanException {
        LoggerProperty loggerProperty = new LoggerProperty();
        if (loggerPropertyFile != null) {
            try {
                loggerProperty = LoggerPropertyReader.read(loggerPropertyFile);
            } catch (IOException e) {
                throw new TestPlanException("WARNING: Cannot read file "+ loggerPropertyFile+
                        " with properties for logger. Default properties will be used.");
            }
        }
        return loggerProperty;
    }
}

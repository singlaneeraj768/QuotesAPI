package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(tags = "", features = {"src/test/resources/features"}, glue = {"com.example.definitions"},
        plugin = { "pretty", "html:target/cucumber-reports" },
        monochrome = true)


public class CucumberRunnerTests extends AbstractTestNGCucumberTests {

}

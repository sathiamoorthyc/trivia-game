package org.trivia.game.integration.bdd.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/",
                 plugin = {"pretty", "html:target/cucumber/trivia"},
                 glue = "org.trivia.game.integration.cucumber")
public class CucumberIntegrationTest {
}

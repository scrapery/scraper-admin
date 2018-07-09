package io.github.scrapery.scheduler.cucumber.stepdefs;

import io.github.scrapery.scheduler.ScraperSchedulerApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = ScraperSchedulerApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}

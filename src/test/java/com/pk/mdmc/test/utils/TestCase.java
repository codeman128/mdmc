package com.pk.mdmc.test.utils;


import com.pk.mdmc.core.Config;

/**
 * Created by PavelK on 4/16/2016.
 */
public class TestCase {
    private String name;
    private Scenario scenario;
    private ExpectedResults expectedResults;

    private TestCase(){}

    public  TestCase(Config cnfg, String name) {
        this.name = name;
        scenario = new Scenario(cnfg);
        expectedResults = new ExpectedResults();
    }

    public String getName(){
        return name;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public ExpectedResults getExpectedResults() {
        return expectedResults;
    }
}

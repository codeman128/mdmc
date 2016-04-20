package com.pk.mdmc.test.utils;


import com.pk.mdmc.core.*;
import com.pk.mdmc.impl.MDMCConfig;
import com.pk.mdmc.test.mock.DisrupterMessageHandlerMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 4/16/2016.
 */
public class AbstractTestRunner {
    protected static final String LINE = "----------------------------------------------------";
    protected IConfig cnfg = new MDMCConfig();
    protected DisrupterMessageHandlerMock eHandler = new DisrupterMessageHandlerMock();
    protected IMessageBuffer messageProducer = new MessageDisruptor(cnfg, eHandler);//new MessageHandlerImpl(cnfg);
    protected Window window = new Window(cnfg, messageProducer);
    protected Packet p = new Packet(cnfg);
    protected List<TestCase> list = new ArrayList<TestCase>();

    protected TestCase addTestCase(String name){
        TestCase testCase = new TestCase(cnfg, name);
        list.add(testCase);
        return testCase;
    }

    public boolean run(){
        boolean result = true;
        for (TestCase testCase: list) {
            eHandler.reset();
            window.init();
            System.out.println("\n\nRunning "+testCase.getName()+" ("+testCase.getScenario().getList().size()+" packets) :\n"+LINE);
            for (Packet p : testCase.getScenario().getList()) {
                p = window.processPacket(p);
            }
            try {
                Thread.yield();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean testResult = testCase.getExpectedResults().compare(eHandler.getResults());
            System.out.println(LINE);
            result = result & testResult;
            if (testResult){
                System.out.println("> Passed");
            } else {
                System.out.println("> Failed");
            }
            System.out.println(LINE);
            System.out.println(eHandler.toString());
            System.out.println(LINE);
        }
        return result;
    }



}

package com.pk.mdmc.test;

import com.pk.mdmc.test.utils.AbstractTestRunner;
import com.pk.mdmc.test.utils.Scenario;
import com.pk.mdmc.test.utils.TestCase;

/**
 * Created by PavelK on 4/16/2016.
 */
public class PacketAssemblyLogicTest extends AbstractTestRunner {

    public PacketAssemblyLogicTest() {
        super();
        addTest1(addTestCase(" Test 1"));
        addTest2(addTestCase(" Test 2"));

    }

    public static void addTest1(TestCase tc){
        Scenario s = tc.getScenario();

        s.add(1000, 1, 3, 1024);
        s.add(1000, 2, 3, 1024);
        s.add(1000, 3, 3, 1024);

        s.add(1001, 1, 4, 1024);
        s.add(1001, 2, 4, 1024);
        s.add(1001, 3, 4, 1024);
        s.add(1001, 4, 4, 1024);

        tc.getExpectedResults().
                addMessage(1000, 3, 3).
                addMessage(1001, 4, 4);

    }

    public static void addTest2(TestCase tc){
        Scenario s = tc.getScenario();

        s.add(1000, 1, 3, 1024);
        s.add(1000, 2, 3, 1024);
        s.add(1000, 3, 3, 1024);

        s.add(1001, 1, 4, 1024);
        s.add(1001, 2, 4, 1024);
        s.add(1001, 3, 4, 1024);

        s.add(1002, 1, 1, 1024);

        s.add(1003, 1, 2, 1024);

        s.add(1004, 1, 2, 1024);

        s.add(1005, 1, 2, 1024);

        s.add(1006, 1, 2, 1024);

        s.add(1007, 1, 2, 1024);

        s.add(1005, 2, 2, 1024);

        s.add(1003, 2, 2, 1024);
        s.add(1004, 2, 2, 1024);

        s.add(1007, 2, 2, 1024);
        s.add(1006, 2, 2, 1024);

        tc.getExpectedResults().
                addMessage(1000, 3, 3).
                addMessage(1001, 3, 4).
                addMessage(1002, 1, 1).
                addMessage(1003, 1, 2).
                addMessage(1004, 1, 2).
                addMessage(1005, 2, 2).
                addMessage(1006, 1, 2).
                addMessage(1007, 2, 2);
    }

    public static void main(String[] args) {
        PacketAssemblyLogicTest test = new PacketAssemblyLogicTest();
        test.run();
    }
}

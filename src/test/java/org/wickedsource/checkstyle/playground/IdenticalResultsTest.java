package org.wickedsource.checkstyle.playground;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class IdenticalResultsTest {

    @Test
    public void multipleCheckerInstancesProvideIdenticalResults() throws Exception {
        File fileToAnalyze = getFileToAnalyze();
        List<Map<String, Integer>> metricMaps = new ArrayList<Map<String, Integer>>();

        // running analysis of the same file multiple times
        for (int i = 0; i < 2; i++) {
            System.out.println(String.format("metrics in round %d", i+1));
            MetricCountingAuditListener listener = new MetricCountingAuditListener();
            Checker checker = createChecker(listener);
            checker.process(Arrays.asList(fileToAnalyze));
            metricMaps.add(listener.getMetricCounts());
        }

        // checking if the results of the multiple analyses have the same results
        Iterator<Map<String,Integer>> iterator = metricMaps.iterator();
        Map<String,Integer> findings1 = iterator.next();
        while (iterator.hasNext()) {
            Map<String,Integer> findings2 = iterator.next();
            assertFindingsAreEqual(findings1, findings2);
            findings1 = findings2;
        }
    }

    private void assertFindingsAreEqual(Map<String, Integer> metricCounts1, Map<String,Integer> metricCounts2) {
        for(String metricName : metricCounts1.keySet()){
            Integer metricCount1 = metricCounts1.get(metricName);
            Integer metricCount2 = metricCounts2.get(metricName);
            Assert.assertEquals(String.format("expecting count for metric %s to be equal", metricName), metricCount1, metricCount2);
        }
    }

    private File getFileToAnalyze() throws Exception{
        byte[] fileContent = IOUtils.toByteArray(getClass().getResourceAsStream("/DiffParser.java.txt"));
        File fileToAnalyze = createTempFile(fileContent);
        Assert.assertTrue(String.format("expecting file to exist: %s", fileToAnalyze.getPath()), fileToAnalyze.exists());
        return fileToAnalyze;
    }

    private Checker createChecker(AuditListener auditListener) throws CheckstyleException {
        Checker checker = new Checker();
        ClassLoader classLoader = Checker.class.getClassLoader();
        checker.setModuleClassLoader(classLoader);
        checker.configure(getDefaultConfiguration());
        checker.addListener(auditListener);
        return checker;
    }

    private Configuration getDefaultConfiguration() throws CheckstyleException {
        return ConfigurationLoader.loadConfiguration(
                new InputSource(getClass().getResourceAsStream("/checkstyle.xml")),
                new PropertiesExpander(new Properties()),
                true);
    }

    private File createTempFile(byte[] fileContent) throws IOException {
        File file = File.createTempFile("checkstyletest-", ".java");
        file.deleteOnExit();
        FileOutputStream out = new FileOutputStream(file);
        out.write(fileContent);
        out.close();
        return file;
    }

}

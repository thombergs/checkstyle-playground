package org.wickedsource.checkstyle.playground;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

import java.util.*;

/**
 * A simple AuditListener implementation that simply counts the checkstyle
 * findings.
 */
public class MetricCountingAuditListener implements AuditListener {

    Map<String, Integer> metricCounts = new HashMap<String, Integer>();

    public void auditStarted(AuditEvent auditEvent) {
        // do nothing
    }

    public void auditFinished(AuditEvent auditEvent) {
        // do nothing
    }

    public void fileStarted(AuditEvent auditEvent) {
        // do nothing
    }

    public void fileFinished(AuditEvent auditEvent) {
        // do nothing
    }

    public void addError(AuditEvent event) {
        if(event.getSeverityLevel() != SeverityLevel.IGNORE) {
            String metricName = event.getSourceName();
            Integer count = this.metricCounts.get(metricName);
            if (count == null) {
                count = 0;
            }
            this.metricCounts.put(metricName, count + 1);
            System.out.println(metricName);
        }
    }

    public void addException(AuditEvent auditEvent, Throwable throwable) {
        // do nothing
    }

    public Map<String, Integer> getMetricCounts(){
        return this.metricCounts;
    }
}

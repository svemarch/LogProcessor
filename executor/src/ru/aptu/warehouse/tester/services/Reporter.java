package ru.aptu.warehouse.tester.services;

import ru.aptu.warehouse.tester.core.report.GraphData;
import ru.aptu.warehouse.tester.core.report.Report;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 6/2/12
 * Time: 12:57 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Reporter {
    public void makeReport(Report reportInfo);
    public List<GraphData> extractReportData(Report report);
}

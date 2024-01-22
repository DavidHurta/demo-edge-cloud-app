//
//  Author: David Hurta (xhurta04)
//  Project: DIP
//

package org.example;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class SystemInfo {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public long getProcessCpuTime() {
        return processCpuTime;
    }

    public void setProcessCpuTime(long processCpuTime) {
        this.processCpuTime = processCpuTime;
    }

    public double getProcessCpuLoad() {
        return processCpuLoad;
    }

    public void setProcessCpuLoad(double processCpuLoad) {
        this.processCpuLoad = processCpuLoad;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public long getTotalMemorySize() {
        return totalMemorySize;
    }

    public void setTotalMemorySize(long totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }

    public long getFreeMemorySize() {
        return freeMemorySize;
    }

    public void setFreeMemorySize(long freeMemorySize) {
        this.freeMemorySize = freeMemorySize;
    }

    public long getTotalSwapSpaceSize() {
        return totalSwapSpaceSize;
    }

    public void setTotalSwapSpaceSize(long totalSwapSpaceSize) {
        this.totalSwapSpaceSize = totalSwapSpaceSize;
    }

    public long getFreeSwapSpaceSize() {
        return freeSwapSpaceSize;
    }

    public void setFreeSwapSpaceSize(long freeSwapSpaceSize) {
        this.freeSwapSpaceSize = freeSwapSpaceSize;
    }

    public long getCommittedVirtualMemorySize() {
        return committedVirtualMemorySize;
    }

    public void setCommittedVirtualMemorySize(long committedVirtualMemorySize) {
        this.committedVirtualMemorySize = committedVirtualMemorySize;
    }

    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    public void setSystemLoadAverage(double systemLoadAverage) {
        this.systemLoadAverage = systemLoadAverage;
    }

    String name;
    String arch;
    String version;
    int availableProcessors;
    long processCpuTime;
    double processCpuLoad;
    double cpuLoad;
    long totalMemorySize;
    long freeMemorySize;
    long totalSwapSpaceSize;
    long freeSwapSpaceSize;
    long committedVirtualMemorySize;
    double systemLoadAverage;

    public SystemInfo() {
        this.Update();
    }

    public void Update() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        this.name = osBean.getName();
        this.arch = osBean.getArch();
        this.version = osBean.getVersion();

        this.availableProcessors = osBean.getAvailableProcessors();
        this.cpuLoad = osBean.getCpuLoad();
        this.processCpuTime= osBean.getProcessCpuTime();
        this.processCpuLoad = osBean.getProcessCpuLoad();

        this.totalMemorySize = osBean.getTotalMemorySize();
        this.freeMemorySize = osBean.getFreeMemorySize();

        this.totalSwapSpaceSize = osBean.getTotalSwapSpaceSize();
        this.freeSwapSpaceSize = osBean.getFreeSwapSpaceSize();

        this.committedVirtualMemorySize = osBean.getCommittedVirtualMemorySize();
        this.systemLoadAverage = osBean.getSystemLoadAverage();
    }

    public void Print() {
        System.out.println(this.name);
        System.out.println(this.arch);
        System.out.println(this.version);

        System.out.println(this.availableProcessors);
        System.out.println(this.processCpuTime);
        System.out.println(this.cpuLoad);
        System.out.println(this.processCpuLoad);

        System.out.println(this.freeMemorySize);
        System.out.println(this.freeSwapSpaceSize);

        System.out.println(this.totalMemorySize);
        System.out.println(this.totalSwapSpaceSize);

        System.out.println(this.committedVirtualMemorySize);
        System.out.println(this.systemLoadAverage);
    }
}

package ConfigurationParser;

/**
 * Created by Spark on 14/11/2016.
 */
public class Costs {

    private Double cpu;
    private Double encryption;
    private Double cpuSpeed;

    public Costs(Double c, Double e, Double s)
    {
        this.cpu = c;
        this.encryption = e;
        this.cpuSpeed = s;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getEncryption() {
        return encryption;
    }

    public Double getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(Double cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    public void setEncryption(Double encryption) {
        this.encryption = encryption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Costs)) return false;

        Costs costs = (Costs) o;

        if (cpu != null ? !cpu.equals(costs.cpu) : costs.cpu != null) return false;
        if (encryption != null ? !encryption.equals(costs.encryption) : costs.encryption != null) return false;
        return cpuSpeed != null ? cpuSpeed.equals(costs.cpuSpeed) : costs.cpuSpeed == null;

    }

    @Override
    public int hashCode() {
        int result = cpu != null ? cpu.hashCode() : 0;
        result = 31 * result + (encryption != null ? encryption.hashCode() : 0);
        result = 31 * result + (cpuSpeed != null ? cpuSpeed.hashCode() : 0);
        return result;
    }
}

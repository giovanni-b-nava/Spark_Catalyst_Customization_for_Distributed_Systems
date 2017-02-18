package ConfigurationParser;

public class Costs {

    private Double cpu;
    private Double encryptionAES;
    private Double encryptionHOMOMORPHIC;
    private Double cpuSpeed;

    public Costs(Double c, Double ea, Double eh, Double s)
    {
        this.cpu = c;
        this.encryptionAES = ea;
        this.encryptionHOMOMORPHIC = eh;
        this.cpuSpeed = s;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getEncryptionAES() {
        return encryptionAES;
    }

    public Double getEncryptionHOMOMORPHIC()
    {
        return encryptionHOMOMORPHIC;
    }

    public Double getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(Double cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    public void setEncryptionAES(Double encryptionAES) {
        this.encryptionAES = encryptionAES;
    }

    public void setEncryptionHOMOMORPHIC(Double encryptionHOMOMORPHIC)
    {
        this.encryptionHOMOMORPHIC = encryptionHOMOMORPHIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Costs)) return false;

        Costs costs = (Costs) o;

        if (cpu != null ? !cpu.equals(costs.cpu) : costs.cpu != null) return false;
        if (encryptionAES != null ? !encryptionAES.equals(costs.encryptionAES) : costs.encryptionAES != null) return false;
        return cpuSpeed != null ? cpuSpeed.equals(costs.cpuSpeed) : costs.cpuSpeed == null;

    }

    @Override
    public int hashCode() {
        int result = cpu != null ? cpu.hashCode() : 0;
        result = 31 * result + (encryptionAES != null ? encryptionAES.hashCode() : 0);
        result = 31 * result + (cpuSpeed != null ? cpuSpeed.hashCode() : 0);
        return result;
    }
}

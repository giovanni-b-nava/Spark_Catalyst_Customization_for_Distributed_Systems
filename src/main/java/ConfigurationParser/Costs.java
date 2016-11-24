package ConfigurationParser;

/**
 * Created by Spark on 14/11/2016.
 */
public class Costs {

    private Double cpu;
    private Double in;
    private Double out;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Costs)) return false;

        Costs costs = (Costs) o;

        if (cpu != null ? !cpu.equals(costs.cpu) : costs.cpu != null) return false;
        if (in != null ? !in.equals(costs.in) : costs.in != null) return false;
        if (out != null ? !out.equals(costs.out) : costs.out != null) return false;
        return encryption != null ? encryption.equals(costs.encryption) : costs.encryption == null;

    }

    @Override
    public int hashCode() {
        int result = cpu != null ? cpu.hashCode() : 0;
        result = 31 * result + (in != null ? in.hashCode() : 0);
        result = 31 * result + (out != null ? out.hashCode() : 0);
        result = 31 * result + (encryption != null ? encryption.hashCode() : 0);
        return result;
    }

    private Double encryption;

    public Costs(Double c, Double i, Double o, Double e) {
        this.cpu = c;
        this.in = i;
        this.out = o;
        this.encryption = e;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getIn() {
        return in;
    }

    public void setIn(Double in) {
        this.in = in;
    }

    public Double getOut() {
        return out;
    }

    public void setOut(Double out) {
        this.out = out;
    }

    public Double getEncryption() {
        return encryption;
    }

    public void setEncryption(Double encryption) {
        this.encryption = encryption;
    }
}

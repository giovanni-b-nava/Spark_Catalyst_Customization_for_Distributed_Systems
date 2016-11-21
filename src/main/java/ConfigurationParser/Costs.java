package ConfigurationParser;

/**
 * Created by Spark on 14/11/2016.
 */
public class Costs {

    private Double cpu;
    private Double in;
    private Double out;
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

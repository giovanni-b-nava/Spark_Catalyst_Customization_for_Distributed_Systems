package ParserConfigurator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spark on 09/11/2016.
 */
public class Table {

    private String name;
    private List<String> plaintext = new ArrayList<>();
    private List<String> encrypted = new ArrayList<>();

    public Table(String n, List<String> p, List<String> e) {
        this.name = n;
        this.plaintext = p;
        this.encrypted = e;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(List<String> plaintext) {
        this.plaintext = plaintext;
    }

    public List<String> getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(List<String> encrypted) {
        this.encrypted = encrypted;
    }
}

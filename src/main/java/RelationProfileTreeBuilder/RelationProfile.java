package RelationProfileTreeBuilder;

import java.util.List;

/**
 * Created by Spark on 17/11/2016.
 */
public class RelationProfile {

    // Attributes visible in plaintext
    private List<String> visiblePlaintext;
    // Attributes visible encrypted
    private List<String> visibleEncrypted;
    // Attributes implicit in plaintext
    private List<String> implicitPlaintext;
    // Attributes implicit encrypted
    private List<String> implicitEncrypted;
    // List of attributes involved in relations between them
    private List<List<String>> equivalenceSets;

    public RelationProfile() {
        this(null, null, null, null, null);
    }

    public RelationProfile(List<String> vp, List<String> ve, List<String> ip, List<String> ie, List<List<String>> es) {
        this.visiblePlaintext = vp;
        this.visibleEncrypted = ve;
        this.implicitPlaintext = ip;
        this.implicitEncrypted = ie;
        this.equivalenceSets = es;
    }

    public List<String> getVisiblePlaintext() {
        return visiblePlaintext;
    }

    public void setVisiblePlaintext(List<String> visiblePlaintext) {
        this.visiblePlaintext = visiblePlaintext;
    }

    public List<String> getVisibleEncrypted() {
        return visibleEncrypted;
    }

    public void setVisibleEncrypted(List<String> visibleEncrypted) {
        this.visibleEncrypted = visibleEncrypted;
    }

    public List<String> getImplicitPlaintext() {
        return implicitPlaintext;
    }

    public void setImplicitPlaintext(List<String> implicitPlaintext) {
        this.implicitPlaintext = implicitPlaintext;
    }

    public List<String> getImplicitEncrypted() {
        return implicitEncrypted;
    }

    public void setImplicitEncrypted(List<String> implicitEncrypted) {
        this.implicitEncrypted = implicitEncrypted;
    }

    public List<List<String>> getEquivalenceSets() {
        return equivalenceSets;
    }

    public void setEquivalenceSets(List<List<String>> equivalenceSets) {
        this.equivalenceSets = equivalenceSets;
    }
}

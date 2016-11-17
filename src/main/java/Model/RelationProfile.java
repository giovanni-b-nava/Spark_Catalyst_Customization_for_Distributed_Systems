package Model;

import java.util.List;

/**
 * Created by Spark on 17/11/2016.
 */
public class RelationProfile {

    private List<String> visiblePlaintext;
    private List<String> visibleEncrypted;
    private List<String> implicitPlaintext;
    private List<String> implicitEncrypted;
    private List<List<String>> equivalenceSets;

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

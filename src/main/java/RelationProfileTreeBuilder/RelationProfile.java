package RelationProfileTreeBuilder;

import java.util.ArrayList;
import java.util.List;

public class RelationProfile
{

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
        this.visiblePlaintext = new ArrayList<>();
        this.visibleEncrypted = new ArrayList<>();
        this.implicitPlaintext = new ArrayList<>();
        this.implicitEncrypted = new ArrayList<>();
        this.equivalenceSets = new ArrayList<>();
    }


    public RelationProfile(List<String> vp, List<String> ve, List<String> ip, List<String> ie, List<List<String>> es) {
        this.visiblePlaintext = vp;
        this.visibleEncrypted = ve;
        this.implicitPlaintext = ip;
        this.implicitEncrypted = ie;
        this.equivalenceSets = es;
    }

    // Create a NEW copy of a RelationProfile
    public RelationProfile(RelationProfile profile)
    {
        this();

        this.visiblePlaintext.addAll(profile.getVisiblePlaintext());
        this.visibleEncrypted.addAll(profile.getVisibleEncrypted());
        this.implicitPlaintext.addAll(profile.getImplicitPlaintext());
        this.implicitEncrypted.addAll(profile.getImplicitEncrypted());
        this.equivalenceSets.addAll(profile.getEquivalenceSets());
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

    public String toString() {
        return "Visible Plaintext: " + this.visiblePlaintext.toString() + "\n" +
                "Visible Encrypted: " + this.visibleEncrypted.toString() +
                "\n" +"Implicit Plaintext: " + this.implicitPlaintext.toString() + "\n" +
                "Implicit Encrypted: " + this.implicitEncrypted.toString() + "\n" +
                "Equivalence Sets: " + this.equivalenceSets.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelationProfile)) return false;

        RelationProfile that = (RelationProfile) o;

        if (visiblePlaintext != null ? !visiblePlaintext.equals(that.visiblePlaintext) : that.visiblePlaintext != null)
            return false;
        if (visibleEncrypted != null ? !visibleEncrypted.equals(that.visibleEncrypted) : that.visibleEncrypted != null)
            return false;
        if (implicitPlaintext != null ? !implicitPlaintext.equals(that.implicitPlaintext) : that.implicitPlaintext != null)
            return false;
        if (implicitEncrypted != null ? !implicitEncrypted.equals(that.implicitEncrypted) : that.implicitEncrypted != null)
            return false;
        return equivalenceSets != null ? equivalenceSets.equals(that.equivalenceSets) : that.equivalenceSets == null;

    }

    @Override
    public int hashCode() {
        int result = visiblePlaintext != null ? visiblePlaintext.hashCode() : 0;
        result = 31 * result + (visibleEncrypted != null ? visibleEncrypted.hashCode() : 0);
        result = 31 * result + (implicitPlaintext != null ? implicitPlaintext.hashCode() : 0);
        result = 31 * result + (implicitEncrypted != null ? implicitEncrypted.hashCode() : 0);
        result = 31 * result + (equivalenceSets != null ? equivalenceSets.hashCode() : 0);
        return result;
    }
}

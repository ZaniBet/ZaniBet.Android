package eu.devolios.zanibet.model;

public class SafetyResponse {

    private String nonce;
    private long timestampMs;
    private String apkPackageName;
    private boolean ctsProfileMatch;
    private boolean basicIntegrity;

    public SafetyResponse(){

    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(long timestampMs) {
        this.timestampMs = timestampMs;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public boolean isCtsProfileMatch() {
        return ctsProfileMatch;
    }

    public void setCtsProfileMatch(boolean ctsProfileMatch) {
        this.ctsProfileMatch = ctsProfileMatch;
    }

    public boolean isBasicIntegrity() {
        return basicIntegrity;
    }

    public void setBasicIntegrity(boolean basicIntegrity) {
        this.basicIntegrity = basicIntegrity;
    }
}

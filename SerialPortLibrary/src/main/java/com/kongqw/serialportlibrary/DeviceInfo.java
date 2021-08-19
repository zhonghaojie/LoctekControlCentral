package com.kongqw.serialportlibrary;

/**
 * Description:
 * Created by loctek on 2021/8/17.
 */
public class DeviceInfo {
    private int gear;
    private int pm25;
    private int jiaquan;
    private String temperature;
    private String shidu;
    private String filterScreen;
    private int uv;
    private int fulizi;
    private int version;
    private byte[] error;

    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getJiaquan() {
        return jiaquan;
    }

    public void setJiaquan(int jiaquan) {
        this.jiaquan = jiaquan;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getFilterScreen() {
        return filterScreen;
    }

    public void setFilterScreen(String filterScreen) {
        this.filterScreen = filterScreen;
    }

    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

    public int getFulizi() {
        return fulizi;
    }

    public void setFulizi(int fulizi) {
        this.fulizi = fulizi;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getError() {
        return error;
    }

    public void setError(byte[] error) {
        this.error = error;
    }
}

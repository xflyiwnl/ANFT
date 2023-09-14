package me.xflyiwnl.anft.object.orient;

public class Orient {

    private OrientSide side;
    private double xa = 0, xb = 0;
    private double ya = 0, yb = 0;
    private double za = 0, zb = 0;

    public Orient() {
    }

    public Orient(OrientSide side, double xa, double xb, double ya, double yb) {
        this.side = side;
        this.xa = xa;
        this.xb = xb;
        this.ya = ya;
        this.yb = yb;
    }

    public Orient(OrientSide side, double xa, double xb, double ya, double yb, double za, double zb) {
        this.side = side;
        this.xa = xa;
        this.xb = xb;
        this.ya = ya;
        this.yb = yb;
        this.za = za;
        this.zb = zb;
    }

    public OrientSide getSide() {
        return side;
    }

    public void setSide(OrientSide side) {
        this.side = side;
    }

    public double getXa() {
        return xa;
    }

    public void setXa(double xa) {
        this.xa = xa;
    }

    public double getXb() {
        return xb;
    }

    public void setXb(double xb) {
        this.xb = xb;
    }

    public double getYa() {
        return ya;
    }

    public void setYa(double ya) {
        this.ya = ya;
    }

    public double getYb() {
        return yb;
    }

    public void setYb(double yb) {
        this.yb = yb;
    }

    public double getZa() {
        return za;
    }

    public void setZa(double za) {
        this.za = za;
    }

    public double getZb() {
        return zb;
    }

    public void setZb(double zb) {
        this.zb = zb;
    }
}

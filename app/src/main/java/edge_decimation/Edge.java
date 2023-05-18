package edge_decimation;

import java.text.DecimalFormat;

public class Edge implements Comparable<Edge> {

    Vector3 a;
    Vector3 b;

    public double cachedError = -1;
    public boolean removed = false;

    Edge(Vector3 a, Vector3 b) {
        if (a.length() < b.length()) {
            this.a = b;
            this.b = a;
        } else {
            this.a = a;
            this.b = b;
        }
        //error();
    }

    public Vector3 getVector3() {
        var q = this.quadric();

        if (Math.abs(q.determinant()) > 1e-3) {
            var v = q.quadricVector();
            if (!Double.isNaN(v.x) && !Double.isNaN(v.y) && !Double.isNaN(v.z))
                return v;
        }

        // cannot compute best vector with matrix
        // look for vest along edge
        int n = 32;
        var a = this.a;
        var b = this.b;
        var bestE = -1d;
        var bestV = new Vector3();

        for (int i = 0; i < n; i++) {
            int frac = i * (1 / n);
            var v = a.lerp(b, frac);
            var e = this.a.q.quadricError(v);
            if (bestE < 0 || e < bestE) {
                bestE = e;
                bestV = v;
            }
        }
        return bestV;
    }

    public double error() {
        if (cachedError < 0) {
            cachedError = quadric().quadricError(getVector3());
        }
        return cachedError;
    }

    public Matrix4 quadric() {
        return a.q.add(b.q);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge other = (Edge) obj;
        return this.a.equals(other.a) && this.b.equals(other.b);
    }

    @Override
    public int hashCode() {
        int result = 17;
        long longBits = Double.doubleToLongBits(this.a.x);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        longBits = Double.doubleToLongBits(this.a.y);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        longBits = Double.doubleToLongBits(this.a.z);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        longBits = Double.doubleToLongBits(this.b.x);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        longBits = Double.doubleToLongBits(this.b.y);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        longBits = Double.doubleToLongBits(this.b.z);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        return result;
    }

    static DecimalFormat df = new DecimalFormat("0.00");
    @Override
    public String toString() {
        return a+","+df.format(a.q.determinant())+" <--> "+b+","+df.format(b.q.determinant()) + " <--> " + cachedError;
    }

    @Override
    public int compareTo(Edge other) {
        if (this.cachedError < other.cachedError) {
            return -1;
        } else if (this.cachedError > other.cachedError) {
            return 1;
        } else {
            return 0;
        }
    }
}

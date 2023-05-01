package edge_decimation;

public class Pair {

    Vector3 a;
    Vector3 b;

    public double cachedError = -1;
    public boolean removed = false;

    Pair(Vector3 a, Vector3 b) {
        if (a.length() < b.length()) {
            this.a = b;
            this.b = a;
        } else {
            this.a = a;
            this.b = b;
        }
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
            cachedError = quadric().quadricError(new Vector3());
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
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair other = (Pair) obj;
        return this.a.equals(other.a) && this.b.equals(other.b);
    }

    @Override
    public int hashCode() {
        return this.a.hashCode() ^ this.b.hashCode();
    }
}

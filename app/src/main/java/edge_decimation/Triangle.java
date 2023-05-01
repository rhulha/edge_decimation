package edge_decimation;

public class Triangle {

    Vector3 p1;
    Vector3 p2;
    Vector3 p3;
    Vector3 normal;
    public boolean removed = false;

    Triangle(Vector3 p1, Vector3 p2, Vector3 p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.normal = new Vector3();
    }

    Triangle(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 normal) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.normal = normal;
    }

    public boolean isDegenerate() {
        return this.p1 == this.p2 || this.p1 == this.p3 || this.p2 == this.p3;
    } 

    public Matrix4 quadric() {
        var n = normal();

        var x = p1.x;
        var y = p1.y;
        var z = p1.z;
        var a = n.x;
        var b = n.y;
        var c = n.z;
        var d = -a * x - b * y - c * z;

        return new Matrix4(
                a * a, a * b, a * c, a * d,
                a * b, b * b, b * c, b * d,
                a * c, b * c, c * c, c * d,
                a * d, b * d, c * d, d * d);
    }

    public Vector3 normal() {
        var e1 = p2.minus(p1);
        var e2 = p3.minus(p2);
        return e1.cross(e2).normalize();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Triangle)) {
            return false;
        }
        Triangle other = (Triangle) obj;
        return this.p1.equals(other.p1) && this.p2.equals(other.p2) && this.p3.equals(other.p3);
    }

    @Override
    public int hashCode() {
        return this.p1.hashCode() ^ this.p2.hashCode() ^ this.p3.hashCode();
    }

}

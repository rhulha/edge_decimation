package edge_decimation;

public class Vector3 {

    public static double eps = 0.01;

	public double x;
	public double y;
	public double z;

    Matrix4 q;
	
	public Vector3() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

    public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
    }
	
    public Vector3(Vector3 o) {
		this.x = o.x;
		this.y = o.y;
		this.z = o.z;
    }

    public Vector3(Vector3 o, Matrix4 q) {
		this.x = o.x;
		this.y = o.y;
		this.z = o.z;
        this.q = q;
    }

    // supports arrays with only 2 elements
	public Vector3( float[] f) {
		this( f[0], f[1], f.length == 3 ? f[2] : 0);
	}

	public Vector3(byte[] b) {
		this( (b[0]&0xFF)/255.0, (b[1]&0xFF)/255.0, (b[2]&0xFF)/255.0);
	}

	public Vector3 getPointSwapZY() {
		return new Vector3( x, z, y);
	}

	public Vector3 minus(Vector3 p2) {
		return new Vector3(x - p2.x, y - p2.y, z - p2.z);
	}

	public Vector3 cross(Vector3 p2) {
		return new Vector3(y * p2.z - z * p2.y, z * p2.x - x * p2.z, x * p2.y - y * p2.x);
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

    public Vector3 lerp(Vector3 other, double t) {
        double newX = this.x + t * (other.x - this.x);
        double newY = this.y + t * (other.y - this.y);
        double newZ = this.z + t * (other.z - this.z);
        return new Vector3(newX, newY, newZ);
    }

	public Vector3 normalize() {
		double len = length();
		if (len == 0) {
			return new Vector3(0, 0, 0);
		} else if (len == 1) {
			return new Vector3(x, y, z);
		} else {
			len = 1 / len;
			return new Vector3(x * len, y * len, z * len);
		}

	}

	public Vector3 negate() {
		return new Vector3( -x, -y, -z);
	}

	public double dot(Vector3 p2) {
		return x * p2.x + y * p2.y + z * p2.z;
	}

	public Vector3 times(double d) {
		return new Vector3(x * d, y * d, z * d);
	}

	public Vector3 dividedBy(double d) {
		return new Vector3(x / d, y / d, z / d);
	}

	public Vector3 plus(int x2, int y2, int z2) {
		return new Vector3(x + x2, y + y2, z + z2);
	}

	public Vector3 plus(Vector3 p) {
		return new Vector3(x + p.x, y + p.y, z + p.z);
	}

	public void scaleInPlace(double d) {
		this.x*=d;
		this.y*=d;
		this.z*=d;
	}

	public Vector3 scale(double d) {
		return new Vector3(this.x*d, this.y*d, this.z*d);
	}

	public Vector3 add(Vector3 p) {
		return new Vector3(this.x+p.x, this.y+p.y, this.z+p.z);
	}

	public static Vector3 getPointSwapZY( double x, double z, double y) {
		return new Vector3( x, y, z);
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Vector3)) {
            return false;
        }
        Vector3 other = (Vector3) obj;
        return Math.abs(this.x - other.x) < eps && Math.abs(this.y - other.y) < eps && Math.abs(this.z - other.z) < eps;
    }

    @Override
    public int hashCode() {
        int result = 17;
        long longBits = Double.doubleToLongBits(this.x);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        longBits = Double.doubleToLongBits(this.y);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        longBits = Double.doubleToLongBits(this.z);
        result = 31 * result + (int) (longBits ^ (longBits >>> 32));
        return result;
    }

	@Override
	public String toString() {
		return "( " + x + " " + y + " " + z +" )";
	}

}
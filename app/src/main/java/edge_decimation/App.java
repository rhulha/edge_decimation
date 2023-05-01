package edge_decimation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {

    public static void main(String[] args) throws IOException {

        String relativePath = ".";
        Path absolutePath = Paths.get(relativePath).toAbsolutePath();
        String absolutePathString = absolutePath.toString();
        System.out.println("Absolute path is: " + absolutePathString);



        List<Triangle> tris = ReadSTL.read("../../bunny.stl");

        List<Triangle> tris_decimated = Converter.simplify(tris);

        FileOutputStream fos = new FileOutputStream("../bunny2.stl");

        String header = "Binary STL file";
        byte[] headerBytes = new byte[80];
        byte[] headerStringBytes = header.getBytes();
        System.arraycopy(headerStringBytes, 0, headerBytes, 0, Math.min(headerStringBytes.length, headerBytes.length));
        fos.write(headerBytes);

        int numTriangles = tris_decimated.size();
        byte[] numTrianglesBytes = new byte[4];
        numTrianglesBytes[0] = (byte) (numTriangles & 0xFF);
        numTrianglesBytes[1] = (byte) ((numTriangles >> 8) & 0xFF);
        numTrianglesBytes[2] = (byte) ((numTriangles >> 16) & 0xFF);
        numTrianglesBytes[3] = (byte) ((numTriangles >> 24) & 0xFF);
        fos.write(numTrianglesBytes);

        for (Triangle triangle : tris_decimated) {

            ByteBuffer buffer = ByteBuffer.allocate(48);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putFloat((float) triangle.normal.x);
            buffer.putFloat((float) triangle.normal.y);
            buffer.putFloat((float) triangle.normal.z);
            buffer.putFloat((float) triangle.p1.x);
            buffer.putFloat((float) triangle.p1.y);
            buffer.putFloat((float) triangle.p1.z);
            buffer.putFloat((float) triangle.p2.x);
            buffer.putFloat((float) triangle.p2.y);
            buffer.putFloat((float) triangle.p2.z);
            buffer.putFloat((float) triangle.p3.x);
            buffer.putFloat((float) triangle.p3.y);
            buffer.putFloat((float) triangle.p3.z);
            fos.write(buffer.array());

            // Write a 2-byte attribute count (usually 0)
            fos.write(new byte[] { 0, 0 });
        }

        fos.close();

    }
}

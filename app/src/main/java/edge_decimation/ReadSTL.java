package edge_decimation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ReadSTL {

    public static List<Triangle> read(String filename) throws IOException {
        
        System.out.println(filename);
        
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        int n = fis.read(data);
        fis.close();
        if (n != file.length()) {
            throw new RuntimeException("read bytes != file size");
        }
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int triNum = bb.getInt(80);
        int triNum2 = (((int) file.length()) - 80 - 4) / 50;
        if (triNum != triNum2) {
            throw new RuntimeException("triNum != triNum2");
        }

        List<Triangle> triangles = new ArrayList<Triangle>();

        int pos = 84;
        while (pos < file.length()) {
            float nx = bb.getFloat(pos); pos += 4;
            float ny = bb.getFloat(pos); pos += 4;
            float nz = bb.getFloat(pos); pos += 4;
            float p1x = bb.getFloat(pos); pos += 4;
            float p1y = bb.getFloat(pos); pos += 4;
            float p1z = bb.getFloat(pos); pos += 4;
            float p2x = bb.getFloat(pos); pos += 4;
            float p2y = bb.getFloat(pos); pos += 4;
            float p2z = bb.getFloat(pos); pos += 4;
            float p3x = bb.getFloat(pos); pos += 4;
            float p3y = bb.getFloat(pos); pos += 4;
            float p3z = bb.getFloat(pos); pos += 4;
            char color = bb.getChar(pos); pos += 2;
            triangles.add(
                new Triangle(
                    new Vector3(p1x, p1y, p1z),
                    new Vector3(p2x, p2y, p2z),
                    new Vector3(p3x, p3y, p3z),
                    new Vector3(nx, ny, nz))
            );
        }
        return triangles;
    }
}

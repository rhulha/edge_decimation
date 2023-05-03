package edge_decimation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {

    public static void main(String[] args) throws IOException {

        /*
        MyPriorityQueue<Integer> mpq = new MyPriorityQueue<Integer>();

        mpq.add(8);
        mpq.add(1);
        mpq.add(9);
        mpq.add(2);
        mpq.add(7);
        mpq.add(4);
        mpq.add(5);
        mpq.add(6);
        mpq.add(1);
        mpq.add(3);

        System.out.println(mpq);

        mpq.remove(4);

        while(mpq.size()>0) {
            System.out.println(mpq.remove());
        }

        if(true) 
            System.exit(0);
        */

        String relativePath = ".";
        Path absolutePath = Paths.get(relativePath).toAbsolutePath();
        String absolutePathString = absolutePath.toString();
        System.out.println("Absolute path is: " + absolutePathString);

        List<Triangle> tris = STLFile.read("../../bunny.stl");
        List<Triangle> tris_decimated = Converter.simplify(tris);
        STLFile.write("../../bunny_java.stl", tris_decimated);
    }
}

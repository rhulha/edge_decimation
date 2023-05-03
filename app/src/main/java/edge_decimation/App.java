package edge_decimation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {

    public static void main(String[] args) throws IOException {
        String relativePath = ".";
        Path absolutePath = Paths.get(relativePath).toAbsolutePath();
        String absolutePathString = absolutePath.toString();
        System.out.println("Absolute path is: " + absolutePathString);

        List<Triangle> tris = STLFile.read("../../bunny.stl");
        List<Triangle> tris_decimated = Converter.simplify(tris, 0.1f);
        STLFile.write("../../bunny_java.stl", tris_decimated);
    }
}

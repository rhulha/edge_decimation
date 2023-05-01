package edge_decimation;


import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;


public class Converter {

    public static List<Triangle> simplify(List<Triangle> tris) {
        HashMap<Vector3, Vector3> vectorAndQuadric = new HashMap<Vector3, Vector3>();

        HashMap<Vector3, List<Triangle>> vertexFaces = new HashMap<Vector3, List<Triangle>>();

        for (Triangle t : tris) {
            vectorAndQuadric.putIfAbsent(t.p1, new Vector3(t.p1));
            vectorAndQuadric.putIfAbsent(t.p2, new Vector3(t.p2));
            vectorAndQuadric.putIfAbsent(t.p2, new Vector3(t.p2));
            Matrix4 q = t.quadric();
            Vector3 v1 = vectorAndQuadric.get(t.p1);
            Vector3 v2 = vectorAndQuadric.get(t.p2);
            Vector3 v3 = vectorAndQuadric.get(t.p3);

            v1.q = v1.q.add(q);
            v2.q = v2.q.add(q);
            v3.q = v3.q.add(q);

            Triangle face = new Triangle(v1, v2, v3);

            vertexFaces.computeIfAbsent(v1, k -> new ArrayList<>()).add(face);
            vertexFaces.computeIfAbsent(v2, k -> new ArrayList<>()).add(face);
            vertexFaces.computeIfAbsent(v3, k -> new ArrayList<>()).add(face);
        }

        HashSet<Pair> pairs = new HashSet<Pair>();
        
        for (Triangle t : tris) 
        {
            Vector3 v1 = vectorAndQuadric.get(t.p1);
            Vector3 v2 = vectorAndQuadric.get(t.p2);
            Vector3 v3 = vectorAndQuadric.get(t.p3);

            pairs.add(new Pair(v1,v2));
            pairs.add(new Pair(v2,v3));
            pairs.add(new Pair(v1,v3));

        }

        HashMap<Vector3, List<Pair>> vertexPairs = new HashMap<Vector3, List<Pair>>();
        for (Pair p : pairs)
        {
            vertexPairs.computeIfAbsent(p.a, k -> new ArrayList<>()).add(p);
            vertexPairs.computeIfAbsent(p.b, k -> new ArrayList<>()).add(p);
        }

        var priorityQueue = new PriorityQueue<Pair>();
        for (Pair p : pairs)
        {
            p.error();
            priorityQueue.add(p); // compare by cachedError
        }

        int currentFaceCount = tris.size();

        for (int i = 0; i < 100; i++) {
            Pair p = priorityQueue.remove();

            if (p.removed)
                continue;

            p.removed = true;

            //get distinct faces 
            var distinctFaces = new HashSet<Triangle>();
            if (vertexFaces.containsKey(p.a)) {
                for(var face : vertexFaces.get(p.a))    {
                    if (!face.removed)
                    {
                        distinctFaces.add(face);
                    }
                }
            }
            if (vertexFaces.containsKey(p.b)) {
                for(var face : vertexFaces.get(p.b))    {
                    if (!face.removed)
                    {
                        distinctFaces.add(face);
                    }
                }
            }

            //get related pairs
            var distintPairs = new HashSet<Pair>();
            if (vertexPairs.containsKey(p.a)) {
                for (var pair : vertexPairs.get(p.a))
                {
                    if (!pair.removed)
                    {
                        distintPairs.add(pair);
                    }
                }
            }
            if (vertexPairs.containsKey(p.b)) {
                for (var pair : vertexPairs.get(p.b))
                {
                    if (!pair.removed)
                    {
                        distintPairs.add(pair);
                    }
                }
            }

            //create new vertex
            Vector3 vaq = new Vector3(p.getVector3());
            vaq.q = p.quadric();


            //updateFaces
            var newFaces = new ArrayList<Triangle>();
            boolean valid = true;
            for (var face_loop : distinctFaces)
            {
                var v1 = face_loop.p1;
                var v2 = face_loop.p2;
                var v3 = face_loop.p3;

                if (v1 == p.a || v1 == p.b)
                    v1 = vaq;

                if (v2 == p.a || v2 == p.b)
                    v2 = vaq;

                if (v3 == p.a || v3 == p.b)
                    v3 = vaq;

                var face_new = new Triangle(v1, v2, v3);

                if (face_new.isDegenerate())
                    continue;

                if (face_new.normal().dot(face_loop.normal()) < 1e-3)
                {
                    valid = false;
                    break;
                }

                newFaces.add(face_new);
            }

            if (!valid)
                continue;

            if (vertexFaces.containsKey(p.a))
                vertexFaces.remove(p.a);

            if (vertexFaces.containsKey(p.b))
                vertexFaces.remove(p.b);

            for (var f : distinctFaces)
            {
                f.removed = true;
                currentFaceCount--;
            }

            for(var f : newFaces)
            {
                currentFaceCount++;

                vertexFaces.computeIfAbsent(f.p1, k -> new ArrayList<>()).add(f);
                vertexFaces.computeIfAbsent(f.p2, k -> new ArrayList<>()).add(f);
                vertexFaces.computeIfAbsent(f.p3, k -> new ArrayList<>()).add(f);
            }

            if (vertexPairs.containsKey(p.a))
                vertexPairs.remove(p.a);

            if (vertexPairs.containsKey(p.b))
                vertexPairs.remove(p.b);

            var seen = new HashMap<Vector3, Boolean>();

            for (var q : distintPairs)
            {
                q.removed = true;
                priorityQueue.remove(q);
                var a = q.a;
                var b = q.b;

                if (a.equals(p.a) || a.equals(p.b))
                {
                    a = vaq;
                }
                if (b.equals(p.a) || b.equals(p.b))
                {
                    b = vaq;
                }
                if (b.equals(vaq))
                {
                    var temp = a;
                    a = b;
                    b = temp;
                }
                if (seen.containsKey(b) && seen.get(b))
                {
                    //ignore duplicates
                    continue;
                }
                if (!seen.containsKey(b))
                    seen.put(b, true);
                else
                    seen.put(b, true);

                var np = new Pair(a, b);
                np.error();
                priorityQueue.add(np); // , np.cachedError

                vertexPairs.computeIfAbsent(a, k -> new ArrayList<>()).add(np);
                vertexPairs.computeIfAbsent(b, k -> new ArrayList<>()).add(np);
            }
        }

        //gather distinct faces
        var finalDistinctFaces = new HashSet<Triangle>();
        for(var faces : vertexFaces.values())
        {
            for(var face : faces)
            {
                if (!face.removed)
                {
                    if (!finalDistinctFaces.contains(face))
                        finalDistinctFaces.add(face);
                }
            }
        }

        //create final mesh

        return new ArrayList<>(finalDistinctFaces);

        //List<Triangle> newMesh = finalDistinctFaces.stream().map(x -> new Triangle(x.p1, x.p2, x.p3)).collect(Collectors.toList());
    }
    
}

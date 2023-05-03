package edge_decimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Converter {

    public static List<Triangle> simplify(List<Triangle> tris, float factor) {
        HashMap<Vector3, Vector3> vectorAndQuadric = new HashMap<Vector3, Vector3>();
        HashMap<Vector3, List<Triangle>> vertexFaces = new HashMap<Vector3, List<Triangle>>();
        HashSet<Pair> pairs = new HashSet<Pair>();
        HashMap<Vector3, List<Pair>> vertexPairs = new HashMap<Vector3, List<Pair>>();

        for (Triangle t : tris) {
            vectorAndQuadric.putIfAbsent(t.p1, new Vector3(t.p1, new Matrix4()));
            vectorAndQuadric.putIfAbsent(t.p2, new Vector3(t.p2, new Matrix4()));
            vectorAndQuadric.putIfAbsent(t.p3, new Vector3(t.p3, new Matrix4()));
            Vector3 v1 = vectorAndQuadric.get(t.p1);
            Vector3 v2 = vectorAndQuadric.get(t.p2);
            Vector3 v3 = vectorAndQuadric.get(t.p3);

            Matrix4 q = t.quadric();
            v1.q = v1.q.add(q);
            v2.q = v2.q.add(q);
            v3.q = v3.q.add(q);

            Triangle face = new Triangle(v1, v2, v3);

            vertexFaces.computeIfAbsent(v1, k -> new ArrayList<>()).add(face);
            vertexFaces.computeIfAbsent(v2, k -> new ArrayList<>()).add(face);
            vertexFaces.computeIfAbsent(v3, k -> new ArrayList<>()).add(face);

            pairs.add(new Pair(v1,v2));
            pairs.add(new Pair(v2,v3));
            pairs.add(new Pair(v1,v3));
        }


        for (Pair p : pairs)
        {
            vertexPairs.computeIfAbsent(p.a, k -> new ArrayList<>()).add(p);
            vertexPairs.computeIfAbsent(p.b, k -> new ArrayList<>()).add(p);
        }

        System.out.println("vertexFaces.size(): " + vertexFaces.size());
        System.out.println("pairs.size(): " + pairs.size());
        System.out.println("vertexPairs.size(): " + vertexPairs.size());

        var priorityQueue = new MyPriorityQueue<Pair>(); 
        for (Pair p : pairs)
        {
            p.error();
            priorityQueue.add(p); // compare by cachedError
        }

        int currentFaceCount = tris.size();
        int targetFaceCount = (int)(currentFaceCount * factor);
        int i=0;
        while(currentFaceCount > targetFaceCount && priorityQueue.size() > 0) {
            Pair p = priorityQueue.remove();

            if (p.removed)
                continue;

            p.removed = true;

            //get distinct faces 
            var distinctFaces = getDistinctFaces(vertexFaces, p);

            //get related pairs
            var distinctPairs = getDistinctPairs(vertexPairs, p);

            if( i++<0 ) {
                System.out.println("distinctFaces.size(): " + distinctFaces.size());
                System.out.println("distinctPairs.size(): " + distinctPairs.size());
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

                if (v1 ==p.a || v1==p.b)
                    v1 = vaq;

                if (v2==p.a || v2==p.b)
                    v2 = vaq;

                if (v3==p.a || v3==p.b)
                    v3 = vaq;

                var face_new = new Triangle(v1, v2, v3);

                if (face_new.isDegenerate())
                {
                    continue;
                }

                if (face_new.normal().dot(face_loop.normal()) < 1e-3) // 0 means 90 degrees
                {
                    valid = false;
                    break;
                }

                newFaces.add(face_new);
            }

            if (!valid)
                continue;

            vertexFaces.remove(p.a);
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

            vertexPairs.remove(p.a);
            vertexPairs.remove(p.b);

            var seen = new HashSet<Vector3>();

            for (var dp : distinctPairs)
            {
                dp.removed = true;
                priorityQueue.remove(dp);
                var a = dp.a;
                var b = dp.b;

                if (a==p.a || a==p.b)
                {
                    a = vaq;
                }
                if (b==p.a || b==p.b)
                {
                    b = vaq;
                }
                if (b.equals(vaq))
                {
                    var temp = a;
                    a = b;
                    b = temp;
                }
                if (seen.contains(b))
                {
                    continue; //ignore duplicates
                }
                seen.add(b);

                var np = new Pair(a, b);
                np.error();
                priorityQueue.add(np);

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

    private static HashSet<Pair> getDistinctPairs(HashMap<Vector3, List<Pair>> vertexPairs, Pair p) {
        var distinctPairs = new HashSet<Pair>();
        if (vertexPairs.containsKey(p.a)) {
            for (var pair : vertexPairs.get(p.a))
            {
                if (!pair.removed)
                {
                    distinctPairs.add(pair);
                }
            }
        }
        if (vertexPairs.containsKey(p.b)) {
            for (var pair : vertexPairs.get(p.b))
            {
                if (!pair.removed)
                {
                    distinctPairs.add(pair);
                }
            }
        }
        return distinctPairs;
    }

    private static HashSet<Triangle> getDistinctFaces(HashMap<Vector3, List<Triangle>> vertexFaces, Pair p) {
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
        return distinctFaces;
    }
    
}

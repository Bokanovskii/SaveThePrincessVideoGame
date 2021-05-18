import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.stream.Stream;

public class RandomPathingStrategy implements PathingStrategy {
    public List<Point> computePath(Point start, Point lastPos,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = potentialNeighbors.apply(start)
                .filter(canPassThrough)
                .filter(pt -> !pt.equals(start) && !pt.equals(lastPos))
                .collect(Collectors.toList());
        Collections.shuffle(path);
        return path.stream().limit(1).collect(Collectors.toList());
    }
}

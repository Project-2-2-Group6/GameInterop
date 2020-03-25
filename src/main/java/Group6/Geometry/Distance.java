package Group6.Geometry;

import Interop.Utils.Require;

/**
 * Represents an euclidean distance.
 *
 * @author Tomasz Darmetko
 */
public final class Distance extends Vector{

    private double distance;

    public Distance(double distance) {
        Require.realNumber(distance, "Distance must be real!");
        Require.notNegative(distance, "Distance can not be negative!");
        this.distance = distance;
    }

    public Distance(Interop.Geometry.Distance distance) {
        this(distance.getValue());
    }

    public double getValue() {
        return distance;
    }

    public Interop.Geometry.Distance toInteropDistance() {
        return new Interop.Geometry.Distance(distance);
    }

}

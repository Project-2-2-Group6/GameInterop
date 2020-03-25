package Group6.Geometry;

import Group6.ExtendedUnitTest;
import Interop.Utils.Utils;

/**
 * @author Tomasz Darmetko
 */
public class VectorTest extends ExtendedUnitTest {

    public static void main(String[] args) {

        System.out.println("\n\nVector Test\n");

        it("allows to do full rotation of a vector", () -> {

            assertEqual(
                new Vector(0, 1).rotate(Math.PI * 2),
                new Vector(0, 1),
                "Rotated by Math.PI * 2"
            );

            assertEqual(
                new Vector(0, 1).rotate(0),
                new Vector(0, 1),
                "Rotated by 0."
            );

        });

        it("allows to do half rotation of a vector", () -> {

            assertEqual(
                new Vector(0, 1).rotate(Math.PI),
                new Vector(0, -1),
                "Rotated by Math.PI"
            );

        });

        it("allows to do 90 degrees rotation a vector", () -> {

            assertEqual(
                new Vector(0, 1).rotate(Math.PI / 2),
                new Vector(1, 0),
                "Rotated by Math.PI / 2"
            );

            assertEqual(
                new Vector(0, 1).rotate(-Math.PI / 2),
                new Vector(-1, 0),
                "Rotated by -Math.PI / 2"
            );

        });

    }

}

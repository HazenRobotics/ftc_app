package org.firstinspires.ftc.teamcode.models;

import org.firstinspires.ftc.teamcode.reflection.BiMapping;
import org.firstinspires.ftc.teamcode.reflection.Mapping;

/**
 * A location or displacement in two dimensions.
 */
public class Vector {
    public static final Vector ZERO = new Vector(0, 0);

    public static final Mapping<Vector, Double> TO_X = new Mapping<Vector, Double>() {
        @Override
        public Double map(Vector x) {
            return x.getX();
        }
    };

    public static final Mapping<Vector, Double> TO_Y = new Mapping<Vector, Double>() {
        @Override
        public Double map(Vector x) {
            return x.getY();
        }
    };

    public static final Mapping<Vector, Double> TO_MAGNITUDE = new Mapping<Vector, Double>() {
        @Override
        public Double map(Vector x) {
            return x.getMagnitude();
        }
    };

    public static final Mapping<Vector, Double> TO_ANGLE = new Mapping<Vector, Double>() {
        @Override
        public Double map(Vector x) {
            return x.getAngle();
        }
    };

    public static final Mapping<Vector, Vector> MAP_NEGATE = liftMap(Mapping.DOUBLE_NEGATE, Mapping.DOUBLE_NEGATE);

    public static final Mapping<Vector, Vector> MAP_NEGATE_X = liftMapX(Mapping.DOUBLE_NEGATE);

    public static final Mapping<Vector, Vector> MAP_NEGATE_Y = liftMapY(Mapping.DOUBLE_NEGATE);

    /**
     * This vector is internally implemented with x/y, but it could also be implemented with polar coordinates and make no difference.
     */
    private final double x, y;

    /**
     * Constructs a new vector from an x and y value.
     *
     * See {@link #fromPolar(double, double)} to make a vector from polar coordinates.
     * @param x
     * @param y
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * An alternative constructor, making a vector from polar coordinates rather than Cartesian ones.
     * See {@link #Vector(double, double)} for the normal constructor.
     *
     * This is a static function rather than a constructor because the types would conflict.
     * @param magnitude The vector's magnitude.
     * @param angle The vector's angle.
     * @return A new vector constructed from polar coordinates.
     */
    public static Vector fromPolar(double magnitude, double angle) {
        return new Vector(
                magnitude * Math.cos(Math.toRadians(angle)),
                magnitude * Math.sin(Math.toRadians(angle))
        );
    }

    public static Mapping<Vector, Vector> liftMap(final Mapping<Double, Double> fx, final Mapping<Double, Double> fy) {
        return new Mapping<Vector, Vector>() {
            @Override
            public Vector map(Vector x) {
                return x.map(fx, fy);
            }
        };
    }

    public static Mapping<Vector, Vector> liftMap(final Mapping<Double, Double> f) {
        return new Mapping<Vector, Vector>() {
            @Override
            public Vector map(Vector x) {
                return x.map(f);
            }
        };
    }

    public static Mapping<Vector, Vector> liftMapMagnitude(final Mapping<Double, Double> f) {
        return new Mapping<Vector, Vector>() {
            @Override
            public Vector map(Vector x) {
                return x.mapMagnitude(f);
            }
        };
    }

    public static Mapping<Vector, Vector> liftMapX(Mapping<Double, Double> f) {
        return liftMap(f, Mapping.IDENTITY);
    }

    public static Mapping<Vector, Vector> liftMapY(Mapping<Double, Double> f) {
        return liftMap(Mapping.IDENTITY, f);
    }

    /**
     * @return The x displacement of the vector in x/y form.
     */
    public double getX() {
        return x;
    }

    /**
     * @return The y displacement of the vector in x/y form.
     */
    public double getY() {
        return y;
    }

    /**
     * @return The angle of the vector, or the angle to get to the x/y from the origin.
     */
    public double getAngle() {
        return Math.toDegrees(Math.atan2(x, y));
    }

    /**
     * @return The magnitude of the vector, or the total distance of the x/y from the origin.
     */
    public double getMagnitude() {
        return Math.hypot(x, y);
    }

    /**
     * Displaces this vector by the given other vector.
     * @param displacement The displacement vector.
     * @return A new vector displaced by the given value.
     */
    public Vector add(Vector displacement) {
        return new Vector(this.x + displacement.x, this.y + displacement.y);
    }

    public Vector sub(Vector displacement) {
        return this.add(displacement.negate());
    }

    public Vector negate() {
        return this.scale(-1);
    }

    public Vector scale(double scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    /**
     * Gets the difference between this vector and another vector.
     * @param other The vector to be subtracted.
     * @return The difference between the vectors.
     */
    public Vector delta(Vector other) {
        return new Vector(x - other.x, y - other.y);
    }

    public Vector adjustMagnitude() {
        return this.scale(1 / this.getMagnitude());
    }

    public Vector bind(BiMapping<Double, Double, Vector> f) {
        return f.map(getX(), getY());
    }

    public Vector map(Mapping<Double, Double> fx, Mapping<Double, Double> fy) {
        return new Vector(fx.map(getX()), fy.map(getY()));
    }

    public Vector map(Mapping<Double, Double> f) {
        return map(f, f);
    }

    public Vector mapX(Mapping<Double, Double> f) {
        return map(f, Mapping.IDENTITY);
    }

    public Vector mapY(Mapping<Double, Double> f) {
        return map(Mapping.IDENTITY, f);
    }

    public Vector mapMagnitude(Mapping<Double, Double> f) {
        return Vector.fromPolar(f.map(getMagnitude()), getAngle());
    }
}

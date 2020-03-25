package Group6.WorldState.Object;

import Group6.Geometry.*;
import Group6.Geometry.Collection.Points;
import Group6.Geometry.Collection.Quadrilaterals;
import Group6.Geometry.Contract.Area;
import Group6.WorldState.Collision;
import Group6.WorldState.Contract.Object;
import Group6.WorldState.Pheromone;
import Group6.WorldState.Teleports;
import Group6.WorldState.WorldState;
import Interop.Action.Action;
import Interop.Action.DropPheromone;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Utils.Require;

import java.util.Set;

/**
 * @author Tomasz Darmetko
 */
public abstract class AgentState implements Object {

    private final double RADIUS = 0.5;

    private Point location;
    private Direction direction;
    private int cooldown;
    private boolean justTeleported;
    private boolean wasLastActionExecuted;

    protected AgentState(Point location, Direction direction, int cooldown, boolean justTeleported, boolean wasLastActionExecuted) {
        this.location = location;
        this.direction = direction;
        this.cooldown = cooldown;
        this.justTeleported = justTeleported;
        this.wasLastActionExecuted = wasLastActionExecuted;
    }

    public Point getLocation() {
        return location;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isInside(Area area) {
        return location.isInside(area);
    }

    public Circle getCircle() {
        return new Circle(location, RADIUS);
    }

    public boolean isInRange(Point point, Distance distance) {
        return getCircle().isInRange(point, distance);
    }

    public Points getIntersections(LineSegment lineSegment) {
        return getCircle().getIntersections(lineSegment);
    }

    public boolean hasInside(Point point) {
        return getCircle().hasInside(point);
    }

    public Direction getPerceivedDirectionTo(Point point) {
        return direction.getRelativeTo(
            point.subtract(location).toPoint().getClockDirection()
        );
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean hasCooldown() {
        return cooldown > 0;
    }

    public void addCooldown(int length) {
        Require.positive(length, "Cooldown length must be at least 1 turn.");
        this.cooldown += length;
    }

    public void nextTurn() {
        if(hasCooldown()) cooldown--;
    }

    public boolean isJustTeleported() {
        return justTeleported;
    }

    public boolean wasLastActionExecuted() {
        return wasLastActionExecuted;
    }

    public void rejectAction() {
        wasLastActionExecuted = false;
    }

    public void move(WorldState worldState, Move action) {
        requireNoCooldown(action);
        move(worldState, new Distance(((Move)action).getDistance()));
        markActionAsExecuted();
    }

    protected void move(WorldState worldState, Distance distance) {

        boolean hasCollision = new Collision(this, distance, worldState.getScenario()).checkCollision();
        if(hasCollision) throw new IllegalAction("move or sprint", "move or sprint resulted in collision");

        Vector displacement = new Vector(0, distance.getValue()).rotate(direction.getRadians());
        location = location.add(displacement).toPoint();

        Teleports teleports = worldState.getScenario().getTeleports();
        if(!isJustTeleported() && isInside(teleports)) {
            teleport(teleports);
        }

    }

    protected void teleport(Teleports teleports) {
        location = teleports.getTargetArea(location).getRandomPointInside();
        direction = Direction.random();
        justTeleported = true;
    }

    public void rotate(WorldState worldState, Rotate action) {
        if(Math.abs(action.getAngle().getRadians()) > worldState.getScenario().getMaxRotationAngle().getRadians()) {
            throw new IllegalAction("move", "rotation bigger than allowed");
        }
        requireNoCooldown(action);
        direction = direction.getChangedBy(
            Angle.fromInteropAngle(action.getAngle())
        );
        markActionAsExecuted();
    }

    public void dropPheromone(WorldState worldState, DropPheromone action) {
        requireNoCooldown(action);
        worldState.addPheromone(
            Pheromone.createByAgent(worldState, this, (DropPheromone)action)
        );
        addCooldown(worldState.getScenario().getPheromoneCooldown());
        markActionAsExecuted();
    }

    public void noAction() {
        markActionAsExecuted();
    }

    protected void markActionAsExecuted() {
        wasLastActionExecuted = true;
    }

    protected void requireNoCooldown(Action action) {
        if (hasCooldown()) throw new IllegalActionDuringCooldown(action.getClass().getName());
    }

    class IllegalAction extends RuntimeException {
        public IllegalAction(String action, String explanation) {
            super(
                "Following action: " + action + " is illegal!\n" +
                "Explanation: " + explanation
            );
        }
    }

    class IllegalActionDuringCooldown extends RuntimeException {
        public IllegalActionDuringCooldown(String action) {
            super(
                "Following action: " + action + " can not be executed during cooldown!\n" +
                "Cooldown left: " + cooldown
            );
        }
    }

}

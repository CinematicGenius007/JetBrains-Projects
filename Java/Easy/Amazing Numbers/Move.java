package amazingNumbers;

public class Move {

	public static void main(String[] args) {
		Robot robot = new Robot(0, 0, Direction.UP);
		moveRobot(robot, 3, 5);
//		System.out.println(robot.getDirection() != Direction.UP);
		System.out.println(robot.getX() + " : " + robot.getY());
//		robot.turnRight();
//		robot.stepForward();
//		System.out.println(robot.getX() + " : " + robot.getY());
		

	}

	public static void moveRobot(Robot robot, int toX, int toY) {
        int cx = robot.getX();
        int cy = robot.getY();
        
        int dx = toX - cx;
        int dy = toY - cy;
        
        System.out.println(robot.getX() + " : " + robot.getY()
        		+ "  " + dx + " : " + dy);
        
        boolean dir = false;
        
        while (cx != toX) {
            if (dx > 0 && !dir) {
                while (robot.getDirection() != Direction.RIGHT) {
                    if (robot.getDirection() == Direction.UP) {
                        robot.turnRight();
                    } else {
                        robot.turnLeft();
                    }
                }
                dir = true;
            } else if (dx < 0 && !dir) {
                while (robot.getDirection() != Direction.LEFT) {
                    if (robot.getDirection() == Direction.UP) {
                        robot.turnLeft();
                    } else {
                        robot.turnRight();
                    }
                }
                dir = true;
            }
            
            robot.stepForward();
            cx = dx > 0 ? ++cx : --cx;
//            System.out.println("cx = " + cx);
        }
        
        dir = false;
        
        while (cy != toY) {
            if (dy > 0 && !dir) {
                while (robot.getDirection() != Direction.UP) {
                    if (robot.getDirection() == Direction.LEFT) {
                        robot.turnRight();
                    } else {
                        robot.turnLeft();
                    }
                }
                dir = true;
            } else if (dy < 0 && !dir) {
                while (robot.getDirection() != Direction.DOWN) {
                    if (robot.getDirection() == Direction.LEFT) {
                        robot.turnLeft();
                    } else {
                        robot.turnRight();
                    }
                }
                dir = true;
            }
            
            robot.stepForward();
            cy = dy > 0 ? ++cy : --cy;
        }
        
    }
}

enum Direction {
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction turnLeft() {
        switch (this) {
            case UP:
                return LEFT;
            case DOWN:
                return RIGHT;
            case LEFT:
                return DOWN;
            case RIGHT:
                return UP;
            default:
                throw new IllegalStateException();
        }
    }

    public Direction turnRight() {
        switch (this) {
            case UP:
                return RIGHT;
            case DOWN:
                return LEFT;
            case LEFT:
                return UP;
            case RIGHT:
                return DOWN;
            default:
                throw new IllegalStateException();
        }
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }
}

class Robot {
    private int x;
    private int y;
    private Direction direction;

    public Robot(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void turnLeft() {
        direction = direction.turnLeft();
    }

    public void turnRight() {
        direction = direction.turnRight();
    }

    public void stepForward() {
        x += direction.dx();
        y += direction.dy();
    }

    public Direction getDirection() {
        return direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

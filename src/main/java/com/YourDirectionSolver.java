package com;

import com.utils.Board;
import com.utils.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Shredinger
 */
public class YourDirectionSolver implements DirectionSolver {

    private final char WALL1 = '#';
    private final char WALL2 = '☼';
    private final char BOMB = '҉';

    private static final Map<Integer, Direction> directions = new HashMap<Integer, Direction>();

    static {
        directions.put(0, Direction.LEFT);
        directions.put(1, Direction.RIGHT);
        directions.put(2, Direction.UP);
        directions.put(3, Direction.DOWN);
    }

    @Override
    public String get(Board board) {
        try {
            Point myBomberMan = board.getBomberman();
            int x = myBomberMan.getX();
            int y = myBomberMan.getY();

            HashMap<Integer, Integer> pointNumSolutionMap = new HashMap<Integer, Integer>();

            Point[] points = new Point[]{new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1)};
            int pointNumber = 0;
            for (int i = 0; i < points.length; i++) {
                Point current = points[i];
                Element currentElement = board.getAt(current.getX(), current.getY());

                board.countNear(x, y, Element.BOMB_TIMER_1);
                if(
                        currentElement != Element.WALL &&
                                currentElement != Element.DESTROY_WALL &&
                                currentElement != Element.DESTROYED_WALL &&
                                currentElement != Element.MEAT_CHOPPER

                        ){

                    //int bombsCountNear = getBombsCountNear(board, myBomberMan, current);
                int wallsNear = getWallsNear(board, current.getX(), current.getY());
                int bombsCountNear = getBombsCount(board, myBomberMan, current);
                pointNumSolutionMap.put(i, bombsCountNear + wallsNear);

                }
            }

            Direction bestDirection = Direction.DOWN;
            int argsMaxFromMap = getArgsMinFromMap(pointNumSolutionMap);

            pointNumSolutionMap.forEach((num, count) -> System.out.print(num + " -> " + count + ", "));
            System.out.println("best: " + argsMaxFromMap);

            try {
                bestDirection = directions.get(argsMaxFromMap);
                if(bestDirection == null){
                    bestDirection = Direction.DOWN;
                    System.out.println("FUCK!!!!!!");
                }

                bestDirection.toString();
            }catch (Exception e){
                e.printStackTrace();
            }



            return bestDirection.toString() + "," + Direction.ACT.toString();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return Direction.DOWN.toString() + "," + Direction.ACT.toString();
    }

    private int getBombsNearNear(Board board, int x, int y){
        return getBombsAt(board, x - 1, y - 1) + getBombsAt(board, x + 1, y + 1) + getBombsAt(board, x - 1, y + 1) + getBombsAt(board, x + 1, y - 1);
    }

    private int getArgsMinFromMap(Map<Integer, Integer> map){
        int indexOfMin = -1;
        for (Integer i : map.keySet()) {
            indexOfMin = indexOfMin == -1 ? i : indexOfMin;
            if(map.get(i) < map.get(indexOfMin)){
                indexOfMin = i;
            }
        }
        return indexOfMin;
    }

    private int getBombsCountNear(Board board, Point me, Point next){
        int count = 0;
        int x = next.getX();
        int y = next.getY();
        count +=  (board.countNear(x, y, Element.BOOM ) * 6);
        count +=  (board.countNear(x, y, Element.BOMB_TIMER_1) * 5);
        count +=  (board.countNear(x, y, Element.BOMB_TIMER_2) * 4);
        count +=  (board.countNear(x, y, Element.BOMB_TIMER_3) * 3);
        count +=  (board.countNear(x, y, Element.BOMB_TIMER_4) * 2);
        count +=  (board.countNear(x, y, Element.BOMB_TIMER_5) * 1);
        count +=  (board.countNear(x, y, Element.WALL) * 5);
        count +=  (board.countNear(x, y, Element.DESTROY_WALL) * 5);

        return count;
    }

    private int getBombsCount(Board board, Point me, Point next){
        int count = 0;
        int x = me.getX();
        int y = me.getY();
        int nx = next.getX();
        int ny = next.getY();
        if(nx < x){//left
            for (int i = x - 1; i >= 0; i--) {
                if(board.isAt(i, ny, Element.WALL) || board.isAt(i, ny, Element.DESTROY_WALL)){
                    break;
                }
                count += getBombsAt(board, i, ny);
            }
            return count;
        }
        if(nx > x){//right
            for (int i = x + 1; i < board.boardSize(); i++) {
                if(board.isAt(i, ny, Element.WALL) || board.isAt(i, ny, Element.DESTROY_WALL)){
                    break;
                }
                count += getBombsAt(board, i, ny);
            }
            return count;
        }
        if(ny < y){//up
            for (int i = y - 1; i >= 0; i--) {
                if(board.isAt(nx, i, Element.WALL) || board.isAt(nx, i, Element.DESTROY_WALL)){
                    break;
                }
                count += getBombsAt(board, nx, i);
            }
            return count;
        }
        if(ny > y){//down
            for (int i = y + 1; i < board.boardSize(); i++) {
                if(board.isAt(nx, i, Element.WALL) || board.isAt(nx, i, Element.DESTROY_WALL)){
                    break;
                }
                count += getBombsAt(board, nx, i);
            }
            return count;
        }

        return count;
    }



    private int getBombsAt(Board board, int x, int y){
        int count = 0;
        count +=  board.isAt(x, y, Element.BOOM) ? 512 : 0;
        count +=  board.isAt(x, y, Element.BOMB_TIMER_1) ? 256 : 0;
        count +=  board.isAt(x, y, Element.BOMB_TIMER_2) ? 8 : 0;
        count +=  board.isAt(x, y, Element.BOMB_TIMER_3) ? 4 : 0;
        count +=  board.isAt(x, y, Element.BOMB_TIMER_4) ? 1 : 0;

        return count;
    }

    private int getWallsNear(Board board, int x, int y){
        int count = 0;
        count += board.countNear(x, y, Element.WALL) * 64;
        count += board.countNear(x, y, Element.DESTROY_WALL) * 32;

        return count;
    }

}

//data = board=☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼   #    # ## # #     &       ##☼☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼#☼☼   #     #&                    ☼☼ ☼ ☼ ☼#☼#☼ ☼ ☼#☼ ☼#☼ ☼ ☼#☼ ☼ ☼ ☼☼  ♥     # #  #     ♥ ♥       # ☼☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼#☼♥☼#☼ ☼ ☼#☼ ☼#☼☼              #              # ☼☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼#☼#☼☼ #        #  #                 ☼☼ ☼#☼ ☼ ☼#☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼#☼☼  #     # ##  #   #♥#  #       ☼☼ ☼ ☼ ☼ ☼ ☼#☼&☼ ☼ ☼#☼#☼ ☼ ☼#☼ ☼ ☼☼          #  #        &      # ☼☼ ☼ ☼ ☼ ☼#☼#☼ ☼ ☼#☼#☼ ☼ ☼ ☼ ☼ ☼ ☼☼    &         ҉҉҉Ѡ♣#           ☼☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼☼#    #   #    &   #           #☼☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼☼H♣҉҉҉          ####    #     # ☼☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼#☼ ☼ ☼#☼☼   #      ♥# ## #      &       ☼☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼ ☼☼             # # #   #   #   ##☼☼ ☼ ☼ ☼#☼ ☼ ☼#☼ ☼♥☼ ☼ ☼ ☼#☼ ☼ ☼ ☼☼ #                             ☼☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼☼     #           #         &&  ☼☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼#☼ ☼ ☼ ☼#☼☼    # #        &               ☼☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼#☼ ☼ ☼♥☼ ☼☼        #          #     #     ☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼
package com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathFinderResult;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.List;

import static com.codenjoy.dojo.services.Direction.ACT;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.model.PathPointPriority.checkPriorityHigher;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.pathfinder.PathFinder.world;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.buildDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.DirectionUtils.getOppositeDirection;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.canPassThrough;
import static com.codenjoy.dojo.snakebattle.client.pathfinder.util.PathFinderUtils.childrenDirections;
import static java.util.stream.Collectors.toList;

public class DirectionProvider {

    public String getFinalDirectionString(PathFinderResult result) {
        System.out.println("Final result: " + result);
        if (result != null) {
            if (result.getDirection().equals(ACT) || result.getNextPoint() == null) {
                return anyDirection();
            }

            System.out.println("Next point: " + result.getNextPoint());
            if (Elements.STONE.equals(result.getNextPoint().getElementType())) {
                world.getMySnake().changeStoneCount(1);
            }

            if (Elements.STONE.equals(result.getNextPoint().getElementType())) {
                world.getMySnake().setFuryCounter(1);
            }

            //if (shouldDropStone(result.getNextPoint().getX(), result.getNextPoint().getY())) {
            //    world.getMySnake().changeStoneCount(-1);
            //    return buildDirection(ACT, result.getDirection());
            //}
            return result.getDirection().toString();
        } else {
            return anyDirection();
        }
    }

    public String anyDirection() {
        Point me = world.getBoard().getMe();

        for (int[] direction : childrenDirections) {
            if (canPassThrough(me.getX() + direction[0], me.getY() + direction[1])) {
                return getDirection(direction).toString();
            }
        }

        Direction[] opposite = getOppositeDirection(world.getMySnake().getDirection());

        return buildDirection(opposite[0], opposite[1]);
    }

    public PathFinderResult getNextResult(List<PathFinderResult> results) {
        List<PathFinderResult> withNextPoint = results.stream()
                .filter(r -> r.getNextPoint() != null).collect(toList());

        int min = withNextPoint.stream()
                .mapToInt(PathFinderResult::getDistance)
                .min().orElse(Integer.MAX_VALUE);

        PathFinderResult result = null;

        results = results.stream()
                .filter(p -> p.getDistance() >= min && p.getDistance() < min + 2)
                .collect(toList());

        for (PathFinderResult currentResult : results) {
            if (checkPriorityHigher(currentResult, result)) {
                result = currentResult;
            }
        }

        // TODO weight
        return result;
    }

    public String furyDirection() {
        if (world.getMySnake().isFury()
                && world.getMySnake().getFuryCounter() < 9
                && world.getMySnake().getFuryCounter() > 0) {
            Direction[] turnAroundDirection = getOppositeDirection(world.getMySnake().getDirection());
            System.out.println("Fury: counter: " + world.getMySnake().getFuryCounter() + ", stones: " + world.getMySnake().getStoneCount());
            return buildDirection(turnAroundDirection[0], turnAroundDirection[1], ACT);
        }

        return anyDirection();
    }
}
